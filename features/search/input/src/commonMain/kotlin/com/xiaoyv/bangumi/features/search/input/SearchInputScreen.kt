package com.xiaoyv.bangumi.features.search.input

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import com.xiaoyv.bangumi.shared.ui.component.layout.BgmScaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.global_back
import com.xiaoyv.bangumi.core_resource.resources.global_clear
import com.xiaoyv.bangumi.core_resource.resources.global_search
import com.xiaoyv.bangumi.core_resource.resources.search_clear_history_confirm
import com.xiaoyv.bangumi.core_resource.resources.search_history
import com.xiaoyv.bangumi.features.search.input.business.SearchInputEvent
import com.xiaoyv.bangumi.features.search.input.business.SearchInputSideEffect
import com.xiaoyv.bangumi.features.search.input.business.SearchInputState
import com.xiaoyv.bangumi.features.search.input.business.SearchInputViewModel
import com.xiaoyv.bangumi.shared.core.mvi.BaseState
import com.xiaoyv.bangumi.shared.core.utils.asTextFieldValue
import com.xiaoyv.bangumi.shared.ui.component.dialog.alert.BgmAlertDialog
import com.xiaoyv.bangumi.shared.ui.component.dialog.alert.rememberAlertDialogState
import com.xiaoyv.bangumi.shared.ui.component.layout.state.StateLayout
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.kts.collectBaseSideEffect
import com.xiaoyv.bangumi.shared.ui.theme.BgmMiuixIcons
import com.xiaoyv.bangumi.shared.ui.theme.contentMargin
import org.jetbrains.compose.resources.stringResource
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.InputField
import top.yukonga.miuix.kmp.basic.SearchBar
import top.yukonga.miuix.kmp.theme.MiuixTheme
import org.koin.compose.viewmodel.koinViewModel
import org.orbitmvi.orbit.compose.collectAsState

@Composable
fun SearchInputRoute(
    viewModel: SearchInputViewModel,
    onNavUp: () -> Unit,
    onNavScreen: (Screen) -> Unit,
) {
    val baseState by viewModel.collectAsState()
    val keyboardController = LocalSoftwareKeyboardController.current

    viewModel.collectBaseSideEffect {
        when (it) {
            is SearchInputSideEffect.OnSearchResult -> {
                keyboardController?.hide()
                onNavScreen(Screen.SearchResult(it.value))
            }
        }
    }

    SearchInputScreen(
        baseState = baseState,
        onActionEvent = viewModel::onEvent,
        onUiEvent = {
            when (it) {
                is SearchInputEvent.UI.OnNavUp -> onNavUp()
                is SearchInputEvent.UI.OnNavScreen -> onNavScreen(it.screen)
            }
        },
    )
}

@Composable
private fun SearchInputScreen(
    baseState: BaseState<SearchInputState>,
    onUiEvent: (SearchInputEvent.UI) -> Unit,
    onActionEvent: (SearchInputEvent.Action) -> Unit,
) {
    BgmScaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .padding(end = 12.dp, bottom = 8.dp, top = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(
                    modifier = Modifier.padding(start = 4.dp),
                    onClick = { onUiEvent(SearchInputEvent.UI.OnNavUp) },
                ) {
                    Icon(
                        imageVector = BgmMiuixIcons.Back,
                        contentDescription = stringResource(Res.string.global_back),
                    )
                }
                Box(modifier = Modifier.weight(1f)) {
                    val queryText = baseState.payload?.query?.text.orEmpty()
                    SearchBar(
                        modifier = Modifier.fillMaxWidth(),
                        inputField = {
                            InputField(
                                query = queryText,
                                onQueryChange = {
                                    onActionEvent(SearchInputEvent.Action.OnQueryChange(it.asTextFieldValue()))
                                },
                                onSearch = { onActionEvent(SearchInputEvent.Action.OnSearch) },
                                expanded = true,
                                onExpandedChange = {},
                                label = stringResource(Res.string.global_search),
                            )
                        },
                        onExpandedChange = {},
                        expanded = false,
                    ) {}
                }
            }
        }
    ) {
        StateLayout(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            onRefresh = { onActionEvent(SearchInputEvent.Action.OnRefresh(it)) },
            baseState = baseState,
        ) { state ->
            SearchInputScreenContent(state, onActionEvent)
        }
    }
}


@Composable
private fun SearchInputScreenContent(
    state: SearchInputState,
    onActionEvent: (SearchInputEvent.Action) -> Unit,
) {
    when {
        state.suggestions.isNotEmpty() -> LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(state.suggestions) {
                Text(
                    modifier = Modifier
                        .clickable {
                            onActionEvent(SearchInputEvent.Action.OnQueryChange(it.asTextFieldValue()))
                            onActionEvent(SearchInputEvent.Action.OnSearch)
                        }
                        .fillMaxWidth()
                        .padding(horizontal = contentMargin, vertical = 12.dp),
                    text = it,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Normal
                )
            }
        }

        state.histories.isNotEmpty() && state.query.text.isBlank() -> SearchInputHistory(
            state = state,
            onActionEvent = onActionEvent
        )
    }
}

@Composable
private fun SearchInputHistory(
    state: SearchInputState,
    onActionEvent: (SearchInputEvent.Action) -> Unit,
) {
    val clearHistoryDialogState = rememberAlertDialogState()

    BgmAlertDialog(
        state = clearHistoryDialogState,
        text = stringResource(Res.string.search_clear_history_confirm),
        onConfirm = { onActionEvent(SearchInputEvent.Action.OnClearHistory) },
    )

    Column(modifier = Modifier.fillMaxSize()) {

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                modifier = Modifier.padding(vertical = 12.dp, horizontal = contentMargin),
                text = stringResource(Res.string.search_history),
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = { clearHistoryDialogState.show() }) {
                Icon(
                    imageVector = BgmMiuixIcons.Delete,
                    tint = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                    contentDescription = stringResource(Res.string.global_clear)
                )
            }
            Spacer(modifier = Modifier.width(contentMargin - 12.dp))
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            items(state.histories, key = { it }) { keyword ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                onActionEvent(SearchInputEvent.Action.OnQueryChange(keyword.asTextFieldValue()))
                                onActionEvent(SearchInputEvent.Action.OnSearch)
                            }
                            .padding(start = contentMargin, top = 12.dp, bottom = 12.dp),
                        text = keyword,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Normal,
                    )
                    IconButton(
                        onClick = {
                            onActionEvent(SearchInputEvent.Action.OnDeleteHistory(keyword))
                        },
                    ) {
                        Icon(
                            imageVector = BgmMiuixIcons.Close,
                            tint = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                            contentDescription = stringResource(Res.string.global_clear),
                        )
                    }
                    Spacer(modifier = Modifier.width(contentMargin - 12.dp))
                }
            }
        }
    }
}

