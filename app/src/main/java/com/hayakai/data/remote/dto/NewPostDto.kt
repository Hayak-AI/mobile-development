package com.hayakai.data.remote.dto

class NewPostDto(
    val title: String,
    val content: String,
    val category: String,
    val location: LocationNewPost? = null
)

class LocationNewPost(
    val name: String,
    val latitude: Double,
    val longitude: Double
)