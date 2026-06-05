package com.mgpark.fridgelab.ui.recipes

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mgpark.fridgelab.domain.model.Recipe
import com.mgpark.fridgelab.ui.components.FridgeIcons
import com.mgpark.fridgelab.ui.components.GlassIconButton
import com.mgpark.fridgelab.ui.components.MatchRing
import com.mgpark.fridgelab.ui.components.StripedPlaceholder
import com.mgpark.fridgelab.ui.components.fridgeCard
import com.mgpark.fridgelab.ui.theme.FridgeRadius
import com.mgpark.fridgelab.ui.theme.FridgeTheme

@Composable
fun RecipeDetailScreen(recipe: Recipe?, onBack: () -> Unit) {
    val c = FridgeTheme.colors
    if (recipe == null) {
        Box(Modifier.fillMaxSize().background(c.bg), contentAlignment = Alignment.Center) {
            Text("레시피를 찾을 수 없어요", color = c.ink2)
        }
        return
    }
    var saved by remember { mutableStateOf(false) }

    Box(Modifier.fillMaxSize().background(c.bg)) {
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 110.dp)
        ) {
            // ── hero ──
            Box {
                StripedPlaceholder(
                    "음식 사진 · 완성 컷",
                    modifier = Modifier.fillMaxWidth().height(260.dp),
                    shape = RoundedCornerShape(0.dp),
                    dark = true
                )
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(260.dp)
                        .background(
                            Brush.verticalGradient(
                                0f to Color(0x59000000), 0.3f to Color.Transparent,
                                0.6f to Color.Transparent, 1f to Color(0x8C0A100D)
                            )
                        )
                )
                Row(
                    Modifier
                        .fillMaxWidth()
                        .windowInsetsPadding(WindowInsets.statusBars)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    GlassIconButton(FridgeIcons.back, "뒤로", onBack)
                    GlassIconButton(
                        if (saved) FridgeIcons.bookmark else FridgeIcons.bookmarkBorder,
                        "저장", { saved = !saved }, active = saved
                    )
                }
                Column(Modifier.align(Alignment.BottomStart).padding(horizontal = 20.dp).padding(bottom = 16.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        recipe.tags.forEach { t ->
                            Box(
                                Modifier.clip(CircleShape).background(Color(0x38FFFFFF)).padding(horizontal = 9.dp, vertical = 3.dp)
                            ) { Text(t, color = Color.White, fontSize = 11.5.sp, fontWeight = FontWeight.SemiBold) }
                        }
                    }
                    Text(recipe.name, color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(top = 8.dp))
                }
            }

            Column(Modifier.padding(horizontal = 20.dp).padding(top = 18.dp)) {
                Text(recipe.desc, color = c.ink2, fontSize = 14.sp, lineHeight = 22.sp)

                // ── stat row ──
                Row(Modifier.fillMaxWidth().padding(top = 18.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    StatCard(FridgeIcons.clock, "${recipe.timeMin}분", "조리시간", Modifier.weight(1f))
                    StatCard(FridgeIcons.flame, recipe.level.label, "난이도", Modifier.weight(1f))
                    StatCard(FridgeIcons.users, "${recipe.servings}인분", "분량", Modifier.weight(1f))
                }

                // ── match banner ──
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                        .clip(RoundedCornerShape(FridgeRadius.md))
                        .background(c.primarySoft)
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(13.dp)
                ) {
                    MatchRing(recipe.matchPct, size = 52.dp, stroke = 5.dp)
                    Column {
                        Text("내 냉장고와 ${recipe.matchPct}% 일치", color = c.ink, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        Text("보유 ${recipe.haveCount}개 · 부족 ${recipe.missCount}개", color = c.ink2, fontSize = 13.sp, modifier = Modifier.padding(top = 2.dp))
                    }
                }

                // ── ingredients ──
                SectionTitle(FridgeIcons.leaf, "재료", "(${recipe.need.size})")
                Column(Modifier.fillMaxWidth().fridgeCard()) {
                    recipe.need.forEachIndexed { i, n ->
                        Row(
                            Modifier.fillMaxWidth().padding(horizontal = 15.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(11.dp)
                        ) {
                            Box(
                                Modifier.size(22.dp).clip(CircleShape).background(if (n.have) c.primary else c.chip),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    if (n.have) FridgeIcons.check else FridgeIcons.close, null,
                                    tint = if (n.have) c.primaryInk else c.ink3, modifier = Modifier.size(13.dp)
                                )
                            }
                            Text(
                                n.name, color = if (n.have) c.ink else c.ink3,
                                fontSize = 14.5.sp, fontWeight = if (n.have) FontWeight.SemiBold else FontWeight.Medium,
                                modifier = Modifier.weight(1f)
                            )
                            Text(n.amount, color = c.ink2, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                            if (!n.have) {
                                Text("장보기", color = c.warn, fontSize = 11.5.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 4.dp))
                            }
                        }
                        if (i != recipe.need.lastIndex) Box(Modifier.fillMaxWidth().height(1.dp).background(c.line))
                    }
                }

                // ── steps ──
                SectionTitle(FridgeIcons.scan, "조리 순서", null)
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    recipe.steps.forEachIndexed { i, s ->
                        Row(horizontalArrangement = Arrangement.spacedBy(13.dp)) {
                            Box(
                                Modifier.size(28.dp).clip(CircleShape).background(c.primarySoft),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("${i + 1}", color = c.primary, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
                            }
                            Text(s, color = c.ink, fontSize = 14.5.sp, lineHeight = 23.sp, modifier = Modifier.padding(top = 3.dp))
                        }
                    }
                }
            }
        }

        // ── sticky CTA ──
        Row(
            Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(Brush.verticalGradient(0f to Color.Transparent, 0.38f to c.bg))
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(FridgeRadius.md))
                    .background(c.surface)
                    .border(1.dp, c.line, RoundedCornerShape(FridgeRadius.md))
                    .clickable { saved = !saved },
                contentAlignment = Alignment.Center
            ) {
                Icon(if (saved) FridgeIcons.bookmark else FridgeIcons.bookmarkBorder, "저장", tint = if (saved) c.primary else c.ink2, modifier = Modifier.size(22.dp))
            }
            Row(
                Modifier
                    .weight(1f)
                    .height(56.dp)
                    .clip(RoundedCornerShape(FridgeRadius.md))
                    .background(c.primary)
                    .clickable { },
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(FridgeIcons.flame, null, tint = c.primaryInk, modifier = Modifier.size(19.dp))
                Text("  요리 시작하기", color = c.primaryInk, fontSize = 16.5.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun StatCard(icon: ImageVector, value: String, label: String, modifier: Modifier) {
    val c = FridgeTheme.colors
    Column(
        modifier = modifier.fridgeCard(RoundedCornerShape(FridgeRadius.md)).padding(vertical = 13.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(icon, null, tint = c.primary, modifier = Modifier.size(19.dp))
        Text(value, color = c.ink, fontSize = 15.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 6.dp))
        Text(label, color = c.ink3, fontSize = 11.sp, modifier = Modifier.padding(top = 2.dp))
    }
}

@Composable
private fun SectionTitle(icon: ImageVector, title: String, suffix: String?) {
    val c = FridgeTheme.colors
    Row(
        Modifier.padding(top = 24.dp, bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(7.dp)
    ) {
        Icon(icon, null, tint = c.primary, modifier = Modifier.size(18.dp))
        Text(title, color = c.ink, fontSize = 17.sp, fontWeight = FontWeight.ExtraBold)
        if (suffix != null) Text(suffix, color = c.ink3, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
    }
}
