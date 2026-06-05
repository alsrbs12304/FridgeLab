package com.mgpark.fridgelab.data.mapper

import com.mgpark.fridgelab.data.dto.IngredientDto
import com.mgpark.fridgelab.domain.model.Ingredient

fun IngredientDto.toDomain(): Ingredient = Ingredient(
    name = name,
    quantity = quantity?.takeIf { it.isNotBlank() }
)
