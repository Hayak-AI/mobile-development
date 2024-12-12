package com.hayakai.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.hayakai.data.local.entity.News
import kotlinx.coroutines.flow.Flow

@Dao
interface NewsDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(news: News)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(news: List<News>)

    @Query("DELETE FROM news")
    suspend fun deleteAll()

    @Query("SELECT * FROM news ORDER BY id ASC")
    fun getAllNews(): LiveData<List<News>>

    @Delete
    suspend fun delete(news: News)

    @Query("SELECT COUNT(*) FROM news")
    fun getNewsCount(): Flow<Int>
}