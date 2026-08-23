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

/** MIUIX pill tabs used only when the UI style is MIUIX. */
@Composable
internal fun BgmMiuixTabRow(
    tabs: List<String>,
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState(),
) {
    val cornerRadius = TabRowDefaults.TabRowCornerRadius
    val selectedIndex = selectedTabIndex.coerceIn(0, (tabs.size - 1).coerceAtLeast(0))

    LaunchedEffect(selectedIndex, tabs.size) {
        if (tabs.isEmpty()) return@LaunchedEffect
        if (listState.layoutInfo.visibleItemsInfo.none { it.index == selectedIndex }) {
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
                                color = MiuixTheme.colorScheme.surfaceContainer,
                                cornerRadius = cornerRadius,
                            )
                        } else {
                            Modifier.squircleBorder(
                                width = 1.dp,
                                color = MiuixTheme.colorScheme.outline,
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
                    color = if (selected) {
                        MiuixTheme.colorScheme.onBackground
                    } else {
                        MiuixTheme.colorScheme.onSurfaceVariantSummary
                    },
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                    fontSize = MiuixTheme.textStyles.body1.fontSize,
                    maxLines = 1,
                    softWrap = false,
                )
            }
        }
    }
}
