package com.github.sdpteam15.polyevents.model.database.local.dao
import androidx.room.*
import com.github.sdpteam15.polyevents.model.database.local.entity.GenericEntity
import com.github.sdpteam15.polyevents.model.database.remote.DatabaseConstant.CollectionConstant
import com.github.sdpteam15.polyevents.model.database.local.entity.EventLocal

/**
 * Data access object for the GenericEntity entity on the local room database
 */
@Dao
interface GenericEntityDao {

    @Query("SELECT * FROM entity_table WHERE entity_collection LIKE :collection")
    suspend fun getAll(collection: CollectionConstant): List<GenericEntity>

    @Query("SELECT * FROM entity_table WHERE entity_id LIKE :id AND entity_collection LIKE :collection ")
    suspend fun getEventById(id: String, collection: CollectionConstant): GenericEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(event: GenericEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(events: List<GenericEntity>)

    @Delete
    suspend fun delete(event: EventLocal)

    @Query("DELETE FROM entity_table")
    suspend fun deleteAll()
}