package com.unsoed.appscanning.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Reminder::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun reminderDao(): ReminderDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            // Gunakan instance yang sudah ada jika tersedia
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "reminder_database"
                )
                    // Tambahkan fallback agar tidak crash jika versi DB berubah
                    .fallbackToDestructiveMigration()
                    // Tambahkan allowMainThreadQueries() untuk debugging (opsional)
                    //.allowMainThreadQueries()
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}
