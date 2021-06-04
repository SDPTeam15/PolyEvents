package com.github.sdpteam15.polyevents.model.database.local.dao

import androidx.room.*
import com.github.sdpteam15.polyevents.helper.HelperFunctions.apply
import com.github.sdpteam15.polyevents.model.database.local.adapter.LocalAdapter
import com.github.sdpteam15.polyevents.model.database.local.entity.GenericEntity

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
     * @return the generic entity in a list
     */
    @Query("SELECT * FROM entity_table WHERE id = :id AND collection = :collection")
    suspend fun getList(id: String, collection: String): List<GenericEntity>

    /**
     * Get a element
     * @param id id of the element
     * @param collection collection where is the element
     * @return the generic entity
     */
    suspend fun get(id: String, collection: String): GenericEntity? {
        val list = getList(id, collection)
        return if (list.isEmpty()) null
        else list[0]
    }

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
     * Delete all from the local cash in the collection
     * @param collection collection to clean
     */
    @Query("DELETE FROM entity_table WHERE id = :id AND collection = :collection")
    suspend fun delete(id: String, collection: String)

    /**
     * Delete all from the local cash
     */
    @Query("DELETE FROM entity_table")
    suspend fun deleteAll()

    /**
     * Delete all from the local cash in the collection
     * @param collection collection to clean
     */
    @Query("DELETE FROM entity_table WHERE collection = :collection")
    suspend fun deleteAll(collection: String)

    /**
     * Get the last time the collection has been updated
     * @param collection the collection
     * @return serialized version of the date
     */
    @Query("SELECT MAX(update_time) FROM entity_table WHERE collection = :collection")
    suspend fun lastUpdate(collection: String): String?

    /**
     * Get the last time the collection has been updated
     * @param collection the collection
     * @return the date
     */
    suspend fun lastUpdateDate(collection: String) =
        lastUpdate(collection).apply {
            LocalAdapter.SimpleDateFormat.parse(it)
        }
}


