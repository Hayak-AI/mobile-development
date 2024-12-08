package com.hayakai.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.hayakai.data.local.entity.CommunityPost


@Dao
interface CommunityPostDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(communityPosts: List<CommunityPost>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(communityPost: CommunityPost)

    @Query("DELETE FROM communitypost")
    suspend fun deleteAll()

    @Query("SELECT * FROM communitypost WHERE by_me = 1 ORDER BY created_at DESC")
    fun getMyPosts(): LiveData<List<CommunityPost>>

    @Query("SELECT * FROM communitypost WHERE by_me = 0 ORDER BY created_at DESC")
    fun getExplorePosts(): LiveData<List<CommunityPost>>

    @Query("SELECT * FROM communitypost WHERE id = :id")
    fun getPostById(id: Int): LiveData<CommunityPost>

    @Query("SELECT * FROM communitypost WHERE user_id = :userId ORDER BY created_at DESC")
    fun getPostsByUserId(userId: Int): LiveData<List<CommunityPost>>

    @Query("SELECT * FROM communitypost WHERE category = :category ORDER BY created_at DESC")
    fun getPostsByCategory(category: String): LiveData<List<CommunityPost>>

    @Delete
    suspend fun deletePostById(communityPost: CommunityPost)

    @Query("DELETE FROM communitypost WHERE user_id = :userId")
    suspend fun deletePostsByUserId(userId: Int)

    @Query("DELETE FROM communitypost WHERE category = :category")
    suspend fun deletePostsByCategory(category: String)

    @Query("DELETE FROM communitypost WHERE by_me = 1")
    suspend fun deleteMyPosts()

    @Query("DELETE FROM communitypost WHERE by_me = 0")
    suspend fun deleteExplorePosts()

    @Update
    suspend fun update(communityPost: CommunityPost)

    @Query("SELECT * FROM communitypost ORDER BY created_at DESC")
    fun getAll(): LiveData<List<CommunityPost>>

}