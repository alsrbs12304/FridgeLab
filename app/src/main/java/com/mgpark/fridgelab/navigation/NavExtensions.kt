package com.mgpark.fridgelab.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController

/**
 * NavGraph(FRIDGE_GRAPH_ROUTE) 범위로 스코핑된 공유 ViewModel을 가져온다.
 *
 * 각 화면(BackStackEntry)이 아니라 그래프 BackStackEntry에 ViewModel을 묶어,
 * 카메라/재료/레시피 화면이 같은 [FridgeSessionViewModel] 인스턴스를 공유하게 한다.
 */
@Composable
inline fun <reified VM : ViewModel> NavBackStackEntry.sharedViewModel(
    navController: NavController
): VM {
    val parentEntry = remember(this) {
        navController.getBackStackEntry(FRIDGE_GRAPH_ROUTE)
    }
    return hiltViewModel(parentEntry)
}
