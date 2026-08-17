package com.xiaoyv.bangumi.features.main

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.rememberNavBackStack
import com.xiaoyv.bangumi.features.main.business.MainEvent
import com.xiaoyv.bangumi.features.main.business.MainState
import com.xiaoyv.bangumi.features.main.business.MainViewModel
import com.xiaoyv.bangumi.shared.data.manager.shared.LocalHideNavIcon
import com.xiaoyv.bangumi.shared.data.manager.shared.LocalSharedState
import com.xiaoyv.bangumi.shared.data.manager.shared.currentSettings
import com.xiaoyv.bangumi.shared.ui.component.navigation.PagerNavHost
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.component.navigation.current
import com.xiaoyv.bangumi.shared.ui.component.navigation.selectBottomTab
import com.xiaoyv.bangumi.shared.ui.component.navigation.stateConfiguration
import org.orbitmvi.orbit.compose.collectAsState

@Composable
fun MainRoute(
    viewModel: MainViewModel,
    onNavScreen: (Screen) -> Unit,
) {
    val state by viewModel.collectAsState()

    state.content {
        MainScreen(
            state = this,
            onUiEvent = {
                when (it) {
                    is MainEvent.UI.OnNavScreen -> onNavScreen(it.screen)
                }
            },
            onActionEvent = {

            }
        )
    }
}

@Composable
fun MainScreen(
    state: MainState,
    onUiEvent: (MainEvent.UI) -> Unit,
    onActionEvent: (MainEvent.Action) -> Unit,
) {
    val settings = currentSettings()
    val bottomTabs = state.rememberBottomTabs()
    val startDestination = remember(bottomTabs, state.defaultSelected) {
        bottomTabs.getOrNull(state.defaultSelected) ?: bottomTabs.first()
    }
    val backStack = rememberNavBackStack(stateConfiguration, startDestination.first)
    val appState = LocalSharedState.current
    val unreadCnt = appState.unreadNotification + appState.unreadMessage

    BgmNavigationSuiteScaffold(
        appearance = settings.homeTab.appearance,
        tabs = bottomTabs,
        selected = backStack.current,
        unreadBadge = unreadCnt,
        onTabClick = { backStack.selectBottomTab(it, startDestination.first) },
        content = {
            CompositionLocalProvider(LocalHideNavIcon provides true) {
                PagerNavHost(
                    modifier = Modifier.fillMaxSize(),
                    backStack = backStack,
                    onBack = {
                        backStack.selectBottomTab(startDestination.first, startDestination.first)
                    },
                )
            }
        },
    )
}
