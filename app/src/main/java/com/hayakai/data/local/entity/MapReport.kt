package com.hayakai.data.local.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize


@Entity
@Parcelize
data class MapReport(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "id")
    var id: Int = 0,

    @ColumnInfo(name = "name")
    var name: String = "",

    @ColumnInfo(name = "description")
    var description: String = "",

    @ColumnInfo(name = "latitude")
    var latitude: Double = 0.0,

    @ColumnInfo(name = "longitude")
    var longitude: Double = 0.0,

    @ColumnInfo(name = "evidence_url")
    var evidenceUrl: String? = "",

    @ColumnInfo(name = "verified")
    var verified: Boolean = false,

    @ColumnInfo(name = "user_id")
    var userId: Int = 0,

    @ColumnInfo(name = "user_name")
    var userName: String = "",

    @ColumnInfo(name = "user_image")
    var userImage: String? = "",

    @ColumnInfo(name = "by_me")
    var byMe: Boolean = false
) : Parcelable
