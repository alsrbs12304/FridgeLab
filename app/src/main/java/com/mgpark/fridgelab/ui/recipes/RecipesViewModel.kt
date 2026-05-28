package com.mgpark.fridgelab.ui.recipes

import androidx.lifecycle.ViewModel
import com.mgpark.fridgelab.domain.usecase.RecommendRecipesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class RecipesViewModel @Inject constructor(
    private val recommendRecipes: RecommendRecipesUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(RecipesState())
    val state: StateFlow<RecipesState> = _state.asStateFlow()

    fun handleIntent(intent: RecipesIntent) {
        // TODO: intent별 처리 (recommendRecipes UseCase 활용)
    }
}