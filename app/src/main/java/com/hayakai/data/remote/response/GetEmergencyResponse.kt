package com.hayakai.data.remote.response

import com.google.gson.annotations.SerializedName

// Response utama dari API yang akan mengembalikan status dan data darurat
data class GetEmergencyResponse(

    @field:SerializedName("status")
    val responseStatus: String? = null, // Mengubah nama status untuk menghindari conflict

    @field:SerializedName("data")
    val emergencyData: List<EmergencyDataItem> = emptyList() // Mengganti nama 'data' menjadi 'emergencyData'
)

// Kelas untuk data lokasi, bisa jadi lokasi kejadian darurat
data class EmergencyLocation(

    @field:SerializedName("latitude")
    val latitude: Double,

    @field:SerializedName("name")
    val locationName: Double, // Mengubah nama 'name' menjadi 'locationName'

    @field:SerializedName("longitude")
    val longitude: Double
)

// Kelas untuk item darurat yang berisi informasi mengenai kejadian darurat
data class EmergencyDataItem(

    @field:SerializedName("emergency_id")
    val emergencyId: Int, // ID untuk setiap laporan darurat

    @field:SerializedName("description")
    val description: String, // Deskripsi dari kejadian darurat

    @field:SerializedName("location")
    val location: EmergencyLocation, // Lokasi kejadian darurat

    @field:SerializedName("evidence_url")
    val evidenceUrl: String? = null, // URL bukti terkait kejadian darurat

    @field:SerializedName("status")
    val status: String // Status dari kejadian darurat (misalnya: resolved)
)
