package com.hayakai.data.local.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.hayakai.data.local.dao.ContactDao
import com.hayakai.data.local.entity.Contact

@Database(entities = [Contact::class], version = 1)
abstract class ContactRoomDatabase : RoomDatabase() {
    abstract fun contactDao(): ContactDao

    companion object {
        @Volatile
        private var INSTANCE: ContactRoomDatabase? = null

        @JvmStatic
        fun getDatabase(context: Context): ContactRoomDatabase {
            if (INSTANCE == null) {
                synchronized(ContactRoomDatabase::class.java) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        ContactRoomDatabase::class.java, "contact_database"
                    )
                        .build()
                }
            }
            return INSTANCE as ContactRoomDatabase
        }
    }
}