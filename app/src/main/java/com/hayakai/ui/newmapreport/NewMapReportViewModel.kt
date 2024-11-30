package com.hayakai.ui.newmapreport

import androidx.lifecycle.ViewModel
import com.hayakai.data.remote.dto.NewReportMapDto
import com.hayakai.data.repository.MapReportRepository
import okhttp3.MultipartBody

class NewMapReportViewModel(
    private val mapReportRepository: MapReportRepository
) : ViewModel() {

    fun newMapReport(newReportMapDto: NewReportMapDto) =
        mapReportRepository.newMapReport(newReportMapDto)

    fun uploadEvidence(file: MultipartBody.Part) = mapReportRepository.uploadEvidence(file)
}