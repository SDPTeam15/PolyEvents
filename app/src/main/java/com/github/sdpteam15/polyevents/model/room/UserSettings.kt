package com.github.sdpteam15.polyevents.model.room

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * A class representing user settings and preferences for the application.
 * Since there will only be a single instance of UserSettings, and room needs a primary key
 * to store instances, we store a default constant value that will serve as primary key for that instance.
 * @property userUid: the default id for the user settings instance
 * @property trackLocation: user setting to allow the app to track his location (e.g. for heat map functionality)
 * @property isSendingLocationOn: flag to indicate whether the user allows sending his location
 * @property locationId: the location id of the device
 */
@Entity(tableName = "user_settings_table")
data class UserSettings(
        @PrimaryKey
        @ColumnInfo(name = "user_uid")
        @NonNull
        // Ideally should have its setter private but Room needs it to reconstruct objects
        val userUid: Int = DEFAULT_ID,
        @ColumnInfo(name = "track_location")
        val trackLocation: Boolean = false,
        @ColumnInfo(name = "is_sending_location_on")
        val isSendingLocationOn: Boolean = false,
        @ColumnInfo(name = "location_id")
        val locationId: String? = null
) {
        companion object {
                const val DEFAULT_ID = 0
        }
}