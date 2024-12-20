package com.hayakai.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import com.google.gson.Gson
import com.hayakai.data.local.dao.ContactDao
import com.hayakai.data.local.entity.Contact
import com.hayakai.data.pref.UserPreference
import com.hayakai.data.remote.dto.DeleteContactDto
import com.hayakai.data.remote.dto.NewContactDto
import com.hayakai.data.remote.dto.UpdateContactDto
import com.hayakai.data.remote.response.ErrorResponse
import com.hayakai.data.remote.retrofit.ApiService
import com.hayakai.utils.MyResult
import com.hayakai.utils.asJWT
import kotlinx.coroutines.flow.first
import retrofit2.HttpException

class ContactRepository(
    private val contactDao: ContactDao,
    private val apiService: ApiService,
    private val userPreference: UserPreference
) {


    fun getContacts(): LiveData<MyResult<List<Contact>>> = liveData {
        emit(MyResult.Loading)
        try {
            val response =
                apiService.getAllContacts(userPreference.getSession().first().token.asJWT())
            val contacts = response.data.map {
                Contact(it.contactId, it.name, it.phone, it.email, it.notify, it.message)
            }
            contactDao.deleteAll()
            contactDao.insertAll(contacts)
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
            emit(MyResult.Error(errorResponse.message))
        } catch (e: Exception) {
            emit(MyResult.Error(e.message ?: "An error occurred"))
        } finally {
            val localData: LiveData<MyResult<List<Contact>>> =
                contactDao.getAllContacts().map { MyResult.Success(it) }
            emitSource(localData)
        }
    }

    fun deleteContact(deleteContactDto: DeleteContactDto) = liveData {
        emit(MyResult.Loading)
        try {
            val response =
                apiService.deleteContact(
                    deleteContactDto,
                    userPreference.getSession().first().token.asJWT()
                )
            contactDao.delete(Contact(deleteContactDto.contact_id))
            emit(MyResult.Success(response.status))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
            emit(MyResult.Error(errorResponse.message))
        } catch (e: Exception) {
            emit(MyResult.Error(e.message ?: "An error occurred"))
        }
    }

    fun updateContact(updateContactDto: UpdateContactDto) = liveData {
        emit(MyResult.Loading)
        try {
            val response =
                apiService.updateContact(
                    updateContactDto,
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

    fun newContact(newContactDto: NewContactDto) = liveData {
        emit(MyResult.Loading)
        try {
            val response =
                apiService.addContact(
                    newContactDto,
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
        contactDao.deleteAll()
    }


    companion object {
        @Volatile
        private var INSTANCE: ContactRepository? = null

        fun getInstance(
            contactDao: ContactDao,
            apiService: ApiService,
            userPreference: UserPreference
        ): ContactRepository {
            return INSTANCE ?: synchronized(this) {
                val instance = ContactRepository(contactDao, apiService, userPreference)
                INSTANCE = instance
                instance
            }
        }
    }
}