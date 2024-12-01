package com.hayakai.ui.editpost

import androidx.lifecycle.ViewModel
import com.hayakai.data.remote.dto.UpdatePostDto
import com.hayakai.data.repository.CommunityPostRepository

class EditPostViewModel(private val communityPostRepository: CommunityPostRepository) :
    ViewModel() {
    fun editPost(updatePostDto: UpdatePostDto) = communityPostRepository.updatePost(updatePostDto)
}