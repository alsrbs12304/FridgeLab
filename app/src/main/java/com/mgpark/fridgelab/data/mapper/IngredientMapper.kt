package com.mgpark.fridgelab.data.mapper

import com.mgpark.fridgelab.data.dto.IngredientDto
import com.mgpark.fridgelab.domain.model.Category
import com.mgpark.fridgelab.domain.model.Freshness
import com.mgpark.fridgelab.domain.model.Ingredient

/** 사진 인식 결과는 신선도를 알 수 없으므로 기본 '신선'으로 두고 사용자가 편집한다. */
fun IngredientDto.toDomain(id: String): Ingredient = Ingredient(
    id = id,
    name = name,
    category = Category.fromKey(category),
    qty = qty.coerceAtLeast(1),
    unit = unit.ifBlank { "개" },
    freshness = Freshness.FRESH,
    confidence = confidence.coerceIn(0f, 1f),
    selected = true
)
