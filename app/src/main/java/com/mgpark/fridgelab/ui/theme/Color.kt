package com.mgpark.fridgelab.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * 디자인 핸드오프의 전체 토큰 묶음.
 * Material3 colorScheme로는 다 담기지 않는 보조 토큰(chip, ink-2/3, primary-soft 등)과
 * 카테고리·신선도·경고 색을 함께 보관한다.
 */
@Immutable
data class FridgeColors(
    val bg: Color,
    val surface: Color,
    val chip: Color,
    val ink: Color,
    val ink2: Color,
    val ink3: Color,
    val line: Color,
    val primary: Color,
    val primaryInk: Color,
    val primarySoft: Color,
    val primaryBright: Color,
    // 카테고리 점/라벨
    val catVeg: Color,
    val catMeat: Color,
    val catDairy: Color,
    val catFruit: Color,
    val catEtc: Color,
    // 신선도 (글자 / 배경)
    val freshFg: Color,
    val freshBg: Color,
    val okFg: Color,
    val okBg: Color,
    val soonFg: Color,
    val soonBg: Color,
    // 경고/부족 (앰버)
    val warn: Color,
    val warnBadgeBg: Color,
    val missChipBg: Color
)

/** 기본 테마 = 민트 (README 4.1). */
val MintColors = FridgeColors(
    bg = Color(0xFFF4FDF7),
    surface = Color(0xFFFFFFFF),
    chip = Color(0xFFE7F1EB),
    ink = Color(0xFF19251E),
    ink2 = Color(0xFF5B6760),
    ink3 = Color(0xFF949E98),
    line = Color(0xFFE2EAE5),
    primary = Color(0xFF2D9D68),
    primaryInk = Color(0xFFFFFFFF),
    primarySoft = Color(0xFFD6F8E4),
    primaryBright = Color(0xFF58DA98),
    catVeg = Color(0xFF3A8357),
    catMeat = Color(0xFFA4585B),
    catDairy = Color(0xFF4075AA),
    catFruit = Color(0xFF966626),
    catEtc = Color(0xFF7B63A3),
    freshFg = Color(0xFF1A763F),
    freshBg = Color(0xFFD2FBDB),
    okFg = Color(0xFF855B00),
    okBg = Color(0xFFFFECBD),
    soonFg = Color(0xFFAA3700),
    soonBg = Color(0xFFFFDDC6),
    warn = Color(0xFF974D00),
    warnBadgeBg = Color(0xFFFFEAC2),
    missChipBg = Color(0xFFFFE9CB)
)

val LocalFridgeColors = staticCompositionLocalOf { MintColors }
