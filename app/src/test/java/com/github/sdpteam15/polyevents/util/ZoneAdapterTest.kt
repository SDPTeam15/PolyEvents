package com.github.sdpteam15.polyevents.util

import com.github.sdpteam15.polyevents.database.DatabaseConstant.ZONE_LOCATION
import com.github.sdpteam15.polyevents.database.DatabaseConstant.ZONE_NAME
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
    val zoneId = "idZone"

    @Before
    fun setupZone() {
        zone = Zone(zoneId,zoneName, location)
    }

    @Test
    fun conversionOfZoneToDocumentPreservesData() {
        val document = ZoneAdapter.toZoneDocument(zone)

        assertEquals(document[ZONE_NAME], zone.zoneName)
        assertEquals(document[ZONE_LOCATION], zone.location)
    }

    @Test
    fun conversionOfDocumentToZonePreservesData() {
        val zoneDocumentData: HashMap<String, Any?> = hashMapOf(
            ZONE_NAME to zone.zoneName,
            ZONE_LOCATION to zone.location
        )

        val obtainedZone = ZoneAdapter.toZoneEntity(zoneDocumentData,zoneId)
        assertEquals(obtainedZone, zone)
    }

}