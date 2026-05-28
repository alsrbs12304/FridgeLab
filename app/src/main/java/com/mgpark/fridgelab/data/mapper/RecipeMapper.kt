package com.mgpark.fridgelab.data.mapper

import com.mgpark.fridgelab.data.dto.RecipeDto
import com.mgpark.fridgelab.domain.model.Recipe

fun RecipeDto.toDomain(): Recipe = Recipe(
    title = title,
    ingredients = ingredients,
    steps = steps
)
