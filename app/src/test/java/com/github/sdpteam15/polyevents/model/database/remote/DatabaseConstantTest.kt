package com.github.sdpteam15.polyevents.model.database.remote

import org.junit.Assert.assertEquals
import org.junit.Test

class DatabaseConstantTest {
    @Test
    fun testEventConstants() {
        assertEquals(
            DatabaseConstant.EventConstant.EVENT_DOCUMENT_ID.value,
            DatabaseConstant.EventConstant.EVENT_DOCUMENT_ID.toString()
        )
    }

    @Test
    fun testItemConstants() {
        assertEquals(
            DatabaseConstant.ItemConstants.ITEM_NAME.value,
            DatabaseConstant.ItemConstants.ITEM_NAME.toString()
        )
    }

    @Test
    fun testProfileConstants() {
        assertEquals(
            DatabaseConstant.ProfileConstants.PROFILE_NAME.value,
            DatabaseConstant.ProfileConstants.PROFILE_NAME.toString()
        )
    }

    @Test
    fun testUserConstants() {
        assertEquals(
            DatabaseConstant.UserConstants.USER_NAME.value,
            DatabaseConstant.UserConstants.USER_NAME.toString()
        )
    }

    @Test
    fun testZoneConstants() {
        assertEquals(
            DatabaseConstant.ZoneConstant.ZONE_NAME.value,
            DatabaseConstant.ZoneConstant.ZONE_NAME.toString()
        )
    }

    @Test
    fun testLocationConstants() {
        assertEquals(
            DatabaseConstant.LocationConstant.LOCATIONS_DEVICE.value,
            DatabaseConstant.LocationConstant.LOCATIONS_DEVICE.toString()
        )
    }

    @Test
    fun testMaterialRequestConstants() {
        assertEquals(
            DatabaseConstant.MaterialRequestConstant.MATERIAL_REQUEST_LIST.value,
            DatabaseConstant.MaterialRequestConstant.MATERIAL_REQUEST_LIST.toString()
        )
    }

    @Test
    fun testUserSettingsConstants() {
        assertEquals(
            DatabaseConstant.UserSettingsConstant.USER_SETTINGS_LOCATION_ID.value,
            DatabaseConstant.UserSettingsConstant.USER_SETTINGS_LOCATION_ID.toString()
        )
    }
}