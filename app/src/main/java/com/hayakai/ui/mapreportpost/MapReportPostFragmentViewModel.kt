package com.hayakai.ui.mapreportpost

import androidx.lifecycle.ViewModel
import com.hayakai.data.remote.dto.DeleteReportMapDto
import com.hayakai.data.repository.MapReportRepository

class MapReportPostFragmentViewModel(
    private val mapReportRepository: MapReportRepository
) : ViewModel() {
    fun deleteReportMap(deleteReportMapDto: DeleteReportMapDto) =
        mapReportRepository.deleteMapReport(deleteReportMapDto)
}