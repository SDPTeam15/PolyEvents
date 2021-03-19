package com.github.sdpteam15.polyevents.util

import com.github.sdpteam15.polyevents.model.Item
import com.github.sdpteam15.polyevents.model.Zone
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class ZoneAdapterTest {
    lateinit var zone: Zone

    val zoneName = "zone A"
    val location = "Esplanade"

    @Before
    fun setupZone() {
        zone = Zone(zoneName, location)
    }

    @Test
    fun conversionOfZoneToDocumentPreservesData() {
        val document = ZoneAdapter.toZoneDocument(zone)

        assertEquals(document["zoneName"], zone.zoneName)
        assertEquals(document["location"], zone.location)
    }

    @Test
    fun conversionOfDocumentToZonePreservesData() {
        val zoneDocumentData: HashMap<String, Any?> = hashMapOf(
            "zoneName" to zone.zoneName,
            "location" to zone.location
        )

        val obtainedZone = ZoneAdapter.toZoneEntity(zoneDocumentData)
        assertEquals(obtainedZone, zone)
    }

}