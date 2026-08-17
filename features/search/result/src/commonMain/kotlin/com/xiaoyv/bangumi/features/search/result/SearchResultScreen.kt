package com.xiaoyv.bangumi.features.search.result

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import com.xiaoyv.bangumi.shared.ui.component.layout.BgmScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.global_search
import com.xiaoyv.bangumi.features.search.result.business.SearchResultEvent
import com.xiaoyv.bangumi.features.search.result.business.SearchResultState
import com.xiaoyv.bangumi.features.search.result.business.SearchResultViewModel
import com.xiaoyv.bangumi.features.search.result.page.SearchResultCharacter
import com.xiaoyv.bangumi.features.search.result.page.SearchResultIndex
import com.xiaoyv.bangumi.features.search.result.page.SearchResultPerson
import com.xiaoyv.bangumi.features.search.result.page.SearchResultSubject
import com.xiaoyv.bangumi.features.search.result.page.SearchResultTag
import com.xiaoyv.bangumi.features.search.result.page.SearchResultTopic
import com.xiaoyv.bangumi.shared.core.mvi.BaseState
import com.xiaoyv.bangumi.shared.core.types.SearchType
import com.xiaoyv.bangumi.shared.ui.component.bar.BgmTopAppBar
import com.xiaoyv.bangumi.shared.ui.component.layout.state.StateLayout
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.component.pager.BgmTabHorizontalPager
import com.xiaoyv.bangumi.shared.ui.kts.collectBaseSideEffect
import com.xiaoyv.bangumi.shared.ui.theme.BgmMiuixIcons
import org.jetbrains.compose.resources.stringResource
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import org.koin.compose.viewmodel.koinViewModel
import org.orbitmvi.orbit.compose.collectAsState

@Composable
fun SearchResultRoute(
    viewModel: SearchResultViewModel,
    onNavUp: () -> Unit,
    onNavScreen: (Screen) -> Unit,
) {
    val baseState by viewModel.collectAsState()

    viewModel.collectBaseSideEffect {

    }

    SearchResultScreen(
        baseState = baseState,
        onActionEvent = viewModel::onEvent,
        onUiEvent = {
            when (it) {
                is SearchResultEvent.UI.OnNavUp -> onNavUp()
                is SearchResultEvent.UI.OnNavScreen -> onNavScreen(it.screen)
            }
        },
    )
}

@Composable
private fun SearchResultScreen(
    baseState: BaseState<SearchResultState>,
    onUiEvent: (SearchResultEvent.UI) -> Unit,
    onActionEvent: (SearchResultEvent.Action) -> Unit,
) {
    BgmScaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            baseState.content {
                SearchResultTopBar(
                    state = this,
                    onUiEvent = onUiEvent,
                )
            }
        }
    ) {
        StateLayout(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            onRefresh = { onActionEvent(SearchResultEvent.Action.OnRefresh(it)) },
            baseState = baseState,
        ) { state ->
            SearchResultScreenContent(state, onUiEvent, onActionEvent)
        }
    }
}


@Composable
private fun SearchResultScreenContent(
    state: SearchResultState,
    onUiEvent: (SearchResultEvent.UI) -> Unit,
    onActionEvent: (SearchResultEvent.Action) -> Unit,
) {
    BgmTabHorizontalPager(
        modifier = Modifier.fillMaxSize(),
        tabs = state.tabs,
    ) {
        when (state.tabs[it].type) {
            SearchType.SUBJECT -> SearchResultSubject(state, onUiEvent, onActionEvent)
            SearchType.PERSON -> SearchResultPerson(state, onUiEvent, onActionEvent)
            SearchType.CHARACTER -> SearchResultCharacter(state, onUiEvent, onActionEvent)
            SearchType.INDEX -> SearchResultIndex(state, onUiEvent, onActionEvent)
            SearchType.TOPIC -> SearchResultTopic(state, onUiEvent, onActionEvent)
            SearchType.TAG -> SearchResultTag(state, onUiEvent, onActionEvent)
        }
    }
}

@Composable
private fun SearchResultTopBar(
    state: SearchResultState,
    onUiEvent: (SearchResultEvent.UI) -> Unit,
) {
    BgmTopAppBar(
        modifier = Modifier.fillMaxWidth(),
        title = state.query,
        actions = {
            IconButton(
                onClick = {
                    onUiEvent(SearchResultEvent.UI.OnNavScreen(Screen.SearchInput(state.query)))
                },
            ) {
                Icon(
                    imageVector = BgmMiuixIcons.Search,
                    contentDescription = stringResource(Res.string.global_search),
                )
            }
        },
        onNavigationClick = { onUiEvent(SearchResultEvent.UI.OnNavUp) }
    )
}
