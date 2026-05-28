package com.mgpark.fridgelab.data.repository

import com.mgpark.fridgelab.data.remote.IngredientRecognizer
import com.mgpark.fridgelab.domain.model.Ingredient
import com.mgpark.fridgelab.domain.repository.IngredientRepository
import javax.inject.Inject

class AiIngredientRepository @Inject constructor(
    private val recognizer: IngredientRecognizer
) : IngredientRepository {
    override suspend fun recognize(image: ByteArray): List<Ingredient> =
        recognizer.recognize(image)
}
