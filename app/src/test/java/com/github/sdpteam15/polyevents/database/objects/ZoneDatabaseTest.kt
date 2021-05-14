package com.github.sdpteam15.polyevents.database.objects

import com.github.sdpteam15.polyevents.database.HelperTestFunction
import com.github.sdpteam15.polyevents.model.database.remote.DatabaseConstant
import com.github.sdpteam15.polyevents.model.database.remote.DatabaseConstant.ZoneConstant.*
import com.github.sdpteam15.polyevents.model.database.remote.DatabaseInterface
import com.github.sdpteam15.polyevents.model.database.remote.adapter.EventAdapter
import com.github.sdpteam15.polyevents.model.database.remote.adapter.ZoneAdapter
import com.github.sdpteam15.polyevents.model.database.remote.objects.ZoneDatabase
import com.github.sdpteam15.polyevents.model.database.remote.objects.ZoneDatabaseInterface
import com.github.sdpteam15.polyevents.model.entity.*
import com.github.sdpteam15.polyevents.model.observable.Observable
import com.github.sdpteam15.polyevents.model.observable.ObservableList
import com.google.firebase.firestore.FirebaseFirestore
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

private const val zoneID = "ZONEID"
private const val zoneName = "ZONENAME"
private const val zoneDesc = "ZONEDESC"
private const val zoneLoc = "ZONELOCATION"
private const val displayNameTest = "Test displayName"
private const val emailTest = "Test email"
private const val uidTest = "Test uid"

@Suppress("UNCHECKED_CAST")
class ZoneDatabaseTest {
    lateinit var user: UserEntity
    lateinit var database: DatabaseInterface
    lateinit var zoneDocument: HashMap<String, Any?>
    lateinit var mockedZoneDatabase: ZoneDatabaseInterface
    lateinit var zone: Zone

    @Before
    fun setup() {
        user = UserEntity(
            uid = uidTest,
            name = displayNameTest,
            email = emailTest
        )

        zoneDocument = hashMapOf(
            ZONE_DOCUMENT_ID.value to zoneID,
            ZONE_DESCRIPTION.value to zoneDesc,
            ZONE_LOCATION.value to zoneLoc,
            ZONE_NAME.value to zoneName
        )

        zone = Zone(zoneId = zoneID,zoneName = zoneName, description = zoneDesc,location = zoneLoc)

        val mockDatabaseInterface = HelperTestFunction.mockDatabaseInterface()
        mockedZoneDatabase = ZoneDatabase(mockDatabaseInterface)
        HelperTestFunction.clearQueue()
    }


    @Test
    fun updateZone() {
        val userAccess = UserProfile()

        HelperTestFunction.nextSetEntity { true }
        mockedZoneDatabase.updateZoneInformation(zone.zoneId!!, zone, userAccess)
            .observeOnce { assert(it.value) }.then.postValue(true)

        val set = HelperTestFunction.lastSetEntity()!!

        assertEquals(zone, set.element)
        assertEquals(zone.zoneId, set.id)
        assertEquals(DatabaseConstant.CollectionConstant.ZONE_COLLECTION, set.collection)
        assertEquals(ZoneAdapter, set.adapter)
    }

    @Test
    fun addZone() {
        val userAccess = UserProfile()

        HelperTestFunction.nextAddEntity { true }
        mockedZoneDatabase.createZone(zone, userAccess)
            .observeOnce { assert(it.value) }.then.postValue(true)

        val set = HelperTestFunction.lastAddEntity()!!

        assertEquals(zone, set.element)
        assertEquals(DatabaseConstant.CollectionConstant.ZONE_COLLECTION, set.collection)
        assertEquals(ZoneAdapter, set.adapter)
    }

    @Test
    fun getZoneList() {
        val zones = ObservableList<Zone>()
        val userAccess = UserProfile("uid")

        HelperTestFunction.nextGetListEntity { true }
        mockedZoneDatabase.getAllZones(null, null, zones, userAccess)
            .observeOnce { assert(it.value) }.then.postValue(false)

        val getList = HelperTestFunction.lastGetListEntity()!!

        assertEquals(zones, getList.element)
        assertEquals(DatabaseConstant.CollectionConstant.ZONE_COLLECTION, getList.collection)
        assertEquals(ZoneAdapter, getList.adapter)
    }

    @Test
    fun removeZone() {
        val userAccess = UserProfile("uid")

        HelperTestFunction.nextSetEntity { true }
        mockedZoneDatabase.deleteZone(zone, userAccess)
            .observeOnce { assert(it.value) }.then.postValue(false)


        val del = HelperTestFunction.lastDeleteEntity()!!

        assertEquals(zone.zoneId, del.id)
        assertEquals(DatabaseConstant.CollectionConstant.ZONE_COLLECTION, del.collection)
    }

    @Test
    fun getZoneFromId() {
        val zones = Observable<Zone>()
        val userAccess = UserProfile("uid")

        HelperTestFunction.nextGetEntity { true }
        mockedZoneDatabase.getZoneInformation(zone.zoneId!!, zones, userAccess)
            .observeOnce { assert(it.value) }.then.postValue(false)

        val getEntity = HelperTestFunction.lastGetEntity()!!

        assertEquals(zones, getEntity.element)
        assertEquals(zone.zoneId, getEntity.id)
        assertEquals(DatabaseConstant.CollectionConstant.ZONE_COLLECTION, getEntity.collection)
        assertEquals(ZoneAdapter, getEntity.adapter)
    }
}