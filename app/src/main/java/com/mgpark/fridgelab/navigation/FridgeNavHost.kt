package com.mgpark.fridgelab.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mgpark.fridgelab.ui.camera.CameraScreen
import com.mgpark.fridgelab.ui.ingredients.IngredientsScreen
import com.mgpark.fridgelab.ui.recipes.RecipesScreen

/**
 * 앱 전체 네비게이션 그래프.
 *
 * 카메라 → 재료 → 레시피 순서로 이동하며, 세 화면은 [FridgeSessionViewModel]을
 * NavGraph 범위로 공유한다(Phase 2 이후 sharedViewModel()로 연결).
 */
@Composable
fun FridgeNavHost(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = FridgeRoute.Camera.route,
        route = FRIDGE_GRAPH_ROUTE
    ) {
        composable(FridgeRoute.Camera.route) { entry ->
            val session = entry.sharedViewModel<FridgeSessionViewModel>(navController)
            CameraScreen(
                onImageCaptured = { image ->
                    session.setCapturedImage(image)
                    navController.navigate(FridgeRoute.Ingredients.route)
                }
            )
        }
        composable(FridgeRoute.Ingredients.route) { entry ->
            val session = entry.sharedViewModel<FridgeSessionViewModel>(navController)
            val image by session.capturedImage.collectAsStateWithLifecycle()
            IngredientsScreen(
                image = image,
                onIngredientsConfirmed = { ingredients ->
                    session.setIngredients(ingredients)
                    navController.navigate(FridgeRoute.Recipes.route)
                }
            )
        }
        composable(FridgeRoute.Recipes.route) {
            RecipesScreen(
                onRestart = {
                    navController.navigate(FridgeRoute.Camera.route) {
                        popUpTo(FridgeRoute.Camera.route) { inclusive = true }
                    }
                }
            )
        }
    }
}
