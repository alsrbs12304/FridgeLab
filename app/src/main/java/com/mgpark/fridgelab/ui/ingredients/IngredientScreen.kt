package com.mgpark.fridgelab.ui.ingredients

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.ExperimentalLayoutApi
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mgpark.fridgelab.domain.model.Category
import com.mgpark.fridgelab.domain.model.Ingredient
import com.mgpark.fridgelab.ui.components.CategoryDot
import com.mgpark.fridgelab.ui.components.FreshnessChip
import com.mgpark.fridgelab.ui.components.FridgeIcons
import com.mgpark.fridgelab.ui.components.QtyStepper
import com.mgpark.fridgelab.ui.components.categoryColor
import com.mgpark.fridgelab.ui.components.fridgeCard
import com.mgpark.fridgelab.ui.theme.FridgeRadius
import com.mgpark.fridgelab.ui.theme.FridgeTheme

@Composable
fun IngredientScreen(
    ingredients: List<Ingredient>,
    onBack: () -> Unit,
    onToggle: (String) -> Unit,
    onQty: (String, Int) -> Unit,
    onFresh: (String) -> Unit,
    onRemove: (String) -> Unit,
    onAdd: (String, Category) -> Unit,
    onToggleAll: () -> Unit,
    onFind: () -> Unit
) {
    val c = FridgeTheme.colors
    var adding by remember { mutableStateOf(false) }

    val selCount = ingredients.count { it.selected }
    val allSel = ingredients.isNotEmpty() && ingredients.all { it.selected }
    val lowConf = ingredients.count { it.needsReview && it.selected }

    Box(Modifier.fillMaxSize().background(c.bg)) {
        Column(Modifier.fillMaxSize()) {
            // ── header ──
            Column(
                Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .padding(top = 6.dp)
            ) {
                Row(
                    Modifier.fillMaxWidth().padding(start = 12.dp, end = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconBtn(FridgeIcons.back, "뒤로", onBack)
                    Row(
                        Modifier.clip(CircleShape).clickable(onClick = onBack).padding(6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Icon(FridgeIcons.camera, null, tint = c.ink2, modifier = Modifier.size(16.dp))
                        Text("다시 촬영", color = c.ink2, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
                Column(Modifier.padding(horizontal = 22.dp).padding(top = 6.dp, bottom = 14.dp)) {
                    Text("인식된 재료", color = c.ink, fontSize = 27.sp, fontWeight = FontWeight.ExtraBold)
                    Row(Modifier.padding(top = 6.dp)) {
                        Text("${ingredients.size}개", color = c.primary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text("를 찾았어요. 확인하고 수정해 주세요.", color = c.ink2, fontSize = 14.sp)
                    }
                    if (lowConf > 0) {
                        Row(
                            Modifier
                                .padding(top = 11.dp)
                                .clip(CircleShape)
                                .background(c.warnBadgeBg)
                                .padding(horizontal = 11.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(FridgeIcons.edit, null, tint = c.warn, modifier = Modifier.size(13.dp))
                            Text("${lowConf}개 재료는 AI 인식이 헷갈려 해요 — 확인해 주세요", color = c.warn, fontSize = 12.5.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
                Row(
                    Modifier.fillMaxWidth().padding(horizontal = 22.dp).padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        Modifier.clickable(onClick = onToggleAll),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CheckBox(on = allSel)
                        Text("전체 선택", color = c.ink2, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    }
                    Row(
                        Modifier.clickable { adding = !adding },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(FridgeIcons.add, null, tint = c.primary, modifier = Modifier.size(15.dp))
                        Text("직접 추가", color = c.primary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Box(Modifier.fillMaxWidth().height(1.dp).background(c.line))
            }

            // ── scroll body ──
            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth().padding(horizontal = 16.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(top = 6.dp, bottom = 130.dp)
            ) {
                if (adding) {
                    item {
                        AddCard(
                            onAdd = { name, cat -> onAdd(name, cat); adding = false },
                            onCancel = { adding = false }
                        )
                    }
                }
                for (cat in Category.entries) {
                    val rows = ingredients.filter { it.category == cat }
                    if (rows.isEmpty()) continue
                    item(key = "sec_${cat.key}") {
                        Row(
                            Modifier.padding(top = 18.dp, bottom = 8.dp, start = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(Modifier.size(9.dp).clip(CircleShape).background(categoryColor(cat)))
                            Text(cat.label, color = c.ink, fontSize = 13.5.sp, fontWeight = FontWeight.Bold)
                            Text("${rows.size}", color = c.ink3, fontSize = 12.5.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                    item(key = "card_${cat.key}") {
                        Column(Modifier.fillMaxWidth().fridgeCard()) {
                            rows.forEachIndexed { idx, it ->
                                IngredientRow(
                                    item = it, last = idx == rows.lastIndex,
                                    onToggle = { onToggle(it.id) },
                                    onQty = { q -> onQty(it.id, q) },
                                    onFresh = { onFresh(it.id) },
                                    onRemove = { onRemove(it.id) }
                                )
                            }
                        }
                    }
                }
                item {
                    Text(
                        "신선도 칩을 눌러 직접 바꿀 수 있어요",
                        color = c.ink3, fontSize = 12.sp,
                        modifier = Modifier.fillMaxWidth().padding(top = 26.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        }

        // ── sticky CTA ──
        Box(
            Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(Brush.verticalGradient(0f to Color.Transparent, 0.38f to c.bg))
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            val enabled = selCount > 0
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(FridgeRadius.md))
                    .background(if (enabled) c.primary else c.chip)
                    .then(if (enabled) Modifier.clickable(onClick = onFind) else Modifier),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(FridgeIcons.search, null, tint = if (enabled) c.primaryInk else c.ink3, modifier = Modifier.size(19.dp))
                Text(
                    "  레시피 찾기" + if (selCount > 0) "  · ${selCount}개 재료" else "",
                    color = if (enabled) c.primaryInk else c.ink3, fontSize = 16.5.sp, fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun IngredientRow(
    item: Ingredient,
    last: Boolean,
    onToggle: () -> Unit,
    onQty: (Int) -> Unit,
    onFresh: () -> Unit,
    onRemove: () -> Unit
) {
    val c = FridgeTheme.colors
    Column {
        Row(
            Modifier
                .fillMaxWidth()
                .alpha(if (item.selected) 1f else 0.5f)
                .padding(horizontal = 12.dp, vertical = 11.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(9.dp)
        ) {
            Box(Modifier.clickable(onClick = onToggle)) { CheckBox(on = item.selected) }
            CategoryDot(item.category)
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        item.name, color = c.ink, fontSize = 15.sp, fontWeight = FontWeight.SemiBold,
                        maxLines = 1, overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    FreshnessChip(item.freshness, onClick = onFresh)
                }
                if (item.needsReview) {
                    Row(
                        Modifier.padding(top = 3.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Icon(FridgeIcons.edit, null, tint = c.warn, modifier = Modifier.size(11.dp))
                        Text("AI 확인 필요", color = c.warn, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
            QtyStepper(item.qty, item.unit, onQty)
            Box(Modifier.size(28.dp).clip(CircleShape).clickable(onClick = onRemove), contentAlignment = Alignment.Center) {
                Icon(FridgeIcons.trash, "삭제", tint = c.ink3, modifier = Modifier.size(16.dp))
            }
        }
        if (!last) Box(Modifier.fillMaxWidth().height(1.dp).background(c.line))
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AddCard(onAdd: (String, Category) -> Unit, onCancel: () -> Unit) {
    val c = FridgeTheme.colors
    var name by remember { mutableStateOf("") }
    var cat by remember { mutableStateOf(Category.VEG) }

    fun submit() { if (name.trim().isNotEmpty()) onAdd(name.trim(), cat) }

    Column(
        Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
            .clip(RoundedCornerShape(FridgeRadius.lg))
            .background(c.surface)
            .border(1.5.dp, c.primary, RoundedCornerShape(FridgeRadius.lg))
            .padding(16.dp)
    ) {
        Text("재료 추가", color = c.ink, fontSize = 13.5.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 10.dp))
        OutlinedTextField(
            value = name, onValueChange = { name = it },
            placeholder = { Text("재료 이름 (예: 감자)") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { submit() }),
            modifier = Modifier.fillMaxWidth()
        )
        FlowRow(
            Modifier.fillMaxWidth().padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(7.dp)
        ) {
            for (ct in Category.entries) {
                val sel = ct == cat
                Row(
                    Modifier
                        .clip(CircleShape)
                        .background(if (sel) c.primarySoft else c.surface)
                        .border(1.dp, if (sel) c.primary else c.line, CircleShape)
                        .clickable { cat = ct }
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    CategoryDot(ct, 7.dp)
                    Text(ct.label, color = if (sel) c.primary else c.ink2, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            PillBtn("취소", c.chip, c.ink2, onCancel)
            Box(Modifier.size(8.dp))
            PillBtn("추가", c.primary, c.primaryInk) { submit() }
        }
    }
}

@Composable
private fun PillBtn(text: String, bg: Color, fg: Color, onClick: () -> Unit) {
    Box(
        Modifier.clip(CircleShape).background(bg).clickable(onClick = onClick).padding(horizontal = 18.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text, color = fg, fontSize = 14.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun CheckBox(on: Boolean) {
    val c = FridgeTheme.colors
    Box(
        Modifier
            .size(22.dp)
            .clip(RoundedCornerShape(7.dp))
            .background(if (on) c.primary else Color.Transparent)
            .border(1.5.dp, if (on) c.primary else c.ink3, RoundedCornerShape(7.dp)),
        contentAlignment = Alignment.Center
    ) {
        if (on) Icon(FridgeIcons.check, null, tint = c.primaryInk, modifier = Modifier.size(14.dp))
    }
}

@Composable
private fun IconBtn(icon: androidx.compose.ui.graphics.vector.ImageVector, cd: String, onClick: () -> Unit) {
    Box(Modifier.size(40.dp).clip(CircleShape).clickable(onClick = onClick), contentAlignment = Alignment.Center) {
        Icon(icon, cd, tint = FridgeTheme.colors.ink, modifier = Modifier.size(22.dp))
    }
}
