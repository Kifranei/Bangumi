package com.xiaoyv.bangumi.features.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveComponentOverrideApi
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldOverride
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldOverrideScope
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kyant.backdrop.backdrops.layerBackdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.xiaoyv.bangumi.shared.core.types.settings.SettingBottomBarAppearance
import com.xiaoyv.bangumi.shared.ui.component.navigation.FloatingBottomBar
import com.xiaoyv.bangumi.shared.ui.component.navigation.FloatingBottomBarItem
import com.xiaoyv.bangumi.shared.ui.component.navigation.FloatingBottomBarMode
import com.xiaoyv.bangumi.shared.ui.component.navigation.LocalFloatingBottomBarContentColor
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.component.tab.ComposeVectorTab
import com.xiaoyv.bangumi.shared.ui.kts.isWideScreen
import kotlinx.collections.immutable.PersistentList
import org.jetbrains.compose.resources.stringResource
import top.yukonga.miuix.kmp.basic.Badge
import top.yukonga.miuix.kmp.basic.BadgedBox
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.NavigationBar
import top.yukonga.miuix.kmp.basic.NavigationBarItem
import top.yukonga.miuix.kmp.basic.NavigationRail
import top.yukonga.miuix.kmp.basic.NavigationRailItem
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme

@ExperimentalMaterial3AdaptiveComponentOverrideApi
object DefaultNavigationSuiteScaffoldOverride : NavigationSuiteScaffoldOverride {
    @Composable
    override fun NavigationSuiteScaffoldOverrideScope.NavigationSuiteScaffold() {
        content()
    }
}

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
    val wide = isWideScreen
    if (wide) {
        Scaffold(modifier = modifier) {
            Row(modifier = Modifier.fillMaxSize()) {
                NavigationRail(modifier = Modifier.fillMaxHeight()) {
                    tabs.forEach { item ->
                        val label = stringResource(item.second.label)
                        NavigationRailItem(
                            selected = selected == item.first,
                            onClick = { onTabClick(item.first) },
                            icon = item.second.icon,
                            label = label,
                            badge = profileBadge(item.first, unreadBadge),
                        )
                    }
                }
                Box(modifier = Modifier.weight(1f).fillMaxSize()) {
                    content()
                }
            }
        }
        return
    }

    if (appearance == SettingBottomBarAppearance.LIQUID_GLASS) {
        val backdrop = rememberLayerBackdrop()
        var bottomBarHeightPx by remember { mutableIntStateOf(0) }
        val bottomBarHeight = with(LocalDensity.current) { bottomBarHeightPx.toDp() }
        Box(modifier = modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .layerBackdrop(backdrop),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = bottomBarHeight + 4.dp),
                ) {
                    content()
                }
            }

            FloatingBottomBar(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .onSizeChanged { bottomBarHeightPx = it.height }
                    .padding(horizontal = 16.dp)
                    .navigationBarsPadding(),
                selectedIndex = { tabs.indexOfFirst { it.first == selected }.coerceAtLeast(0) },
                onSelected = { onTabClick(tabs[it].first) },
                backdrop = backdrop,
                tabsCount = tabs.size,
                mode = FloatingBottomBarMode.LiquidGlass,
            ) {
                tabs.forEach { item ->
                    val selectedTab = selected == item.first
                    FloatingBottomBarItem(
                        onClick = {
                            if (!selectedTab) onTabClick(item.first)
                        },
                    ) {
                        HalcyonTabContent(item, unreadBadge)
                    }
                }
            }
        }
        return
    }

    Scaffold(
        modifier = modifier,
        bottomBar = {
            NavigationBar(
                showDivider = false,
            ) {
                tabs.forEach { item ->
                    val label = stringResource(item.second.label)
                    NavigationBarItem(
                        selected = selected == item.first,
                        onClick = { onTabClick(item.first) },
                        icon = item.second.icon,
                        label = label,
                        badge = profileBadge(item.first, unreadBadge),
                    )
                }
            }
        },
    ) { contentPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = contentPadding.calculateBottomPadding() + 4.dp),
        ) {
            content()
        }
    }
}

@Composable
private fun HalcyonTabContent(
    item: Pair<Screen, ComposeVectorTab<String>>,
    unreadBadge: Int,
) {
    val label = stringResource(item.second.label)
    val tint = LocalFloatingBottomBarContentColor.current
        .takeIf { it != Color.Unspecified }
        ?: MiuixTheme.colorScheme.onSurface
    val showBadge = unreadBadge > 0 && item.first == Screen.Profile
    if (showBadge) {
        BadgedBox(badge = { Badge { Text(unreadBadge.toString()) } }) {
            Icon(
                imageVector = item.second.icon,
                contentDescription = label,
                tint = tint,
                modifier = Modifier.size(26.dp),
            )
        }
    } else {
        Icon(
            imageVector = item.second.icon,
            contentDescription = label,
            tint = tint,
            modifier = Modifier.size(26.dp),
        )
    }
    Text(
        text = label,
        fontSize = 11.sp,
        color = tint,
    )
}

@Composable
private fun profileBadge(screen: Screen, unreadBadge: Int): (@Composable () -> Unit)? {
    if (unreadBadge <= 0 || screen != Screen.Profile) return null
    return {
        Badge { Text(unreadBadge.toString()) }
    }
}
