package com.mgpark.fridgelab.domain.model

data class Ingredient(
    val id: String,
    val name: String,
    val category: Category,
    val qty: Int = 1,
    val unit: String = "개",
    val freshness: Freshness = Freshness.FRESH,
    val confidence: Float = 1f,      // AI 신뢰도 0~1 (<0.8 이면 "확인 필요")
    val selected: Boolean = true
) {
    val needsReview: Boolean get() = confidence < 0.8f
}
