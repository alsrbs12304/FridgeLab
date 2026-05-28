package com.mgpark.fridgelab.domain.model

data class Recipe(
    val title: String,
    val ingredients: List<String>,
    val steps: List<String>
)