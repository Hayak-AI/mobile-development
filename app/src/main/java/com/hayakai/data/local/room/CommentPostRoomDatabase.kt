package com.hayakai.data.local.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.hayakai.data.local.dao.CommentPostDao
import com.hayakai.data.local.entity.CommentPost


@Database(entities = [CommentPost::class], version = 1, exportSchema = false)
abstract class CommentPostRoomDatabase : RoomDatabase() {
    abstract fun commentPostDao(): CommentPostDao

    companion object {
        @Volatile
        private var INSTANCE: CommentPostRoomDatabase? = null

        @JvmStatic
        fun getDatabase(context: Context): CommentPostRoomDatabase {
            if (INSTANCE == null) {
                synchronized(CommentPostRoomDatabase::class.java) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        CommentPostRoomDatabase::class.java, "commentpost_database"
                    )
                        .build()
                }
            }
            return INSTANCE as CommentPostRoomDatabase
        }
    }
}