package com.xiaoyv.bangumi.shared.ui.component.bar

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.global_back
import com.xiaoyv.bangumi.shared.data.manager.shared.LocalHideNavIcon
import com.xiaoyv.bangumi.shared.ui.theme.BgmMiuixIcons
import org.jetbrains.compose.resources.stringResource
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.ScrollBehavior
import top.yukonga.miuix.kmp.basic.SmallTopAppBar
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun rememberBgmScrollBehavior(): ScrollBehavior = MiuixScrollBehavior()

@Composable
fun BgmTopAppBar(
    modifier: Modifier = Modifier,
    title: String? = null,
    titleContent: @Composable () -> Unit = {},
    onNavigationClick: () -> Unit = {},
    navigationIcon: (@Composable () -> Unit)? = if (LocalHideNavIcon.current) null else {
        {
            IconButton(onClick = onNavigationClick) {
                Icon(
                    imageVector = BgmMiuixIcons.Back,
                    contentDescription = stringResource(Res.string.global_back),
                )
            }
        }
    },
    actions: @Composable RowScope.() -> Unit = {},
    color: Color = Color.Unspecified,
    titleColor: Color = Color.Unspecified,
    colors: TopAppBarColors = TopAppBarDefaults.topAppBarColors(),
    scrollBehavior: ScrollBehavior? = null,
) {
    SmallTopAppBar(
        title = title.orEmpty(),
        modifier = modifier,
        color = if (color != Color.Unspecified) color else MiuixTheme.colorScheme.surface,
        titleColor = if (titleColor != Color.Unspecified) titleColor else MiuixTheme.colorScheme.onSurface,
        navigationIcon = navigationIcon ?: {},
        actions = actions,
        scrollBehavior = scrollBehavior,
        bottomContent = titleContent,
    )
}

@Composable
fun BgmLargeTopAppBar(
    modifier: Modifier = Modifier,
    scrollBehavior: ScrollBehavior? = null,
    title: String? = null,
    titleContent: @Composable () -> Unit = {},
    onNavigationClick: () -> Unit = {},
    navigationIcon: (@Composable () -> Unit)? = {
        IconButton(onClick = onNavigationClick) {
            Icon(
                imageVector = BgmMiuixIcons.Back,
                contentDescription = stringResource(Res.string.global_back),
            )
        }
    },
    actions: @Composable RowScope.() -> Unit = {},
    color: Color = Color.Unspecified,
    colors: TopAppBarColors = TopAppBarDefaults.largeTopAppBarColors(),
) {
    TopAppBar(
        title = title.orEmpty(),
        modifier = modifier,
        color = if (color != Color.Unspecified) color else MiuixTheme.colorScheme.surface,
        navigationIcon = navigationIcon ?: {},
        actions = actions,
        scrollBehavior = scrollBehavior,
        bottomContent = titleContent,
    )
}