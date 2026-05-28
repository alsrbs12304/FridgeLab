package com.mgpark.fridgelab.ui.recipes

import com.mgpark.fridgelab.domain.model.Recipe

data class RecipesState(
    val isLoading: Boolean = false,
    val recipes: List<Recipe> = emptyList(),
    val errorMessage: String? = null
)