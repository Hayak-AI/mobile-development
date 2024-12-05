package com.hayakai.data.local.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity
@Parcelize
class News(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0,

    @ColumnInfo(name = "title")
    var title: String = "",

    @ColumnInfo(name = "snippet")
    var snippet: String = "",

    @ColumnInfo(name = "link")
    var link: String = "",

    @ColumnInfo(name = "displayLink")
    var displayLink: String = "",

    @ColumnInfo(name = "image")
    var image: String = "",

    @ColumnInfo(name = "safetyScore")
    var safetyScore: Int = 0
) : Parcelable