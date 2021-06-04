package com.github.sdpteam15.polyevents.model.database.local.entity

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * A class representing user settings and preferences for the application.
 * There will only be a single instance of UserSettings in the local cache, pertaining to the current user.
 * But in remote the user_uid will serve to retrieve the user settings of a user.
 * Since there will only be a single instance of UserSettings, and room needs a primary key
 * to store instances, we store a default constant value that will serve as primary key for that instance.
 * @property userUid: The user uid these settings belong to. Note that while using the app
 * the user_uid will be reverted to default. It's only when we store or retrieve the user settings
 * in the remote database, that we put in the actual user uid.
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
    val userUid: String = DEFAULT_ID,
    @ColumnInfo(name = "track_location")
    var trackLocation: Boolean = true,
    @ColumnInfo(name = "is_sending_location_on")
    var isSendingLocationOn: Boolean = true,
    @ColumnInfo(name = "location_id")
    var locationId: String? = null
) {
    companion object {
        const val DEFAULT_ID = "0"
    }
}