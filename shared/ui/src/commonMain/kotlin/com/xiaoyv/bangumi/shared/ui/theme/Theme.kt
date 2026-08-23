package com.xiaoyv.bangumi.shared.ui.theme

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.xiaoyv.bangumi.shared.component.SideEffectForStatusBar
import com.xiaoyv.bangumi.shared.core.types.settings.SettingIndication
import com.xiaoyv.bangumi.shared.core.types.settings.SettingTheme
import com.xiaoyv.bangumi.shared.core.types.settings.SettingUiStyle
import com.xiaoyv.bangumi.shared.data.manager.shared.currentSettings
import org.koin.compose.KoinApplicationPreview
import org.koin.dsl.ModuleDeclaration
import org.koin.dsl.module
import top.yukonga.miuix.kmp.theme.ColorSchemeMode
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.theme.ThemeController
import top.yukonga.miuix.kmp.basic.Scaffold as MiuixScaffold

val BgmIcons = Icons.Rounded
val BgmDefaultIcons = Icons.Default
val BgmIconsMirrored = Icons.AutoMirrored.Rounded

val LocalBgmUiStyle = staticCompositionLocalOf { SettingUiStyle.MATERIAL3 }

@Composable
fun isMiuixUi(): Boolean = LocalBgmUiStyle.current == SettingUiStyle.MIUIX

private val lightScheme = lightColorScheme(
    primary = primaryLight,
    onPrimary = onPrimaryLight,
    primaryContainer = primaryContainerLight,
    onPrimaryContainer = onPrimaryContainerLight,
    secondary = secondaryLight,
    onSecondary = onSecondaryLight,
    secondaryContainer = secondaryContainerLight,
    onSecondaryContainer = onSecondaryContainerLight,
    tertiary = tertiaryLight,
    onTertiary = onTertiaryLight,
    tertiaryContainer = tertiaryContainerLight,
    onTertiaryContainer = onTertiaryContainerLight,
    error = errorLight,
    onError = onErrorLight,
    errorContainer = errorContainerLight,
    onErrorContainer = onErrorContainerLight,
    background = backgroundLight,
    onBackground = onBackgroundLight,
    surface = surfaceLight,
    onSurface = onSurfaceLight,
    surfaceVariant = surfaceVariantLight,
    onSurfaceVariant = onSurfaceVariantLight,
    outline = outlineLight,
    outlineVariant = outlineVariantLight,
    scrim = scrimLight,
    inverseSurface = inverseSurfaceLight,
    inverseOnSurface = inverseOnSurfaceLight,
    inversePrimary = inversePrimaryLight,
    surfaceDim = surfaceDimLight,
    surfaceBright = surfaceBrightLight,
    surfaceContainerLowest = surfaceContainerLowestLight,
    surfaceContainerLow = surfaceContainerLowLight,
    surfaceContainer = surfaceContainerLight,
    surfaceContainerHigh = surfaceContainerHighLight,
    surfaceContainerHighest = surfaceContainerHighestLight,
)

private val darkScheme = darkColorScheme(
    primary = primaryDark,
    onPrimary = onPrimaryDark,
    primaryContainer = primaryContainerDark,
    onPrimaryContainer = onPrimaryContainerDark,
    secondary = secondaryDark,
    onSecondary = onSecondaryDark,
    secondaryContainer = secondaryContainerDark,
    onSecondaryContainer = onSecondaryContainerDark,
    tertiary = tertiaryDark,
    onTertiary = onTertiaryDark,
    tertiaryContainer = tertiaryContainerDark,
    onTertiaryContainer = onTertiaryContainerDark,
    error = errorDark,
    onError = onErrorDark,
    errorContainer = errorContainerDark,
    onErrorContainer = onErrorContainerDark,
    background = backgroundDark,
    onBackground = onBackgroundDark,
    surface = surfaceDark,
    onSurface = onSurfaceDark,
    surfaceVariant = surfaceVariantDark,
    onSurfaceVariant = onSurfaceVariantDark,
    outline = outlineDark,
    outlineVariant = outlineVariantDark,
    scrim = scrimDark,
    inverseSurface = inverseSurfaceDark,
    inverseOnSurface = inverseOnSurfaceDark,
    inversePrimary = inversePrimaryDark,
    surfaceDim = surfaceDimDark,
    surfaceBright = surfaceBrightDark,
    surfaceContainerLowest = surfaceContainerLowestDark,
    surfaceContainerLow = surfaceContainerLowDark,
    surfaceContainer = surfaceContainerDark,
    surfaceContainerHigh = surfaceContainerHighDark,
    surfaceContainerHighest = surfaceContainerHighestDark,
)

@Composable
fun currentInDarkTheme(): Boolean {
    return when (currentSettings().ui.theme) {
        SettingTheme.SYSTEM -> isSystemInDarkTheme()
        SettingTheme.DARK -> true
        SettingTheme.LIGHT -> false
        else -> isSystemInDarkTheme()
    }
}


@Composable
fun BgmAppTheme(
    darkTheme: Boolean = currentInDarkTheme(),
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    SideEffectForStatusBar(darkTheme)

    val settings = currentSettings()
    val uiStyle = settings.ui.style
    val baseMaterialScheme = if (darkTheme) darkScheme else lightScheme
    val miuixController = remember(darkTheme, settings.ui.monetTheme, settings.ui.themeColor) {
        ThemeController(
            colorSchemeMode = when {
                settings.ui.monetTheme && darkTheme -> ColorSchemeMode.MonetDark
                settings.ui.monetTheme -> ColorSchemeMode.MonetLight
                darkTheme -> ColorSchemeMode.Dark
                else -> ColorSchemeMode.Light
            },
            keyColor = settings.ui.themeColor.takeIf { settings.ui.monetTheme }?.let { Color(it.toInt()) },
            isDark = darkTheme,
        )
    }

    MiuixTheme(controller = miuixController) {
        val miuixScheme = MiuixTheme.colorScheme
        val materialScheme = if (uiStyle == SettingUiStyle.MIUIX || settings.ui.monetTheme) {
            baseMaterialScheme.copy(
                primary = miuixScheme.primary,
                onPrimary = miuixScheme.onPrimary,
                primaryContainer = miuixScheme.primaryContainer,
                onPrimaryContainer = miuixScheme.onPrimaryContainer,
                secondary = miuixScheme.secondary,
                onSecondary = miuixScheme.onSecondary,
                secondaryContainer = miuixScheme.secondaryContainer,
                onSecondaryContainer = miuixScheme.onSecondaryContainer,
                background = miuixScheme.background,
                onBackground = miuixScheme.onBackground,
                surface = miuixScheme.surface,
                onSurface = miuixScheme.onSurface,
                surfaceVariant = miuixScheme.surfaceVariant,
                onSurfaceVariant = miuixScheme.onSurfaceVariantSummary,
                outline = miuixScheme.outline,
                error = miuixScheme.error,
                onError = miuixScheme.onError,
                errorContainer = miuixScheme.errorContainer,
                onErrorContainer = miuixScheme.onErrorContainer,
                surfaceContainer = miuixScheme.surfaceContainer,
                surfaceContainerHigh = miuixScheme.surfaceContainerHigh,
                surfaceContainerHighest = miuixScheme.surfaceContainerHighest,
            )
        } else {
            baseMaterialScheme
        }

        MaterialTheme(
            colorScheme = materialScheme,
            typography = rememberAppTypography(),
            content = {
                val rippleIndication = LocalIndication.current
                val contentMargins = rememberContentMargins()

                CompositionLocalProvider(
                    LocalBgmUiStyle provides uiStyle,
                    LocalContentMargins provides contentMargins,
                    LocalMinimumInteractiveComponentSize provides 20.dp,
                    LocalContentColor provides materialScheme.onSurface,
                    LocalIndication provides when (settings.ui.indication) {
                        SettingIndication.RIPPLE -> rippleIndication
                        SettingIndication.FADE -> DefaultIndication
                        else -> NoIndication
                    }
                ) {
                    if (uiStyle == SettingUiStyle.MIUIX) {
                        MiuixScaffold(
                            modifier = modifier,
                            containerColor = Color.Transparent,
                            contentWindowInsets = WindowInsets(0, 0, 0, 0),
                        ) {
                            Box(content = content)
                        }
                    } else {
                        Box(
                            modifier = modifier,
                            content = content
                        )
                    }
                }
            }
        )
    }
}

@Composable
fun PreviewColumn(
    modifier: Modifier = Modifier,
    darkTheme: Boolean = isSystemInDarkTheme(),
    module: ModuleDeclaration = {},
    content: @Composable ColumnScope.() -> Unit,
) {
    KoinApplicationPreview(application = { module(moduleDeclaration = module) }) {
        BgmAppTheme(
            darkTheme,
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier, content = content)
        }
    }
}
