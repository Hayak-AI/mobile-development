package com.hayakai.data.remote.response

import com.google.gson.annotations.SerializedName

data class GetReportMapsResponse(

    @field:SerializedName("data")
    val data: List<ReportedMap>, // Daftar laporan peta

    @field:SerializedName("status")
    val status: String? = null
)

data class Location(

    @field:SerializedName("latitude")
    val latitude: Double,

    @field:SerializedName("name")
    val name: String,

    @field:SerializedName("longitude")
    val longitude: Double
)

data class ReportedMap(

    @field:SerializedName("report_id")
    val reportId: Int,

    @field:SerializedName("verified")
    val verified: Boolean,

    @field:SerializedName("description")
    val description: String,

    @field:SerializedName("location")
    val location: Location,

    @field:SerializedName("evidence_url")
    val evidenceUrl: String? = null,

    @field:SerializedName("by_me")
    val byMe: Boolean,

    @field:SerializedName("user")
    val user: UserData
)

data class UserData(
    @field:SerializedName("name")
    val name: String,

    @field:SerializedName("user_id")
    val id: Int,

    @field:SerializedName("profile_photo")
    val image: String
)

