package com.mgpark.fridgelab.ui.ingredients

import com.mgpark.fridgelab.domain.model.Ingredient

sealed interface IngredientsIntent {
    data class OnRecognize(val image: ByteArray) : IngredientsIntent
    data class OnAdd(val ingredient: Ingredient) : IngredientsIntent
    data class OnRemove(val ingredient: Ingredient) : IngredientsIntent
}
