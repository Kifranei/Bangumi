package com.xiaoyv.bangumi.shared.ui.component.layout.refresh

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.pull_refresh_complete
import com.xiaoyv.bangumi.core_resource.resources.pull_refresh_pulling
import com.xiaoyv.bangumi.core_resource.resources.pull_refresh_refreshing
import com.xiaoyv.bangumi.core_resource.resources.pull_refresh_release
import org.jetbrains.compose.resources.stringResource
import top.yukonga.miuix.kmp.basic.PullToRefresh
import top.yukonga.miuix.kmp.basic.PullToRefreshState
import top.yukonga.miuix.kmp.basic.rememberPullToRefreshState

@Composable
fun PullToRefreshBox(
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    state: PullToRefreshState = rememberPullToRefreshState(),
    contentAlignment: Alignment = Alignment.TopStart,
    enabled: Boolean = true,
    indicator: @Composable BoxScope.() -> Unit = {},
    content: @Composable BoxScope.() -> Unit,
) {
    if (!enabled) {
        Box(modifier = modifier, contentAlignment = contentAlignment, content = content)
        return
    }
    val refreshTexts = listOf(
        stringResource(Res.string.pull_refresh_pulling),
        stringResource(Res.string.pull_refresh_release),
        stringResource(Res.string.pull_refresh_refreshing),
        stringResource(Res.string.pull_refresh_complete),
    )
    PullToRefresh(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        modifier = modifier,
        pullToRefreshState = state,
        refreshTexts = refreshTexts,
    ) {
        Box(contentAlignment = contentAlignment, content = content)
    }
}