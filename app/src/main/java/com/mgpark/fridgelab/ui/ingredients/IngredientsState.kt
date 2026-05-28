package com.mgpark.fridgelab.ui.ingredients

import com.mgpark.fridgelab.domain.model.Ingredient

data class IngredientsState(
    val isLoading: Boolean = false,
    val ingredients: List<Ingredient> = emptyList(),
    val errorMessage: String? = null
)