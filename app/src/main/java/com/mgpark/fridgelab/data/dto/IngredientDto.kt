package com.mgpark.fridgelab.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class IngredientDto(
    val name: String = "",
    val quantity: String? = null
)
