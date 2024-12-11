package com.hayakai.data.local.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.hayakai.data.local.dao.CommunityPostDao
import com.hayakai.data.local.entity.CommunityPost

@Database(entities = [CommunityPost::class], version = 1, exportSchema = false)
abstract class CommunityPostRoomDatabase : RoomDatabase() {
    abstract fun communityPostDao(): CommunityPostDao

    companion object {
        @Volatile
        private var INSTANCE: CommunityPostRoomDatabase? = null

        @JvmStatic
        fun getDatabase(context: Context): CommunityPostRoomDatabase {
            if (INSTANCE == null) {
                synchronized(CommunityPostRoomDatabase::class.java) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        CommunityPostRoomDatabase::class.java, "communitypost_database"
                    )
                        .build()
                }
            }
            return INSTANCE as CommunityPostRoomDatabase
        }
    }
}