package com.github.sdpteam15.polyevents.model.database.local.entity

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity

/**
 * A generic entity to be stored in the local database. Contains only the relevant information to
 * be displayed.
 * @property id id of the entity
 * @property collection collection of the entity
 * @property data serialized data
 * @property update_time time of the last update
 * with its own unique id and the collection.
 */
@Entity(tableName = "entity_table", primaryKeys = ["id", "collection"])
data class GenericEntity(
    @ColumnInfo(name = "id")
    @NonNull
    val id: String,
    @ColumnInfo(name = "collection")
    @NonNull
    val collection: String = "",
    @ColumnInfo(name = "data")
    val data: String? = null,
    @ColumnInfo(name = "update_time")
    val update_time: String? = null,
)