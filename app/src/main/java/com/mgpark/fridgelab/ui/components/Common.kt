package com.mgpark.fridgelab.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mgpark.fridgelab.domain.model.Category
import com.mgpark.fridgelab.domain.model.Freshness
import com.mgpark.fridgelab.ui.theme.FridgeRadius
import com.mgpark.fridgelab.ui.theme.FridgeTheme

@Composable
fun categoryColor(category: Category): Color {
    val c = FridgeTheme.colors
    return when (category) {
        Category.VEG -> c.catVeg
        Category.MEAT -> c.catMeat
        Category.DAIRY -> c.catDairy
        Category.FRUIT -> c.catFruit
        Category.ETC -> c.catEtc
    }
}

data class FreshPalette(val fg: Color, val bg: Color)

@Composable
fun freshPalette(f: Freshness): FreshPalette {
    val c = FridgeTheme.colors
    return when (f) {
        Freshness.FRESH -> FreshPalette(c.freshFg, c.freshBg)
        Freshness.OK -> FreshPalette(c.okFg, c.okBg)
        Freshness.SOON -> FreshPalette(c.soonFg, c.soonBg)
    }
}

/** 디자인 "그림자" 모드 카드 — 부드러운 녹색기 그림자 + surface 배경. */
@Composable
fun Modifier.fridgeCard(shape: Shape = RoundedCornerShape(FridgeRadius.lg)): Modifier =
    this
        .shadow(
            elevation = 12.dp,
            shape = shape,
            spotColor = Color(0x2914281E),
            ambientColor = Color(0x1414281E)
        )
        .background(FridgeTheme.colors.surface, shape)

@Composable
fun CategoryDot(category: Category, size: Dp = 8.dp) {
    Box(
        Modifier
            .size(size)
            .clip(CircleShape)
            .background(categoryColor(category))
    )
}

/** 신선도 칩 (탭하면 신선→보통→임박 순환). */
@Composable
fun FreshnessChip(freshness: Freshness, onClick: (() -> Unit)? = null) {
    val p = freshPalette(freshness)
    Row(
        modifier = Modifier
            .clip(CircleShape)
            .background(p.bg)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(horizontal = 8.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(Modifier.size(5.dp).clip(CircleShape).background(p.fg))
        Text(
            freshness.label, color = p.fg, fontSize = 11.5.sp, fontWeight = FontWeight.Bold,
            maxLines = 1, softWrap = false
        )
    }
}

/** 수량 스텝퍼 — ⊖ [수량+단위] ⊕ (pill 버튼). ⊖는 최소 1에서 비활성. */
@Composable
fun QtyStepper(qty: Int, unit: String, onChange: (Int) -> Unit) {
    val c = FridgeTheme.colors
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        StepBtn(FridgeIcons.remove, enabled = qty > 1) { onChange(qty - 1) }
        Row(verticalAlignment = Alignment.Bottom) {
            Text("$qty", color = c.ink, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Text(unit, color = c.ink2, fontSize = 10.5.sp, fontWeight = FontWeight.SemiBold)
        }
        StepBtn(FridgeIcons.add, enabled = true) { onChange(qty + 1) }
    }
}

@Composable
private fun StepBtn(icon: ImageVector, enabled: Boolean, onClick: () -> Unit) {
    val c = FridgeTheme.colors
    Box(
        modifier = Modifier
            .size(24.dp)
            .clip(CircleShape)
            .background(c.chip)
            .then(if (enabled) Modifier.clickable(onClick = onClick) else Modifier),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = null, tint = if (enabled) c.ink else c.ink3, modifier = Modifier.size(14.dp))
    }
}

/** 원형 매칭률 링. */
@Composable
fun MatchRing(pct: Int, size: Dp = 46.dp, stroke: Dp = 4.dp, showLabel: Boolean = true) {
    val c = FridgeTheme.colors
    Box(Modifier.size(size), contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            val sw = stroke.toPx()
            val d = this.size.minDimension - sw
            val tl = Offset(sw / 2f, sw / 2f)
            drawArc(
                color = c.line, startAngle = -90f, sweepAngle = 360f, useCenter = false,
                topLeft = tl, size = Size(d, d), style = Stroke(sw)
            )
            drawArc(
                color = c.primary, startAngle = -90f, sweepAngle = 360f * (pct / 100f), useCenter = false,
                topLeft = tl, size = Size(d, d), style = Stroke(sw, cap = StrokeCap.Round)
            )
        }
        if (showLabel) {
            Row(verticalAlignment = Alignment.Bottom) {
                Text("$pct", color = c.ink, fontSize = (size.value * 0.3f).sp, fontWeight = FontWeight.Bold)
                Text("%", color = c.ink3, fontSize = (size.value * 0.17f).sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

/** 줄무늬 음식 사진 플레이스홀더 (실제 이미지 연결 전). */
@Composable
fun StripedPlaceholder(
    label: String,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(FridgeRadius.md),
    dark: Boolean = false
) {
    val c = FridgeTheme.colors
    val base = if (dark) Color(0xFF1A2620) else Color(0xFFE9F2EC)
    val stripe = if (dark) Color(0xFF223029) else Color(0xFFF3F8F4)
    val ink = if (dark) Color(0x80FFFFFF) else c.ink3
    Box(
        modifier = modifier
            .clip(shape)
            .background(
                androidx.compose.ui.graphics.Brush.linearGradient(
                    0.0f to base, 0.5f to base, 0.5f to stripe, 1.0f to stripe,
                    start = Offset(0f, 0f), end = Offset(16f, 16f),
                    tileMode = androidx.compose.ui.graphics.TileMode.Repeated
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            label,
            color = ink,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier
                .clip(CircleShape)
                .background(if (dark) Color(0x40000000) else Color(0xB3FFFFFF))
                .padding(horizontal = 9.dp, vertical = 4.dp)
        )
    }
}

/** 카메라/상세 위 반투명 글래스 원형 버튼. */
@Composable
fun GlassIconButton(
    icon: ImageVector,
    contentDescription: String?,
    onClick: () -> Unit,
    active: Boolean = false,
    size: Dp = 42.dp
) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(if (active) Color(0xEBFFFFFF) else Color(0x52000000))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            icon,
            contentDescription = contentDescription,
            tint = if (active) Color(0xFF1C1C1C) else Color.White,
            modifier = Modifier.size(size * 0.46f)
        )
    }
}
