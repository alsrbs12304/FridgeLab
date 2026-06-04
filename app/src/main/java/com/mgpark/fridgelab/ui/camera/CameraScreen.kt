package com.mgpark.fridgelab.ui.camera

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
import com.mgpark.fridgelab.ui.theme.FridgeLabTheme

@Composable
fun CameraScreen(
    onNavigateToIngredients: () -> Unit,
    viewModel: CameraViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    CameraContent(
        state = state,
        onNavigateToIngredients = onNavigateToIngredients
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CameraContent(
    state: CameraState,
    onNavigateToIngredients: () -> Unit
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("냉장고 촬영") }) }
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
                    text = "카메라 촬영 화면 (Phase 2에서 구현)",
                    style = MaterialTheme.typography.bodyLarge
                )
                Button(onClick = onNavigateToIngredients) {
                    Text("재료 인식하기")
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Camera - 기본")
@Composable
private fun CameraContentPreview() {
    FridgeLabTheme {
        CameraContent(state = CameraState(), onNavigateToIngredients = {})
    }
}

@Preview(showBackground = true, name = "Camera - 로딩")
@Composable
private fun CameraContentLoadingPreview() {
    FridgeLabTheme {
        CameraContent(state = CameraState(isLoading = true), onNavigateToIngredients = {})
    }
}
