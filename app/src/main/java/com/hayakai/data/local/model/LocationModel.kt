package com.hayakai.data.local.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class LocationModel(
    val name: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
) : Parcelable