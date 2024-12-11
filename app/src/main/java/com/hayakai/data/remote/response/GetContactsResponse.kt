package com.hayakai.data.remote.response

import com.google.gson.annotations.SerializedName

data class GetContactsResponse(

    @field:SerializedName("data")
    val data: List<DataItem> = emptyList(),

    @field:SerializedName("status")
    val status: String? = null
)

data class DataItem(

    @field:SerializedName("phone")
    val phone: String,

    @field:SerializedName("name")
    val name: String,

    @field:SerializedName("contact_id")
    val contactId: Int,

    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("email")
    val email: String,

    @field:SerializedName("notify")
    val notify: Boolean
)
