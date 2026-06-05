package com.mgpark.fridgelab.domain.model

/** 재료 카테고리 (디자인 핸드오프 data.js). */
enum class Category(val key: String, val label: String) {
    VEG("veg", "채소"),
    MEAT("meat", "고기·해산물"),
    DAIRY("dairy", "유제품·달걀"),
    FRUIT("fruit", "과일"),
    ETC("etc", "양념·기타");

    companion object {
        fun fromKey(key: String): Category =
            entries.firstOrNull { it.key == key.lowercase() } ?: ETC
    }
}
