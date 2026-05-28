package com.mgpark.fridgelab.domain.repository

import com.mgpark.fridgelab.domain.model.Ingredient
import com.mgpark.fridgelab.domain.model.Recipe

interface RecipeRepository {
    suspend fun recommend(ingredients: List<Ingredient>): List<Recipe>
}