package com.xiaoyv.bangumi.shared.ui.component.bar

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.DecayAnimationSpec
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.global_back
import com.xiaoyv.bangumi.shared.data.manager.shared.LocalHideNavIcon
import com.xiaoyv.bangumi.shared.ui.theme.BgmIconsMirrored
import com.xiaoyv.bangumi.shared.ui.theme.ContentMargin
import com.xiaoyv.bangumi.shared.ui.theme.isMiuixUi
import org.jetbrains.compose.resources.stringResource
import top.yukonga.miuix.kmp.basic.Icon as MiuixIcon
import top.yukonga.miuix.kmp.basic.IconButton as MiuixIconButton
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.ScrollBehavior as MiuixScrollBehaviorType
import top.yukonga.miuix.kmp.basic.SmallTopAppBar as MiuixSmallTopAppBar
import top.yukonga.miuix.kmp.basic.TopAppBar as MiuixTopAppBar
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Stable
class BgmScrollBehavior internal constructor(
    internal val material3: TopAppBarScrollBehavior?,
    internal val miuix: MiuixScrollBehaviorType?,
) {
    val nestedScrollConnection: NestedScrollConnection
        get() = material3?.nestedScrollConnection ?: requireNotNull(miuix).nestedScrollConnection

    val collapsedFraction: Float
        get() = material3?.state?.collapsedFraction ?: miuix?.state?.collapsedFraction ?: 0f
}

@Composable
fun rememberBgmScrollBehavior(
    canScroll: () -> Boolean = { true },
    snapAnimationSpec: AnimationSpec<Float>? = null,
    flingAnimationSpec: DecayAnimationSpec<Float>? = null,
): BgmScrollBehavior {
    return if (isMiuixUi()) {
        val behavior = if (snapAnimationSpec == null && flingAnimationSpec == null) {
            MiuixScrollBehavior(canScroll = canScroll)
        } else {
            MiuixScrollBehavior(
                canScroll = canScroll,
                snapAnimationSpec = snapAnimationSpec,
                flingAnimationSpec = flingAnimationSpec,
            )
        }
        BgmScrollBehavior(material3 = null, miuix = behavior)
    } else {
        val behavior = if (snapAnimationSpec == null && flingAnimationSpec == null) {
            TopAppBarDefaults.exitUntilCollapsedScrollBehavior(canScroll = canScroll)
        } else {
            TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
                canScroll = canScroll,
                snapAnimationSpec = snapAnimationSpec,
                flingAnimationSpec = flingAnimationSpec,
            )
        }
        BgmScrollBehavior(material3 = behavior, miuix = null)
    }
}


@Composable
fun BgmTopAppBar(
    modifier: Modifier = Modifier,
    title: String? = null,
    titleContent: @Composable () -> Unit = {
        if (title != null && !isMiuixUi()) Text(
            text = title,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    },
    onNavigationClick: () -> Unit = {},
    navigationIcon: (@Composable () -> Unit)? = if (LocalHideNavIcon.current) null else {
        {
            BgmNavigationIcon(onClick = onNavigationClick)
        }
    },
    actions: @Composable RowScope.() -> Unit = {},
    expandedHeight: Dp = TopAppBarDefaults.TopAppBarExpandedHeight,
    windowInsets: WindowInsets = TopAppBarDefaults.windowInsets,
    colors: TopAppBarColors = TopAppBarDefaults.topAppBarColors(),
    scrollBehavior: BgmScrollBehavior? = null,
) {
    if (isMiuixUi()) {
        MiuixSmallTopAppBar(
            title = title.orEmpty(),
            modifier = modifier,
            color = colors.containerColor,
            titleColor = colors.titleContentColor,
            navigationIcon = navigationIcon ?: {},
            actions = actions,
            scrollBehavior = scrollBehavior?.miuix,
            bottomContent = titleContent,
        )
        return
    }

    TopAppBar(
        title = titleContent,
        modifier = modifier,
        navigationIcon = navigationIcon ?: {},
        actions = actions,
        expandedHeight = expandedHeight,
        windowInsets = windowInsets,
        colors = colors,
        scrollBehavior = scrollBehavior?.material3
    )
}

@Composable
fun BgmLargeTopAppBar(
    modifier: Modifier = Modifier,
    scrollBehavior: BgmScrollBehavior? = null,
    title: String? = null,
    titleContent: @Composable () -> Unit = {
        if (title != null && !isMiuixUi()) {
            val progress = scrollBehavior?.collapsedFraction ?: 0f
            Text(
                modifier = Modifier.padding(horizontal = (ContentMargin - 16.dp) * (1 - progress)),
                text = title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    },
    onNavigationClick: () -> Unit = {},
    navigationIcon: (@Composable () -> Unit)? = {
        BgmNavigationIcon(onClick = onNavigationClick)
    },
    actions: @Composable RowScope.() -> Unit = {},
    collapsedHeight: Dp = TopAppBarDefaults.LargeAppBarCollapsedHeight,
    expandedHeight: Dp = TopAppBarDefaults.LargeAppBarExpandedHeight,
    windowInsets: WindowInsets = TopAppBarDefaults.windowInsets,
    colors: TopAppBarColors = TopAppBarDefaults.topAppBarColors(),
) {
    if (isMiuixUi()) {
        MiuixTopAppBar(
            title = title.orEmpty(),
            modifier = modifier,
            color = colors.containerColor,
            titleColor = colors.titleContentColor,
            largeTitleColor = colors.titleContentColor,
            navigationIcon = navigationIcon ?: {},
            actions = actions,
            scrollBehavior = scrollBehavior?.miuix,
            bottomContent = titleContent,
        )
        return
    }

    LargeTopAppBar(
        title = titleContent,
        modifier = modifier,
        navigationIcon = navigationIcon ?: {},
        actions = actions,
        collapsedHeight = collapsedHeight,
        expandedHeight = expandedHeight,
        windowInsets = windowInsets,
        colors = colors,
        scrollBehavior = scrollBehavior?.material3
    )
}

@Composable
private fun BgmNavigationIcon(onClick: () -> Unit) {
    if (isMiuixUi()) {
        MiuixIconButton(onClick = onClick) {
            MiuixIcon(
                imageVector = BgmIconsMirrored.ArrowBack,
                contentDescription = stringResource(Res.string.global_back),
                tint = MiuixTheme.colorScheme.onSurface,
            )
        }
    } else {
        IconButton(onClick = onClick) {
            Icon(
                imageVector = BgmIconsMirrored.ArrowBack,
                contentDescription = stringResource(Res.string.global_back),
            )
        }
    }
}
