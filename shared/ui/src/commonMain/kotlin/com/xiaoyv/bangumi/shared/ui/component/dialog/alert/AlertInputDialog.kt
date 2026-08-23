package com.xiaoyv.bangumi.shared.ui.component.dialog.alert

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.global_cancel
import com.xiaoyv.bangumi.core_resource.resources.global_confirm
import com.xiaoyv.bangumi.shared.core.utils.digit
import com.xiaoyv.bangumi.shared.ui.theme.isMiuixUi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import top.yukonga.miuix.kmp.basic.TextButton as MiuixTextButton
import top.yukonga.miuix.kmp.basic.TextField as MiuixTextField
import top.yukonga.miuix.kmp.overlay.OverlayDialog
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun BgmAlertInputDialog(
    modifier: Modifier = Modifier,
    confirm: String = stringResource(Res.string.global_confirm),
    cancel: String? = stringResource(Res.string.global_cancel),
    state: AlertInputDialogState = rememberAlertInputDialogState(),
    icon: @Composable (() -> Unit)? = null,
    onConfirm: (AlertInputDialogState.Data) -> Unit = { },
    onCancel: () -> Unit = { },
) {
    val scope = rememberCoroutineScope()
    if (isMiuixUi()) {
        val focusRequester = remember { FocusRequester() }
        val data = state.data
        var text by remember(data) {
            mutableStateOf(TextFieldValue(data.value, TextRange(data.value.length)))
        }

        OverlayDialog(
            show = state.showing,
            modifier = modifier.padding(WindowInsets.ime.asPaddingValues()),
            title = data.title,
            onDismissRequest = { state.dismiss() },
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                icon?.invoke()
                MiuixTextField(
                    modifier = Modifier
                        .focusRequester(focusRequester)
                        .fillMaxWidth(),
                    value = text,
                    keyboardOptions = KeyboardOptions(keyboardType = if (data.onlyNumber) KeyboardType.Number else KeyboardType.Text),
                    singleLine = data.singleLine,
                    minLines = data.minLines,
                    maxLines = data.maxLines,
                    onValueChange = { text = if (data.onlyNumber) it.digit(text) else it },
                )
                Spacer(modifier = Modifier.height(12.dp))
                MiuixTextButton(
                    modifier = Modifier.fillMaxWidth(),
                    text = confirm,
                    onClick = {
                        state.dismiss()
                        scope.launch {
                            delay(200.milliseconds)
                            onConfirm(data.copy(value = text.text.trim()))
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
                                delay(200.milliseconds)
                                onCancel()
                            }
                        },
                    )
                }
            }
        }

        LaunchedEffect(state.showing) {
            if (state.showing) focusRequester.requestFocus()
        }
    } else if (state.showing) {
        val focusRequester = remember { FocusRequester() }
        val data = state.data
        var text by remember(data) {
            mutableStateOf(TextFieldValue(data.value, TextRange(data.value.length)))
        }

        AlertDialog(
            modifier = modifier.padding(WindowInsets.ime.asPaddingValues()),
            onDismissRequest = { state.dismiss() },
            confirmButton = {
                TextButton(
                    onClick = {
                        state.dismiss()
                        scope.launch {
                            delay(200.milliseconds)
                            onConfirm(data.copy(value = text.text.trim()))
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
                                delay(200.milliseconds)
                                onCancel()
                            }
                        },
                        content = { Text(cancel) }
                    )
                }
            },
            icon = icon,
            title = data.title?.let { { Text(it) } },
            text = {
                OutlinedTextField(
                    modifier = Modifier
                        .focusRequester(focusRequester)
                        .fillMaxWidth(),
                    value = text,
                    keyboardOptions = KeyboardOptions(keyboardType = if (data.onlyNumber) KeyboardType.Number else KeyboardType.Text),
                    singleLine = data.singleLine,
                    minLines = data.minLines,
                    maxLines = data.maxLines,
                    onValueChange = { text = if (data.onlyNumber) it.digit(text) else it }
                )

                LaunchedEffect(Unit) {
                    focusRequester.requestFocus()
                }
            },
            properties = state.properties
        )
    }
}
