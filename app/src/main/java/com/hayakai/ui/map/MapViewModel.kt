package com.hayakai.ui.map

import androidx.lifecycle.ViewModel
import com.hayakai.data.repository.MapReportRepository

class MapViewModel(
    private val mapReportRepository: MapReportRepository
) : ViewModel() {
    fun getMapReports() = mapReportRepository.getMapReports()
}