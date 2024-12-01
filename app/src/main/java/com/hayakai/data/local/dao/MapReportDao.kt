package com.hayakai.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.hayakai.data.local.entity.MapReport

@Dao
interface MapReportDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(mapReport: MapReport)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(mapReport: List<MapReport>)

    @Query("DELETE FROM mapreport")
    suspend fun deleteAll()

    @Query("SELECT * FROM mapreport ORDER BY id ASC")
    fun getAllMapReports(): LiveData<List<MapReport>>

    @Delete
    suspend fun delete(mapReport: MapReport)
}