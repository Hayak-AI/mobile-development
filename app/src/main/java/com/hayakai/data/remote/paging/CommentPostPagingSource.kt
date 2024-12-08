package com.hayakai.data.remote.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.hayakai.data.pref.UserPreference
import com.hayakai.data.remote.response.PostDataItemComment
import com.hayakai.data.remote.retrofit.ApiService
import com.hayakai.utils.asJWT
import kotlinx.coroutines.flow.first

class CommentPostPagingSource(
    private val apiService: ApiService,
    private val userPreference: UserPreference,
    private val postId: Int
) : PagingSource<Int, PostDataItemComment>() {
    companion object {
        const val POST_STARTING_PAGE_INDEX = 0
    }

    override fun getRefreshKey(state: PagingState<Int, PostDataItemComment>): Int? {
        return null

    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PostDataItemComment> {
        return try {
            val position = params.key ?: POST_STARTING_PAGE_INDEX

            val response = apiService.getPostComments(
                postId,
                params.loadSize,
                params.key ?: POST_STARTING_PAGE_INDEX,
                userPreference.getSession().first().token.asJWT()
            )

            LoadResult.Page(
                data = response.data,
                prevKey = null,
                nextKey = if (response.data.isEmpty()) null else position + params.loadSize
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}