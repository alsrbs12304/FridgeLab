package com.mgpark.fridgelab.navigation

import androidx.lifecycle.ViewModel
import com.mgpark.fridgelab.domain.model.Ingredient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

/**
 * 카메라 → 재료 → 레시피 화면이 공유하는 세션 상태 홀더.
 *
 * NavGraph(FRIDGE_GRAPH_ROUTE) 범위로 스코핑되어 화면 전환 간 데이터를 전달한다.
 * 이미지(ByteArray)를 nav 인자로 넘기는 대신 이 홀더로 공유한다.
 * - Phase 2: 촬영 이미지(capturedImage) 채움
 * - Phase 3: 인식된 재료(ingredients) 채움
 */
@HiltViewModel
class FridgeSessionViewModel @Inject constructor() : ViewModel() {
    private val _capturedImage = MutableStateFlow<ByteArray?>(null)
    val capturedImage: StateFlow<ByteArray?> = _capturedImage.asStateFlow()

    private val _ingredients = MutableStateFlow<List<Ingredient>>(emptyList())
    val ingredients: StateFlow<List<Ingredient>> = _ingredients.asStateFlow()

    fun setCapturedImage(image: ByteArray) {
        _capturedImage.value = image
    }

    fun setIngredients(ingredients: List<Ingredient>) {
        _ingredients.value = ingredients
    }
}
