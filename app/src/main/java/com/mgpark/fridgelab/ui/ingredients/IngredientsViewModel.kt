package com.mgpark.fridgelab.ui.ingredients

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mgpark.fridgelab.domain.usecase.RecognizeIngredientsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IngredientsViewModel @Inject constructor(
    private val recognizeIngredients: RecognizeIngredientsUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(IngredientsState())
    val state: StateFlow<IngredientsState> = _state.asStateFlow()

    // 동일 이미지로 재인식이 반복되지 않도록 가드
    private var recognitionStarted = false

    fun handleIntent(intent: IngredientsIntent) {
        when (intent) {
            is IngredientsIntent.OnRecognize -> recognize(intent.image)
            is IngredientsIntent.OnAdd -> _state.update {
                it.copy(ingredients = it.ingredients + intent.ingredient)
            }
            is IngredientsIntent.OnRemove -> _state.update {
                it.copy(ingredients = it.ingredients - intent.ingredient)
            }
        }
    }

    private fun recognize(image: ByteArray) {
        if (recognitionStarted) return
        recognitionStarted = true
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val result = recognizeIngredients(image)
                _state.update { it.copy(isLoading = false, ingredients = result) }
            } catch (e: Exception) {
                _state.update {
                    it.copy(isLoading = false, errorMessage = "재료 인식에 실패했습니다: ${e.message}")
                }
            }
        }
    }
}
