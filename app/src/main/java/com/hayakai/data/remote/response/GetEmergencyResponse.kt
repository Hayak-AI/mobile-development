package com.hayakai.data.remote.response

import com.google.gson.annotations.SerializedName

// Response utama dari API yang akan mengembalikan status dan data darurat
data class GetEmergencyResponse(

    @field:SerializedName("status")
    val responseStatus: String? = null, // Mengubah nama status untuk menghindari conflict

    @field:SerializedName("data")
    val emergencyData: List<EmergencyDataItem?>? = null // Mengganti nama 'data' menjadi 'emergencyData'
)

// Kelas untuk data lokasi, bisa jadi lokasi kejadian darurat
data class EmergencyLocation(

    @field:SerializedName("latitude")
    val latitude: String? = null,

    @field:SerializedName("name")
    val locationName: String? = null, // Mengubah nama 'name' menjadi 'locationName'

    @field:SerializedName("longitude")
    val longitude: String? = null
)

// Kelas untuk item darurat yang berisi informasi mengenai kejadian darurat
data class EmergencyDataItem(

    @field:SerializedName("emergency_id")
    val emergencyId: Int? = null, // ID untuk setiap laporan darurat

    @field:SerializedName("description")
    val emergencyDescription: String? = null, // Deskripsi dari kejadian darurat

    @field:SerializedName("location")
    val emergencyLocation: EmergencyLocation? = null, // Lokasi kejadian darurat

    @field:SerializedName("evidence_url")
    val evidenceUrl: String? = null, // URL bukti terkait kejadian darurat

    @field:SerializedName("status")
    val emergencyStatus: String? = null // Status dari kejadian darurat (misalnya: resolved)
)
