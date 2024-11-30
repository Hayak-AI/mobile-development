package com.hayakai.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.hayakai.data.local.entity.CommentReport

@Dao
interface CommentReportDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(commentReport: CommentReport)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(commentReport: List<CommentReport>)

    @Query("DELETE FROM commentreport WHERE report_id = :reportId")
    suspend fun deleteByReportId(reportId: Int)

    @Query("DELETE FROM commentreport")
    suspend fun deleteAll()

    @Query("SELECT * FROM commentreport WHERE report_id = :reportId ORDER BY created_at DESC")
    fun getAll(reportId: Int): LiveData<List<CommentReport>>

    @Delete
    suspend fun delete(mapReport: CommentReport)
}