package com.mgpark.fridgelab.ui.ingredients

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mgpark.fridgelab.domain.model.Ingredient
import com.mgpark.fridgelab.ui.theme.FridgeLabTheme

@Composable
fun IngredientsScreen(
    image: ByteArray?,
    onIngredientsConfirmed: (List<Ingredient>) -> Unit,
    viewModel: IngredientsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(image) {
        if (image != null) viewModel.handleIntent(IngredientsIntent.OnRecognize(image))
    }

    IngredientsContent(
        state = state,
        onAdd = { viewModel.handleIntent(IngredientsIntent.OnAdd(it)) },
        onRemove = { viewModel.handleIntent(IngredientsIntent.OnRemove(it)) },
        onConfirm = { onIngredientsConfirmed(state.ingredients) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun IngredientsContent(
    state: IngredientsState,
    onAdd: (Ingredient) -> Unit,
    onRemove: (Ingredient) -> Unit,
    onConfirm: () -> Unit
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("인식된 재료") }) }
    ) { padding ->
        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator()
                    Text(
                        text = "사진에서 재료를 찾는 중...",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 20.dp)
            ) {
                state.errorMessage?.let { message ->
                    Text(
                        text = message,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                }

                if (state.ingredients.isEmpty()) {
                    Text(
                        text = "인식된 재료가 없습니다. 아래에서 직접 추가해 보세요.",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                }

                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(state.ingredients) { ingredient ->
                        IngredientRow(ingredient = ingredient, onRemove = { onRemove(ingredient) })
                        HorizontalDivider()
                    }
                }

                AddIngredientRow(onAdd = onAdd)

                Button(
                    onClick = onConfirm,
                    enabled = state.ingredients.isNotEmpty(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                ) {
                    Text("레시피 추천받기")
                }
            }
        }
    }
}

@Composable
private fun IngredientRow(ingredient: Ingredient, onRemove: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = ingredient.name,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
        ingredient.quantity?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(end = 8.dp)
            )
        }
        TextButton(onClick = onRemove) { Text("삭제") }
    }
}

@Composable
private fun AddIngredientRow(onAdd: (Ingredient) -> Unit) {
    var text by remember { mutableStateOf("") }

    fun submit() {
        val name = text.trim()
        if (name.isNotEmpty()) {
            onAdd(Ingredient(name = name))
            text = ""
        }
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            label = { Text("재료 추가") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { submit() }),
            modifier = Modifier.weight(1f)
        )
        Button(onClick = { submit() }) { Text("추가") }
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
            onAdd = {},
            onRemove = {},
            onConfirm = {}
        )
    }
}

@Preview(showBackground = true, name = "Ingredients - 로딩")
@Composable
private fun IngredientsContentLoadingPreview() {
    FridgeLabTheme {
        IngredientsContent(
            state = IngredientsState(isLoading = true),
            onAdd = {},
            onRemove = {},
            onConfirm = {}
        )
    }
}

@Preview(showBackground = true, name = "Ingredients - 비어있음/에러")
@Composable
private fun IngredientsContentEmptyPreview() {
    FridgeLabTheme {
        IngredientsContent(
            state = IngredientsState(errorMessage = "재료 인식에 실패했습니다: 네트워크 오류"),
            onAdd = {},
            onRemove = {},
            onConfirm = {}
        )
    }
}
