package com.xiaoyv.bangumi.shared.ui.component.layout

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.xiaoyv.bangumi.shared.ui.component.layout.state.DoubleTapToScrollTopHost
import com.xiaoyv.bangumi.shared.ui.component.layout.state.DoubleTapToScrollTopState
import com.xiaoyv.bangumi.shared.ui.component.layout.state.LocalDoubleTapToScrollTopState
import top.yukonga.miuix.kmp.basic.FabPosition
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun BgmScaffold(
    modifier: Modifier = Modifier,
    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    snackbarHost: @Composable () -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    floatingActionButtonPosition: FabPosition = FabPosition.End,
    containerColor: Color = MiuixTheme.colorScheme.surface,
    contentWindowInsets: WindowInsets? = null,
    content: @Composable (PaddingValues) -> Unit,
) {
    val doubleTapToScrollTopState = remember { DoubleTapToScrollTopState() }
    val topBarWithDoubleTap: @Composable () -> Unit = {
        DoubleTapToScrollTopHost(state = doubleTapToScrollTopState) {
            topBar()
        }
    }

    CompositionLocalProvider(
        LocalDoubleTapToScrollTopState provides doubleTapToScrollTopState,
    ) {
        if (contentWindowInsets != null) {
            Scaffold(
                modifier = modifier,
                topBar = topBarWithDoubleTap,
                bottomBar = bottomBar,
                snackbarHost = snackbarHost,
                floatingActionButton = floatingActionButton,
                floatingActionButtonPosition = floatingActionButtonPosition,
                containerColor = containerColor,
                contentWindowInsets = contentWindowInsets,
                content = content,
            )
        } else {
            Scaffold(
                modifier = modifier,
                topBar = topBarWithDoubleTap,
                bottomBar = bottomBar,
                snackbarHost = snackbarHost,
                floatingActionButton = floatingActionButton,
                floatingActionButtonPosition = floatingActionButtonPosition,
                containerColor = containerColor,
                content = content,
            )
        }
    }
}
