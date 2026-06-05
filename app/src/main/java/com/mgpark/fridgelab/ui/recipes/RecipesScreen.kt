package com.mgpark.fridgelab.ui.recipes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mgpark.fridgelab.domain.model.Ingredient
import com.mgpark.fridgelab.domain.model.Recipe
import com.mgpark.fridgelab.ui.components.AccentButton
import com.mgpark.fridgelab.ui.components.EmptyState
import com.mgpark.fridgelab.ui.components.ErrorState
import com.mgpark.fridgelab.ui.components.LoadingState
import com.mgpark.fridgelab.ui.theme.FridgeLabTheme

@Composable
fun RecipesScreen(
    ingredients: List<Ingredient>,
    onRestart: () -> Unit,
    viewModel: RecipesViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(ingredients) {
        if (ingredients.isNotEmpty()) {
            viewModel.handleIntent(RecipesIntent.OnRecommend(ingredients))
        }
    }

    RecipesContent(
        state = state,
        onRefresh = { viewModel.handleIntent(RecipesIntent.OnRefresh) },
        onRestart = onRestart
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RecipesContent(
    state: RecipesState,
    onRefresh: () -> Unit,
    onRestart: () -> Unit
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("추천 레시피") }) }
    ) { padding ->
        if (state.isLoading) {
            LoadingState(
                message = "레시피를 만드는 중...",
                modifier = Modifier.padding(padding)
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 20.dp)
            ) {
                when {
                    state.recipes.isEmpty() && state.errorMessage != null ->
                        ErrorState(
                            message = state.errorMessage,
                            modifier = Modifier.weight(1f)
                        )

                    state.recipes.isEmpty() ->
                        EmptyState(
                            message = "추천된 레시피가 없습니다.",
                            modifier = Modifier.weight(1f)
                        )

                    else -> LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(vertical = 12.dp)
                    ) {
                        items(state.recipes) { recipe ->
                            RecipeCard(recipe = recipe)
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onRefresh,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("다시 추천")
                    }
                    AccentButton(
                        text = "다시 촬영하기",
                        onClick = onRestart,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun RecipeCard(recipe: Recipe) {
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = recipe.title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            SectionHeader("재료")
            recipe.ingredients.forEach { ingredient ->
                Text(
                    text = "• $ingredient",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            SectionHeader("조리 순서")
            recipe.steps.forEachIndexed { index, step ->
                Row(modifier = Modifier.padding(bottom = 4.dp)) {
                    Text(
                        text = "${index + 1}.",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.padding(end = 6.dp)
                    )
                    Text(text = step, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.secondary,
        modifier = Modifier.padding(top = 14.dp, bottom = 6.dp)
    )
}

private val SAMPLE_RECIPES = listOf(
    Recipe(
        title = "토마토 계란 볶음",
        ingredients = listOf("토마토 2개", "계란 3개", "소금 약간", "대파 1대"),
        steps = listOf("토마토를 한입 크기로 썬다", "계란을 풀어 스크램블한다", "토마토를 넣고 볶는다", "소금으로 간한다")
    ),
    Recipe(
        title = "양파 수프",
        ingredients = listOf("양파 1개", "버터 1큰술", "물 2컵", "소금 약간"),
        steps = listOf("양파를 채 썬다", "버터에 갈색이 나도록 볶는다", "물을 붓고 끓인다", "소금으로 간한다")
    )
)

@Preview(showBackground = true, name = "Recipes - 레시피 있음")
@Composable
private fun RecipesContentPreview() {
    FridgeLabTheme {
        RecipesContent(
            state = RecipesState(recipes = SAMPLE_RECIPES),
            onRefresh = {},
            onRestart = {}
        )
    }
}

@Preview(showBackground = true, name = "Recipes - 로딩")
@Composable
private fun RecipesContentLoadingPreview() {
    FridgeLabTheme {
        RecipesContent(
            state = RecipesState(isLoading = true),
            onRefresh = {},
            onRestart = {}
        )
    }
}

@Preview(showBackground = true, name = "Recipes - 에러")
@Composable
private fun RecipesContentErrorPreview() {
    FridgeLabTheme {
        RecipesContent(
            state = RecipesState(errorMessage = "레시피를 불러오지 못했습니다: 네트워크 오류"),
            onRefresh = {},
            onRestart = {}
        )
    }
}
