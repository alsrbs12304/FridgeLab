package com.mgpark.fridgelab.domain.model

import kotlin.math.roundToInt

/** 레시피에 필요한 개별 재료. have=false 이면 부족(장보기 필요). */
data class NeededIngredient(
    val name: String,
    val amount: String,
    val have: Boolean
)

data class Recipe(
    val id: String,
    val name: String,
    val imageUrl: String? = null,
    val timeMin: Int,
    val level: Level,
    val servings: Int,
    val tags: List<String>,
    val desc: String,
    val need: List<NeededIngredient>,
    val steps: List<String>
) {
    val haveCount: Int get() = need.count { it.have }
    val missCount: Int get() = need.size - haveCount
    val missing: List<NeededIngredient> get() = need.filter { !it.have }

    /** 매칭률 = 보유 / 전체 * 100 (반올림). */
    val matchPct: Int
        get() = if (need.isEmpty()) 0 else (haveCount.toFloat() / need.size * 100).roundToInt()
}
