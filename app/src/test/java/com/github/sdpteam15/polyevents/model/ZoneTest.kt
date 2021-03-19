package com.github.sdpteam15.polyevents.model

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class ZoneTest {
    lateinit var zone: Zone

    val zoneName = "zone A"
    val location = "Esplanade"

    @Before
    fun setupZone() {
        zone = Zone(zoneName, location)
    }

    @Test
    fun testZoneProperties() {
        assertEquals(zone.zoneName, zoneName)
        assertEquals(zone.location, location)
    }
}