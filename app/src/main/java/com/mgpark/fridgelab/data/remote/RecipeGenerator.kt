package com.mgpark.fridgelab.data.remote

import com.google.firebase.ai.GenerativeModel
import com.mgpark.fridgelab.data.dto.RecipeDto
import com.mgpark.fridgelab.domain.model.Ingredient
import javax.inject.Inject

class RecipeGenerator @Inject constructor(
    private val model: GenerativeModel
) {
    // TODO: 재료를 프롬프트로 만들어 Gemini로부터 구조화된 응답을 받기
    suspend fun generate(ingredients: List<Ingredient>): List<RecipeDto> = emptyList()
}
