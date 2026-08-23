package com.xiaoyv.bangumi.shared.ui.component.dialog.alert

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.global_cancel
import com.xiaoyv.bangumi.core_resource.resources.global_confirm
import com.xiaoyv.bangumi.shared.ui.theme.isMiuixUi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import top.yukonga.miuix.kmp.basic.TextButton as MiuixTextButton
import top.yukonga.miuix.kmp.overlay.OverlayDialog

/**
 * [BgmAlertDialog]
 *
 * @author why
 * @since 2025/1/14
 */
@Composable
fun BgmAlertDialog(
    confirm: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    state: AlertDialogState = rememberAlertDialogState(),
    cancel: @Composable (() -> Unit)? = null,
    icon: @Composable (() -> Unit)? = null,
    title: @Composable (() -> Unit)? = null,
    text: @Composable (() -> Unit)? = null,
) {
    if (isMiuixUi()) {
        OverlayDialog(
            show = state.showing,
            modifier = modifier,
            onDismissRequest = { state.dismiss() },
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                icon?.invoke()
                title?.invoke()
                text?.invoke()
                Spacer(modifier = Modifier.height(12.dp))
                confirm()
                cancel?.invoke()
            }
        }
    } else if (state.showing) {
        AlertDialog(
            modifier = modifier,
            onDismissRequest = { state.dismiss() },
            confirmButton = confirm,
            dismissButton = cancel,
            icon = icon,
            title = title,
            text = text,
            properties = state.properties
        )
    }
}


@Composable
fun BgmAlertDialog(
    text: String,
    confirm: String = stringResource(Res.string.global_confirm),
    cancel: String? = stringResource(Res.string.global_cancel),
    title: String? = null,
    modifier: Modifier = Modifier,
    state: AlertDialogState = rememberAlertDialogState(),
    icon: @Composable (() -> Unit)? = null,
    onConfirm: () -> Unit = { },
    onCancel: () -> Unit = { },
) {
    val scope = rememberCoroutineScope()
    if (isMiuixUi()) {
        OverlayDialog(
            show = state.showing,
            modifier = modifier,
            title = title,
            summary = text,
            onDismissRequest = { state.dismiss() },
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                icon?.invoke()
                MiuixTextButton(
                    modifier = Modifier.fillMaxWidth(),
                    text = confirm,
                    onClick = {
                        state.dismiss()
                        scope.launch {
                            delay(200)
                            onConfirm()
                        }
                    },
                )
                if (cancel != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    MiuixTextButton(
                        modifier = Modifier.fillMaxWidth(),
                        text = cancel,
                        onClick = {
                            state.dismiss()
                            scope.launch {
                                delay(200)
                                onCancel()
                            }
                        },
                    )
                }
            }
        }
    } else if (state.showing) {
        AlertDialog(
            modifier = modifier,
            onDismissRequest = { state.dismiss() },
            confirmButton = {
                TextButton(
                    onClick = {
                        state.dismiss()
                        scope.launch {
                            delay(200)
                            onConfirm()
                        }
                    },
                    content = { Text(confirm) }
                )
            },
            dismissButton = cancel?.let {
                {
                    TextButton(
                        onClick = {
                            state.dismiss()
                            scope.launch {
                                delay(200)
                                onCancel()
                            }
                        },
                        content = { Text(cancel) }
                    )
                }
            },
            icon = icon,
            title = title?.let { { Text(it) } },
            text = { Text(text) },
            properties = state.properties
        )
    }
}
