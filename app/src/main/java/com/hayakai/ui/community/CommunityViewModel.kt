package com.hayakai.ui.community

import androidx.lifecycle.ViewModel
import com.hayakai.data.remote.dto.DeletePostDto
import com.hayakai.data.repository.CommunityPostRepository
import com.hayakai.data.repository.NewsRepository

class CommunityViewModel(
    private val communityPostRepository: CommunityPostRepository,
    private val newsRepository: NewsRepository
) :
    ViewModel() {

    fun getAllPosts() = communityPostRepository.getAllPosts()

    fun getMyPosts() = communityPostRepository.getMyPosts()

    fun deletePost(deletePostDto: DeletePostDto) = communityPostRepository.deletePost(deletePostDto)

    fun getAllNews(location: String) = newsRepository.getAllNews(location)

}