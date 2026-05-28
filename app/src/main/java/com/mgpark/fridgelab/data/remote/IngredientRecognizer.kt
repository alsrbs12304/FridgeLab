package com.mgpark.fridgelab.data.remote

import com.google.firebase.ai.GenerativeModel
import com.mgpark.fridgelab.domain.model.Ingredient
import javax.inject.Inject

class IngredientRecognizer @Inject constructor(
    private val model: GenerativeModel
) {
    // TODO: 이미지를 Gemini에 전달해 재료를 인식
    suspend fun recognize(image: ByteArray): List<Ingredient> = emptyList()
}
