package com.hayakai.data.remote.response

import com.google.gson.annotations.SerializedName

data class AddUserToEmergencyResponse(

	@field:SerializedName("status")
	val status: String? = null
)
