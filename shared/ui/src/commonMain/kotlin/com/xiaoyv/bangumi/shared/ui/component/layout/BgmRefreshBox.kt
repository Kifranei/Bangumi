package com.xiaoyv.bangumi.shared.ui.component.layout

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.xiaoyv.bangumi.shared.ui.component.layout.refresh.PullToRefreshBox

val LocalCollapsingPullRefresh = staticCompositionLocalOf { true }

@Composable
fun BgmRefreshBox(
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    isRefreshEnabled: Boolean = true,
    contentAlignment: Alignment = Alignment.TopStart,
    content: @Composable BoxScope.() -> Unit,
) {
    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        modifier = modifier,
        contentAlignment = contentAlignment,
        enabled = isRefreshEnabled,
        content = content,
    )
}
