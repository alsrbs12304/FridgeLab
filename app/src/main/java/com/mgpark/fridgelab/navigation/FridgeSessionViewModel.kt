package com.mgpark.fridgelab.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mgpark.fridgelab.domain.model.Category
import com.mgpark.fridgelab.domain.model.Freshness
import com.mgpark.fridgelab.domain.model.Ingredient
import com.mgpark.fridgelab.domain.model.Recipe
import com.mgpark.fridgelab.domain.usecase.RecognizeIngredientsUseCase
import com.mgpark.fridgelab.domain.usecase.RecommendRecipesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

/** 카메라 셔터 후 AI 분석 진행 상태. */
data class AnalysisState(
    val analyzing: Boolean = false,
    val progress: Float = 0f,
    val foundCount: Int = 0,
    val done: Boolean = false
)

/** 레시피 추천 로딩/결과 상태. */
data class RecipesUiState(
    val loading: Boolean = false,
    val recipes: List<Recipe> = emptyList(),
    val error: String? = null
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

    private var addCounter = 0

    // ── 카메라: 셔터 → 분석 애니메이션 + 실제 Gemini 인식 ──
    fun startAnalysis(image: ByteArray, onComplete: () -> Unit) {
        if (_analysis.value.analyzing) return
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
            val result = try {
                recognizeIngredients(image)
            } catch (e: Exception) {
                emptyList()
            }
            anim.cancel()
            _ingredients.value = result
            _analysis.value = AnalysisState(
                analyzing = true, progress = 1f, foundCount = result.size, done = true
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

    // ── 레시피 추천 ──
    fun recommendIfNeeded() {
        if (_recipes.value.loading || _recipes.value.recipes.isNotEmpty()) return
        recommend()
    }

    fun recommend() {
        val selected = _ingredients.value.filter { it.selected }
        if (selected.isEmpty()) return
        viewModelScope.launch {
            _recipes.update { it.copy(loading = true, error = null) }
            try {
                val result = recommendRecipes(selected)
                _recipes.value = RecipesUiState(loading = false, recipes = result)
            } catch (e: Exception) {
                _recipes.value = RecipesUiState(
                    loading = false,
                    error = "레시피를 불러오지 못했어요: ${e.message}"
                )
            }
        }
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
