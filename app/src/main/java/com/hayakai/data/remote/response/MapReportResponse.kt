package com.hayakai.data.remote.response

import com.google.gson.annotations.SerializedName

data class MapReportResponse(
    @SerializedName("status")
    val status: String,
    @SerializedName("data")
    val data: List<MapReport>?
)

data class MapReport(
    @SerializedName("report_id")
    val reportId: Int,
    @SerializedName("description")
    val description: String,
    @SerializedName("evidence_url")
    val evidenceUrl: String,
    @SerializedName("verified")
    val verified: Boolean,
    @SerializedName("location")
    val location: Location
)

data class Location(
    @SerializedName("name")
    val name: String,
    @SerializedName("latitude")
    val latitude: Double,
    @SerializedName("longitude")
    val longitude: Double
)
