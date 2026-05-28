package com.mgpark.fridgelab.domain.repository

import com.mgpark.fridgelab.domain.model.Ingredient

interface IngredientRepository {
    suspend fun recognize(image: ByteArray): List<Ingredient>
}
