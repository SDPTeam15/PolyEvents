package com.github.sdpteam15.polyevents.model.room

import com.github.sdpteam15.polyevents.model.database.local.entity.UserSettings
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class UserSettingsTest {
    private val isSendingLocationOn = false
    private val location_id = "here"
    private val trackLocation = true

    private lateinit var userSettings: UserSettings

    @Before
    fun setup() {
        userSettings = UserSettings(
                isSendingLocationOn = isSendingLocationOn,
                trackLocation = trackLocation,
                locationId = location_id
        )
    }

    @Test
    fun testGetters() {
        assertEquals(userSettings.userUid, UserSettings.DEFAULT_ID)
        assertEquals(userSettings.trackLocation, trackLocation)
        assertEquals(userSettings.isSendingLocationOn, isSendingLocationOn)
        assertEquals(userSettings.locationId, location_id)
    }

    @Test
    fun testDefaultUserSettingsInstance() {
        val userSettingsDefault = UserSettings()
        assertFalse(userSettingsDefault.trackLocation)
        assertFalse(userSettingsDefault.isSendingLocationOn)
        assertNull(userSettingsDefault.locationId)
    }
}