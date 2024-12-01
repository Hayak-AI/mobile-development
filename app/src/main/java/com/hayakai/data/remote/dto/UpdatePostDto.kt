package com.hayakai.data.remote.dto

class UpdatePostDto(
    val post_id: Int,
    val title: String,
    val content: String,
    val category: String,
    val location: LocationUpdatePost? = null
)

class LocationUpdatePost(
    val name: String,
    val latitude: Double,
    val longitude: Double
)