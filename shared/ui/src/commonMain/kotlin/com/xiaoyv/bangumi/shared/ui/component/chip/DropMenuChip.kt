package com.xiaoyv.bangumi.shared.ui.component.chip

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.global_unselected
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import com.xiaoyv.bangumi.shared.ui.component.tab.ComposeTextTab
import com.xiaoyv.bangumi.shared.ui.theme.BgmMiuixIcons
import org.jetbrains.compose.resources.stringResource
import top.yukonga.miuix.kmp.basic.DropdownDefaults
import top.yukonga.miuix.kmp.basic.DropdownEntry
import top.yukonga.miuix.kmp.basic.DropdownItem
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.menu.OverlayIconDropdownMenu
import top.yukonga.miuix.kmp.popup.OverlayDropdownPopup
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun <T : Any> DropMenuActionButton(
    options: SerializeList<ComposeTextTab<T>>,
    modifier: Modifier = Modifier,
    imageVector: ImageVector = BgmMiuixIcons.More,
    imageTint: Color = LocalContentColor.current,
    onOptionClick: (ComposeTextTab<T>) -> Unit = {},
) {
    OverlayIconDropdownMenu(
        modifier = modifier,
        entry = DropdownEntry(
            items = options.map { tab ->
                DropdownItem(
                    text = tab.displayText(),
                    onClick = { onOptionClick(tab) },
                )
            },
        ),
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = null,
            tint = imageTint,
        )
    }
}

@Composable
fun FilterActionChip(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(MiuixTheme.colorScheme.surfaceContainerHigh)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp),
    ) {
        Text(
            text = text,
            color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
        )
    }
}

@Composable
fun <T : Any> DropMenuChip(
    options: SerializeList<ComposeTextTab<T>>,
    current: T? = options.firstOrNull()?.type,
    labelPrefix: String? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    colors: Color = MiuixTheme.colorScheme.surfaceContainerHigh,
    border: Color? = null,
    onOptionClick: (ComposeTextTab<T>) -> Unit = {},
) {
    Box {
        var expanded by rememberSaveable { mutableStateOf(false) }
        val target = options.find { it.type == current }
        val label = buildString {
            if (labelPrefix != null) append(labelPrefix)
            if (target != null) {
                if (labelPrefix != null) append(" ")
                append(target.displayText())
            } else {
                append(stringResource(Res.string.global_unselected))
            }
        }

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .background(colors)
                .clickable { expanded = true }
                .padding(horizontal = 14.dp, vertical = 8.dp),
        ) {
            Text(
                text = label,
                color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
            )
        }

        OverlayDropdownPopup(
            entry = DropdownEntry(
                items = options.map { tab ->
                    DropdownItem(
                        text = tab.displayText(),
                        selected = tab.type == current,
                        onClick = { onOptionClick(tab) },
                    )
                },
            ),
            show = expanded,
            onDismiss = { expanded = false },
            onDismissFinished = {},
            maxHeight = null,
            dropdownColors = DropdownDefaults.dropdownColors(),
            renderInRootScaffold = true,
            collapseOnSelection = true,
        )
    }
}
