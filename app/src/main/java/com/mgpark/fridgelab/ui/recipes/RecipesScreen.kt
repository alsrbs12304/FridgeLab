package com.mgpark.fridgelab.ui.recipes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mgpark.fridgelab.domain.model.Recipe
import com.mgpark.fridgelab.ui.theme.FridgeLabTheme

@Composable
fun RecipesScreen(
    onRestart: () -> Unit,
    viewModel: RecipesViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    RecipesContent(
        state = state,
        onRestart = onRestart
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RecipesContent(
    state: RecipesState,
    onRestart: () -> Unit
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("추천 레시피") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
        ) {
            if (state.isLoading) {
                CircularProgressIndicator()
            } else {
                Text(
                    text = "추천 레시피 ${state.recipes.size}개 (Phase 4에서 구현)",
                    style = MaterialTheme.typography.bodyLarge
                )
                Button(onClick = onRestart) {
                    Text("다시 촬영하기")
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Recipes - 레시피 있음")
@Composable
private fun RecipesContentPreview() {
    FridgeLabTheme {
        RecipesContent(
            state = RecipesState(
                recipes = listOf(
                    Recipe(
                        title = "토마토 계란 볶음",
                        ingredients = listOf("토마토 2개", "계란 3개", "소금 약간"),
                        steps = listOf("토마토를 썬다", "계란을 푼다", "함께 볶는다")
                    ),
                    Recipe(
                        title = "양파 수프",
                        ingredients = listOf("양파 1개", "버터", "물"),
                        steps = listOf("양파를 볶는다", "물을 붓고 끓인다")
                    )
                )
            ),
            onRestart = {}
        )
    }
}

@Preview(showBackground = true, name = "Recipes - 로딩")
@Composable
private fun RecipesContentLoadingPreview() {
    FridgeLabTheme {
        RecipesContent(state = RecipesState(isLoading = true), onRestart = {})
    }
}
