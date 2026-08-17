package com.xiaoyv.bangumi.shared.ui.component.pager

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.xiaoyv.bangumi.shared.core.utils.clickWithoutRipped
import top.yukonga.miuix.kmp.basic.TabRowDefaults
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.squircle.squircleBackground
import top.yukonga.miuix.kmp.squircle.squircleBorder
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun BgmTabRow(
    tabs: List<String>,
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState(),
) {
    val cornerRadius = TabRowDefaults.TabRowCornerRadius
    val selectedBg = MiuixTheme.colorScheme.surfaceContainer
    val selectedContent = MiuixTheme.colorScheme.onBackground
    val unselectedContent = MiuixTheme.colorScheme.onSurfaceVariantSummary
    val outlineColor = MiuixTheme.colorScheme.outline
    val selectedIndex = selectedTabIndex.coerceIn(0, (tabs.size - 1).coerceAtLeast(0))

    LaunchedEffect(selectedIndex, tabs.size) {
        if (tabs.isEmpty()) return@LaunchedEffect
        val visible = listState.layoutInfo.visibleItemsInfo
        val alreadyVisible = visible.any { it.index == selectedIndex }
        if (!alreadyVisible) {
            listState.animateScrollToItem(selectedIndex)
        }
    }

    LazyRow(
        state = listState,
        modifier = modifier.height(TabRowDefaults.TabRowHeight),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(9.dp),
    ) {
        itemsIndexed(tabs, key = { index, label -> "$index-$label" }) { index, label ->
            val selected = index == selectedIndex
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .then(
                        if (selected) {
                            Modifier.squircleBackground(
                                color = selectedBg,
                                cornerRadius = cornerRadius,
                            )
                        } else {
                            Modifier.squircleBorder(
                                width = 1.dp,
                                color = outlineColor,
                                cornerRadius = cornerRadius,
                            )
                        }
                    )
                    .clickWithoutRipped { onTabSelected(index) }
                    .padding(horizontal = 14.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = label,
                    color = if (selected) selectedContent else unselectedContent,
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                    fontSize = MiuixTheme.textStyles.body1.fontSize,
                    maxLines = 1,
                    softWrap = false,
                )
            }
        }
    }
}
