package com.xiaoyv.bangumi.shared.ui.component.layout.state

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.global_double_tap_scroll_top
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

internal val DoubleTapTopAreaHeight = 40.dp
internal val DoubleTapTopAreaHorizontalPadding = 96.dp

private const val HintDurationMillis = 2_800L

private var doubleTapHintShown = false

@Stable
internal class DoubleTapToScrollTopState {
    private var ownIsScrolled by mutableStateOf(false)
    private var ownShowHint by mutableStateOf(false)
    private var delegatedState by mutableStateOf<DoubleTapToScrollTopState?>(null)

    val isScrolled: Boolean
        get() = delegatedState?.isScrolled ?: ownIsScrolled

    val showHint: Boolean
        get() = delegatedState?.showHint ?: ownShowHint

    private var scrollToTop: (() -> Unit)? = null

    fun update(
        isScrolled: Boolean,
        scrollToTop: () -> Unit,
    ) {
        ownIsScrolled = isScrolled
        this.scrollToTop = scrollToTop
        if (isScrolled && !doubleTapHintShown) {
            doubleTapHintShown = true
            ownShowHint = true
        }
    }

    fun clear() {
        ownIsScrolled = false
        ownShowHint = false
        scrollToTop = null
    }

    fun delegateTo(state: DoubleTapToScrollTopState) {
        if (state !== this) delegatedState = state
    }

    fun clearDelegate(state: DoubleTapToScrollTopState) {
        if (delegatedState === state) delegatedState = null
    }

    fun invokeScrollToTop() {
        delegatedState?.invokeScrollToTop() ?: scrollToTop?.invoke()
    }

    fun dismissHint() {
        delegatedState?.dismissHint() ?: run { ownShowHint = false }
    }
}

internal val LocalDoubleTapToScrollTopState =
    staticCompositionLocalOf<DoubleTapToScrollTopState?> { null }

@Composable
internal fun DoubleTapToScrollTopHost(
    state: DoubleTapToScrollTopState,
    content: @Composable BoxScope.() -> Unit,
) {
    Layout(
        content = {
            Box(modifier = Modifier.fillMaxWidth()) {
                content()
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(DoubleTapTopAreaHeight),
            ) {
                DoubleTapToScrollTopOverlay(
                    state = state,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = DoubleTapTopAreaHorizontalPadding)
                        .zIndex(1f),
                )
            }
        },
    ) { measurables, constraints ->
        val topBar = measurables[0].measure(constraints.copy(minHeight = 0))
        val overlay = measurables[1].measure(
            constraints.copy(
                minWidth = topBar.width,
                maxWidth = topBar.width,
                minHeight = 0,
                maxHeight = topBar.height,
            ),
        )

        layout(topBar.width, topBar.height) {
            topBar.placeRelative(0, 0)
            overlay.placeRelative(0, (topBar.height - overlay.height).coerceAtLeast(0))
        }
    }
}

@Composable
internal fun BoxScope.DoubleTapToScrollTop(
    isScrolled: Boolean,
    onScrollToTop: suspend () -> Unit,
) {
    val hostState = LocalDoubleTapToScrollTopState.current
    val state = hostState ?: remember { DoubleTapToScrollTopState() }
    val scope = rememberCoroutineScope()
    val currentOnScrollToTop by rememberUpdatedState(onScrollToTop)

    SideEffect {
        state.update(
            isScrolled = isScrolled,
            scrollToTop = {
                scope.launch { currentOnScrollToTop() }
            },
        )
    }

    DisposableEffect(state) {
        onDispose { state.clear() }
    }

    if (hostState == null) {
        DoubleTapToScrollTopOverlay(
            state = state,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .padding(horizontal = DoubleTapTopAreaHorizontalPadding)
                .height(DoubleTapTopAreaHeight)
                .zIndex(1f),
        )
    }
}

@Composable
internal fun DoubleTapToScrollTopOverlay(
    state: DoubleTapToScrollTopState,
    modifier: Modifier,
) {
    val hint = stringResource(Res.string.global_double_tap_scroll_top)

    LaunchedEffectHint(state)

    if (state.isScrolled) {
        Box(
            modifier = modifier
                .pointerInput(state) {
                    detectTapGestures(onDoubleTap = { state.invokeScrollToTop() })
                },
        ) {
            if (state.showHint) {
                Text(
                    modifier = Modifier.align(Alignment.Center),
                    text = hint,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.labelLarge,
                )
            }
        }
    }
}

@Composable
private fun LaunchedEffectHint(state: DoubleTapToScrollTopState) {
    androidx.compose.runtime.LaunchedEffect(state.showHint) {
        if (state.showHint) {
            delay(HintDurationMillis)
            state.dismissHint()
        }
    }
}
