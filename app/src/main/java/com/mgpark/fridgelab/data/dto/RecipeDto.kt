package com.mgpark.fridgelab.data.dto

data class RecipeDto(
    val title: String = "",
    val ingredients: List<String> = emptyList(),
    val steps: List<String> = emptyList()
)
