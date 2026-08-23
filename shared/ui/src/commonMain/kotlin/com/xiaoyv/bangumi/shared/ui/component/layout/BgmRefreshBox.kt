package com.xiaoyv.bangumi.shared.ui.component.layout

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.xiaoyv.bangumi.shared.ui.component.layout.refresh.BgmPullToRefreshState
import com.xiaoyv.bangumi.shared.ui.component.layout.refresh.PullToRefreshBox
import com.xiaoyv.bangumi.shared.ui.component.layout.refresh.rememberBgmPullToRefreshState

val LocalCollapsingPullRefresh = compositionLocalOf { true }

/**
 * [BgmRefreshBox]
 */
@Composable
fun BgmRefreshBox(
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    state: BgmPullToRefreshState = rememberBgmPullToRefreshState(),
    isRefreshEnabled: Boolean = true,
    contentAlignment: Alignment = Alignment.TopStart,
    indicatorPaddingTop: Dp = 0.dp,
    indicator: @Composable BoxScope.() -> Unit = {
        Indicator(
            modifier = Modifier.align(Alignment.TopCenter),
            isRefreshing = isRefreshing,
            state = state.material
        )
    },
    content: @Composable BoxScope.() -> Unit,
) {
    PullToRefreshBox(
        modifier = modifier,
        state = state,
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        enabled = isRefreshEnabled,
        contentAlignment = contentAlignment,
        indicatorPaddingTop = indicatorPaddingTop,
        indicator = indicator,
        content = content,
    )
}
