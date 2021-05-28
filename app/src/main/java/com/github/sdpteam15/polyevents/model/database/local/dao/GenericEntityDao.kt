package com.github.sdpteam15.polyevents.model.database.local.dao

import androidx.room.*
import com.github.sdpteam15.polyevents.model.database.local.entity.EventLocal
import com.github.sdpteam15.polyevents.model.database.local.entity.GenericEntity

/**
 * Data access object for the GenericEntity entity on the local room database
 */
@Dao
interface GenericEntityDao {

    @Query("SELECT * FROM entity_table WHERE collection = :collection")
    suspend fun getAll(collection: String): List<GenericEntity>

    @Query("SELECT * FROM entity_table WHERE id = :id AND collection = :collection ")
    suspend fun get(id: String, collection: String): GenericEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(event: GenericEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(events: List<GenericEntity>)

    @Delete
    suspend fun delete(event: EventLocal)

    @Query("DELETE FROM entity_table")
    suspend fun deleteAll()

    @Query("SELECT MAX(update_time) FROM entity_table WHERE collection = :collection")
    suspend fun lastUpdate(collection: String): String?
}