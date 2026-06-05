package com.mgpark.fridgelab.domain.model

/** 레시피 난이도 — 정렬 시 rank 사용(쉬움<보통<어려움). */
enum class Level(val label: String, val rank: Int) {
    EASY("쉬움", 0),
    MEDIUM("보통", 1),
    HARD("어려움", 2);

    companion object {
        fun fromLabel(label: String): Level =
            entries.firstOrNull { it.label == label.trim() } ?: EASY
    }
}
