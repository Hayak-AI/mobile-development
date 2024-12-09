package com.hayakai.data.remote.dto

data class GeminiDto(
    val contents: List<Content>,
)

data class Content(
    val parts: List<Part>,
)

data class Part(
    val text: String,
)
