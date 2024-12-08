package com.hayakai.data.remote.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.hayakai.data.pref.UserPreference
import com.hayakai.data.remote.response.DataItemComment
import com.hayakai.data.remote.retrofit.ApiService
import com.hayakai.utils.asJWT
import kotlinx.coroutines.flow.first

class CommentReportPagingSource(
    private val apiService: ApiService,
    private val userPreference: UserPreference,
    private val postId: Int
) : PagingSource<Int, DataItemComment>() {
    companion object {
        const val POST_STARTING_PAGE_INDEX = 0
    }

    override fun getRefreshKey(state: PagingState<Int, DataItemComment>): Int? {
        return null

    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, DataItemComment> {
        return try {
            val position = params.key ?: POST_STARTING_PAGE_INDEX

            val response = apiService.getReportComments(
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