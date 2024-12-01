package com.hayakai.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.hayakai.data.local.entity.CommentPost

@Dao
interface CommentPostDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(commentPost: CommentPost)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(commentPost: List<CommentPost>)

    @Query("DELETE FROM commentpost WHERE post_id = :postId")
    suspend fun deleteByPostId(postId: Int)

    @Query("DELETE FROM commentpost")
    suspend fun deleteAll()

    @Query("SELECT * FROM commentpost WHERE post_id = :postId ORDER BY created_at DESC")
    fun getAll(postId: Int): LiveData<List<CommentPost>>

    @Delete
    suspend fun delete(commentPost: CommentPost)
}