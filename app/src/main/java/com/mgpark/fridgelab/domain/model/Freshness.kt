package com.mgpark.fridgelab.domain.model

/** 신선도 — 신선 / 보통 / 임박. 칩 탭 시 순환. */
enum class Freshness(val label: String) {
    FRESH("신선"),
    OK("보통"),
    SOON("임박");

    fun next(): Freshness = when (this) {
        FRESH -> OK
        OK -> SOON
        SOON -> FRESH
    }

    companion object {
        fun fromKey(key: String): Freshness = when (key.lowercase()) {
            "fresh" -> FRESH
            "ok" -> OK
            "soon" -> SOON
            else -> FRESH
        }
    }
}
