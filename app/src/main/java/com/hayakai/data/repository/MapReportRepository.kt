package com.hayakai.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import com.google.gson.Gson
import com.hayakai.data.local.dao.MapReportDao
import com.hayakai.data.local.entity.MapReport
import com.hayakai.data.pref.UserPreference
import com.hayakai.data.remote.dto.DeleteReportMapDto
import com.hayakai.data.remote.dto.NewReportMapDto
import com.hayakai.data.remote.response.ErrorResponse
import com.hayakai.data.remote.retrofit.ApiService
import com.hayakai.utils.MyResult
import com.hayakai.utils.asJWT
import kotlinx.coroutines.flow.first
import okhttp3.MultipartBody
import retrofit2.HttpException

class MapReportRepository(
    private val mapReportDao: MapReportDao,
    private val apiService: ApiService,
    private val userPreference: UserPreference
) {


    fun getMapReports(): LiveData<MyResult<List<MapReport>>> = liveData {
        emit(MyResult.Loading)
        try {
            val response =
                apiService.getReportedMaps(userPreference.getSession().first().token.asJWT())
            val mapReports = response.data.map {
                MapReport(
                    it.reportId,
                    it.location.name,
                    it.description,
                    it.location.latitude,
                    it.location.longitude,
                    it.evidenceUrl,
                    it.verified,
                    it.user.id,
                    it.user.name,
                    it.user.image,
                    it.byMe
                )
            }
            mapReportDao.deleteAll()
            mapReportDao.insertAll(mapReports)
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
            emit(MyResult.Error(errorResponse.message))
        } catch (e: Exception) {
            emit(MyResult.Error(e.message ?: "An error occurred"))
        } finally {
            val localData: LiveData<MyResult<List<MapReport>>> =
                mapReportDao.getAllMapReports().map { MyResult.Success(it) }
            emitSource(localData)
        }
    }

    fun deleteMapReport(deleteReportMapDto: DeleteReportMapDto) = liveData {
        emit(MyResult.Loading)
        try {
            val response =
                apiService.deleteReportMap(
                    deleteReportMapDto,
                    userPreference.getSession().first().token.asJWT()
                )
            mapReportDao.delete(MapReport(deleteReportMapDto.report_id))
            emit(MyResult.Success(response.status))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
            emit(MyResult.Error(errorResponse.message))
        } catch (e: Exception) {
            emit(MyResult.Error(e.message ?: "An error occurred"))
        }
    }

    fun newMapReport(newReportMapDto: NewReportMapDto) = liveData {
        emit(MyResult.Loading)
        try {
            val response =
                apiService.reportMaps(
                    newReportMapDto,
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

    fun uploadEvidence(file: MultipartBody.Part) = liveData {
        emit(MyResult.Loading)
        try {
            val response = apiService.uploadEvidence(
                file,
                userPreference.getSession().first().token.asJWT()
            )
            emit(MyResult.Success(response.data))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
            emit(MyResult.Error(errorResponse.message))
        } catch (e: Exception) {
            emit(MyResult.Error(e.message ?: "An error occurred"))
        }
    }

    suspend fun clearLocalData() {
        mapReportDao.deleteAll()
    }


    companion object {
        @Volatile
        private var INSTANCE: MapReportRepository? = null

        fun getInstance(
            mapReportDao: MapReportDao,
            apiService: ApiService,
            userPreference: UserPreference
        ): MapReportRepository {
            return INSTANCE ?: synchronized(this) {
                val instance = MapReportRepository(mapReportDao, apiService, userPreference)
                INSTANCE = instance
                instance
            }
        }
    }
}