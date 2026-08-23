package com.xiaoyv.bangumi.shared.ui.component.layout

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.ListItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.xiaoyv.bangumi.shared.ui.theme.isMiuixUi
import top.yukonga.miuix.kmp.basic.BasicComponent

/** A list row that keeps Material 3 and MIUIX layout behavior separate. */
@Composable
fun BgmListItem(
    headlineContent: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    overlineContent: (@Composable () -> Unit)? = null,
    supportingContent: (@Composable () -> Unit)? = null,
    leadingContent: (@Composable () -> Unit)? = null,
    trailingContent: (@Composable () -> Unit)? = null,
) {
    if (!isMiuixUi()) {
        ListItem(
            modifier = modifier,
            headlineContent = headlineContent,
            overlineContent = overlineContent,
            supportingContent = supportingContent,
            leadingContent = leadingContent,
            trailingContent = trailingContent,
        )
        return
    }

    BasicComponent(
        modifier = modifier,
        startAction = leadingContent,
        endActions = trailingContent?.let { action ->
            @Composable { _: RowScope -> action() }
        },
    ) {
        overlineContent?.invoke()
        headlineContent()
        supportingContent?.invoke()
    }
}
