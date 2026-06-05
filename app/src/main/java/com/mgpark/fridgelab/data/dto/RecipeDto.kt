package com.mgpark.fridgelab.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class RecipeDto(
    val title: String = "",
    val ingredients: List<String> = emptyList(),
    val steps: List<String> = emptyList()
)
