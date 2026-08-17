package com.xiaoyv.bangumi.features.settings.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import com.xiaoyv.bangumi.shared.ui.component.layout.BgmScaffold
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.settings_appearance
import com.xiaoyv.bangumi.core_resource.resources.settings_cache_state
import com.xiaoyv.bangumi.core_resource.resources.settings_cache_state_desc
import com.xiaoyv.bangumi.core_resource.resources.settings_content
import com.xiaoyv.bangumi.core_resource.resources.settings_deeplink
import com.xiaoyv.bangumi.core_resource.resources.settings_indication
import com.xiaoyv.bangumi.core_resource.resources.settings_navigation_animation
import com.xiaoyv.bangumi.core_resource.resources.settings_performance
import com.xiaoyv.bangumi.core_resource.resources.settings_theme
import com.xiaoyv.bangumi.core_resource.resources.settings_theme_color
import com.xiaoyv.bangumi.core_resource.resources.settings_theme_color_reset
import com.xiaoyv.bangumi.core_resource.resources.settings_monet_theme
import com.xiaoyv.bangumi.core_resource.resources.settings_monet_theme_desc
import com.xiaoyv.bangumi.core_resource.resources.settings_home_shortcuts
import com.xiaoyv.bangumi.core_resource.resources.settings_time_machine_grid_limit
import com.xiaoyv.bangumi.core_resource.resources.settings_ui
import com.xiaoyv.bangumi.core_resource.resources.global_confirm
import com.xiaoyv.bangumi.core_resource.resources.global_cancel
import com.xiaoyv.bangumi.features.settings.ui.business.SettingsUiEvent
import com.xiaoyv.bangumi.features.settings.ui.business.SettingsUiState
import com.xiaoyv.bangumi.features.settings.ui.business.SettingsUiViewModel
import com.xiaoyv.bangumi.shared.System
import com.xiaoyv.bangumi.shared.core.mvi.BaseState
import com.xiaoyv.bangumi.shared.core.types.settings.SettingIndication
import com.xiaoyv.bangumi.shared.core.types.settings.SettingNavigationAnimation
import com.xiaoyv.bangumi.shared.core.types.settings.SettingTheme
import com.xiaoyv.bangumi.shared.data.manager.shared.currentSettings
import com.xiaoyv.bangumi.shared.ui.component.bar.BgmLargeTopAppBar
import com.xiaoyv.bangumi.shared.ui.component.bar.rememberBgmScrollBehavior
import com.xiaoyv.bangumi.shared.ui.component.layout.state.StateLayout
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.component.settings.SettingContainer
import com.xiaoyv.bangumi.shared.ui.component.settings.SettingItem
import com.xiaoyv.bangumi.shared.ui.component.settings.SettingOptionItem
import com.xiaoyv.bangumi.shared.ui.component.settings.SettingSwitchItem
import com.xiaoyv.bangumi.shared.ui.composition.TabTokens
import com.xiaoyv.bangumi.shared.ui.composition.TabTokens.settingIndicationItems
import com.xiaoyv.bangumi.shared.ui.composition.TabTokens.settingNavigationAnimationItems
import com.xiaoyv.bangumi.shared.ui.kts.collectBaseSideEffect
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.orbitmvi.orbit.compose.collectAsState
import top.yukonga.miuix.kmp.basic.ColorPicker
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.overlay.OverlayDialog

@Composable
fun SettingsUiRoute(
    viewModel: SettingsUiViewModel,
    onNavUp: () -> Unit,
    onNavScreen: (Screen) -> Unit,
) {
    val baseState by viewModel.collectAsState()

    viewModel.collectBaseSideEffect {

    }

    SettingsUiScreen(
        baseState = baseState,
        onActionEvent = viewModel::onEvent,
        onUiEvent = {
            when (it) {
                is SettingsUiEvent.UI.OnNavUp -> onNavUp()
                is SettingsUiEvent.UI.OnNavScreen -> onNavScreen(it.screen)
            }
        },
    )
}

@Composable
private fun SettingsUiScreen(
    baseState: BaseState<SettingsUiState>,
    onUiEvent: (SettingsUiEvent.UI) -> Unit,
    onActionEvent: (SettingsUiEvent.Action) -> Unit,
) {
    val scrollBehavior = rememberBgmScrollBehavior()

    BgmScaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            BgmLargeTopAppBar(
                title = stringResource(Res.string.settings_ui),
                scrollBehavior = scrollBehavior,
                onNavigationClick = { onUiEvent(SettingsUiEvent.UI.OnNavUp) }
            )
        }
    ) {
        StateLayout(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(it),
            baseState = baseState,
        ) { state ->
            SettingsUiScreenContent(state, onUiEvent, onActionEvent)
        }
    }
}


@Composable
private fun SettingsUiScreenContent(
    state: SettingsUiState,
    onUiEvent: (SettingsUiEvent.UI) -> Unit,
    onActionEvent: (SettingsUiEvent.Action) -> Unit,
) {
    val settings = currentSettings()

    Column(modifier = Modifier.padding(vertical = 24.dp)) {
        SettingContainer(title = stringResource(Res.string.settings_appearance)) {
            SettingOptionItem(
                title = stringResource(Res.string.settings_theme),
                value = stringResource(SettingTheme.string(settings.ui.theme)),
                items = TabTokens.settingThemeItems,
                onClick = {
                    onActionEvent(SettingsUiEvent.Action.OnUpdate(settings.ui.copy(theme = it)))
                }
            )

            SettingSwitchItem(
                title = stringResource(Res.string.settings_monet_theme),
                desc = stringResource(Res.string.settings_monet_theme_desc),
                value = settings.ui.monetTheme,
                onValueChange = {
                    onActionEvent(SettingsUiEvent.Action.OnUpdate(settings.ui.copy(monetTheme = it)))
                },
            )

            if (settings.ui.monetTheme) {
                var showColorPicker by remember { mutableStateOf(false) }
                var draftColor by remember { mutableStateOf(Color(settings.ui.themeColor.toInt())) }
                OverlayDialog(
                    show = showColorPicker,
                    title = stringResource(Res.string.settings_theme_color),
                    onDismissRequest = { showColorPicker = false },
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        ColorPicker(
                            color = draftColor,
                            onColorChanged = { draftColor = it },
                            modifier = Modifier.fillMaxWidth(),
                        )
                        Spacer(Modifier.height(12.dp))
                        TextButton(
                            text = stringResource(Res.string.global_confirm),
                            onClick = {
                                onActionEvent(
                                    SettingsUiEvent.Action.OnUpdate(
                                        settings.ui.copy(themeColor = draftColor.toArgb().toLong() and 0xFFFFFFFFL)
                                    )
                                )
                                showColorPicker = false
                            },
                            modifier = Modifier.fillMaxWidth(),
                        )
                        Spacer(Modifier.height(8.dp))
                        TextButton(
                            text = stringResource(Res.string.settings_theme_color_reset),
                            onClick = {
                                draftColor = Color(0xFFB44C71)
                                onActionEvent(
                                    SettingsUiEvent.Action.OnUpdate(settings.ui.copy(themeColor = 0xFFB44C71))
                                )
                                showColorPicker = false
                            },
                            modifier = Modifier.fillMaxWidth(),
                        )
                        Spacer(Modifier.height(8.dp))
                        TextButton(
                            text = stringResource(Res.string.global_cancel),
                            onClick = { showColorPicker = false },
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
                SettingItem(
                    title = stringResource(Res.string.settings_theme_color),
                    trailingContent = {
                        Box(
                            modifier = Modifier
                                .size(22.dp)
                                .clip(CircleShape)
                                .background(Color(settings.ui.themeColor.toInt()))
                        )
                    },
                    onClick = {
                        draftColor = Color(settings.ui.themeColor.toInt())
                        showColorPicker = true
                    }
                )
            }

            SettingOptionItem(
                title = stringResource(Res.string.settings_indication),
                items = settingIndicationItems,
                value = stringResource(SettingIndication.string(settings.ui.indication)),
                onClick = {
                    onActionEvent(SettingsUiEvent.Action.OnUpdate(settings.ui.copy(indication = it)))
                }
            )
        }

        SettingContainer(title = stringResource(Res.string.settings_performance)) {
            SettingOptionItem(
                title = stringResource(Res.string.settings_time_machine_grid_limit),
                value = settings.ui.timeMachineGridLimit.toString(),
                items = TabTokens.settingTimeMachineGridLimitItems,
                onClick = {
                    onActionEvent(SettingsUiEvent.Action.OnUpdate(settings.ui.copy(timeMachineGridLimit = it)))
                }
            )

            SettingOptionItem(
                title = stringResource(Res.string.settings_navigation_animation),
                value = stringResource(SettingNavigationAnimation.string(settings.ui.navigationAnimation)),
                items = settingNavigationAnimationItems,
                onClick = {
                    onActionEvent(SettingsUiEvent.Action.OnUpdate(settings.ui.copy(navigationAnimation = it)))
                }
            )

            SettingSwitchItem(
                title = stringResource(Res.string.settings_cache_state),
                desc = stringResource(Res.string.settings_cache_state_desc),
                value = settings.ui.cacheState,
                onValueChange = {
                    onActionEvent(SettingsUiEvent.Action.OnUpdate(settings.ui.copy(cacheState = it)))
                },
            )
        }

        SettingContainer(title = stringResource(Res.string.settings_home_shortcuts)) {
            val hidden = settings.ui.hiddenHomeShortcuts
            TabTokens.mainHomeActions.forEach { tab ->
                val type = tab.type.toString()
                SettingSwitchItem(
                    title = stringResource(tab.label),
                    value = type !in hidden,
                    onValueChange = { checked ->
                        val next = if (checked) hidden - type else hidden + type
                        onActionEvent(
                            SettingsUiEvent.Action.OnUpdate(
                                settings.ui.copy(hiddenHomeShortcuts = next.distinct())
                            )
                        )
                    },
                )
            }
        }


        SettingContainer(title = stringResource(Res.string.settings_content)) {
            SettingItem(
                title = stringResource(Res.string.settings_deeplink),
                onClick = {
                    System.launchDeeplinkSettings()
                }
            )
        }
    }
}

