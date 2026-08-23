package com.xiaoyv.bangumi.shared.ui.component.layout

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold as MaterialScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.xiaoyv.bangumi.shared.ui.component.layout.state.DoubleTapToScrollTopHost
import com.xiaoyv.bangumi.shared.ui.component.layout.state.DoubleTapToScrollTopState
import com.xiaoyv.bangumi.shared.ui.component.layout.state.LocalDoubleTapToScrollTopState
import com.xiaoyv.bangumi.shared.ui.theme.isMiuixUi
import top.yukonga.miuix.kmp.basic.Scaffold as MiuixScaffold
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun BgmScaffold(
    modifier: Modifier = Modifier,
    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    snackbarHost: @Composable () -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    containerColor: Color = if (isMiuixUi()) MiuixTheme.colorScheme.surface else MaterialTheme.colorScheme.background,
    contentWindowInsets: WindowInsets? = null,
    content: @Composable (PaddingValues) -> Unit,
) {
    val doubleTapState = remember { DoubleTapToScrollTopState() }
    val hostedTopBar: @Composable () -> Unit = {
        DoubleTapToScrollTopHost(doubleTapState) { topBar() }
    }
    CompositionLocalProvider(LocalDoubleTapToScrollTopState provides doubleTapState) {
        if (isMiuixUi()) {
            if (contentWindowInsets == null) {
                MiuixScaffold(
                    modifier = modifier,
                    topBar = hostedTopBar,
                    bottomBar = bottomBar,
                    snackbarHost = snackbarHost,
                    floatingActionButton = floatingActionButton,
                    containerColor = containerColor,
                    content = content,
                )
            } else {
                MiuixScaffold(
                    modifier = modifier,
                    topBar = hostedTopBar,
                    bottomBar = bottomBar,
                    snackbarHost = snackbarHost,
                    floatingActionButton = floatingActionButton,
                    containerColor = containerColor,
                    contentWindowInsets = contentWindowInsets,
                    content = content,
                )
            }
        } else {
            if (contentWindowInsets == null) {
                MaterialScaffold(
                    modifier = modifier,
                    topBar = hostedTopBar,
                    bottomBar = bottomBar,
                    snackbarHost = snackbarHost,
                    floatingActionButton = floatingActionButton,
                    containerColor = containerColor,
                    content = content,
                )
            } else {
                MaterialScaffold(
                    modifier = modifier,
                    topBar = hostedTopBar,
                    bottomBar = bottomBar,
                    snackbarHost = snackbarHost,
                    floatingActionButton = floatingActionButton,
                    containerColor = containerColor,
                    contentWindowInsets = contentWindowInsets,
                    content = content,
                )
            }
        }
    }
}
