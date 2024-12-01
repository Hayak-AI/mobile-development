package com.hayakai.ui.newpost

import androidx.lifecycle.ViewModel
import com.hayakai.data.remote.dto.NewPostDto
import com.hayakai.data.repository.CommunityPostRepository

class NewPostViewModel(private val communityPostRepository: CommunityPostRepository) : ViewModel() {
    fun newPost(newPostDto: NewPostDto) = communityPostRepository.newPost(newPostDto)
}