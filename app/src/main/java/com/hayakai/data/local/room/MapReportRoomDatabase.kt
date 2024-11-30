package com.hayakai.data.local.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.hayakai.data.local.dao.MapReportDao
import com.hayakai.data.local.entity.MapReport

@Database(entities = [MapReport::class], version = 2)
abstract class MapReportRoomDatabase : RoomDatabase() {
    abstract fun mapReportDao(): MapReportDao

    companion object {
        @Volatile
        private var INSTANCE: MapReportRoomDatabase? = null

        @JvmStatic
        fun getDatabase(context: Context): MapReportRoomDatabase {
            if (INSTANCE == null) {
                synchronized(MapReportRoomDatabase::class.java) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        MapReportRoomDatabase::class.java, "mapreport_database"
                    )
                        .build()
                }
            }
            return INSTANCE as MapReportRoomDatabase
        }
    }

}