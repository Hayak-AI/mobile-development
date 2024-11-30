package com.hayakai.data.remote.dto


class NewReportMapDto(
    val description: String,
    val evidence_url: String,
    val location: LocationData
)

class LocationData(
    val name: String,
    val latitude: Double,
    val longitude: Double
)