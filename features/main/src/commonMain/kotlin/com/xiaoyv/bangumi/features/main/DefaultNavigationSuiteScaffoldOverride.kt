package com.xiaoyv.bangumi.features.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kyant.backdrop.backdrops.layerBackdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.kyant.backdrop.catalog.components.LiquidBottomTab
import com.kyant.backdrop.catalog.components.LiquidBottomTabs
import com.xiaoyv.bangumi.shared.core.types.settings.SettingBottomBarAppearance
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.component.navigation.FloatingBottomBar
import com.xiaoyv.bangumi.shared.ui.component.navigation.FloatingBottomBarItem
import com.xiaoyv.bangumi.shared.ui.component.tab.ComposeVectorTab
import com.xiaoyv.bangumi.shared.ui.kts.isWideScreen
import com.xiaoyv.bangumi.shared.ui.theme.isMiuixUi
import kotlinx.collections.immutable.PersistentList
import org.jetbrains.compose.resources.stringResource
import top.yukonga.miuix.kmp.basic.Badge as MiuixBadge
import top.yukonga.miuix.kmp.basic.Icon as MiuixIcon
import top.yukonga.miuix.kmp.basic.NavigationBar as MiuixNavigationBar
import top.yukonga.miuix.kmp.basic.NavigationBarItem as MiuixNavigationBarItem
import top.yukonga.miuix.kmp.basic.NavigationRail as MiuixNavigationRail
import top.yukonga.miuix.kmp.basic.NavigationRailItem as MiuixNavigationRailItem
import top.yukonga.miuix.kmp.basic.Text as MiuixText
import top.yukonga.miuix.kmp.theme.MiuixTheme

/** Main navigation with independent UI-style and bar-appearance choices. */
@Composable
fun BgmNavigationSuiteScaffold(
    @SettingBottomBarAppearance appearance: Int,
    tabs: PersistentList<Pair<Screen, ComposeVectorTab<String>>>,
    selected: Screen?,
    unreadBadge: Int,
    onTabClick: (Screen) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit = {},
) {
    val miuix = isMiuixUi()

    if (isWideScreen) {
        if (miuix) {
            MiuixWideNavigation(modifier, tabs, selected, unreadBadge, onTabClick, content)
        } else {
            MaterialNavigation(modifier, tabs, selected, unreadBadge, onTabClick, content)
        }
        return
    }

    when {
        appearance == SettingBottomBarAppearance.LIQUID_GLASS && miuix -> MiuixLiquidNavigation(
            modifier = modifier,
            tabs = tabs,
            selected = selected,
            unreadBadge = unreadBadge,
            onTabClick = onTabClick,
            content = content,
        )

        appearance == SettingBottomBarAppearance.LIQUID_GLASS -> LiquidNavigation(
            modifier = modifier,
            tabs = tabs,
            selected = selected,
            unreadBadge = unreadBadge,
            onTabClick = onTabClick,
            content = content,
        )

        miuix -> MiuixNormalNavigation(
            modifier = modifier,
            tabs = tabs,
            selected = selected,
            unreadBadge = unreadBadge,
            onTabClick = onTabClick,
            content = content,
        )

        else -> MaterialNavigation(modifier, tabs, selected, unreadBadge, onTabClick, content)
    }
}

@Composable
private fun MaterialNavigation(
    modifier: Modifier,
    tabs: PersistentList<Pair<Screen, ComposeVectorTab<String>>>,
    selected: Screen?,
    unreadBadge: Int,
    onTabClick: (Screen) -> Unit,
    content: @Composable () -> Unit,
) {
    val indicatorColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
    val itemColors = NavigationSuiteDefaults.itemColors(
        navigationBarItemColors = NavigationBarItemDefaults.colors(indicatorColor = indicatorColor),
    )

    NavigationSuiteScaffold(
        modifier = modifier,
        navigationSuiteItems = {
            tabs.forEach { item ->
                val isSelected = selected == item.first
                item(
                    label = {
                        Text(
                            text = stringResource(item.second.label),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    },
                    icon = {
                        Icon(
                            imageVector = item.second.icon,
                            contentDescription = stringResource(item.second.label),
                        )
                    },
                    selected = isSelected,
                    colors = itemColors,
                    badge = materialProfileBadge(item.first, unreadBadge),
                    onClick = { if (!isSelected) onTabClick(item.first) },
                )
            }
        },
        content = content,
    )
}

@Composable
private fun MiuixWideNavigation(
    modifier: Modifier,
    tabs: PersistentList<Pair<Screen, ComposeVectorTab<String>>>,
    selected: Screen?,
    unreadBadge: Int,
    onTabClick: (Screen) -> Unit,
    content: @Composable () -> Unit,
) {
    Row(modifier = modifier.fillMaxSize()) {
        MiuixNavigationRail(modifier = Modifier.fillMaxHeight()) {
            tabs.forEach { item ->
                val isSelected = selected == item.first
                MiuixNavigationRailItem(
                    selected = isSelected,
                    onClick = { if (!isSelected) onTabClick(item.first) },
                    icon = item.second.icon,
                    label = stringResource(item.second.label),
                    badge = miuixProfileBadge(item.first, unreadBadge),
                )
            }
        }
        Box(modifier = Modifier.weight(1f).fillMaxSize()) { content() }
    }
}

@Composable
private fun MiuixNormalNavigation(
    modifier: Modifier,
    tabs: PersistentList<Pair<Screen, ComposeVectorTab<String>>>,
    selected: Screen?,
    unreadBadge: Int,
    onTabClick: (Screen) -> Unit,
    content: @Composable () -> Unit,
) {
    Column(modifier = modifier.fillMaxSize()) {
        Box(modifier = Modifier.weight(1f).fillMaxSize()) { content() }
        MiuixNavigationBar(showDivider = false) {
            tabs.forEach { item ->
                val isSelected = selected == item.first
                MiuixNavigationBarItem(
                    selected = isSelected,
                    onClick = { if (!isSelected) onTabClick(item.first) },
                    icon = item.second.icon,
                    label = stringResource(item.second.label),
                    badge = miuixProfileBadge(item.first, unreadBadge),
                )
            }
        }
    }
}

@Composable
private fun LiquidNavigation(
    modifier: Modifier,
    tabs: PersistentList<Pair<Screen, ComposeVectorTab<String>>>,
    selected: Screen?,
    unreadBadge: Int,
    onTabClick: (Screen) -> Unit,
    content: @Composable () -> Unit,
) {
    val backdrop = rememberLayerBackdrop()
    var barHeightPx by remember { mutableIntStateOf(0) }
    val barHeight = with(LocalDensity.current) { barHeightPx.toDp() }

    Box(modifier = modifier.fillMaxSize()) {
        Box(modifier = Modifier.matchParentSize().layerBackdrop(backdrop)) {
            Box(modifier = Modifier.fillMaxSize().padding(bottom = barHeight)) { content() }
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .onSizeChanged { barHeightPx = it.height },
        ) {
            LiquidBottomTabs(
                modifier = Modifier.padding(horizontal = 24.dp),
                selectedTabIndex = { tabs.indexOfFirst { it.first == selected }.coerceAtLeast(0) },
                onTabSelected = { onTabClick(tabs[it].first) },
                backdrop = backdrop,
                tabsCount = tabs.size,
            ) {
                tabs.forEachIndexed { index, item ->
                    LiquidBottomTab(onClick = { onTabClick(tabs[index].first) }) {
                        LiquidTabContent(item, unreadBadge)
                    }
                }
            }
            Box(Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars))
        }
    }
}

@Composable
private fun LiquidTabContent(
    item: Pair<Screen, ComposeVectorTab<String>>,
    unreadBadge: Int,
) {
    val label = stringResource(item.second.label)
    val showBadge = unreadBadge > 0 && item.first == Screen.Profile

    if (showBadge) {
        BadgedBox(badge = { Badge { Text(unreadBadge.toString()) } }) {
            Icon(item.second.icon, label, Modifier.size(26.dp))
        }
    } else {
        Icon(item.second.icon, label, Modifier.size(26.dp))
    }
    Text(text = label, style = MaterialTheme.typography.bodySmall, maxLines = 1)
}

@Composable
private fun MiuixLiquidNavigation(
    modifier: Modifier,
    tabs: PersistentList<Pair<Screen, ComposeVectorTab<String>>>,
    selected: Screen?,
    unreadBadge: Int,
    onTabClick: (Screen) -> Unit,
    content: @Composable () -> Unit,
) {
    val backdrop = rememberLayerBackdrop()
    var barHeightPx by remember { mutableIntStateOf(0) }
    val barHeight = with(LocalDensity.current) { barHeightPx.toDp() }
    Box(modifier = modifier.fillMaxSize()) {
        Box(modifier = Modifier.matchParentSize().layerBackdrop(backdrop)) {
            Box(modifier = Modifier.fillMaxSize().padding(bottom = barHeight + 4.dp)) { content() }
        }
        FloatingBottomBar(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .onSizeChanged { barHeightPx = it.height }
                .padding(horizontal = 16.dp)
                .navigationBarsPadding(),
            selectedIndex = { tabs.indexOfFirst { it.first == selected }.coerceAtLeast(0) },
            onSelected = { onTabClick(tabs[it].first) },
            backdrop = backdrop,
            tabsCount = tabs.size,
        ) {
            tabs.forEach { item ->
                FloatingBottomBarItem(onClick = { onTabClick(item.first) }) {
                    val label = stringResource(item.second.label)
                    val showBadge = unreadBadge > 0 && item.first == Screen.Profile
                    val tint = com.xiaoyv.bangumi.shared.ui.component.navigation.LocalFloatingBottomBarContentColor.current
                        .takeIf { it != Color.Unspecified }
                        ?: MiuixTheme.colorScheme.onSurface
                    if (showBadge) {
                        top.yukonga.miuix.kmp.basic.BadgedBox(
                            badge = { MiuixBadge { MiuixText(unreadBadge.toString()) } },
                        ) {
                            MiuixIcon(item.second.icon, label, Modifier.size(26.dp), tint)
                        }
                    } else {
                        MiuixIcon(item.second.icon, label, Modifier.size(26.dp), tint)
                    }
                    MiuixText(text = label, color = tint, fontSize = 11.sp, maxLines = 1)
                }
            }
        }
    }
}

private fun materialProfileBadge(screen: Screen, unreadBadge: Int): (@Composable () -> Unit)? {
    if (unreadBadge <= 0 || screen != Screen.Profile) return null
    return { Badge { Text(unreadBadge.toString()) } }
}

private fun miuixProfileBadge(screen: Screen, unreadBadge: Int): (@Composable () -> Unit)? {
    if (unreadBadge <= 0 || screen != Screen.Profile) return null
    return { MiuixBadge { MiuixText(unreadBadge.toString()) } }
}
