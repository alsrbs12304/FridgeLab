package com.mgpark.fridgelab.domain.usecase

import com.mgpark.fridgelab.domain.model.Ingredient
import com.mgpark.fridgelab.domain.model.Recipe
import com.mgpark.fridgelab.domain.repository.RecipeRepository
import javax.inject.Inject

class RecommendRecipesUseCase @Inject constructor(
    private val repository: RecipeRepository
) {
    suspend operator fun invoke(ingredients: List<Ingredient>): List<Recipe> =
        repository.recommend(ingredients)
}
