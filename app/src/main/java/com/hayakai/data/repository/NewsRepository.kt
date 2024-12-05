package com.hayakai.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import com.google.gson.Gson
import com.hayakai.data.local.dao.NewsDao
import com.hayakai.data.local.entity.News
import com.hayakai.data.pref.UserPreference
import com.hayakai.data.remote.response.ErrorResponse
import com.hayakai.data.remote.retrofit.ApiService
import com.hayakai.utils.MyResult
import com.hayakai.utils.asJWT
import kotlinx.coroutines.flow.first
import retrofit2.HttpException

class NewsRepository(
    private val newsDao: NewsDao,
    private val apiService: ApiService,
    private val userPreference: UserPreference
) {


    fun getAllNews(location: String): LiveData<MyResult<List<News>>> = liveData {
        emit(MyResult.Loading)
        try {
            val response =
                apiService.getNews(
                    location,
                    userPreference.getSession().first().token.asJWT()
                )
            val newsList = response.data.news.map {
                News(
                    title = it.title,
                    link = it.link,
                    snippet = it.snippet,
                    displayLink = it.displayLink,
                    safetyScore = response.data.safetyScore,
                    image = it.pagemap?.cseThumbnail?.firstOrNull()?.src ?: ""
                )
            }
            newsDao.deleteAll()
            newsDao.insertAll(newsList)
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
            emit(MyResult.Error(errorResponse.message))
        } catch (e: Exception) {
            emit(MyResult.Error(e.message ?: "An error occurred"))
        } finally {
            val localData: LiveData<MyResult<List<News>>> =
                newsDao.getAllNews().map { MyResult.Success(it) }
            emitSource(localData)
        }
    }


    suspend fun clearLocalData() {
        newsDao.deleteAll()
    }


    companion object {
        @Volatile
        private var INSTANCE: NewsRepository? = null

        fun getInstance(
            newsDao: NewsDao,
            apiService: ApiService,
            userPreference: UserPreference
        ): NewsRepository {
            return INSTANCE ?: synchronized(this) {
                val instance = NewsRepository(newsDao, apiService, userPreference)
                INSTANCE = instance
                instance
            }
        }
    }
}