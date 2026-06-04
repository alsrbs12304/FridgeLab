package com.mgpark.fridgelab.ui.camera

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview as ComposePreview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mgpark.fridgelab.ui.theme.FridgeLabTheme

@Composable
fun CameraScreen(
    onImageCaptured: (ByteArray) -> Unit,
    viewModel: CameraViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED
        )
    }
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> hasPermission = granted }

    LaunchedEffect(Unit) {
        if (!hasPermission) permissionLauncher.launch(Manifest.permission.CAMERA)
    }

    if (hasPermission) {
        CameraPreviewContent(
            errorMessage = state.errorMessage,
            onImageCaptured = onImageCaptured,
            onError = { viewModel.handleIntent(CameraIntent.OnError(it)) },
            onErrorShown = { viewModel.handleIntent(CameraIntent.OnErrorShown) }
        )
    } else {
        PermissionRequiredContent(
            onRequestPermission = { permissionLauncher.launch(Manifest.permission.CAMERA) }
        )
    }
}

@Composable
private fun CameraPreviewContent(
    errorMessage: String?,
    onImageCaptured: (ByteArray) -> Unit,
    onError: (String) -> Unit,
    onErrorShown: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val previewView = remember { PreviewView(context) }
    val imageCapture = remember { ImageCapture.Builder().build() }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(previewView) {
        try {
            val cameraProvider = context.awaitCameraProvider()
            val preview = Preview.Builder().build().apply {
                setSurfaceProvider(previewView.surfaceProvider)
            }
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                CameraSelector.DEFAULT_BACK_CAMERA,
                preview,
                imageCapture
            )
        } catch (e: Exception) {
            onError("카메라를 시작할 수 없습니다: ${e.message}")
        }
    }

    LaunchedEffect(errorMessage) {
        if (errorMessage != null) {
            snackbarHostState.showSnackbar(errorMessage)
            onErrorShown()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            AndroidView(
                factory = { previewView },
                modifier = Modifier.fillMaxSize()
            )
            Button(
                onClick = {
                    try {
                        imageCapture.takePicture(
                            ContextCompat.getMainExecutor(context),
                            object : ImageCapture.OnImageCapturedCallback() {
                                override fun onCaptureSuccess(image: ImageProxy) {
                                    val bytes = image.toJpegByteArray()
                                    image.close()
                                    onImageCaptured(bytes)
                                }

                                override fun onError(exception: ImageCaptureException) {
                                    onError("촬영에 실패했습니다: ${exception.message}")
                                }
                            }
                        )
                    } catch (e: Exception) {
                        onError("카메라가 아직 준비되지 않았습니다.")
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 40.dp)
            ) {
                Text("촬영")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PermissionRequiredContent(
    onRequestPermission: () -> Unit
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
            Text(
                text = "냉장고 사진을 촬영하려면 카메라 권한이 필요합니다.",
                style = MaterialTheme.typography.bodyLarge
            )
            Button(onClick = onRequestPermission) {
                Text("카메라 권한 허용")
            }
        }
    }
}

@ComposePreview(showBackground = true, name = "Camera - 권한 필요")
@Composable
private fun PermissionRequiredPreview() {
    FridgeLabTheme {
        PermissionRequiredContent(onRequestPermission = {})
    }
}
