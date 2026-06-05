package com.mgpark.fridgelab.ui.recipes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mgpark.fridgelab.domain.model.Ingredient
import com.mgpark.fridgelab.domain.usecase.RecommendRecipesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecipesViewModel @Inject constructor(
    private val recommendRecipes: RecommendRecipesUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(RecipesState())
    val state: StateFlow<RecipesState> = _state.asStateFlow()

    private var lastIngredients: List<Ingredient> = emptyList()
    private var recommendStarted = false

    fun handleIntent(intent: RecipesIntent) {
        when (intent) {
            is RecipesIntent.OnRecommend -> {
                lastIngredients = intent.ingredients
                // 최초 1회만 자동 추천 (리컴포지션 시 중복 호출 방지)
                if (!recommendStarted) {
                    recommendStarted = true
                    recommend(intent.ingredients)
                }
            }

            RecipesIntent.OnRefresh -> recommend(lastIngredients)
        }
    }

    private fun recommend(ingredients: List<Ingredient>) {
        if (ingredients.isEmpty()) return
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val result = recommendRecipes(ingredients)
                _state.update { it.copy(isLoading = false, recipes = result) }
            } catch (e: Exception) {
                _state.update {
                    it.copy(isLoading = false, errorMessage = "레시피를 불러오지 못했습니다: ${e.message}")
                }
            }
        }
    }
}
