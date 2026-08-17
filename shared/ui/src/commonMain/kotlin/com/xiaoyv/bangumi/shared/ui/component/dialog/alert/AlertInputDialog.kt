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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.global_cancel
import com.xiaoyv.bangumi.core_resource.resources.global_confirm
import com.xiaoyv.bangumi.shared.core.utils.digit
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.basic.TextField as MiuixTextField
import top.yukonga.miuix.kmp.overlay.OverlayDialog

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
    val showing by state.showing.collectAsStateWithLifecycle()
    if (showing) {
        val focusRequester = remember { FocusRequester() }
        val data by state.data.collectAsStateWithLifecycle()
        var text by remember(data) {
            mutableStateOf(TextFieldValue(data.value, TextRange(data.value.length)))
        }

        OverlayDialog(
            show = true,
            modifier = modifier.padding(WindowInsets.ime.asPaddingValues()),
            title = data.title,
            onDismissRequest = { state.dismiss() },
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
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
                LaunchedEffect(Unit) {
                    focusRequester.requestFocus()
                }
                Spacer(modifier = Modifier.height(12.dp))
                TextButton(
                    text = confirm,
                    onClick = {
                        state.dismiss()
                        scope.launch {
                            delay(200)
                            onConfirm(data.copy(value = text.text.trim()))
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
}