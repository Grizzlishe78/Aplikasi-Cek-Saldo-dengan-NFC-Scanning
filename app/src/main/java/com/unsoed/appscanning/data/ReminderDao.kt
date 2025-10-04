package com.unsoed.appscanning.data

import androidx.room.*

@Dao
interface ReminderDao {

    @Insert
    suspend fun insert(reminder: Reminder): Long

    @Update
    suspend fun update(reminder: Reminder)

    @Delete
    suspend fun delete(reminder: Reminder)

    @Query("SELECT * FROM reminder ORDER BY time ASC")  // ✅ samakan nama tabel
    suspend fun getAllReminders(): List<Reminder>

    @Query("SELECT * FROM reminder WHERE id = :id LIMIT 1")  // ✅ samakan nama tabel
    suspend fun getReminderById(id: Int): Reminder?
}
