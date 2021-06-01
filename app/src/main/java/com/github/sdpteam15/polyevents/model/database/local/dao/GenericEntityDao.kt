package com.github.sdpteam15.polyevents.model.database.local.dao

import androidx.room.*
import com.github.sdpteam15.polyevents.helper.HelperFunctions.thenReturn
import com.github.sdpteam15.polyevents.model.database.local.entity.GenericEntity
import com.github.sdpteam15.polyevents.model.database.local.room.LocalAdapter
import java.util.*

/**
 * Data access object for the GenericEntity entity on the local room database
 */
@Dao
interface GenericEntityDao {

    /**
     * Get a list of all elements in a collection
     * @param collection collection where are the element
     * @return the list of generic entity
     */
    @Query("SELECT * FROM entity_table WHERE collection = :collection")
    suspend fun getAll(collection: String): List<GenericEntity>

    /**
     * Get a element
     * @param id id of the element
     * @param collection collection where is the element
     * @return the generic entity
     */
    @Query("SELECT * FROM entity_table WHERE id = :id AND collection = :collection ")
    suspend fun get(id: String, collection: String): GenericEntity

    /**
     * Insert a element
     * @param element the element
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(element: GenericEntity)

    /**
     * Insert all a elements in the list
     * @param elements the element list
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(elements: List<GenericEntity>)

    /**
     * Delete a element from the local cash
     * @param element the element
     */
    @Delete
    suspend fun delete(element: GenericEntity)

    /**
     * Delete all from the local cash
     */
    @Query("DELETE FROM entity_table")
    suspend fun deleteAll()

    /**
     * Get the last time the collection has been updated
     * @param collection the collection
     * @return serialized version of the date
     */
    @Query("SELECT MAX(update_time) FROM entity_table WHERE collection = :collection")
    suspend fun lastUpdate(collection: String): String?


    suspend
            /**
             * Get the last time the collection has been updated
             * @param collection the collection
             * @return the date
             */
    fun lastUpdateDate(collection: String): Date? =
        lastUpdate(collection).thenReturn { LocalAdapter.SimpleDateFormat.parse(it) }
}

