package com.xiaoyv.bangumi.shared.ui.component.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material3.ListItemColors
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import com.xiaoyv.bangumi.shared.ui.component.dialog.alert.BgmAlertInputDialog
import com.xiaoyv.bangumi.shared.ui.component.dialog.alert.rememberAlertInputDialogState
import com.xiaoyv.bangumi.shared.ui.component.space.LayoutPaddingHalf
import com.xiaoyv.bangumi.shared.ui.component.tab.ComposeTextTab
import com.xiaoyv.bangumi.shared.ui.theme.BgmIconsMirrored
import top.yukonga.miuix.kmp.basic.BasicComponent
import top.yukonga.miuix.kmp.basic.BasicComponentDefaults
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.DropdownColors
import top.yukonga.miuix.kmp.basic.DropdownDefaults
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.preference.ArrowPreference
import top.yukonga.miuix.kmp.preference.OverlayDropdownPreference
import top.yukonga.miuix.kmp.preference.SwitchPreference
import top.yukonga.miuix.kmp.theme.MiuixTheme

/**
 * [SettingContainer]
 *
 * @author why
 * @since 2025/1/15
 */
@Composable
fun SettingContainer(
    modifier: Modifier = Modifier,
    title: String? = null,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    label: @Composable (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .then(modifier),
        verticalArrangement = verticalArrangement,
        horizontalAlignment = horizontalAlignment,
    ) {
        when {
            title != null -> SmallTitle(text = title)
            label != null -> label()
        }
        Card(
            modifier = Modifier
                .padding(horizontal = 12.dp)
                .padding(bottom = 12.dp),
            insideMargin = PaddingValues(0.dp),
            content = content,
        )
    }
}

@Composable
fun SettingItem(
    modifier: Modifier = Modifier,
    title: String,
    summary: String? = null,
    icon: ImageVector? = null,
    leadingContent: @Composable (() -> Unit)? = icon?.let {
        {
            Icon(imageVector = icon, contentDescription = title)
        }
    },
    trailingContent: @Composable (() -> Unit)? = DefaultSettingTrailing,
    supportingContent: @Composable (() -> Unit)? = null,
    divider: Boolean = false,
    colors: ListItemColors = ListItemDefaults.colors(),
    textStyle: TextStyle = LocalTextStyle.current,
    onClick: () -> Unit = {},
) {
    val titleColor = if (textStyle.color != Color.Unspecified) {
        BasicComponentDefaults.titleColor(color = textStyle.color)
    } else {
        BasicComponentDefaults.titleColor()
    }
    if (trailingContent === DefaultSettingTrailing) {
        ArrowPreference(
            title = title,
            modifier = modifier,
            titleColor = titleColor,
            summary = summary,
            startAction = leadingContent,
            onClick = onClick,
        )
    } else {
        BasicComponent(
            modifier = modifier,
            title = title,
            titleColor = titleColor,
            summary = summary,
            startAction = leadingContent,
            endActions = trailingContent?.let { { it() } },
            bottomAction = supportingContent,
            onClick = onClick,
        )
    }
}

private val DefaultSettingTrailing: @Composable () -> Unit = { SettingItemTrailing() }

@Composable
fun <T : Any> SettingOptionItem(
    title: String,
    value: String,
    items: SerializeList<ComposeTextTab<T>>,
    dropdownColors: DropdownColors = DropdownDefaults.dropdownColors(),
    onClick: (T) -> Unit,
) {
    val labels = items.map { it.displayText() }
    val selectedIndex = items.indexOfFirst { it.displayText() == value }.let { if (it < 0) 0 else it }
    OverlayDropdownPreference(
        items = labels,
        selectedIndex = selectedIndex,
        title = title,
        dropdownColors = dropdownColors,
        onSelectedIndexChange = { index ->
            items.getOrNull(index)?.let { onClick(it.type) }
        },
    )
}

@Composable
fun SettingSwitchItem(
    title: String,
    desc: String? = null,
    value: Boolean,
    onValueChange: (Boolean) -> Unit,
) {
    SwitchPreference(
        checked = value,
        onCheckedChange = onValueChange,
        title = title,
        summary = desc,
    )
}

@Composable
fun SettingInputItem(
    title: String,
    value: String,
    onClick: (String) -> Unit,
) {
    val dialogState = rememberAlertInputDialogState()

    BgmAlertInputDialog(
        state = dialogState,
        onConfirm = {
            onClick(it.value)
        },
    )

    ArrowPreference(
        title = title,
        summary = value,
        onClick = { dialogState.show { it.copy(value = value, title = title) } },
    )
}

@Composable
fun SettingItemTrailing(
    modifier: Modifier = Modifier,
    text: String? = null,
    imageVector: ImageVector? = BgmIconsMirrored.KeyboardArrowRight,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(LayoutPaddingHalf),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (!text.isNullOrBlank()) {
            Text(
                modifier = Modifier.widthIn(max = 120.dp),
                text = text,
                style = MiuixTheme.textStyles.body2,
                color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        if (imageVector != null) {
            Icon(imageVector, text.orEmpty())
        }
    }
}
