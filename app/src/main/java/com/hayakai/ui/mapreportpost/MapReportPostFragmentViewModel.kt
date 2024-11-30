package com.hayakai.ui.mapreportpost

import androidx.lifecycle.ViewModel
import com.hayakai.data.remote.dto.DeleteCommentDto
import com.hayakai.data.remote.dto.DeleteReportMapDto
import com.hayakai.data.remote.dto.NewCommentReportDto
import com.hayakai.data.repository.CommentRepository
import com.hayakai.data.repository.MapReportRepository

class MapReportPostFragmentViewModel(
    private val mapReportRepository: MapReportRepository,
    private val commentRepository: CommentRepository
) : ViewModel() {
    fun deleteReportMap(deleteReportMapDto: DeleteReportMapDto) =
        mapReportRepository.deleteMapReport(deleteReportMapDto)

    fun deleteCommentReport(deleteCommentDto: DeleteCommentDto) =
        commentRepository.deleteCommentReport(deleteCommentDto)

    fun getCommentReports(reportId: Int) = commentRepository.getReportComments(reportId)

    fun newCommentReport(newCommentReportDto: NewCommentReportDto) =
        commentRepository.newCommentReport(newCommentReportDto)
}