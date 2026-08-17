package com.xiaoyv.bangumi.features.subject.detail.page

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.xiaoyv.bangumi.features.subject.detail.business.SubjectDetailEvent
import com.xiaoyv.bangumi.features.subject.detail.business.SubjectDetailState
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeComment
import com.xiaoyv.bangumi.shared.ui.component.divider.BgmHorizontalDivider
import com.xiaoyv.bangumi.shared.ui.component.layout.state.StateLazyColumn
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.component.paging.LazyPagingItems
import com.xiaoyv.bangumi.shared.ui.view.comment.CommentItem

@Composable
fun SubjectDetailCommentScreen(
    state: SubjectDetailState,
    commentPagingItems: LazyPagingItems<ComposeComment>,
    onUiEvent: (SubjectDetailEvent.UI) -> Unit,
    onActionEvent: (SubjectDetailEvent.Action) -> Unit,
) {
    StateLazyColumn(
        modifier = Modifier.fillMaxSize(),
        pagingItems = commentPagingItems,
        key = { item, _ -> item.id },
        itemContent = { item, index ->
            if (index > 0 && item.parent == null) BgmHorizontalDivider()
            CommentItem(
                modifier = Modifier.fillMaxWidth(),
                item = item,
                reactions = state.reactionsOf(item),
                onClickReaction = {
                    onActionEvent(
                        SubjectDetailEvent.Action.OnReactionClick(
                            commentId = item.emojiParam.likeCommentId.ifBlank { item.id },
                            displayId = item.id,
                            value = it.value,
                        )
                    )
                },
                onClickUser = { onUiEvent(SubjectDetailEvent.UI.OnNavScreen(Screen.UserDetail(it))) },
            )
        }
    )
}
