package com.hayakai.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import com.google.gson.Gson
import com.hayakai.data.local.dao.CommunityPostDao
import com.hayakai.data.local.entity.CommunityPost
import com.hayakai.data.pref.UserPreference
import com.hayakai.data.remote.dto.DeletePostDto
import com.hayakai.data.remote.dto.NewPostDto
import com.hayakai.data.remote.dto.UpdatePostDto
import com.hayakai.data.remote.response.ErrorResponse
import com.hayakai.data.remote.retrofit.ApiService
import com.hayakai.utils.MyResult
import com.hayakai.utils.asJWT
import kotlinx.coroutines.flow.first
import retrofit2.HttpException

class CommunityPostRepository(
    private val communityPostDao: CommunityPostDao,
    private val apiService: ApiService,
    private val userPreference: UserPreference
) {


    fun getAllPosts(): LiveData<MyResult<List<CommunityPost>>> = liveData {
        emit(MyResult.Loading)
        try {
            val response =
                apiService.getAllPosts(
                    userPreference.getSession().first().token.asJWT()
                )
            val communityPostList = response.data.map {
                CommunityPost(
                    it.id,
                    it.title,
                    it.content,
                    it.category,
                    it.user.id,
                    it.user.name,
                    it.user.image,
                    it.byMe,
                    it.createdAt,
                    it.updatedAt,
                    it.location?.locationName ?: "",
                    it.location?.latitude ?: 0.0,
                    it.location?.longitude ?: 0.0,
                    it.totalComments
                )
            }
            communityPostDao.deleteAll()
            communityPostDao.insertAll(communityPostList)
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
            emit(MyResult.Error(errorResponse.message))
        } catch (e: Exception) {
            emit(MyResult.Error(e.message ?: "An error occurred"))
        } finally {
            val localData: LiveData<MyResult<List<CommunityPost>>> =
                communityPostDao.getAll().map { MyResult.Success(it) }
            emitSource(localData)
        }
    }

    fun getMyPosts(): LiveData<MyResult<List<CommunityPost>>> = liveData {
        emit(MyResult.Loading)
        try {
            val response =
                apiService.getMyPosts(
                    userPreference.getSession().first().token.asJWT()
                )
            val communityPostList = response.data.map {
                CommunityPost(
                    it.id,
                    it.title,
                    it.content,
                    it.category,
                    it.user.id,
                    it.user.name,
                    it.user.image,
                    it.byMe,
                    it.createdAt,
                    it.updatedAt,
                    it.location?.locationName ?: "",
                    it.location?.latitude ?: 0.0,
                    it.location?.longitude ?: 0.0,
                    it.totalComments
                )
            }
            communityPostDao.deleteMyPosts()
            communityPostDao.insertAll(communityPostList)
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
            emit(MyResult.Error(errorResponse.message))
        } catch (e: Exception) {
            emit(MyResult.Error(e.message ?: "An error occurred"))
        } finally {
            val localData: LiveData<MyResult<List<CommunityPost>>> =
                communityPostDao.getMyPosts().map { MyResult.Success(it) }
            emitSource(localData)
        }
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

    suspend fun clearLocalData() {
        communityPostDao.deleteAll()
    }


    companion object {
        @Volatile
        private var INSTANCE: CommunityPostRepository? = null

        fun getInstance(
            communityPostDao: CommunityPostDao,
            apiService: ApiService,
            userPreference: UserPreference
        ): CommunityPostRepository {
            return INSTANCE ?: synchronized(this) {
                val instance = CommunityPostRepository(communityPostDao, apiService, userPreference)
                INSTANCE = instance
                instance
            }
        }
    }
}