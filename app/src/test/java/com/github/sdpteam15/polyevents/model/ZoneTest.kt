package com.github.sdpteam15.polyevents.model

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class ZoneTest {
    lateinit var zone: Zone
    val zoneId = "zoneId1"
    val zoneName = "zone A"
    val location = "Esplanade"
    val description = "A simple cool zone"

    @Before
    fun setupZone() {
        zone = Zone(zoneId, zoneName, location, description)
    }

    @Test
    fun testZoneProperties() {
        assertEquals(zone.zoneName, zoneName)
        assertEquals(zone.location, location)
        assertEquals(zone.zoneId, zoneId)
        assertEquals(zone.description,description)
    }
}