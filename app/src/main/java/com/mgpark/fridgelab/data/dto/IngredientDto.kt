package com.mgpark.fridgelab.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class IngredientDto(
    val name: String = "",
    val category: String = "etc",     // veg / meat / dairy / fruit / etc
    val qty: Int = 1,
    val unit: String = "개",
    val confidence: Float = 1f         // 0~1
)
