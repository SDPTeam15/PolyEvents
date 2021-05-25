package com.github.sdpteam15.polyevents.model.database.local.dao

import androidx.room.*
import com.github.sdpteam15.polyevents.model.database.local.entity.EventLocal

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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(events: List<EventLocal>)

    @Delete
    suspend fun delete(event: EventLocal)

    @Query("DELETE FROM event_table")
    suspend fun deleteAll()
}