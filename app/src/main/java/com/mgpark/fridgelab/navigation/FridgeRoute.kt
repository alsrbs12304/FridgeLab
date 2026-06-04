package com.mgpark.fridgelab.navigation

/** 카메라 → 재료 → 레시피 흐름을 감싸는 NavGraph 라우트. 공유 ViewModel 스코핑 기준이 된다. */
const val FRIDGE_GRAPH_ROUTE = "fridge_graph"

/** 앱의 화면 라우트 정의. */
sealed class FridgeRoute(val route: String) {
    data object Camera : FridgeRoute("camera")
    data object Ingredients : FridgeRoute("ingredients")
    data object Recipes : FridgeRoute("recipes")
}
