package com.github.sdpteam15.polyevents.util

import com.github.sdpteam15.polyevents.database.DatabaseConstant.ZONE_DESCRIPTION
import com.github.sdpteam15.polyevents.database.DatabaseConstant.ZONE_DOCUMENT_ID
import com.github.sdpteam15.polyevents.database.DatabaseConstant.ZONE_LOCATION
import com.github.sdpteam15.polyevents.database.DatabaseConstant.ZONE_NAME
import com.github.sdpteam15.polyevents.model.Zone
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class ZoneAdapterTest {
    lateinit var zone: Zone

    val zoneName = "zone A"
    val location = "Esplanade"
    val zoneId = "idZone"
    val zoneDescription = "description"

    @Before
    fun setupZone() {
        zone = Zone(zoneId, zoneName, location, zoneDescription)
    }

    @Test
    fun conversionOfZoneToDocumentPreservesData() {
        val document = ZoneAdapter.toDocument(zone)

        assertEquals(document[ZONE_NAME], zone.zoneName)
        assertEquals(document[ZONE_LOCATION], zone.location)
        assertEquals(document[ZONE_DESCRIPTION], zone.description)
        assertEquals(document[ZONE_DOCUMENT_ID], zone.zoneId)
    }

    @Test
    fun conversionOfDocumentToZonePreservesData() {
        val zoneDocumentData: HashMap<String, Any?> = hashMapOf(
            ZONE_NAME to zone.zoneName,
            ZONE_LOCATION to zone.location,
            ZONE_DESCRIPTION to zone.description,
            ZONE_DOCUMENT_ID to zone.zoneId
        )
        val obtainedZone = ZoneAdapter.fromDocument(zoneDocumentData, zoneId)
        assertEquals(obtainedZone, zone)
    }

    @Test
    fun conversionOfZoneToDocumentWithoutIDAddNoId() {
        val document = ZoneAdapter.toDocument(Zone(null, zoneName, location, zoneDescription))
        print(document)
        assertEquals(document[ZONE_NAME], zone.zoneName)
        assertEquals(document[ZONE_LOCATION], zone.location)
        assertEquals(document[ZONE_DESCRIPTION], zone.description)
        println(document.containsKey(ZONE_DOCUMENT_ID))
        println(document[ZONE_DOCUMENT_ID])
        assert(!document.containsKey(ZONE_DOCUMENT_ID))
    }
}