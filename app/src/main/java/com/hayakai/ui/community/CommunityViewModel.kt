package com.hayakai.ui.community

import androidx.lifecycle.ViewModel
import com.hayakai.data.remote.dto.DeletePostDto
import com.hayakai.data.repository.CommunityPostRepository

class CommunityViewModel(private val communityPostRepository: CommunityPostRepository) :
    ViewModel() {

    fun getAllPosts() = communityPostRepository.getAllPosts()

    fun getMyPosts() = communityPostRepository.getMyPosts()

    fun deletePost(deletePostDto: DeletePostDto) = communityPostRepository.deletePost(deletePostDto)
}