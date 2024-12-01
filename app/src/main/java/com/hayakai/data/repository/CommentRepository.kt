package com.hayakai.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import com.google.gson.Gson
import com.hayakai.data.local.dao.CommentPostDao
import com.hayakai.data.local.dao.CommentReportDao
import com.hayakai.data.local.entity.CommentPost
import com.hayakai.data.local.entity.CommentReport
import com.hayakai.data.pref.UserPreference
import com.hayakai.data.remote.dto.DeleteCommentDto
import com.hayakai.data.remote.dto.NewCommentReportDto
import com.hayakai.data.remote.dto.NewPostCommentDto
import com.hayakai.data.remote.response.ErrorResponse
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


    fun getReportComments(reportId: Int): LiveData<MyResult<List<CommentReport>>> = liveData {
        emit(MyResult.Loading)
        try {
            val response =
                apiService.getReportComments(
                    reportId,
                    userPreference.getSession().first().token.asJWT()
                )
            val commentReports = response.data.map {
                CommentReport(
                    it.commentId,
                    it.reportId,
                    it.content,
                    it.user.id,
                    it.user.name,
                    it.user.profilePhoto,
                    it.byMe,
                    it.createdAt
                )
            }
            commentReportDao.deleteByReportId(reportId)
            commentReportDao.insertAll(commentReports)
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
            emit(MyResult.Error(errorResponse.message))
        } catch (e: Exception) {
            emit(MyResult.Error(e.message ?: "An error occurred"))
        } finally {
            val localData: LiveData<MyResult<List<CommentReport>>> =
                commentReportDao.getAll(reportId).map { MyResult.Success(it) }
            emitSource(localData)
        }
    }

    fun deleteCommentReport(deleteCommentDto: DeleteCommentDto) = liveData {
        emit(MyResult.Loading)
        try {
            val response =
                apiService.deleteReportMap(
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

    fun getPostComments(postId: Int): LiveData<MyResult<List<CommentPost>>> = liveData {
        emit(MyResult.Loading)
        try {
            val response =
                apiService.getPostComments(
                    postId,
                    userPreference.getSession().first().token.asJWT()
                )
            val postCommentList = response.data.map {
                CommentPost(
                    it.commentId,
                    it.postId,
                    it.content,
                    it.user.id,
                    it.user.name,
                    it.user.profilePhoto,
                    it.byMe,
                    it.createdAt
                )
            }
            commentPostDao.deleteByPostId(postId)
            commentPostDao.insertAll(postCommentList)
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
            emit(MyResult.Error(errorResponse.message))
        } catch (e: Exception) {
            emit(MyResult.Error(e.message ?: "An error occurred"))
        } finally {
            val localData: LiveData<MyResult<List<CommentPost>>> =
                commentPostDao.getAll(postId).map { MyResult.Success(it) }
            emitSource(localData)
        }
    }

    fun deleteCommentPost(deleteCommentDto: DeleteCommentDto) = liveData {
        emit(MyResult.Loading)
        try {
            val response =
                apiService.deleteReportMap(
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