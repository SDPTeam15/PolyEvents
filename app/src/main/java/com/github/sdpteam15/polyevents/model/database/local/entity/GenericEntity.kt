package com.github.sdpteam15.polyevents.model.database.local.entity

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "entity_table", primaryKeys = arrayOf("entity_id", "entity_collection"))
data class GenericEntity(
    @ColumnInfo(name = "entity_id")
    @NonNull
    val id: String,
    @ColumnInfo(name = "entity_collection")
    @NonNull
    val collection: String = "",
    @ColumnInfo(name = "entity_data")
    val data: String? = null,
    @ColumnInfo(name = "entity_update")
    val update: String? = null,
)