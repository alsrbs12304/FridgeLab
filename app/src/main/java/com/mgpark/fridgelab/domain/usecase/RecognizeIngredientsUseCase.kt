package com.mgpark.fridgelab.domain.usecase

import com.mgpark.fridgelab.domain.model.Ingredient
import com.mgpark.fridgelab.domain.repository.IngredientRepository
import javax.inject.Inject

class RecognizeIngredientsUseCase @Inject constructor(
    private val repository: IngredientRepository
) {
    suspend operator fun invoke(image: ByteArray): List<Ingredient> =
        repository.recognize(image)
}