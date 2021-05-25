package com.github.sdpteam15.polyevents.model.database.local.entity

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "entity_table")
data class GenericEntity(
    @PrimaryKey
    @ColumnInfo(name = "entity_id")
    @NonNull
    val id: String,
    @PrimaryKey
    @ColumnInfo(name = "entity_collection")
    val collection: String? = null,
    @ColumnInfo(name = "entity_data")
    val data: String? = null,
    @ColumnInfo(name = "entity_update")
    val update: String? = null,
)