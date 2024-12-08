package com.hayakai.ui.mapreportpost

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.hayakai.data.remote.dto.DeleteCommentDto
import com.hayakai.data.remote.dto.DeleteReportMapDto
import com.hayakai.data.remote.dto.NewCommentReportDto
import com.hayakai.data.remote.response.DataItemComment
import com.hayakai.data.repository.CommentRepository
import com.hayakai.data.repository.MapReportRepository

class MapReportPostFragmentViewModel(
    private val mapReportRepository: MapReportRepository,
    private val commentRepository: CommentRepository
) : ViewModel() {
    lateinit var reportComments: LiveData<PagingData<DataItemComment>>

    fun deleteReportMap(deleteReportMapDto: DeleteReportMapDto) =
        mapReportRepository.deleteMapReport(deleteReportMapDto)

    fun deleteCommentReport(deleteCommentDto: DeleteCommentDto) =
        commentRepository.deleteCommentReport(deleteCommentDto)

    fun getReportComments(reportId: Int): LiveData<PagingData<DataItemComment>> {
        reportComments = commentRepository.getReportComments(reportId).cachedIn(viewModelScope)
        return reportComments
    }

    fun newCommentReport(newCommentReportDto: NewCommentReportDto) =
        commentRepository.newCommentReport(newCommentReportDto)
}