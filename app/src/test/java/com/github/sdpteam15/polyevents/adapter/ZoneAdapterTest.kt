package com.github.sdpteam15.polyevents.adapter

import com.github.sdpteam15.polyevents.model.database.remote.DatabaseConstant.ZoneConstant.*
import com.github.sdpteam15.polyevents.model.database.remote.adapter.ZoneAdapter
import com.github.sdpteam15.polyevents.model.entity.Zone
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

        assertEquals(document[ZONE_NAME.value], zone.zoneName)
        assertEquals(document[ZONE_LOCATION.value], zone.location)
        assertEquals(document[ZONE_DESCRIPTION.value], zone.description)
        assertEquals(document[ZONE_DOCUMENT_ID.value], zone.zoneId)
    }

    @Test
    fun conversionOfDocumentToZonePreservesData() {
        val zoneDocumentData: HashMap<String, Any?> = hashMapOf(
            ZONE_NAME.value to zone.zoneName,
            ZONE_LOCATION.value to zone.location,
            ZONE_DESCRIPTION.value to zone.description,
            ZONE_DOCUMENT_ID.value to zone.zoneId
        )
        val obtainedZone = ZoneAdapter.fromDocument(zoneDocumentData, zoneId)
        assertEquals(obtainedZone, zone)
    }

    @Test
    fun conversionOfZoneToDocumentWithoutIDAddNoId() {
        val document = ZoneAdapter.toDocument(Zone(null, zoneName, location, zoneDescription))
        print(document)
        assertEquals(document[ZONE_NAME.value], zone.zoneName)
        assertEquals(document[ZONE_LOCATION.value], zone.location)
        assertEquals(document[ZONE_DESCRIPTION.value], zone.description)
        println(document.containsKey(ZONE_DOCUMENT_ID.value))
        println(document[ZONE_DOCUMENT_ID.value])
        assert(!document.containsKey(ZONE_DOCUMENT_ID.value))
    }
}