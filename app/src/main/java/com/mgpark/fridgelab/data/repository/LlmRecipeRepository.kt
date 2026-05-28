package com.mgpark.fridgelab.data.repository

import com.mgpark.fridgelab.data.mapper.toDomain
import com.mgpark.fridgelab.data.remote.RecipeGenerator
import com.mgpark.fridgelab.domain.model.Ingredient
import com.mgpark.fridgelab.domain.model.Recipe
import com.mgpark.fridgelab.domain.repository.RecipeRepository
import javax.inject.Inject

class LlmRecipeRepository @Inject constructor(
    private val generator: RecipeGenerator
) : RecipeRepository {
    override suspend fun recommend(ingredients: List<Ingredient>): List<Recipe> =
        generator.generate(ingredients).map { it.toDomain() }
}
