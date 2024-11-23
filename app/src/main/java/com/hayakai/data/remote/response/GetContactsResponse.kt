package com.hayakai.data.remote.response

import com.google.gson.annotations.SerializedName

data class GetContactsResponse(

	@field:SerializedName("data")
	val data: List<DataItem?>? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class DataItem(

	@field:SerializedName("phone")
	val phone: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("contact_id")
	val contactId: Int? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("email")
	val email: String? = null,

	@field:SerializedName("notify")
	val notify: Boolean? = null
)
