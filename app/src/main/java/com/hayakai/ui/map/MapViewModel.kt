package com.hayakai.ui.map

import androidx.lifecycle.ViewModel
import com.hayakai.data.repository.MapReportRepository
import com.hayakai.data.repository.NewsRepository

class MapViewModel(
    private val mapReportRepository: MapReportRepository,
    private val newsRepository: NewsRepository
) : ViewModel() {
    fun getMapReports() = mapReportRepository.getMapReports()

    fun getAllNews(location: String) = newsRepository.getAllNews(location)
}