package com.mgpark.fridgelab.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.mgpark.fridgelab.R

/** 디자인 핸드오프 기본 폰트 — Pretendard (400/500/600/700/800). */
val Pretendard = FontFamily(
    Font(R.font.pretendard_regular, FontWeight.Normal),
    Font(R.font.pretendard_medium, FontWeight.Medium),
    Font(R.font.pretendard_semibold, FontWeight.SemiBold),
    Font(R.font.pretendard_bold, FontWeight.Bold),
    Font(R.font.pretendard_extrabold, FontWeight.ExtraBold)
)

private val base = Typography()

/** 모든 Material 타이포 역할에 Pretendard 적용. (화면별 세부 크기는 각 Composable에서 지정) */
val FridgeTypography = Typography(
    displayLarge = base.displayLarge.copy(fontFamily = Pretendard),
    displayMedium = base.displayMedium.copy(fontFamily = Pretendard),
    displaySmall = base.displaySmall.copy(fontFamily = Pretendard),
    headlineLarge = base.headlineLarge.copy(fontFamily = Pretendard),
    headlineMedium = base.headlineMedium.copy(fontFamily = Pretendard),
    headlineSmall = base.headlineSmall.copy(fontFamily = Pretendard),
    titleLarge = base.titleLarge.copy(fontFamily = Pretendard),
    titleMedium = base.titleMedium.copy(fontFamily = Pretendard),
    titleSmall = base.titleSmall.copy(fontFamily = Pretendard),
    bodyLarge = base.bodyLarge.copy(fontFamily = Pretendard),
    bodyMedium = base.bodyMedium.copy(fontFamily = Pretendard),
    bodySmall = base.bodySmall.copy(fontFamily = Pretendard),
    labelLarge = base.labelLarge.copy(fontFamily = Pretendard),
    labelMedium = base.labelMedium.copy(fontFamily = Pretendard),
    labelSmall = base.labelSmall.copy(fontFamily = Pretendard)
)
