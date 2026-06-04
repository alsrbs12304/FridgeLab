package com.mgpark.fridgelab.ui.ingredients

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
import com.mgpark.fridgelab.domain.model.Ingredient
import com.mgpark.fridgelab.ui.theme.FridgeLabTheme

@Composable
fun IngredientsScreen(
    onNavigateToRecipes: () -> Unit,
    viewModel: IngredientsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    IngredientsContent(
        state = state,
        onNavigateToRecipes = onNavigateToRecipes
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun IngredientsContent(
    state: IngredientsState,
    onNavigateToRecipes: () -> Unit
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("인식된 재료") }) }
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
                    text = "인식된 재료 ${state.ingredients.size}개 (Phase 3에서 구현)",
                    style = MaterialTheme.typography.bodyLarge
                )
                Button(onClick = onNavigateToRecipes) {
                    Text("레시피 추천받기")
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Ingredients - 재료 있음")
@Composable
private fun IngredientsContentPreview() {
    FridgeLabTheme {
        IngredientsContent(
            state = IngredientsState(
                ingredients = listOf(
                    Ingredient("토마토", "2개"),
                    Ingredient("계란", "5개"),
                    Ingredient("양파")
                )
            ),
            onNavigateToRecipes = {}
        )
    }
}

@Preview(showBackground = true, name = "Ingredients - 로딩")
@Composable
private fun IngredientsContentLoadingPreview() {
    FridgeLabTheme {
        IngredientsContent(state = IngredientsState(isLoading = true), onNavigateToRecipes = {})
    }
}
