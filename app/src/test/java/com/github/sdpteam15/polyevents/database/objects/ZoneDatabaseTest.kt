package com.github.sdpteam15.polyevents.database.objects

import com.github.sdpteam15.polyevents.database.HelperTestFunction
import com.github.sdpteam15.polyevents.model.database.remote.Database
import com.github.sdpteam15.polyevents.model.database.remote.DatabaseConstant
import com.github.sdpteam15.polyevents.model.database.remote.DatabaseConstant.ZoneConstant.*
import com.github.sdpteam15.polyevents.model.database.remote.DatabaseInterface
import com.github.sdpteam15.polyevents.model.database.remote.FirestoreDatabaseProvider
import com.github.sdpteam15.polyevents.model.database.remote.adapter.ZoneAdapter
import com.github.sdpteam15.polyevents.model.database.remote.objects.ZoneDatabase
import com.github.sdpteam15.polyevents.model.database.remote.objects.ZoneDatabaseInterface
import com.github.sdpteam15.polyevents.model.entity.UserEntity
import com.github.sdpteam15.polyevents.model.entity.Zone
import com.github.sdpteam15.polyevents.model.observable.Observable
import com.github.sdpteam15.polyevents.model.observable.ObservableList
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import kotlin.test.assertEquals

private const val zoneID = "ZONEID"
private const val zoneName = "ZONENAME"
private const val zoneDesc = "ZONEDESC"
private const val zoneLoc = "ZONELOCATION"
private const val displayNameTest = "Test displayName"
private const val emailTest = "Test email"
private const val uidTest = "Test uid"

@Suppress("UNCHECKED_CAST", "TYPE_INFERENCE_ONLY_INPUT_TYPES_WARNING")
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

        zone =
            Zone(zoneId = zoneID, zoneName = zoneName, description = zoneDesc, location = zoneLoc)

        val mockDatabaseInterface = HelperTestFunction.mockDatabaseInterface()
        mockedZoneDatabase = ZoneDatabase(mockDatabaseInterface)
        HelperTestFunction.clearQueue()
    }

    @Test
    fun getCurrentUserReturnCorrectOne() {
        val mockedDatabase = Mockito.mock(DatabaseInterface::class.java)
        Mockito.`when`(mockedDatabase.currentUser).thenReturn(UserEntity(""))
        Database.currentDatabase = mockedDatabase
        assertEquals(mockedZoneDatabase.currentUser, UserEntity(""))

        Database.currentDatabase = mockedDatabase

        Database.currentDatabase = FirestoreDatabaseProvider
    }

    @Test
    fun updateZone() {

        HelperTestFunction.nextSetEntity { true }
        mockedZoneDatabase.updateZoneInformation(zone.zoneId!!, zone)
            .observeOnce { assert(it.value) }.then.postValue(true)

        val set = HelperTestFunction.lastSetEntity()!!

        assertEquals(zone, set.element)
        assertEquals(zone.zoneId, set.id)
        assertEquals(DatabaseConstant.CollectionConstant.ZONE_COLLECTION, set.collection)
        assertEquals(ZoneAdapter, set.adapter)
    }

    @Test
    fun addZone() {

        HelperTestFunction.nextAddEntity { true }
        mockedZoneDatabase.createZone(zone)
            .observeOnce { assert(it.value) }.then.postValue(true)

        val set = HelperTestFunction.lastAddEntity()!!

        assertEquals(zone, set.element)
        assertEquals(DatabaseConstant.CollectionConstant.ZONE_COLLECTION, set.collection)
        assertEquals(ZoneAdapter, set.adapter)
    }

    @Test
    fun getZoneList() {
        val zones = ObservableList<Zone>()

        HelperTestFunction.nextGetListEntity { true }
        mockedZoneDatabase.getAllZones(zones)
            .observeOnce { assert(it.value) }.then.postValue(false)

        val getList = HelperTestFunction.lastGetListEntity()!!

        assertEquals(zones, getList.element)
        assertEquals(DatabaseConstant.CollectionConstant.ZONE_COLLECTION, getList.collection)
        assertEquals(ZoneAdapter, getList.adapter)
    }

    @Test
    fun removeZone() {

        HelperTestFunction.nextSetEntity { true }
        mockedZoneDatabase.deleteZone(zone)
            .observeOnce { assert(it.value) }.then.postValue(false)


        val del = HelperTestFunction.lastDeleteEntity()!!

        assertEquals(zone.zoneId, del.id)
        assertEquals(DatabaseConstant.CollectionConstant.ZONE_COLLECTION, del.collection)
    }

    @Test
    fun getZoneFromId() {
        val zones = Observable<Zone>()

        HelperTestFunction.nextGetEntity { true }
        mockedZoneDatabase.getZoneInformation(zone.zoneId!!, zones)
            .observeOnce { assert(it.value) }.then.postValue(false)

        val getEntity = HelperTestFunction.lastGetEntity()!!

        assertEquals(zones, getEntity.element)
        assertEquals(zone.zoneId, getEntity.id)
        assertEquals(DatabaseConstant.CollectionConstant.ZONE_COLLECTION, getEntity.collection)
        assertEquals(ZoneAdapter, getEntity.adapter)
    }
}