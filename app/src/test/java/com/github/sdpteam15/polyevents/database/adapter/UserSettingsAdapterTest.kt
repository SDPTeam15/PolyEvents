package com.github.sdpteam15.polyevents.database.adapter

import com.github.sdpteam15.polyevents.model.database.remote.DatabaseConstant.UserSettingsConstant.*
import com.github.sdpteam15.polyevents.model.database.remote.adapter.UserSettingsAdapter
import com.github.sdpteam15.polyevents.model.room.UserSettings
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class UserSettingsAdapterTest {
    lateinit var userSettings: UserSettings
    lateinit var userSettingsDocument: HashMap<String, Any?>

    val isSendingLocationOn = true
    val locationId = "here"

    @Before
    fun setup() {
        userSettings = UserSettings(isSendingLocationOn = isSendingLocationOn)
        userSettingsDocument = UserSettingsAdapter.toDocument(userSettings)
    }

    @Test
    fun conversionOfUserSettingsToDocumentPreservesData() {
        assertEquals(
                userSettingsDocument[USER_SETTINGS_TRACK_LOCATION.value],
                userSettings.trackLocation
        )

        assertEquals(
                userSettingsDocument[USER_SETTINGS_SENDING_LOCATION_ON.value],
                userSettings.isSendingLocationOn
        )

        assertEquals(
                userSettingsDocument[USER_SETTINGS_LOCATION_ID.value],
                userSettings.locationId
        )
    }

    @Test
    fun conversionOfDocumentToUserEntityPreservesData() {
        assertEquals(
                userSettings,
                UserSettingsAdapter.fromDocument(userSettingsDocument, "")
        )
    }
}