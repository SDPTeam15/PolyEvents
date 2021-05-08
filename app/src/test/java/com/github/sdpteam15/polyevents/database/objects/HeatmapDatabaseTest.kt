package com.github.sdpteam15.polyevents.database.objects

import com.github.sdpteam15.polyevents.Settings
import com.github.sdpteam15.polyevents.database.HelperTestFunction
import com.github.sdpteam15.polyevents.model.database.remote.DatabaseConstant
import com.github.sdpteam15.polyevents.model.database.remote.adapter.DeviceLocationAdapter
import com.github.sdpteam15.polyevents.model.database.remote.objects.HeatmapDatabase
import com.github.sdpteam15.polyevents.model.entity.DeviceLocation
import com.github.sdpteam15.polyevents.model.observable.ObservableList
import com.google.android.gms.maps.model.LatLng
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@Suppress("UNCHECKED_CAST")
class HeatmapDatabaseTest {
    lateinit var mackHeatmapDatabase : HeatmapDatabase
    @Before
    fun setup() {
        val mockDatabaseInterface = HelperTestFunction.mockDatabaseInterface()
        mackHeatmapDatabase = HeatmapDatabase(mockDatabaseInterface)
        HelperTestFunction.clearQueue()
    }

    @Test
    fun setLocation() {
        Settings.LocationId = ""
        val latLng = LatLng(1.0,1.0)

        HelperTestFunction.nextString("ici")
        mackHeatmapDatabase.setLocation(latLng).observeOnce { assert(it.value) }

        val add = HelperTestFunction.addEntityAndGetIdQueue.peek()!!

        assertEquals(latLng, (add.element as DeviceLocation).location)
        assertEquals(DatabaseConstant.CollectionConstant.LOCATION_COLLECTION, add.collection)
        assertEquals(DeviceLocationAdapter, add.adapter)

        Settings.LocationId = "id"
        HelperTestFunction.nextBoolean(true)
        mackHeatmapDatabase.setLocation(latLng).observeOnce { assert(it.value) }

        val set = HelperTestFunction.setEntityQueue.peek()!!

        assertEquals(latLng, (set.element as DeviceLocation).location)
        assertEquals("id", set.id)
        assertEquals(DatabaseConstant.CollectionConstant.LOCATION_COLLECTION, set.collection)
        assertEquals(DeviceLocationAdapter, set.adapter)
    }

    @Test
    fun getLocations(){
        HelperTestFunction.nextBoolean(true)
        mackHeatmapDatabase.getLocations(ObservableList()).observeOnce { assert(it.value) }

        val getlist = HelperTestFunction.getListEntityQueue.peek()!!

        assertNull(getlist.ids)
        assertNotNull(getlist.matcher)
        assertEquals(DatabaseConstant.CollectionConstant.LOCATION_COLLECTION, getlist.collection)
        assertNotNull(getlist.adapter)
    }
}