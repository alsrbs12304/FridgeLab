package com.mgpark.fridgelab.navigation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mgpark.fridgelab.domain.model.Category
import com.mgpark.fridgelab.domain.model.Freshness
import com.mgpark.fridgelab.domain.model.Ingredient
import com.mgpark.fridgelab.domain.model.Recipe
import com.mgpark.fridgelab.domain.usecase.RecognizeIngredientsUseCase
import com.mgpark.fridgelab.domain.usecase.RecommendRecipesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

/** 카메라 셔터 후 AI 분석 진행 상태. notice = 진행 중 보조 안내(예: 자동 재시도). */
data class AnalysisState(
    val analyzing: Boolean = false,
    val progress: Float = 0f,
    val foundCount: Int = 0,
    val done: Boolean = false,
    val notice: String? = null
)

/** 레시피 추천 로딩/결과 상태. notice = 로딩 중 보조 안내(예: 자동 재시도). */
data class RecipesUiState(
    val loading: Boolean = false,
    val recipes: List<Recipe> = emptyList(),
    val error: String? = null,
    val notice: String? = null
)

/**
 * 카메라 → 재료 → 레시피 → 상세 화면이 공유하는 **앱 전역 단일 상태 저장소**.
 * NavGraph(FRIDGE_GRAPH_ROUTE) 범위로 스코핑되어 편집 상태가 화면 이동에도 보존된다.
 */
@HiltViewModel
class FridgeSessionViewModel @Inject constructor(
    private val recognizeIngredients: RecognizeIngredientsUseCase,
    private val recommendRecipes: RecommendRecipesUseCase
) : ViewModel() {

    private val _analysis = MutableStateFlow(AnalysisState())
    val analysis: StateFlow<AnalysisState> = _analysis.asStateFlow()

    private val _ingredients = MutableStateFlow<List<Ingredient>>(emptyList())
    val ingredients: StateFlow<List<Ingredient>> = _ingredients.asStateFlow()

    private val _recipes = MutableStateFlow(RecipesUiState())
    val recipes: StateFlow<RecipesUiState> = _recipes.asStateFlow()

    private val _openRecipeId = MutableStateFlow<String?>(null)
    val openRecipeId: StateFlow<String?> = _openRecipeId.asStateFlow()

    // 인식 실패 사유(0개일 때 재료 화면에 안내). 성공 시 null.
    private val _recognizeError = MutableStateFlow<String?>(null)
    val recognizeError: StateFlow<String?> = _recognizeError.asStateFlow()

    private var addCounter = 0
    private var recommendJob: Job? = null
    private var lastQuery: String? = null

    // ── 카메라: 셔터 → 분석 애니메이션 + 실제 Gemini 인식 ──
    fun startAnalysis(image: ByteArray, onComplete: () -> Unit) {
        if (_analysis.value.analyzing) return
        _recognizeError.value = null
        _analysis.value = AnalysisState(analyzing = true)
        viewModelScope.launch {
            // 진행률 애니메이션을 실제 호출과 함께 진행(완료 전까지 92%까지만)
            val anim = launch {
                var p = 0f
                while (isActive && p < 0.92f) {
                    delay(70)
                    p = (p + 0.02f).coerceAtMost(0.92f)
                    _analysis.update { it.copy(progress = p, foundCount = (p * 13).toInt()) }
                }
            }

            var attempt = 0
            var ingredients: List<Ingredient> = emptyList()
            var error: String? = null
            while (true) {
                try {
                    ingredients = recognizeIngredients(image)
                    break
                } catch (e: Exception) {
                    Log.e(TAG, "재료 인식 실패 (시도 ${attempt + 1})", e)
                    val waitMs = quotaRetryMillis(e)
                    if (waitMs != null && attempt < MAX_RETRY) {
                        attempt++
                        val sec = (waitMs + 999) / 1000
                        _analysis.update { it.copy(notice = "요청이 많아요 · ${sec}초 후 자동 재시도 ($attempt/$MAX_RETRY)") }
                        delay(waitMs)
                        _analysis.update { it.copy(notice = null) }
                    } else {
                        error = when {
                            isQuotaError(e) && quotaRetryMillis(e) == null ->
                                "무료 사용량(일일 한도)을 모두 사용했어요. 잠시 뒤 다시 시도하거나 요금제를 확인해 주세요."
                            isQuotaError(e) ->
                                "요청 한도를 초과했어요. 잠시 후 다시 촬영해 주세요."
                            else ->
                                "재료를 인식하지 못했어요. 다시 촬영하거나 직접 추가해 주세요."
                        }
                        break
                    }
                }
            }
            anim.cancel()
            _ingredients.value = ingredients
            // 호출은 성공했지만 인식 결과가 0개인 경우도 안내
            _recognizeError.value = error
                ?: if (ingredients.isEmpty()) "사진에서 재료를 찾지 못했어요. 다시 촬영하거나 직접 추가해 주세요." else null
            _analysis.value = AnalysisState(
                analyzing = true, progress = 1f, foundCount = ingredients.size, done = true
            )
            delay(450)
            _analysis.value = AnalysisState()
            onComplete()
        }
    }

    // ── 재료 편집 (단일 소스) ──
    fun toggleSelect(id: String) = updateItem(id) { it.copy(selected = !it.selected) }

    fun setQty(id: String, qty: Int) = updateItem(id) { it.copy(qty = qty.coerceAtLeast(1)) }

    fun cycleFreshness(id: String) = updateItem(id) { it.copy(freshness = it.freshness.next()) }

    fun remove(id: String) {
        _ingredients.update { list -> list.filterNot { it.id == id } }
    }

    fun add(name: String, category: Category) {
        val trimmed = name.trim()
        if (trimmed.isEmpty()) return
        val item = Ingredient(
            id = "user_${addCounter++}",
            name = trimmed,
            category = category,
            qty = 1,
            unit = "개",
            freshness = Freshness.FRESH,
            confidence = 1f,
            selected = true
        )
        _ingredients.update { it + item }
    }

    fun toggleAll() {
        val list = _ingredients.value
        val allSelected = list.isNotEmpty() && list.all { it.selected }
        _ingredients.value = list.map { it.copy(selected = !allSelected) }
    }

    val selectedCount: Int get() = _ingredients.value.count { it.selected }

    // ── 레시피 추천 (중복 호출 방지로 API 할당량 절약) ──
    fun recommend(force: Boolean = false) {
        val selected = _ingredients.value.filter { it.selected }
        if (selected.isEmpty()) return

        // 선택된 재료 구성의 서명(id+수량). 같으면 같은 결과가 나오므로 재요청 불필요.
        val signature = selected.joinToString("|") { "${it.id}:${it.qty}" }
        val state = _recipes.value

        // 동일 재료이고 이미 결과가 있거나 로딩 중이면 재요청 생략
        if (!force && signature == lastQuery && (state.loading || state.recipes.isNotEmpty())) return
        // 진행 중이면 연타 무시
        if (recommendJob?.isActive == true) return

        lastQuery = signature
        recommendJob = viewModelScope.launch {
            _recipes.update { it.copy(loading = true, error = null, notice = null) }
            var attempt = 0
            while (true) {
                try {
                    val result = recommendRecipes(selected)
                    _recipes.value = RecipesUiState(loading = false, recipes = result)
                    return@launch
                } catch (e: Exception) {
                    val waitMs = quotaRetryMillis(e)
                    if (waitMs != null && attempt < MAX_RETRY) {
                        // 재시도 시간이 명시된 quota 오류(분당 한도) → 그만큼 기다렸다 자동 재시도
                        attempt++
                        val sec = (waitMs + 999) / 1000
                        _recipes.update {
                            it.copy(
                                loading = true, error = null,
                                notice = "요청이 많아요 · ${sec}초 후 자동 재시도 ($attempt/$MAX_RETRY)"
                            )
                        }
                        delay(waitMs)
                    } else {
                        lastQuery = null  // 실패 시 동일 재료로도 재시도 가능하게
                        _recipes.value = RecipesUiState(loading = false, error = friendlyError(e))
                        return@launch
                    }
                }
            }
        }
    }

    /** quota/rate-limit 오류 여부. */
    private fun isQuotaError(e: Throwable): Boolean {
        val cls = e.javaClass.name
        if (cls.contains("Quota", true) || cls.contains("ResourceExhausted", true)) return true
        val msg = (e.message ?: "") + " " + (e.cause?.message ?: "")
        return listOf("quota", "RESOURCE_EXHAUSTED", "429", "rate limit", "exceeded your current quota")
            .any { msg.contains(it, ignoreCase = true) }
    }

    /**
     * 재시도 권장 시간(ms)을 반환. **명시된 retry 시간이 있을 때만** 값을 주고,
     * 없으면(=일일/하드 한도라 기다려도 소용없음) null → 재시도하지 않는다.
     */
    private fun quotaRetryMillis(e: Throwable): Long? {
        if (!isQuotaError(e)) return null
        val msg = (e.message ?: "") + " " + (e.cause?.message ?: "")
        val sec = Regex("""retry in ([0-9]+(?:\.[0-9]+)?)\s*s""", RegexOption.IGNORE_CASE)
            .find(msg)?.groupValues?.get(1)?.toDoubleOrNull()
            ?: Regex("""retryDelay"?\s*[:=]?\s*"?([0-9]+(?:\.[0-9]+)?)s""", RegexOption.IGNORE_CASE)
                .find(msg)?.groupValues?.get(1)?.toDoubleOrNull()
            ?: return null   // 재시도 시간이 없으면 재시도하지 않음
        return (sec * 1000).toLong().coerceIn(1000L, 60_000L) + 500L
    }

    private fun friendlyError(e: Throwable): String = when {
        isQuotaError(e) && quotaRetryMillis(e) == null ->
            "무료 사용량(일일 한도)을 모두 사용했어요. 잠시 뒤 다시 시도하거나 요금제를 확인해 주세요."
        isQuotaError(e) ->
            "요청 한도를 초과했어요. 잠시 후 다시 시도해 주세요."
        else ->
            "레시피를 불러오지 못했어요: ${e.message}"
    }

    private companion object {
        const val MAX_RETRY = 2
        const val TAG = "FridgeLab"
    }

    // ── 상세 ──
    fun openRecipe(id: String) {
        _openRecipeId.value = id
    }

    fun recipeById(id: String?): Recipe? =
        _recipes.value.recipes.firstOrNull { it.id == id }

    private inline fun updateItem(id: String, crossinline transform: (Ingredient) -> Ingredient) {
        _ingredients.update { list -> list.map { if (it.id == id) transform(it) else it } }
    }
}
