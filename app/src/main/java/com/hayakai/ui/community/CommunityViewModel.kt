package com.hayakai.ui.community

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.hayakai.data.remote.dto.DeletePostDto
import com.hayakai.data.remote.response.PostItem
import com.hayakai.data.repository.CommunityPostRepository
import com.hayakai.data.repository.NewsRepository

class CommunityViewModel(
    private val communityPostRepository: CommunityPostRepository,
    private val newsRepository: NewsRepository
) :
    ViewModel() {

    val allPosts: LiveData<PagingData<PostItem>> =
        communityPostRepository.getAllPosts().cachedIn(viewModelScope)
    val myPosts: LiveData<PagingData<PostItem>> =
        communityPostRepository.getMyPosts().cachedIn(viewModelScope)

    fun deletePost(deletePostDto: DeletePostDto) = communityPostRepository.deletePost(deletePostDto)

    fun getAllNews(location: String) = newsRepository.getAllNews(location)

}