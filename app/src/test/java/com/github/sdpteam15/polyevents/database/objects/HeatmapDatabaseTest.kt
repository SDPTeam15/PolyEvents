package com.github.sdpteam15.polyevents.database.objects

import com.github.sdpteam15.polyevents.database.HelperTestFunction
import com.github.sdpteam15.polyevents.model.database.remote.DatabaseConstant
import com.github.sdpteam15.polyevents.model.database.remote.adapter.DeviceLocationAdapter
import com.github.sdpteam15.polyevents.model.database.remote.objects.HeatmapDatabase
import com.github.sdpteam15.polyevents.model.entity.DeviceLocation
import com.github.sdpteam15.polyevents.model.observable.ObservableList
import com.github.sdpteam15.polyevents.model.room.UserSettings
import com.google.android.gms.maps.model.LatLng
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@Suppress("UNCHECKED_CAST")
class HeatmapDatabaseTest {
    lateinit var mackHeatmapDatabase: HeatmapDatabase

    @Before
    fun setup() {
        val mockDatabaseInterface = HelperTestFunction.mockDatabaseInterface()
        mackHeatmapDatabase = HeatmapDatabase(mockDatabaseInterface)
        HelperTestFunction.clearQueue()
    }

    @Test
    fun setLocation() {
        val userSettingsObservable = ObservableList<UserSettings>()
        userSettingsObservable.add(UserSettings())
        val latLng = LatLng(1.0, 1.0)

        HelperTestFunction.nextAddEntityAndGetId { "ici" }
        mackHeatmapDatabase.setLocation(latLng, userSettingsObservable)
            .observeOnce { assert(it.value) }

        val add = HelperTestFunction.lastAddEntityAndGetId()!!

        assertEquals(latLng, (add.element as DeviceLocation).location)
        assertEquals(DatabaseConstant.CollectionConstant.LOCATION_COLLECTION, add.collection)
        assertEquals(DeviceLocationAdapter, add.adapter)

        userSettingsObservable.clear()
        userSettingsObservable.add(UserSettings(locationId = "id"))
        HelperTestFunction.nextSetEntity { true }
        mackHeatmapDatabase.setLocation(latLng, userSettingsObservable)
            .observeOnce { assert(it.value) }

        val set = HelperTestFunction.lastSetEntity()!!

        assertEquals(latLng, (set.element as DeviceLocation).location)
        assertEquals("id", set.id)
        assertEquals(DatabaseConstant.CollectionConstant.LOCATION_COLLECTION, set.collection)
        assertEquals(DeviceLocationAdapter, set.adapter)
    }

    @Test
    fun getLocations() {
        HelperTestFunction.nextGetListEntity { true }
        mackHeatmapDatabase.getLocations(ObservableList()).observeOnce { assert(it.value) }

        val getlist = HelperTestFunction.lastGetListEntity()!!

        assertNull(getlist.ids)
        assertNotNull(getlist.matcher)
        assertEquals(DatabaseConstant.CollectionConstant.LOCATION_COLLECTION, getlist.collection)
        assertNotNull(getlist.adapter)
    }
}