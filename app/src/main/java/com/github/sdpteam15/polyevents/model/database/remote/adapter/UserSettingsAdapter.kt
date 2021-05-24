package com.github.sdpteam15.polyevents.model.database.remote.adapter

import com.github.sdpteam15.polyevents.model.database.remote.DatabaseConstant.UserSettingsConstant.*
import com.github.sdpteam15.polyevents.model.room.UserSettings

/**
 * A class for converting between user setting entities in our code and
 * documents in the Firebase database. Not unlike the conversion to
 * DTO (Data transfer object) concept.
 */
object UserSettingsAdapter: AdapterInterface<UserSettings> {
    override fun toDocument(element: UserSettings): HashMap<String, Any?> =
        hashMapOf(
                USER_SETTINGS_TRACK_LOCATION.value to element.trackLocation,
                USER_SETTINGS_SENDING_LOCATION_ON.value to element.isSendingLocationOn,
                USER_SETTINGS_LOCATION_ID.value to element.locationId
        )

    override fun fromDocument(document: Map<String, Any?>, id: String): UserSettings =
        UserSettings(
                trackLocation = document[USER_SETTINGS_TRACK_LOCATION.value] as Boolean,
                isSendingLocationOn = document[USER_SETTINGS_SENDING_LOCATION_ON.value] as Boolean,
                locationId = document[USER_SETTINGS_LOCATION_ID.value] as String?
        )
}