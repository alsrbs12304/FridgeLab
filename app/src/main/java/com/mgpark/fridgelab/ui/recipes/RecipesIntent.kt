package com.mgpark.fridgelab.ui.recipes

import com.mgpark.fridgelab.domain.model.Ingredient

sealed interface RecipesIntent {
    data class OnRecommend(val ingredients: List<Ingredient>) : RecipesIntent
    data object OnRefresh : RecipesIntent
}
