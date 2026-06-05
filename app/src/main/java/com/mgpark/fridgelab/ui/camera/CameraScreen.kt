package com.mgpark.fridgelab.ui.camera

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.mgpark.fridgelab.navigation.AnalysisState
import com.mgpark.fridgelab.ui.components.FridgeIcons
import com.mgpark.fridgelab.ui.components.GlassIconButton
import com.mgpark.fridgelab.ui.theme.FridgeTheme
import kotlinx.coroutines.launch

private val CamDark = Color(0xFF0E1411)

/** 촬영 JPEG을 회전 보정해 정지 표시용 ImageBitmap으로 디코드. */
private fun decodeRotated(bytes: ByteArray, rotation: Int): ImageBitmap? {
    val bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size) ?: return null
    val out = if (rotation != 0) {
        val m = Matrix().apply { postRotate(rotation.toFloat()) }
        Bitmap.createBitmap(bmp, 0, 0, bmp.width, bmp.height, m, true)
    } else {
        bmp
    }
    return out.asImageBitmap()
}

@Composable
fun CameraScreen(
    analysis: AnalysisState,
    onCapture: (ByteArray) -> Unit,
    onClose: () -> Unit
) {
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

    Box(Modifier.fillMaxSize().background(CamDark)) {
        if (hasPermission) {
            CameraContent(analysis = analysis, onCapture = onCapture, onClose = onClose)
        } else {
            PermissionContent { permissionLauncher.launch(Manifest.permission.CAMERA) }
        }
    }
}

@Composable
private fun BoxScope.CameraContent(
    analysis: AnalysisState,
    onCapture: (ByteArray) -> Unit,
    onClose: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()

    val previewView = remember { PreviewView(context) }
    val imageCapture = remember { ImageCapture.Builder().build() }
    var lensBack by remember { mutableStateOf(true) }
    var flashOn by remember { mutableStateOf(false) }
    val flashFx = remember { Animatable(0f) }
    var captured by remember { mutableStateOf<ImageBitmap?>(null) }

    val analyzing = analysis.analyzing

    LaunchedEffect(lensBack) {
        runCatching {
            val provider = context.awaitCameraProvider()
            val preview = Preview.Builder().build().apply {
                setSurfaceProvider(previewView.surfaceProvider)
            }
            val selector =
                if (lensBack) CameraSelector.DEFAULT_BACK_CAMERA else CameraSelector.DEFAULT_FRONT_CAMERA
            provider.unbindAll()
            provider.bindToLifecycle(lifecycleOwner, selector, preview, imageCapture)
        }
    }
    LaunchedEffect(flashOn) {
        imageCapture.flashMode = if (flashOn) ImageCapture.FLASH_MODE_ON else ImageCapture.FLASH_MODE_OFF
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            runCatching {
                context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
            }.getOrNull()?.let { bytes ->
                captured = decodeRotated(bytes, 0)
                onCapture(bytes)
            }
        }
    }

    fun capture() {
        scope.launch { flashFx.snapTo(1f); flashFx.animateTo(0f, tween(260)) }
        runCatching {
            imageCapture.takePicture(
                ContextCompat.getMainExecutor(context),
                object : ImageCapture.OnImageCapturedCallback() {
                    override fun onCaptureSuccess(image: ImageProxy) {
                        val bytes = image.toJpegByteArray()
                        val rotation = image.imageInfo.rotationDegrees
                        image.close()
                        captured = decodeRotated(bytes, rotation)
                        onCapture(bytes)
                    }

                    override fun onError(exception: ImageCaptureException) {}
                }
            )
        }
    }

    // ── viewfinder ──
    AndroidView(factory = { previewView }, modifier = Modifier.fillMaxSize())

    // 셔터를 누르면 촬영한 사진을 정지 화면으로 고정 (분석 중 라이브 흔들림 방지)
    captured?.let { frame ->
        Image(
            bitmap = frame,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }

    // dim during analysis
    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = if (analyzing) 0.42f else 0f))
    )

    if (!analyzing) {
        CaptureGuide()
    } else {
        ScanLine()
    }

    // shutter white flash
    if (flashFx.value > 0f) {
        Box(Modifier.fillMaxSize().alpha(flashFx.value).background(Color.White))
    }

    // top bar
    if (!analyzing) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            GlassIconButton(FridgeIcons.close, "닫기", onClose)
            Row(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Color(0x52000000))
                    .padding(horizontal = 13.dp, vertical = 7.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(FridgeIcons.leaf, null, tint = FridgeTheme.colors.primaryBright, modifier = Modifier.size(14.dp))
                Text("냉장고 스캔", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            }
            GlassIconButton(
                if (flashOn) FridgeIcons.flashOn else FridgeIcons.flashOff,
                "플래시", { flashOn = !flashOn }, active = flashOn
            )
        }

        // caption
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(top = 80.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("냉장고 안이 잘 보이도록 맞춰주세요", color = Color(0xEBFFFFFF), fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
            Text("문을 열고 한 발 물러서면 더 정확해요", color = Color(0x99FFFFFF), fontSize = 12.5.sp, modifier = Modifier.padding(top = 4.dp))
        }

        // bottom controls
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(horizontal = 40.dp, vertical = 28.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // gallery
            Box(
                Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(13.dp))
                    .border(2.dp, Color(0x80FFFFFF), RoundedCornerShape(13.dp))
                    .background(Color(0xFF2A3A32))
                    .clickable {
                        galleryLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(FridgeIcons.gallery, "갤러리", tint = Color(0xD9FFFFFF), modifier = Modifier.size(20.dp))
            }
            // shutter
            ShutterButton(onClick = { capture() })
            // flip
            Box(
                Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(Color(0x29FFFFFF))
                    .clickable { lensBack = !lensBack },
                contentAlignment = Alignment.Center
            ) {
                Icon(FridgeIcons.flip, "카메라 전환", tint = Color.White, modifier = Modifier.size(22.dp))
            }
        }
    } else {
        AnalyzingCard(
            progress = analysis.progress,
            found = analysis.foundCount,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(horizontal = 24.dp, vertical = 96.dp)
        )
    }
}

@Composable
private fun ShutterButton(onClick: () -> Unit) {
    val pressed = remember { Animatable(1f) }
    val scope = rememberCoroutineScope()
    Box(
        modifier = Modifier
            .size(78.dp)
            .clickable {
                scope.launch { pressed.snapTo(0.86f); pressed.animateTo(1f, tween(140)) }
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        Box(
            Modifier
                .size(78.dp)
                .clip(CircleShape)
                .border(4.dp, Color(0xF2FFFFFF), CircleShape)
        )
        Box(
            Modifier
                .size(60.dp * pressed.value)
                .clip(CircleShape)
                .background(Color.White)
        )
    }
}

@Composable
private fun CaptureGuide() {
    val frameColor = Color(0xF2FFFFFF)
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Canvas(Modifier.size(290.dp, 360.dp)) {
            val arm = 38.dp.toPx()    // ㄱ자 한 변 길이
            val w = 3.dp.toPx()       // 선 두께
            val r = 18.dp.toPx()      // 모서리 곡률
            val i = w / 2f            // 선이 영역 안에 들어오도록 inset
            val ww = size.width
            val hh = size.height
            val stroke = Stroke(width = w, cap = StrokeCap.Round, join = StrokeJoin.Round)

            // 각 모서리를 직선→곡선→직선 하나의 Path로 그려 끊김 없이 둥근 ㄱ자 형성
            fun bracket(build: Path.() -> Unit) =
                drawPath(Path().apply(build), color = frameColor, style = stroke)

            // 좌상단 ┌
            bracket {
                moveTo(i + arm, i)
                lineTo(i + r, i)
                quadraticBezierTo(i, i, i, i + r)
                lineTo(i, i + arm)
            }
            // 우상단 ┐
            bracket {
                moveTo(ww - i - arm, i)
                lineTo(ww - i - r, i)
                quadraticBezierTo(ww - i, i, ww - i, i + r)
                lineTo(ww - i, i + arm)
            }
            // 좌하단 └
            bracket {
                moveTo(i, hh - i - arm)
                lineTo(i, hh - i - r)
                quadraticBezierTo(i, hh - i, i + r, hh - i)
                lineTo(i + arm, hh - i)
            }
            // 우하단 ┘
            bracket {
                moveTo(ww - i, hh - i - arm)
                lineTo(ww - i, hh - i - r)
                quadraticBezierTo(ww - i, hh - i, ww - i - r, hh - i)
                lineTo(ww - i - arm, hh - i)
            }
        }
    }
}

@Composable
private fun ScanLine() {
    val transition = rememberInfiniteTransition(label = "scan")
    val pos by transition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1700, easing = LinearEasing), RepeatMode.Restart),
        label = "scanPos"
    )
    val primary = FridgeTheme.colors.primary
    Canvas(Modifier.fillMaxSize()) {
        val bandH = 120.dp.toPx()
        val y = (size.height + bandH) * pos - bandH
        drawRect(
            brush = Brush.verticalGradient(
                0f to Color.Transparent,
                0.5f to primary.copy(alpha = 0.35f),
                1f to Color.Transparent,
                startY = y, endY = y + bandH
            ),
            topLeft = Offset(0f, y),
            size = androidx.compose.ui.geometry.Size(size.width, bandH)
        )
    }
}

@Composable
private fun AnalyzingCard(progress: Float, found: Int, modifier: Modifier) {
    val primaryBright = FridgeTheme.colors.primaryBright

    // 별 아이콘 회전 (1.6초 루프) — AI가 분석 중인 느낌
    val spin = rememberInfiniteTransition(label = "sparkle")
    val angle by spin.animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(1600, easing = LinearEasing), RepeatMode.Restart),
        label = "angle"
    )
    // 진행률을 부드럽게 보간 → 빈 바가 채워지는 느낌
    val animated by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(220, easing = LinearEasing),
        label = "progress"
    )

    Box(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(22.dp))
                .background(Color(0xB8101814))
                .padding(20.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Icon(FridgeIcons.sparkle, null, tint = primaryBright, modifier = Modifier.size(20.dp).rotate(angle))
                Column(Modifier.weight(1f)) {
                    Text("AI가 재료를 분석하고 있어요", color = Color.White, fontSize = 15.5.sp, fontWeight = FontWeight.Bold)
                    Text("지금까지 ${found}개 재료를 찾았어요", color = Color(0x99FFFFFF), fontSize = 12.5.sp)
                }
            }
            // 커스텀 진행 바 — 빈 트랙 위로 채움이 늘어남 (gap/stop 표시 없음)
            Box(
                Modifier
                    .fillMaxWidth()
                    .padding(top = 13.dp)
                    .height(6.dp)
                    .clip(CircleShape)
                    .background(Color(0x24FFFFFF))
            ) {
                Box(
                    Modifier
                        .fillMaxWidth(animated)
                        .height(6.dp)
                        .clip(CircleShape)
                        .background(primaryBright)
                )
            }
        }
    }
}

@Composable
private fun PermissionContent(onRequest: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
    ) {
        Text("냉장고를 촬영하려면 카메라 권한이 필요해요", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
        Box(
            Modifier
                .clip(CircleShape)
                .background(FridgeTheme.colors.primary)
                .clickable(onClick = onRequest)
                .padding(horizontal = 22.dp, vertical = 12.dp)
        ) {
            Text("권한 허용", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
        }
    }
}
