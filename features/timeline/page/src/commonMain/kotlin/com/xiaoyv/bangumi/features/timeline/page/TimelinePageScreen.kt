package com.xiaoyv.bangumi.features.timeline.page

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.global_delete
import com.xiaoyv.bangumi.core_resource.resources.timeline_delete_confirm
import com.xiaoyv.bangumi.features.timeline.page.business.TimelinePageEvent
import com.xiaoyv.bangumi.features.timeline.page.business.TimelinePageSideEffect
import com.xiaoyv.bangumi.features.timeline.page.business.TimelinePageViewModel
import com.xiaoyv.bangumi.features.timeline.page.business.koinTimelinePageViewModel
import com.xiaoyv.bangumi.shared.core.types.TopicDetailType
import com.xiaoyv.bangumi.shared.core.types.TimelineCat
import com.xiaoyv.bangumi.shared.core.types.TimelineSubjectAction
import com.xiaoyv.bangumi.shared.core.utils.clickWithoutRipped
import com.xiaoyv.bangumi.shared.core.utils.formatAgo
import com.xiaoyv.bangumi.shared.data.model.request.list.timeline.ListTimelineParam
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeGroup
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeRating
import com.xiaoyv.bangumi.shared.data.model.response.bgm.subject.ComposeSubject
import com.xiaoyv.bangumi.shared.data.model.response.bgm.user.ComposeUser
import com.xiaoyv.bangumi.shared.data.model.response.bgm.timeline.ComposeTimeline
import com.xiaoyv.bangumi.shared.data.model.response.bgm.timeline.ComposeTimelineBatch
import com.xiaoyv.bangumi.shared.data.model.response.bgm.timeline.ComposeTimelineDaily
import com.xiaoyv.bangumi.shared.data.model.response.bgm.timeline.ComposeTimelineMemo
import com.xiaoyv.bangumi.shared.data.model.response.bgm.timeline.ComposeTimelineSingle
import com.xiaoyv.bangumi.shared.data.model.response.bgm.timeline.ComposeTimelineSubject
import com.xiaoyv.bangumi.shared.data.manager.shared.currentUser
import com.xiaoyv.bangumi.shared.ui.component.dialog.alert.BgmAlertDialog
import com.xiaoyv.bangumi.shared.ui.component.dialog.alert.rememberAlertDialogState
import com.xiaoyv.bangumi.shared.ui.component.image.StateImage
import com.xiaoyv.bangumi.shared.ui.component.layout.state.StateLazyColumn
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.component.paging.LazyPagingItems
import com.xiaoyv.bangumi.shared.ui.component.paging.collectAsLazyPagingItems
import com.xiaoyv.bangumi.shared.ui.component.space.LayoutPaddingHalf
import com.xiaoyv.bangumi.shared.ui.kts.collectBaseSideEffect
import com.xiaoyv.bangumi.shared.ui.theme.BgmMiuixIcons
import com.xiaoyv.bangumi.shared.ui.theme.PreviewColumn
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.stringResource
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton

internal const val CONTENT_TYPE_TIMELINE = "CONTENT_TYPE_TIMELINE"
internal const val CONTENT_TYPE_TIMELINE_SUBJECT = "CONTENT_TYPE_TIMELINE_SUBJECT"
internal const val CONTENT_TYPE_TIMELINE_MONO = "CONTENT_TYPE_TIMELINE_MONO"
internal const val CONTENT_TYPE_TIMELINE_DAILY = "CONTENT_TYPE_TIMELINE_DAILY"

@Composable
fun TimelinePageRoute(
    param: ListTimelineParam,
    onNavScreen: (Screen) -> Unit,
) {
    if (LocalInspectionMode.current) return
    val viewModel: TimelinePageViewModel = koinTimelinePageViewModel(param)
    val pagingItems = viewModel.timelines.collectAsLazyPagingItems()

    viewModel.collectBaseSideEffect {
        when (it) {
            TimelinePageSideEffect.OnDeleted -> pagingItems.refresh()
        }
    }

    TimelinePageScreen(
        pagingItems = pagingItems,
        onActionEvent = viewModel::onEvent,
        onUiEvent = {
            when (it) {
                is TimelinePageEvent.UI.OnNavScreen -> onNavScreen(it.screen)
            }
        },
    )
}

@Composable
private fun TimelinePageScreen(
    pagingItems: LazyPagingItems<ComposeTimeline>,
    onUiEvent: (TimelinePageEvent.UI) -> Unit,
    onActionEvent: (TimelinePageEvent.Action) -> Unit,
) {
    val deleteDialog = rememberAlertDialogState()
    var pendingDelete by remember { mutableStateOf<ComposeTimeline?>(null) }

    BgmAlertDialog(
        state = deleteDialog,
        title = stringResource(Res.string.global_delete),
        text = stringResource(Res.string.timeline_delete_confirm),
        onConfirm = {
            pendingDelete?.let {
                onActionEvent(TimelinePageEvent.Action.OnDelete(it.id))
            }
        },
    )

    StateLazyColumn(
        modifier = Modifier.fillMaxSize(),
        pagingItems = pagingItems,
        enableDoubleTapToScrollTop = true,
        key = { item, _ -> item.id },
        contentType = { CONTENT_TYPE_TIMELINE }
    ) { item, _ ->
        TimelinePageItem(
            modifier = Modifier.fillMaxWidth(),
            item = item,
            onActionEvent = onActionEvent,
            onUiEvent = onUiEvent,
            onDeleteClick = {
                pendingDelete = item
                deleteDialog.show()
            },
        )

        HorizontalDivider()
    }

}

@Composable
private fun TimelinePageItem(
    modifier: Modifier,
    item: ComposeTimeline,
    onUiEvent: (TimelinePageEvent.UI) -> Unit,
    onActionEvent: (TimelinePageEvent.Action) -> Unit,
    onDeleteClick: () -> Unit = {},
) {
    val me = currentUser()
    val isMine = me.id != 0L && (
        item.uid == me.id || item.user.id == me.id || item.user.username == me.username
        )

    Box(modifier = modifier) {
    ListItem(
        modifier = Modifier
            .fillMaxWidth()
            .padding(end = if (isMine) 36.dp else 0.dp),
        leadingContent = {
            StateImage(
                modifier = Modifier
                    .size(44.dp)
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, MaterialTheme.shapes.small)
                    .clickWithoutRipped { onUiEvent(TimelinePageEvent.UI.OnNavScreen(Screen.UserDetail(item.user.username))) },
                shape = MaterialTheme.shapes.small,
                model = item.user.avatar.displaySmallImage
            )
        },
        headlineContent = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = LayoutPaddingHalf),
                verticalArrangement = Arrangement.spacedBy(LayoutPaddingHalf)
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = item.rememberTimelineTitle(
                        onUserClickListener = {
                            onUiEvent(TimelinePageEvent.UI.OnNavScreen(Screen.UserDetail(it.username)))
                        },
                        onGroupClickListener = {
                            onUiEvent(TimelinePageEvent.UI.OnNavScreen(Screen.GroupDetail(it.name)))
                        },
                        onSubjectClickListener = {
                            onUiEvent(TimelinePageEvent.UI.OnNavScreen(Screen.SubjectDetail(it.id)))
                        },
                        onEpisodeClickListener = {
                            onUiEvent(TimelinePageEvent.UI.OnNavScreen(Screen.Article(it.id, TopicDetailType.TYPE_EP)))
                        },
                        onBlogClickListener = {
                            onUiEvent(TimelinePageEvent.UI.OnNavScreen(Screen.Article(it.id, TopicDetailType.TYPE_BLOG)))
                        },
                        onIndexClickListener = {
                            onUiEvent(TimelinePageEvent.UI.OnNavScreen(Screen.IndexDetail(it.id)))
                        },
                        onMonoClickListener = { it, type ->
                            onUiEvent(TimelinePageEvent.UI.OnNavScreen(Screen.MonoDetail(it.id, type)))
                        }
                    )
                )

                when (item.cat) {
                    TimelineCat.SUBJECT -> TimelinePageItemSubject(
                        item = item,
                        onClick = {
                            onUiEvent(TimelinePageEvent.UI.OnNavScreen(Screen.SubjectDetail(it.id)))
                        }
                    )

                    TimelineCat.PROGRESS if (item.memo.progress.single != ComposeTimelineSingle.Empty) -> {
                        TimelinePageItemSubjectItem(
                            subject = item.memo.progress.single.subject,
                            onClick = {
                                onUiEvent(TimelinePageEvent.UI.OnNavScreen(Screen.SubjectDetail(it.id)))
                            }
                        )
                    }

                    TimelineCat.PROGRESS if (item.memo.progress.batch != ComposeTimelineBatch.Empty) -> {
                        TimelinePageItemSubjectItem(
                            subject = item.memo.progress.batch.subject,
                            onClick = {
                                onUiEvent(TimelinePageEvent.UI.OnNavScreen(Screen.SubjectDetail(it.id)))
                            }
                        )
                    }

                    TimelineCat.WIKI -> TimelinePageItemSubjectItem(
                        subject = item.memo.wiki.subject,
                        onClick = {
                            onUiEvent(TimelinePageEvent.UI.OnNavScreen(Screen.SubjectDetail(it.id)))
                        }
                    )

                    TimelineCat.MONO -> TimelinePageItemMono(
                        item = item,
                        onClick = { mono, type ->
                            onUiEvent(TimelinePageEvent.UI.OnNavScreen(Screen.MonoDetail(mono.id, type)))
                        }
                    )

                    TimelineCat.DAILY -> TimelinePageItemDaily(
                        item = item,
                        onClickGroup = {
                            onUiEvent(TimelinePageEvent.UI.OnNavScreen(Screen.GroupDetail(it.name)))
                        },
                        onClickUser = {
                            onUiEvent(TimelinePageEvent.UI.OnNavScreen(Screen.UserDetail(it.username)))
                        }
                    )

                    TimelineCat.BLOG -> TimelinePageItemBlog(
                        item = item,
                        onClick = {
                            onUiEvent(TimelinePageEvent.UI.OnNavScreen(Screen.Article(it.id, TopicDetailType.TYPE_BLOG)))
                        }
                    )

                    TimelineCat.INDEX -> TimelinePageItemIndex(
                        item = item,
                        onClick = {
                            onUiEvent(TimelinePageEvent.UI.OnNavScreen(Screen.IndexDetail(it.id)))
                        }
                    )
                }
            }
        },
        supportingContent = {
            Text(
                text = buildString {
                    append(item.createdAt.formatAgo())
                    if (item.source.name.isNotBlank()) {
                        append(" · ")
                        append(item.source.name)
                    }
                },
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        },
        overlineContent = {
            Text(
                modifier = Modifier.clickWithoutRipped {
                    onUiEvent(TimelinePageEvent.UI.OnNavScreen(Screen.UserDetail(item.user.username)))
                },
                text = item.user.nickname,
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )
            )
        }
    )

        if (isMine) {
            IconButton(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(end = 4.dp, top = 2.dp),
                onClick = onDeleteClick,
            ) {
                Icon(
                    imageVector = BgmMiuixIcons.Delete,
                    contentDescription = stringResource(Res.string.global_delete),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}


@Preview
@Composable
private fun PreviewTimelinePageScreen() {
    PreviewColumn {
        TimelinePageItem(
            modifier = Modifier.fillMaxWidth(),
            item = ComposeTimeline(
                id = 0,
                type = TimelineSubjectAction.DROPPED,
                cat = TimelineCat.SUBJECT,
                user = ComposeUser(nickname = "Test"),
                memo = ComposeTimelineMemo(
                    daily = ComposeTimelineDaily(
                        users = persistentListOf(ComposeUser(nickname = "Test")),
                        groups = persistentListOf(ComposeGroup())
                    ),
                    subject = persistentListOf(
                        ComposeTimelineSubject(
                            subject = ComposeSubject(
                                name = "Subject",
                                info = "Test Info",
                                rating = ComposeRating(score = 9.9)
                            )
                        )
                    )
                ),
                batch = false,
            ),
            onActionEvent = {},
            onUiEvent = {}
        )
    }
}
