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
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.xiaoyv.bangumi.shared.component.SideEffectForStatusBar
import com.xiaoyv.bangumi.shared.core.types.settings.SettingIndication
import com.xiaoyv.bangumi.shared.core.types.settings.SettingTheme
import com.xiaoyv.bangumi.shared.data.manager.shared.currentSettings
import org.koin.compose.KoinApplicationPreview
import org.koin.dsl.ModuleDeclaration
import org.koin.dsl.module
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.theme.ColorSchemeMode
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.theme.ThemeController

import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.extended.Back
import top.yukonga.miuix.kmp.icon.extended.Close
import top.yukonga.miuix.kmp.icon.extended.Delete
import top.yukonga.miuix.kmp.icon.extended.Filter
import top.yukonga.miuix.kmp.icon.extended.GridView
import top.yukonga.miuix.kmp.icon.extended.Info
import top.yukonga.miuix.kmp.icon.extended.ListView
import top.yukonga.miuix.kmp.icon.extended.More
import top.yukonga.miuix.kmp.icon.extended.Refresh
import top.yukonga.miuix.kmp.icon.extended.Search
import top.yukonga.miuix.kmp.icon.extended.Settings
import top.yukonga.miuix.kmp.icon.extended.Share

val BgmIcons = Icons.Rounded
val BgmDefaultIcons = Icons.Default
val BgmIconsMirrored = Icons.AutoMirrored.Rounded

object BgmMiuixIcons {
    val Search get() = MiuixIcons.Search
    val Delete get() = MiuixIcons.Delete
    val Close get() = MiuixIcons.Close
    val Back get() = MiuixIcons.Back
    val More get() = MiuixIcons.More
    val Info get() = MiuixIcons.Info
    val Filter get() = MiuixIcons.Filter
    val Settings get() = MiuixIcons.Settings
    val Refresh get() = MiuixIcons.Refresh
    val Share get() = MiuixIcons.Share
    val GridView get() = MiuixIcons.GridView
    val ListView get() = MiuixIcons.ListView
}

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
    val monet = settings.ui.monetTheme
    val colorSchemeMode = if (monet) {
        when (settings.ui.theme) {
            SettingTheme.LIGHT -> ColorSchemeMode.MonetLight
            SettingTheme.DARK -> ColorSchemeMode.MonetDark
            else -> ColorSchemeMode.MonetSystem
        }
    } else {
        when (settings.ui.theme) {
            SettingTheme.LIGHT -> ColorSchemeMode.Light
            SettingTheme.DARK -> ColorSchemeMode.Dark
            else -> ColorSchemeMode.System
        }
    }
    val seedColor = androidx.compose.ui.graphics.Color(settings.ui.themeColor.toInt())
    val controller = remember(colorSchemeMode, darkTheme, settings.ui.themeColor, monet) {
        ThemeController(
            colorSchemeMode = colorSchemeMode,
            keyColor = if (monet) seedColor else null,
            isDark = darkTheme,
        )
    }

    MiuixTheme(controller = controller) {
        val miuix = MiuixTheme.colorScheme
        val materialScheme = (if (darkTheme) darkScheme else lightScheme).copy(
            primary = miuix.primary,
            onPrimary = miuix.onPrimary,
            primaryContainer = miuix.primaryContainer,
            onPrimaryContainer = miuix.onPrimaryContainer,
            secondary = miuix.secondary,
            onSecondary = miuix.onSecondary,
            secondaryContainer = miuix.secondaryContainer,
            onSecondaryContainer = miuix.onSecondaryContainer,
            background = miuix.background,
            onBackground = miuix.onBackground,
            surface = miuix.surface,
            onSurface = miuix.onSurface,
            surfaceVariant = miuix.surfaceVariant,
            onSurfaceVariant = miuix.onSurfaceVariantSummary,
            outline = miuix.outline,
            error = miuix.error,
            onError = miuix.onError,
            errorContainer = miuix.errorContainer,
            onErrorContainer = miuix.onErrorContainer,
            surfaceContainer = miuix.surfaceContainer,
            surfaceContainerHigh = miuix.surfaceContainerHigh,
            surfaceContainerHighest = miuix.surfaceContainerHighest,
        )

        MaterialTheme(
            colorScheme = materialScheme,
            typography = rememberAppTypography(),
            content = {
                val rippleIndication = LocalIndication.current

                CompositionLocalProvider(
                    LocalMinimumInteractiveComponentSize provides 20.dp,
                    LocalContentColor provides materialScheme.onSurface,
                    LocalIndication provides when (settings.ui.indication) {
                        SettingIndication.RIPPLE -> rippleIndication
                        SettingIndication.FADE -> DefaultIndication
                        else -> NoIndication
                    }
                ) {
                    Scaffold(
                        modifier = modifier,
                        contentWindowInsets = WindowInsets(0),
                    ) {
                        Box(modifier = Modifier.fillMaxSize(), content = content)
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
