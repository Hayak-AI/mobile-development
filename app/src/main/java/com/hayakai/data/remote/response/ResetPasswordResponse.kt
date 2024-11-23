package com.hayakai.data.remote.response

import com.google.gson.annotations.SerializedName

data class ResetPasswordResponse(

	@field:SerializedName("status")
	val status: String? = null
)
