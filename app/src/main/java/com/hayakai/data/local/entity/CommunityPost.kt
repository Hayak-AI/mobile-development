package com.hayakai.data.local.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity
@Parcelize
class CommunityPost(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "id")
    var id: Int = 0,

    @ColumnInfo(name = "title")
    var title: String = "",

    @ColumnInfo(name = "content")
    var content: String = "",

    @ColumnInfo(name = "category")
    var category: String = "",

    @ColumnInfo(name = "user_id")
    var userId: Int = 0,

    @ColumnInfo(name = "user_name")
    var userName: String = "",

    @ColumnInfo(name = "user_image")
    var userImage: String? = "",

    @ColumnInfo(name = "by_me")
    var byMe: Boolean = false,

    @ColumnInfo(name = "created_at")
    var createdAt: String = "",

    @ColumnInfo(name = "updated_at")
    var updatedAt: String = "",

    @ColumnInfo(name = "location_name")
    var locationName: String = "",

    @ColumnInfo(name = "latitude")
    var latitude: Double = 0.0,

    @ColumnInfo(name = "longitude")
    var longitude: Double = 0.0,
) : Parcelable