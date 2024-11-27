package com.hayakai.data.remote.response

import com.google.gson.annotations.SerializedName

data class GetReportMapsResponse(

    @field:SerializedName("data")
    val data: List<ReportedMap>? = null, // Daftar laporan peta

    @field:SerializedName("status")
    val status: String? = null
)

data class Location(

    @field:SerializedName("latitude")
    val latitude: String? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("longitude")
    val longitude: String? = null
)

data class ReportedMap(

    @field:SerializedName("report_id")
    val reportId: Int? = null,

    @field:SerializedName("verified")
    val verified: Boolean? = null,

    @field:SerializedName("description")
    val description: String? = null,

    @field:SerializedName("location")
    val location: Location? = null,

    @field:SerializedName("evidence_url")
    val evidenceUrl: String? = null
)
