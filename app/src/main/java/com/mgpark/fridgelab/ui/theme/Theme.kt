package com.mgpark.fridgelab.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.material3.LocalTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp

/** 라운딩 = "둥글게" (README 4.5). 칩/스텝퍼는 항상 pill. */
object FridgeRadius {
    val sm = 12.dp   // 작은 칩/입력
    val md = 18.dp   // 버튼/이미지/스탯
    val lg = 24.dp   // 카드/리스트
    val pill = 99.dp
}

val FridgeShapes = Shapes(
    small = RoundedCornerShape(FridgeRadius.sm),
    medium = RoundedCornerShape(FridgeRadius.md),
    large = RoundedCornerShape(FridgeRadius.lg)
)

/** 민트 토큰을 Material3 colorScheme로 매핑 (Material 컴포넌트 기본색용). */
private val MintColorScheme = lightColorScheme(
    primary = MintColors.primary,
    onPrimary = MintColors.primaryInk,
    primaryContainer = MintColors.primarySoft,
    onPrimaryContainer = MintColors.ink,
    secondary = MintColors.primary,
    onSecondary = MintColors.primaryInk,
    background = MintColors.bg,
    onBackground = MintColors.ink,
    surface = MintColors.surface,
    onSurface = MintColors.ink,
    surfaceVariant = MintColors.chip,
    onSurfaceVariant = MintColors.ink2,
    outline = MintColors.line,
    error = MintColors.soonFg
)

/** 테마/토큰/라운딩 접근용 헬퍼. `FridgeTheme.colors.primary` 형태로 사용. */
object FridgeTheme {
    val colors: FridgeColors
        @Composable @ReadOnlyComposable get() = LocalFridgeColors.current
    val radius get() = FridgeRadius
}

@Composable
fun FridgeLabTheme(
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalFridgeColors provides MintColors,
        LocalTextStyle provides TextStyle(fontFamily = Pretendard, color = MintColors.ink)
    ) {
        MaterialTheme(
            colorScheme = MintColorScheme,
            typography = FridgeTypography,
            shapes = FridgeShapes,
            content = content
        )
    }
}
