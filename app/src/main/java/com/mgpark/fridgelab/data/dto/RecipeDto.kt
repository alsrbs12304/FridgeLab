package com.mgpark.fridgelab.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class NeededIngredientDto(
    val name: String = "",
    val amount: String = "",
    val have: Boolean = false
)

@Serializable
data class RecipeDto(
    val name: String = "",
    val timeMin: Int = 0,
    val level: String = "쉬움",        // 쉬움 / 보통 / 어려움
    val servings: Int = 1,
    val tags: List<String> = emptyList(),
    val desc: String = "",
    val need: List<NeededIngredientDto> = emptyList(),
    val steps: List<String> = emptyList()
)
