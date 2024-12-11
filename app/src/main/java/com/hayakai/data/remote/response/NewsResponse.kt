package com.hayakai.data.remote.response

import com.google.gson.annotations.SerializedName

data class NewsResponse(

    @field:SerializedName("data")
    val data: NewsData,

    @field:SerializedName("status")
    val status: String
)

data class NewsData(

    @field:SerializedName("news")
    val news: List<NewsItem> = emptyList(),

    @field:SerializedName("safetyScore")
    val safetyScore: Int
)

data class NewsItem(

    @field:SerializedName("snippet")
    val snippet: String,

    @field:SerializedName("displayLink")
    val displayLink: String,

    @field:SerializedName("link")
    val link: String,

    @field:SerializedName("title")
    val title: String,

    @field:SerializedName("pagemap")
    val pagemap: Pagemap?
)

data class Pagemap(

    @field:SerializedName("cse_thumbnail")
    val cseThumbnail: List<CseThumbnailItem> = emptyList()
)

data class CseThumbnailItem(

    @field:SerializedName("src")
    val src: String
)