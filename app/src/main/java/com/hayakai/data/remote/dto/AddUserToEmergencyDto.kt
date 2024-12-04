package com.hayakai.data.remote.dto

class AddUserToEmergencyDto(
    val description: String = "",
    val location: LocationEmergency,
)

class LocationEmergency(
    val name: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
)