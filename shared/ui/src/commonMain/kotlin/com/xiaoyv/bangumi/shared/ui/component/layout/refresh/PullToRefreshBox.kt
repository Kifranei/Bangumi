package com.xiaoyv.bangumi.shared.ui.component.layout.refresh

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.pull_refresh_complete
import com.xiaoyv.bangumi.core_resource.resources.pull_refresh_pulling
import com.xiaoyv.bangumi.core_resource.resources.pull_refresh_refreshing
import com.xiaoyv.bangumi.core_resource.resources.pull_refresh_release
import com.xiaoyv.bangumi.shared.ui.theme.isMiuixUi
import org.jetbrains.compose.resources.stringResource
import androidx.compose.material3.pulltorefresh.PullToRefreshState as MaterialPullToRefreshState
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState as rememberMaterialPullToRefreshState
import top.yukonga.miuix.kmp.basic.PullToRefresh as MiuixPullToRefresh
import top.yukonga.miuix.kmp.basic.PullToRefreshState as MiuixPullToRefreshState
import top.yukonga.miuix.kmp.basic.rememberPullToRefreshState as rememberMiuixPullToRefreshState

@Stable
class BgmPullToRefreshState internal constructor(
    internal val material: MaterialPullToRefreshState,
    internal val miuix: MiuixPullToRefreshState,
)

@Composable
fun rememberBgmPullToRefreshState(): BgmPullToRefreshState {
    val material = rememberMaterialPullToRefreshState()
    val miuix = rememberMiuixPullToRefreshState()
    return remember(material, miuix) { BgmPullToRefreshState(material, miuix) }
}

@Composable
fun PullToRefreshBox(
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    state: BgmPullToRefreshState = rememberBgmPullToRefreshState(),
    contentAlignment: Alignment = Alignment.TopStart,
    enabled: Boolean = true,
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
    if (isMiuixUi()) {
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
        MiuixPullToRefresh(
            isRefreshing = isRefreshing,
            onRefresh = onRefresh,
            modifier = modifier,
            pullToRefreshState = state.miuix,
            contentPadding = PaddingValues(top = indicatorPaddingTop),
            refreshTexts = refreshTexts,
        ) {
            Box(contentAlignment = contentAlignment, content = content)
        }
    } else {
        Box(
            modifier.pullToRefresh(
                state = state.material,
                enabled = enabled,
                isRefreshing = isRefreshing,
                onRefresh = onRefresh,
            ),
            contentAlignment = contentAlignment
        ) {
            content()
            indicator()
        }
    }
}
