package com.xiaoyv.bangumi.shared.ui.component.dialog.alert

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.util.fastForEachIndexed
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import com.xiaoyv.bangumi.shared.ui.component.tab.ComposeTextTab
import top.yukonga.miuix.kmp.basic.ButtonDefaults
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.basic.TextButtonColors
import top.yukonga.miuix.kmp.overlay.OverlayDialog

/**
 * [AlertOptionDialog]
 *
 * @since 2025/5/15
 */
@Composable
fun <Key : Any> AlertOptionDialog(
    state: AlertDialogState,
    title: String,
    items: SerializeList<ComposeTextTab<Key>>,
    itemColors: TextButtonColors = ButtonDefaults.textButtonColors(),
    onClick: (ComposeTextTab<Key>, Int) -> Unit,
) {
    val showing by state.showing.collectAsStateWithLifecycle()
    OverlayDialog(
        show = showing,
        title = title,
        onDismissRequest = { state.dismiss() },
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            items.fastForEachIndexed { i, tab ->
                TextButton(
                    text = tab.displayText(),
                    colors = itemColors,
                    onClick = {
                        onClick(tab, i)
                        state.dismiss()
                    },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@Composable
fun AlertContentDialog(
    state: AlertDialogState,
    modifier: Modifier = Modifier,
    properties: DialogProperties = DialogProperties(),
    content: @Composable () -> Unit,
) {
    val showing by state.showing.collectAsStateWithLifecycle()
    OverlayDialog(
        show = showing,
        modifier = modifier,
        onDismissRequest = { state.dismiss() },
        content = content,
    )
}
