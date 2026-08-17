package com.xiaoyv.bangumi.shared.ui.component.pager

import androidx.annotation.FloatRange
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import com.xiaoyv.bangumi.shared.ui.component.divider.BgmHorizontalDivider
import com.xiaoyv.bangumi.shared.ui.component.layout.state.DoubleTapToScrollTopState
import com.xiaoyv.bangumi.shared.ui.component.layout.state.LocalDoubleTapToScrollTopState
import com.xiaoyv.bangumi.shared.ui.component.space.LayoutPaddingHalf
import com.xiaoyv.bangumi.shared.ui.component.tab.ComposeTextTab
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.abs

@Composable
fun rememberBgmPagerState(
    initialPage: Int = 0,
    @FloatRange(from = -0.5, to = 0.5) initialPageOffsetFraction: Float = 0f,
    onPageChange: (Int) -> Unit = {},
    pageCount: () -> Int,
): PagerState {
    val pagerState = rememberPagerState(initialPage, initialPageOffsetFraction, pageCount)
    LaunchedEffect(Unit) {
        snapshotFlow { pagerState.currentPage }.collect {
            onPageChange(it)
        }
    }
    return pagerState
}

@Composable
fun <Key : Any> BgmTabHorizontalPager(
    tabs: SerializeList<ComposeTextTab<Key>>,
    modifier: Modifier = Modifier,
    initialPage: Int = 0,
    userScrollEnabled: Boolean = true,
    edgePadding: Dp = 0.dp,
    beyondViewportPageCount: Int = 0,
    style: TextStyle = MaterialTheme.typography.bodyLarge,
    key: ((index: Int) -> Any)? = { tabs[it].type },
    onTabSelected: (Int) -> Unit = {},
    pagerState: PagerState = rememberPagerState(
        initialPage = initialPage.coerceAtLeast(0),
        pageCount = { tabs.size }
    ),
    divider: @Composable () -> Unit = @Composable { BgmHorizontalDivider() },
    pageContent: @Composable (page: Int) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val currentPage = pagerState.currentPage.coerceAtMost(tabs.size - 1)
    var inputType by remember { mutableStateOf(PointerType.Touch) }
    val doubleTapStates = remember(tabs.size) {
        List(tabs.size) { DoubleTapToScrollTopState() }
    }
    val parentDoubleTapState = LocalDoubleTapToScrollTopState.current

    androidx.compose.runtime.DisposableEffect(parentDoubleTapState, currentPage, doubleTapStates) {
        val currentState = doubleTapStates.getOrNull(currentPage)
        if (currentState != null) parentDoubleTapState?.delegateTo(currentState)
        onDispose {
            if (currentState != null) parentDoubleTapState?.clearDelegate(currentState)
        }
    }

    val tabLabels = tabs.map { it.displayText() }
    val pageWithDoubleTapState: @Composable (Int) -> Unit = { page ->
        if (parentDoubleTapState == null) {
            pageContent(page)
        } else {
            CompositionLocalProvider(
                LocalDoubleTapToScrollTopState provides doubleTapStates[page],
            ) {
                pageContent(page)
            }
        }
    }

    Column(modifier = modifier.fillMaxSize()) {
        if (tabs.size > 1) {
            BgmTabRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp)
                    .semantics { contentDescription = "scrollable_tab" },
                tabs = tabLabels,
                selectedTabIndex = currentPage,
                onTabSelected = { index ->
                    scope.launch {
                        if (index != currentPage) {
                            onTabSelected(index)
                        }
                        if (abs(currentPage - index) > 1) {
                            pagerState.scrollToPage(index)
                        } else {
                            pagerState.animateScrollToPage(index)
                        }
                    }
                },
            )
        }

        if (tabs.size == 1) {
            pageWithDoubleTapState(0)
        } else {
            divider()
            HorizontalPager(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .pointerInput(Unit) {
                        awaitPointerEventScope {
                            while (true) {
                                val event = awaitPointerEvent()
                                val pointer = event.changes.firstOrNull()
                                if (pointer != null) {
                                    inputType = pointer.type
                                }
                            }
                        }
                    },
                state = pagerState,
                userScrollEnabled = inputType == PointerType.Touch && userScrollEnabled,
                beyondViewportPageCount = beyondViewportPageCount,
                key = key,
                pageContent = { pageWithDoubleTapState(it) }
            )
        }
    }
}


@Composable
fun <Key : Any> BgmChipHorizontalPager(
    tabs: SerializeList<ComposeTextTab<Key>>,
    modifier: Modifier = Modifier,
    initialPage: Int = 0,
    userScrollEnabled: Boolean = true,
    beyondViewportPageCount: Int = 0,
    key: ((index: Int) -> Any)? = { tabs[it].type },
    onTabSelected: (Int) -> Unit = {},
    scope: CoroutineScope = rememberCoroutineScope(),
    listState: LazyListState = rememberLazyListState(),
    pagerState: PagerState = rememberBgmPagerState(
        onPageChange = { scope.launch { listState.animateScrollToItem(it) } },
        initialPage = initialPage.coerceAtLeast(0),
        pageCount = { tabs.size }
    ),
    extra: @Composable (ColumnScope.() -> Unit)? = null,
    pageContent: @Composable (page: Int) -> Unit,
) {
    val tabLabels = tabs.map { it.displayText() }
    Column(modifier = modifier) {
        if (tabs.size > 1) {
            BgmTabRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = LayoutPaddingHalf),
                tabs = tabLabels,
                selectedTabIndex = pagerState.currentPage,
                listState = listState,
                onTabSelected = { index ->
                    onTabSelected(index)
                    scope.launch { pagerState.scrollToPage(index) }
                },
            )
        }

        if (extra != null) extra()

        if (tabs.size == 1) {
            pageContent(0)
        } else HorizontalPager(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            key = key,
            state = pagerState,
            userScrollEnabled = userScrollEnabled,
            beyondViewportPageCount = beyondViewportPageCount,
            pageContent = { pageContent(it) }
        )
    }
}
