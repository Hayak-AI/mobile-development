package com.hayakai.data.local.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.hayakai.data.local.dao.CommentReportDao
import com.hayakai.data.local.entity.CommentReport


@Database(entities = [CommentReport::class], version = 1, exportSchema = false)
abstract class CommentReportRoomDatabase : RoomDatabase() {
    abstract fun commentReportDao(): CommentReportDao

    companion object {
        @Volatile
        private var INSTANCE: CommentReportRoomDatabase? = null

        @JvmStatic
        fun getDatabase(context: Context): CommentReportRoomDatabase {
            if (INSTANCE == null) {
                synchronized(CommentReportRoomDatabase::class.java) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        CommentReportRoomDatabase::class.java, "commentreport_database"
                    )
                        .build()
                }
            }
            return INSTANCE as CommentReportRoomDatabase
        }
    }
}