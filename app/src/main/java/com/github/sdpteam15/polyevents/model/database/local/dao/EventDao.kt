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

    @Query("SELECT * FROM event_table WHERE event_id = :eventId")
    suspend fun getEventById(eventId: String): List<EventLocal>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(event: EventLocal)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(events: List<EventLocal>)

    @Query("DELETE FROM event_table WHERE is_limited = :limitedEvent")
    suspend fun deletedEventsWhereLimited(limitedEvent: Boolean)

    @Query("SELECT * FROM event_table WHERE is_limited = :limitedEvent")
    suspend fun getEventsWhereLimited(limitedEvent: Boolean): List<EventLocal>

    @Delete
    suspend fun delete(event: EventLocal)

    @Query("DELETE FROM event_table")
    suspend fun deleteAll()
}