package com.hayakai.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.google.gson.Gson
import com.hayakai.data.local.dao.CommentPostDao
import com.hayakai.data.local.dao.CommentReportDao
import com.hayakai.data.local.entity.CommentPost
import com.hayakai.data.local.entity.CommentReport
import com.hayakai.data.pref.UserPreference
import com.hayakai.data.remote.dto.DeleteCommentDto
import com.hayakai.data.remote.dto.NewCommentReportDto
import com.hayakai.data.remote.dto.NewPostCommentDto
import com.hayakai.data.remote.paging.CommentPostPagingSource
import com.hayakai.data.remote.paging.CommentReportPagingSource
import com.hayakai.data.remote.response.DataItemComment
import com.hayakai.data.remote.response.ErrorResponse
import com.hayakai.data.remote.response.PostDataItemComment
import com.hayakai.data.remote.retrofit.ApiService
import com.hayakai.utils.MyResult
import com.hayakai.utils.asJWT
import kotlinx.coroutines.flow.first
import retrofit2.HttpException

class CommentRepository(
    private val commentReportDao: CommentReportDao,
    private val commentPostDao: CommentPostDao,
    private val apiService: ApiService,
    private val userPreference: UserPreference
) {


    fun getReportComments(reportId: Int): LiveData<PagingData<DataItemComment>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5,
                enablePlaceholders = true,
                initialLoadSize = 5
            ),
            pagingSourceFactory = {
                CommentReportPagingSource(
                    apiService,
                    userPreference,
                    reportId
                )
            }
        ).liveData
    }

    fun deleteCommentReport(deleteCommentDto: DeleteCommentDto) = liveData {
        emit(MyResult.Loading)
        try {
            val response =
                apiService.deleteReportComment(
                    deleteCommentDto,
                    userPreference.getSession().first().token.asJWT()
                )
            commentReportDao.delete(CommentReport(deleteCommentDto.comment_id))
            emit(MyResult.Success(response.status))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
            emit(MyResult.Error(errorResponse.message))
        } catch (e: Exception) {
            emit(MyResult.Error(e.message ?: "An error occurred"))
        }
    }

    fun newCommentReport(newCommentReportDto: NewCommentReportDto) = liveData {
        emit(MyResult.Loading)
        try {
            val response =
                apiService.newCommentReport(
                    newCommentReportDto,
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

    fun newPostComment(newPostCommentDto: NewPostCommentDto) = liveData {
        emit(MyResult.Loading)
        try {
            val response =
                apiService.newPostComment(
                    newPostCommentDto,
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

    fun getPostComments(postId: Int): LiveData<PagingData<PostDataItemComment>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5,
                enablePlaceholders = true,
                initialLoadSize = 5
            ),
            pagingSourceFactory = { CommentPostPagingSource(apiService, userPreference, postId) }
        ).liveData
    }

    fun deleteCommentPost(deleteCommentDto: DeleteCommentDto) = liveData {
        emit(MyResult.Loading)
        try {
            val response =
                apiService.deletePostComment(
                    deleteCommentDto,
                    userPreference.getSession().first().token.asJWT()
                )
            commentPostDao.delete(CommentPost(deleteCommentDto.comment_id))
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
        commentReportDao.deleteAll()
        commentPostDao.deleteAll()
    }


    companion object {
        @Volatile
        private var INSTANCE: CommentRepository? = null

        fun getInstance(
            commentReportDao: CommentReportDao,
            commentPostDao: CommentPostDao,
            apiService: ApiService,
            userPreference: UserPreference
        ): CommentRepository {
            return INSTANCE ?: synchronized(this) {
                val instance =
                    CommentRepository(commentReportDao, commentPostDao, apiService, userPreference)
                INSTANCE = instance
                instance
            }
        }
    }
}