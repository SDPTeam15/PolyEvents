package com.github.sdpteam15.polyevents.database.dao

import androidx.room.*
import com.github.sdpteam15.polyevents.model.room.EventLocal

/**
 * Data access object for the event entity on the local room database
 */
@Dao
interface EventDao {

    @Query("SELECT * FROM event_table")
    suspend fun getAll(): List<EventLocal>

    @Query("SELECT * FROM event_table WHERE event_id LIKE :eventId")
    suspend fun getEventById(eventId: String): EventLocal

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(event: EventLocal)

    @Delete
    suspend fun delete(event: EventLocal)

    @Query("DELETE FROM event_table")
    suspend fun deleteAll()
}