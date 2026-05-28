package com.mgpark.fridgelab.ui.ingredients

import androidx.lifecycle.ViewModel
import com.mgpark.fridgelab.domain.usecase.RecognizeIngredientsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class IngredientsViewModel @Inject constructor(
    private val recognizeIngredients: RecognizeIngredientsUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(IngredientsState())
    val state: StateFlow<IngredientsState> = _state.asStateFlow()

    fun handleIntent(intent: IngredientsIntent) {
        // TODO: intent별 처리 (recognizeIngredients UseCase 활용)
    }
}