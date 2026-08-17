package com.xiaoyv.bangumi.shared.ui.component.dialog.date

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.global_all
import com.xiaoyv.bangumi.core_resource.resources.global_cancel
import com.xiaoyv.bangumi.core_resource.resources.global_confirm
import com.xiaoyv.bangumi.shared.core.utils.currentYear
import com.xiaoyv.bangumi.shared.ui.component.dialog.alert.AlertDialogState
import org.jetbrains.compose.resources.stringResource
import top.yukonga.miuix.kmp.basic.NumberPicker
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.overlay.OverlayDialog
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun <T> WheelPicker(
    items: List<T>,
    selectedItem: T,
    onItemLabel: @Composable (T) -> String,
    onItemSelected: (T) -> Unit,
    modifier: Modifier = Modifier,
    visibleCount: Int = 5,
    itemHeight: Dp = 40.dp,
) {
    require(visibleCount % 2 == 1) { "visibleCount should be odd to have a unique center." }

    val itemHeightPx = with(LocalDensity.current) { itemHeight.toPx() }
    val halfVisible = visibleCount / 2

    val initialIndex = items.indexOf(selectedItem).coerceAtLeast(0)
    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = initialIndex,
        initialFirstVisibleItemScrollOffset = 0
    )
    val hapticFeedback = LocalHapticFeedback.current
    val centeredFirstVisibleItemIndex by remember {
        derivedStateOf {
            if (listState.firstVisibleItemScrollOffset > itemHeightPx / 2) {
                listState.firstVisibleItemIndex + 1
            } else {
                listState.firstVisibleItemIndex
            }
        }
    }

    LaunchedEffect(centeredFirstVisibleItemIndex) {
        if (centeredFirstVisibleItemIndex != initialIndex) {
            hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            onItemSelected(items[centeredFirstVisibleItemIndex])
        }
    }

    LaunchedEffect(Unit) {
        snapshotFlow { listState.isScrollInProgress }
            .collect { isScrollInProgress ->
                if (!isScrollInProgress) {
                    // 这里避免用户滑动打断 animateScroll 抛出的 CancellationException 终止收集，手动捕获一下
                    try {
                        if (listState.firstVisibleItemScrollOffset > itemHeightPx / 2) {
                            listState.animateScrollToItem(listState.firstVisibleItemIndex + 1, 0)
                        } else {
                            listState.animateScrollToItem(listState.firstVisibleItemIndex, 0)
                        }
                    } catch (_: Exception) {
                    }
                }
            }
    }

    Box(modifier = modifier) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
        ) {
            items(halfVisible) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(itemHeight),
                )
            }
            itemsIndexed(items) { index, item ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(itemHeight),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = onItemLabel(item),
                        textAlign = TextAlign.Center,
                        color = if (index == centeredFirstVisibleItemIndex) {
                            MiuixTheme.colorScheme.onSurface
                        } else {
                            MiuixTheme.colorScheme.onSurfaceVariantSummary
                        },
                    )
                }
            }
            items(halfVisible) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(itemHeight),
                )
            }
        }

        // 中心高亮条（可按需要自定义样式）
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .height(itemHeight)
                .background(MiuixTheme.colorScheme.secondaryContainer.copy(alpha = 0.55f))
        )
    }
}

@Composable
fun MonthPicker(
    dialogState: AlertDialogState,
    currentMonth: Int,
    currentYear: Int,
    onConfirm: (Int, Int) -> Unit,
    wheelHeight: Dp = 200.dp,
    wheelVisibleCount: Int = 5,
) {
    var month by remember { mutableIntStateOf(currentMonth) }
    var year by remember { mutableIntStateOf(currentYear) }
    val years = remember {
        buildList {
            add(0)
            addAll((1970..currentYear() + 5).reversed())
        }
    }
    val yearIndex = years.indexOf(year).coerceAtLeast(0)
    val allLabel = stringResource(Res.string.global_all)
    val showing by dialogState.showing.collectAsStateWithLifecycle()

    OverlayDialog(
        show = showing,
        title = "选择日期",
        onDismissRequest = {
            month = currentMonth
            year = currentYear
            dialogState.dismiss()
        },
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(wheelHeight),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                NumberPicker(
                    modifier = Modifier.weight(1f),
                    value = yearIndex,
                    onValueChange = { year = years[it] },
                    range = years.indices,
                    visibleItemCount = wheelVisibleCount,
                    label = { if (years[it] == 0) allLabel else "${years[it]}年" },
                )
                NumberPicker(
                    modifier = Modifier.weight(1f),
                    value = month,
                    onValueChange = { month = it },
                    range = 0..12,
                    visibleItemCount = wheelVisibleCount,
                    label = { if (it == 0) allLabel else "${it}月" },
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            TextButton(
                text = stringResource(Res.string.global_confirm),
                onClick = {
                    onConfirm(year, month)
                    dialogState.dismiss()
                },
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(8.dp))
            TextButton(
                text = stringResource(Res.string.global_cancel),
                onClick = {
                    month = currentMonth
                    year = currentYear
                    dialogState.dismiss()
                },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
