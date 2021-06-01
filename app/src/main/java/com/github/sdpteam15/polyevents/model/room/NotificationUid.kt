package com.github.sdpteam15.polyevents.model.room

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

const val SENTINEL_VALUE = 0

/**
 * A local uid generator for notifications on the device. It's designed as a singleton, there's
 * never going to be more than one row for this table.
 * @property id sentinel value serving as primary key, which ensures there's not going
 * to be more than one row for this table.
 * @property uid the actual uid to be generated for a notification. When we generate a uid for
 * a notification, we increment it to get a unique id for the next notification.
 */
@Entity(tableName = "notification_uid_table")
data class NotificationUid(
        @PrimaryKey
        @ColumnInfo(name = "id")
        @NonNull
        val id: Int = SENTINEL_VALUE,
        @ColumnInfo(name = "notification_uid")
        @NonNull
        val uid: Int = 0
)