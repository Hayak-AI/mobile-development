package com.hayakai.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.google.gson.Gson
import com.hayakai.data.local.dao.CommunityPostDao
import com.hayakai.data.local.entity.CommunityPost
import com.hayakai.data.pref.UserPreference
import com.hayakai.data.remote.dto.Content
import com.hayakai.data.remote.dto.DeletePostDto
import com.hayakai.data.remote.dto.GeminiDto
import com.hayakai.data.remote.dto.NewPostDto
import com.hayakai.data.remote.dto.Part
import com.hayakai.data.remote.dto.UpdatePostDto
import com.hayakai.data.remote.paging.ExplorePostPagingSource
import com.hayakai.data.remote.paging.MyPostPagingSource
import com.hayakai.data.remote.response.ErrorResponse
import com.hayakai.data.remote.response.PostItem
import com.hayakai.data.remote.retrofit.ApiService
import com.hayakai.utils.MyResult
import com.hayakai.utils.asJWT
import kotlinx.coroutines.flow.first
import retrofit2.HttpException

class CommunityPostRepository(
    private val communityPostDao: CommunityPostDao,
    private val apiService: ApiService,
    private val geminiApiService: ApiService,
    private val userPreference: UserPreference
) {


    fun getAllPosts(): LiveData<PagingData<PostItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5,
                enablePlaceholders = true,
                initialLoadSize = 5
            ),
            pagingSourceFactory = { ExplorePostPagingSource(apiService, userPreference) }
        ).liveData
    }

    fun getMyPosts(): LiveData<PagingData<PostItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5,
                enablePlaceholders = true,
                initialLoadSize = 5
            ),
            pagingSourceFactory = { MyPostPagingSource(apiService, userPreference) }
        ).liveData
    }

    fun deletePost(deletePostDto: DeletePostDto) = liveData {
        emit(MyResult.Loading)
        try {
            val response =
                apiService.deletePost(
                    deletePostDto,
                    userPreference.getSession().first().token.asJWT()
                )
            communityPostDao.deletePostById(CommunityPost(deletePostDto.post_id))
            emit(MyResult.Success(response.status))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
            emit(MyResult.Error(errorResponse.message))
        } catch (e: Exception) {
            emit(MyResult.Error(e.message ?: "An error occurred"))
        }
    }

    fun newPost(newPostDto: NewPostDto) = liveData {
        emit(MyResult.Loading)
        try {
            val response =
                apiService.newPost(
                    newPostDto,
                    userPreference.getSession().first().token.asJWT()
                )
            emit(MyResult.Success(response.status))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
            emit(MyResult.Error(errorResponse.message))
        } catch (e: Exception) {
            emit(MyResult.Error(e.message ?: "An error occurred"))
        }
    }

    fun updatePost(updatePostDto: UpdatePostDto) = liveData {
        emit(MyResult.Loading)
        try {
            val response =
                apiService.updatePost(
                    updatePostDto,
                    userPreference.getSession().first().token.asJWT()
                )
            emit(MyResult.Success(response.status))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
            emit(MyResult.Error(errorResponse.message))
        } catch (e: Exception) {
            emit(MyResult.Error(e.message ?: "An error occurred"))
        }
    }

    fun generate(text: String) = liveData {
        emit(MyResult.Loading)
        try {
            val response =
                geminiApiService.generate(
                    GeminiDto(
                        listOf(
                            Content(
                                listOf(
                                    Part(text)
                                )
                            )
                        )
                    ),
                )
            response.candidates.first().content.parts.first().text.let {
                emit(MyResult.Success(it))
            }
        } catch (e: HttpException) {
            emit(MyResult.Error("Error generating text"))
        } catch (e: Exception) {
            emit(MyResult.Error(e.message ?: "An error occurred"))
        }
    }

    suspend fun clearLocalData() {
        communityPostDao.deleteAll()
    }


    companion object {
        @Volatile
        private var INSTANCE: CommunityPostRepository? = null

        fun getInstance(
            communityPostDao: CommunityPostDao,
            apiService: ApiService,
            geminiApiService: ApiService,
            userPreference: UserPreference
        ): CommunityPostRepository {
            return INSTANCE ?: synchronized(this) {
                val instance = CommunityPostRepository(
                    communityPostDao,
                    apiService,
                    geminiApiService,
                    userPreference
                )
                INSTANCE = instance
                instance
            }
        }
    }
}