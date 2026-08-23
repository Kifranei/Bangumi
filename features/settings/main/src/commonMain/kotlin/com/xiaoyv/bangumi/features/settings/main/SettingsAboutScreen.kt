package com.xiaoyv.bangumi.features.settings.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.app_name
import com.xiaoyv.bangumi.core_resource.resources.settings_about
import com.xiaoyv.bangumi.core_resource.resources.settings_about_app
import com.xiaoyv.bangumi.core_resource.resources.settings_about_author
import com.xiaoyv.bangumi.core_resource.resources.settings_about_license
import com.xiaoyv.bangumi.core_resource.resources.settings_about_oss
import com.xiaoyv.bangumi.core_resource.resources.settings_about_project
import com.xiaoyv.bangumi.core_resource.resources.settings_about_version
import com.xiaoyv.bangumi.features.settings.main.effect.BgEffectBackground
import com.xiaoyv.bangumi.shared.ui.component.action.LocalActionHandler
import com.xiaoyv.bangumi.shared.ui.component.bar.BgmTopAppBar
import com.xiaoyv.bangumi.shared.ui.component.bar.rememberBgmScrollBehavior
import com.xiaoyv.bangumi.shared.ui.component.layout.BgmScaffold
import org.jetbrains.compose.resources.stringResource
import top.yukonga.miuix.kmp.basic.BasicComponent
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.CardDefaults
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.blur.BlendColorEntry
import top.yukonga.miuix.kmp.blur.BlurBlendMode
import top.yukonga.miuix.kmp.blur.BlurColors
import top.yukonga.miuix.kmp.blur.BlurDefaults
import top.yukonga.miuix.kmp.blur.LayerBackdrop
import top.yukonga.miuix.kmp.blur.layerBackdrop
import top.yukonga.miuix.kmp.blur.rememberLayerBackdrop
import top.yukonga.miuix.kmp.blur.textureBlur
import top.yukonga.miuix.kmp.theme.MiuixTheme

private const val APP_VERSION = "2.0.1"

@Composable
fun SettingsAboutRoute(onNavUp: () -> Unit) {
    val scrollBehavior = rememberBgmScrollBehavior()
    val listState = rememberLazyListState()
    var logoHeightPx by remember { mutableIntStateOf(0) }
    val isDark = MiuixTheme.colorScheme.background.luminance() < 0.5f
    val scrollProgress by remember {
        derivedStateOf {
            if (logoHeightPx <= 0) {
                0f
            } else if (listState.firstVisibleItemIndex > 0) {
                1f
            } else {
                (listState.firstVisibleItemScrollOffset.toFloat() / logoHeightPx).coerceIn(0f, 1f)
            }
        }
    }

    BgmScaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = MiuixTheme.colorScheme.background,
        topBar = {
            BgmTopAppBar(
                title = stringResource(Res.string.settings_about),
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MiuixTheme.colorScheme.surface.copy(alpha = scrollProgress),
                    titleContentColor = MiuixTheme.colorScheme.onSurface.copy(alpha = scrollProgress),
                ),
                onNavigationClick = onNavUp,
            )
        },
    ) { padding ->
        AboutBlurContent(
            padding = padding,
            scrollProgress = scrollProgress,
            isDark = isDark,
            listState = listState,
            onLogoHeightChanged = { logoHeightPx = it },
        )
    }
}

@Composable
private fun AboutBlurContent(
    padding: PaddingValues,
    scrollProgress: Float,
    isDark: Boolean,
    listState: LazyListState,
    onLogoHeightChanged: (Int) -> Unit,
) {
    val backdrop = rememberLayerBackdrop()
    val actionHandler = LocalActionHandler.current
    val density = LocalDensity.current
    var logoHeightDp by remember { mutableStateOf(220.dp) }
    val logoLiftPx = with(density) { 96.dp.toPx() }
    val heroTopPadding = 148.dp
    val heroBottomPadding = 112.dp
    val titleBlend = remember(isDark) { aboutTitleBlendColors(isDark) }
    val cardBlend = remember(isDark) { aboutCardBlendColors(isDark) }

    BgEffectBackground(
        dynamicBackground = true,
        modifier = Modifier.fillMaxSize(),
        bgModifier = Modifier.layerBackdrop(backdrop),
        effectBackground = true,
        isDarkTheme = isDark,
        alpha = { (1f - scrollProgress).coerceIn(0f, 1f) },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .graphicsLayer {
                    alpha = (1f - scrollProgress * 1.35f).coerceIn(0f, 1f)
                    translationY = -logoLiftPx * scrollProgress
                }
                .padding(top = padding.calculateTopPadding() + heroTopPadding)
                .onSizeChanged { size -> with(density) { logoHeightDp = size.height.toDp() } },
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stringResource(Res.string.app_name),
                color = MiuixTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold,
                fontSize = 35.sp,
                modifier = Modifier
                    .padding(bottom = 5.dp)
                    .textureBlur(
                        backdrop = backdrop,
                        shape = RoundedCornerShape(16.dp),
                        blurRadius = 150f,
                        noiseCoefficient = BlurDefaults.NoiseCoefficient,
                        colors = BlurColors(blendColors = titleBlend),
                        contentBlendMode = BlendMode.DstIn,
                        enabled = true,
                    ),
                textAlign = TextAlign.Center,
            )
            Text(
                text = "v$APP_VERSION",
                color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                fontSize = 14.sp,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
            )
        }

        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(top = padding.calculateTopPadding()),
        ) {
            item {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(logoHeightDp + heroTopPadding + padding.calculateTopPadding() + heroBottomPadding)
                        .onSizeChanged { onLogoHeightChanged(it.height) },
                )
            }
            item {
                SmallTitle(text = stringResource(Res.string.settings_about_project))
                FrostedCard(
                    backdrop = backdrop,
                    isDark = isDark,
                    cardBlend = cardBlend,
                    scrollProgress = scrollProgress,
                ) {
                    BasicComponent(
                        title = stringResource(Res.string.settings_about_version),
                        summary = "v$APP_VERSION",
                        onClick = { actionHandler.openInBrowser("https://github.com/Kifranei/bangumi/releases") },
                    )
                    BasicComponent(
                        title = stringResource(Res.string.settings_about_license),
                        summary = "GPL-3.0",
                        onClick = { actionHandler.openInBrowser("https://www.gnu.org/licenses/gpl-3.0.html") },
                    )
                }
            }
            item {
                SmallTitle(text = stringResource(Res.string.settings_about_oss))
                FrostedCard(
                    backdrop = backdrop,
                    isDark = isDark,
                    cardBlend = cardBlend,
                    scrollProgress = scrollProgress,
                ) {
                    BasicComponent(
                        title = stringResource(Res.string.settings_about_app),
                        summary = "GitHub",
                        onClick = { actionHandler.openInBrowser("https://github.com/Kifranei/bangumi") },
                    )
                    BasicComponent(
                        title = "Miuix",
                        summary = "HyperOS Compose 组件库",
                        onClick = { actionHandler.openInBrowser("https://github.com/compose-miuix-ui/miuix") },
                    )
                    BasicComponent(
                        title = stringResource(Res.string.settings_about_author),
                        summary = "Kifranei",
                        onClick = { actionHandler.openInBrowser("https://github.com/Kifranei") },
                    )
                }
            }
            item { Spacer(Modifier.fillParentMaxHeight(0.55f)) }
        }
    }
}

@Composable
private fun FrostedCard(
    backdrop: LayerBackdrop,
    isDark: Boolean,
    cardBlend: List<BlendColorEntry>,
    scrollProgress: Float,
    content: @Composable ColumnScope.() -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .padding(bottom = 12.dp)
            .textureBlur(
                backdrop = backdrop,
                shape = RoundedCornerShape(16.dp),
                blurRadius = if (isDark) 72f else 64f,
                noiseCoefficient = BlurDefaults.NoiseCoefficient,
                colors = BlurColors(blendColors = cardBlend),
                enabled = true,
            ),
        colors = CardDefaults.defaultColors(
            color = if (isDark) {
                Color(0xFF252528).copy(alpha = 0.35f + 0.4f * scrollProgress.coerceIn(0f, 1f))
            } else {
                MiuixTheme.colorScheme.surfaceContainer.copy(alpha = 0.55f)
            },
            contentColor = MiuixTheme.colorScheme.onSurface,
        ),
        content = content,
    )
}

private fun aboutTitleBlendColors(isDark: Boolean): List<BlendColorEntry> =
    if (isDark) {
        listOf(
            BlendColorEntry(Color(0xE6A1A1A1), BlurBlendMode.ColorDodge),
            BlendColorEntry(Color(0x4DE6E6E6), BlurBlendMode.LinearLight),
            BlendColorEntry(Color(0xFF1AF500), BlurBlendMode.Lab),
        )
    } else {
        listOf(
            BlendColorEntry(Color(0xCC4A4A4A), BlurBlendMode.ColorBurn),
            BlendColorEntry(Color(0xFF4F4F4F), BlurBlendMode.LinearLight),
            BlendColorEntry(Color(0xFF1AF200), BlurBlendMode.Lab),
        )
    }

private fun aboutCardBlendColors(isDark: Boolean): List<BlendColorEntry> =
    if (isDark) {
        listOf(BlendColorEntry(Color(0x757A7A7A), BlurBlendMode.Luminosity))
    } else {
        listOf(
            BlendColorEntry(Color(0x340034F9), BlurBlendMode.Overlay),
            BlendColorEntry(Color(0xB3FFFFFF), BlurBlendMode.HardLight),
        )
    }
