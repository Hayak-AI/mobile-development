package com.hayakai.ui.detailpost

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.hayakai.data.remote.dto.DeleteCommentDto
import com.hayakai.data.remote.dto.NewPostCommentDto
import com.hayakai.data.remote.response.PostDataItemComment
import com.hayakai.data.repository.CommentRepository

class DetailPostViewModel(
    private val commentRepository: CommentRepository
) : ViewModel() {
    lateinit var postComments: LiveData<PagingData<PostDataItemComment>>

    fun getPostComments(postId: Int): LiveData<PagingData<PostDataItemComment>> {
        postComments = commentRepository.getPostComments(postId).cachedIn(viewModelScope)
        return postComments
    }

    fun newPostComment(newPostCommentDto: NewPostCommentDto) =
        commentRepository.newPostComment(newPostCommentDto)

    fun deletePostComment(deleteCommentDto: DeleteCommentDto) =
        commentRepository.deleteCommentPost(deleteCommentDto)
}