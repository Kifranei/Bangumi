package com.xiaoyv.bangumi.shared.ui.component.dialog.alert

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.global_cancel
import com.xiaoyv.bangumi.core_resource.resources.global_confirm
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import top.yukonga.miuix.kmp.basic.TextButton
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
    val showing by state.showing.collectAsStateWithLifecycle()
    OverlayDialog(
        show = showing,
        modifier = modifier,
        onDismissRequest = { state.dismiss() },
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            title?.invoke()
            text?.invoke()
            Spacer(modifier = Modifier.height(12.dp))
            confirm()
            cancel?.invoke()
        }
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
    val showing by state.showing.collectAsStateWithLifecycle()
    OverlayDialog(
        show = showing,
        modifier = modifier,
        title = title,
        summary = text,
        onDismissRequest = { state.dismiss() },
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            TextButton(
                text = confirm,
                onClick = {
                    state.dismiss()
                    scope.launch {
                        delay(200)
                        onConfirm()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
            )
            if (cancel != null) {
                Spacer(modifier = Modifier.height(8.dp))
                TextButton(
                    text = cancel,
                    onClick = {
                        state.dismiss()
                        scope.launch {
                            delay(200)
                            onCancel()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

