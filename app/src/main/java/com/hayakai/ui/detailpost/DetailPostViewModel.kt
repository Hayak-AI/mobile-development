package com.hayakai.ui.detailpost

import androidx.lifecycle.ViewModel
import com.hayakai.data.remote.dto.DeleteCommentDto
import com.hayakai.data.remote.dto.NewPostCommentDto
import com.hayakai.data.repository.CommentRepository

class DetailPostViewModel(
    private val commentRepository: CommentRepository
) : ViewModel() {
    fun getPostComments(postId: Int) = commentRepository.getPostComments(postId)

    fun newPostComment(newPostCommentDto: NewPostCommentDto) =
        commentRepository.newPostComment(newPostCommentDto)

    fun deletePostComment(deleteCommentDto: DeleteCommentDto) =
        commentRepository.deleteCommentPost(deleteCommentDto)
}