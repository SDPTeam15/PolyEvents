package com.github.sdpteam15.polyevents.model.database.local.entity

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

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
    @ColumnInfo(name = "update")
    val update: String? = null,
)