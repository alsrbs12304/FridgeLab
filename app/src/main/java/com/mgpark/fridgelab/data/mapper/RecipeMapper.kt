package com.mgpark.fridgelab.data.mapper

import com.mgpark.fridgelab.data.dto.RecipeDto
import com.mgpark.fridgelab.domain.model.Level
import com.mgpark.fridgelab.domain.model.NeededIngredient
import com.mgpark.fridgelab.domain.model.Recipe

fun RecipeDto.toDomain(id: String): Recipe = Recipe(
    id = id,
    name = name,
    imageUrl = null,
    timeMin = timeMin.coerceAtLeast(0),
    level = Level.fromLabel(level),
    servings = servings.coerceAtLeast(1),
    tags = tags,
    desc = desc,
    need = need.map { NeededIngredient(it.name, it.amount, it.have) },
    steps = steps
)
