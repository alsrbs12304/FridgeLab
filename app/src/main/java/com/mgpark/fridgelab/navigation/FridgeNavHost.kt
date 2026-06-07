package com.mgpark.fridgelab.navigation

import android.app.Activity
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mgpark.fridgelab.ui.camera.CameraScreen
import com.mgpark.fridgelab.ui.ingredients.IngredientScreen
import com.mgpark.fridgelab.ui.recipes.RecipeDetailScreen
import com.mgpark.fridgelab.ui.recipes.RecipesScreen

private const val DUR = 320

/**
 * 앱 전체 네비게이션 그래프 (카메라 → 재료 → 레시피 → 상세).
 * 네 화면은 [FridgeSessionViewModel]을 NavGraph 범위로 공유한다(단일 상태 소스).
 */
@Composable
fun FridgeNavHost(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = FridgeRoute.Camera.route,
        route = FRIDGE_GRAPH_ROUTE,
        enterTransition = { slideInHorizontally(tween(DUR)) { it / 12 } + fadeIn(tween(DUR)) },
        exitTransition = { fadeOut(tween(DUR / 2)) },
        popEnterTransition = { slideInHorizontally(tween(DUR)) { -it / 12 } + fadeIn(tween(DUR)) },
        popExitTransition = { slideOutHorizontally(tween(DUR)) { it } + fadeOut(tween(DUR)) }
    ) {
        composable(FridgeRoute.Camera.route) { entry ->
            val session = entry.sharedViewModel<FridgeSessionViewModel>(navController)
            val analysis by session.analysis.collectAsStateWithLifecycle()
            val context = LocalContext.current
            CameraScreen(
                analysis = analysis,
                onCapture = { bytes ->
                    session.startAnalysis(bytes) {
                        navController.navigate(FridgeRoute.Ingredients.route)
                    }
                },
                onClose = { (context as? Activity)?.finish() }
            )
        }

        composable(FridgeRoute.Ingredients.route) { entry ->
            val session = entry.sharedViewModel<FridgeSessionViewModel>(navController)
            val items by session.ingredients.collectAsStateWithLifecycle()
            val recogError by session.recognizeError.collectAsStateWithLifecycle()
            IngredientScreen(
                ingredients = items,
                errorNote = recogError,
                onBack = {
                    navController.navigate(FridgeRoute.Camera.route) {
                        popUpTo(FridgeRoute.Camera.route) { inclusive = true }
                    }
                },
                onToggle = session::toggleSelect,
                onQty = session::setQty,
                onFresh = session::cycleFreshness,
                onRemove = session::remove,
                onAdd = session::add,
                onToggleAll = session::toggleAll,
                onFind = {
                    session.recommend()
                    navController.navigate(FridgeRoute.Recipes.route)
                }
            )
        }

        composable(FridgeRoute.Recipes.route) { entry ->
            val session = entry.sharedViewModel<FridgeSessionViewModel>(navController)
            val recipes by session.recipes.collectAsStateWithLifecycle()
            val items by session.ingredients.collectAsStateWithLifecycle()
            RecipesScreen(
                ingredientCount = items.count { it.selected },
                state = recipes,
                onBack = { navController.popBackStack() },
                onOpen = { id ->
                    session.openRecipe(id)
                    navController.navigate(FridgeRoute.Detail.route)
                },
                onRetry = { session.recommend(force = true) }
            )
        }

        composable(FridgeRoute.Detail.route) { entry ->
            val session = entry.sharedViewModel<FridgeSessionViewModel>(navController)
            val openId by session.openRecipeId.collectAsStateWithLifecycle()
            RecipeDetailScreen(
                recipe = session.recipeById(openId),
                onBack = { navController.popBackStack() }
            )
        }
    }
}
