package com.mgpark.fridgelab.ui.recipes

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mgpark.fridgelab.domain.model.Recipe
import com.mgpark.fridgelab.navigation.RecipesUiState
import com.mgpark.fridgelab.ui.components.FridgeIcons
import com.mgpark.fridgelab.ui.components.MatchRing
import com.mgpark.fridgelab.ui.components.StripedPlaceholder
import com.mgpark.fridgelab.ui.components.fridgeCard
import com.mgpark.fridgelab.ui.theme.FridgeRadius
import com.mgpark.fridgelab.ui.theme.FridgeTheme

private enum class Sort(val label: String) { MATCH("매칭률순"), TIME("빠른 조리"), LEVEL("쉬운 난이도") }

@Composable
fun RecipesScreen(
    ingredientCount: Int,
    state: RecipesUiState,
    onBack: () -> Unit,
    onOpen: (String) -> Unit,
    onRetry: () -> Unit
) {
    val c = FridgeTheme.colors
    var sort by remember { mutableStateOf(Sort.MATCH) }
    val listState = rememberLazyListState()

    // 정렬 탭을 바꾸면 리스트를 첫 레시피로 스크롤
    LaunchedEffect(sort) {
        listState.animateScrollToItem(0)
    }

    val sorted = remember(state.recipes, sort) {
        when (sort) {
            Sort.MATCH -> state.recipes.sortedByDescending { it.matchPct }
            Sort.TIME -> state.recipes.sortedBy { it.timeMin }
            Sort.LEVEL -> state.recipes.sortedBy { it.level.rank }
        }
    }

    Column(Modifier.fillMaxSize().background(c.bg)) {
        // ── header ──
        Column(
            Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(top = 6.dp)
        ) {
            Row(Modifier.padding(start = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(40.dp).clip(CircleShape).clickable(onClick = onBack), contentAlignment = Alignment.Center) {
                    Icon(FridgeIcons.back, "뒤로", tint = c.ink, modifier = Modifier.size(22.dp))
                }
            }
            Column(Modifier.padding(horizontal = 22.dp).padding(top = 6.dp, bottom = 14.dp)) {
                Text("추천 레시피", color = c.ink, fontSize = 27.sp, fontWeight = FontWeight.ExtraBold)
                Row(Modifier.padding(top = 6.dp)) {
                    Text("내 재료 ", color = c.ink2, fontSize = 14.sp)
                    Text("${ingredientCount}개", color = c.primary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text("로 만들 수 있는 ${state.recipes.size}가지 요리예요", color = c.ink2, fontSize = 14.sp)
                }
            }
            LazyRow(
                Modifier.fillMaxWidth().padding(bottom = 12.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(Sort.entries) { s ->
                    val sel = s == sort
                    Box(
                        Modifier
                            .height(34.dp)
                            .clip(CircleShape)
                            .background(if (sel) c.primary else c.surface)
                            .border(1.dp, if (sel) c.primary else c.line, CircleShape)
                            .clickable { sort = s }
                            .padding(horizontal = 15.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(s.label, color = if (sel) c.primaryInk else c.ink2, fontSize = 13.5.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }

        // ── body ──
        when {
            state.loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = c.primary)
                    Text("레시피를 찾고 있어요...", color = c.ink2, fontSize = 14.sp, modifier = Modifier.padding(top = 16.dp))
                    state.notice?.let {
                        Text(it, color = c.warn, fontSize = 12.5.sp, modifier = Modifier.padding(top = 6.dp))
                    }
                }
            }

            state.error != null -> Box(Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(state.error, color = c.soonFg, fontSize = 14.sp, textAlign = TextAlign.Center)
                    Box(
                        Modifier.padding(top = 14.dp).clip(CircleShape).background(c.primary)
                            .clickable(onClick = onRetry).padding(horizontal = 20.dp, vertical = 10.dp)
                    ) { Text("다시 시도", color = c.primaryInk, fontWeight = FontWeight.Bold) }
                }
            }

            else -> LazyColumn(
                modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                state = listState,
                contentPadding = PaddingValues(top = 8.dp, bottom = 40.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(sorted, key = { it.id }) { r -> RecipeCard(r) { onOpen(r.id) } }
            }
        }
    }
}

@Composable
private fun RecipeCard(r: Recipe, onClick: () -> Unit) {
    val c = FridgeTheme.colors
    Row(
        Modifier
            .fillMaxWidth()
            .fridgeCard()
            .clickable(onClick = onClick)
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(13.dp)
    ) {
        Box {
            StripedPlaceholder("음식", modifier = Modifier.size(104.dp))
            Row(
                Modifier
                    .padding(6.dp)
                    .clip(CircleShape)
                    .background(Color(0x80000000))
                    .padding(horizontal = 7.dp, vertical = 3.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Icon(FridgeIcons.clock, null, tint = Color.White, modifier = Modifier.size(12.dp))
                Text("${r.timeMin}분", color = Color.White, fontSize = 11.5.sp, fontWeight = FontWeight.SemiBold)
            }
        }
        Column(Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.Top) {
                Column(Modifier.weight(1f)) {
                    Text(r.name, color = c.ink, fontSize = 16.5.sp, fontWeight = FontWeight.Bold)
                    Row(
                        Modifier.padding(top = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(FridgeIcons.flame, null, tint = c.ink3, modifier = Modifier.size(13.dp))
                        Text(r.level.label, color = c.ink2, fontSize = 12.5.sp)
                        Box(Modifier.size(3.dp).clip(CircleShape).background(c.ink3))
                        Icon(FridgeIcons.users, null, tint = c.ink3, modifier = Modifier.size(13.dp))
                        Text("${r.servings}인분", color = c.ink2, fontSize = 12.5.sp)
                    }
                }
                MatchRing(r.matchPct, size = 44.dp, stroke = 4.dp)
            }
            Box(Modifier.height(8.dp))
            if (r.missing.isEmpty()) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                    Icon(FridgeIcons.check, null, tint = c.primary, modifier = Modifier.size(14.dp))
                    Text("재료가 다 있어요", color = c.primary, fontSize = 12.5.sp, fontWeight = FontWeight.SemiBold)
                }
            } else {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("부족", color = c.ink3, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Row(
                        modifier = Modifier.weight(1f).horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        r.missing.forEach { n ->
                            Box(
                                Modifier.clip(CircleShape).background(c.missChipBg).padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    n.name, color = c.warn, fontSize = 12.sp, fontWeight = FontWeight.SemiBold,
                                    maxLines = 1, softWrap = false
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
