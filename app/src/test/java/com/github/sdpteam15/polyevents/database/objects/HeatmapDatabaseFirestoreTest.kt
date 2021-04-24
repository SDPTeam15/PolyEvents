package com.github.sdpteam15.polyevents.database.objects

import com.github.sdpteam15.polyevents.Settings
import com.github.sdpteam15.polyevents.database.DatabaseConstant
import com.github.sdpteam15.polyevents.database.DatabaseInterface
import com.github.sdpteam15.polyevents.database.HelperTestFunction
import com.github.sdpteam15.polyevents.database.observe.ObservableList
import com.github.sdpteam15.polyevents.model.DeviceLocation
import com.github.sdpteam15.polyevents.util.DeviceLocationAdapter
import com.google.android.gms.maps.model.LatLng
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class HeatmapDatabaseTest {

    lateinit var mackHeatmapDatabase : HeatmapDatabase
    lateinit var mockDatabaseInterface : DatabaseInterface
    @Before
    fun setup() {
        mockDatabaseInterface = HelperTestFunction.mockFor()
        mackHeatmapDatabase = HeatmapDatabase(mockDatabaseInterface)
        HelperTestFunction.clearQueue()
    }

    @Test
    fun setLocation() {
        Settings.LocationId = ""
        val latLng = LatLng(1.0,1.0)

        HelperTestFunction.nextString.add("ici")
        mackHeatmapDatabase.setLocation(latLng).observeOnce { assert(it.value) }

        val add = HelperTestFunction.addEntityAndGetIdQueue.peek()!!

        assertEquals(latLng, (add.element as DeviceLocation).location)
        assertEquals(DatabaseConstant.CollectionConstant.LOCATION_COLLECTION, add.collection)
        assertEquals(DeviceLocationAdapter, add.adapter)

        Settings.LocationId = "id"
        HelperTestFunction.nextBoolean.add(true)
        mackHeatmapDatabase.setLocation(latLng).observeOnce { assert(it.value) }

        val set = HelperTestFunction.setEntityQueue.peek()!!

        assertEquals(latLng, (set.element as DeviceLocation).location)
        assertEquals("id", set.id)
        assertEquals(DatabaseConstant.CollectionConstant.LOCATION_COLLECTION, set.collection)
        assertEquals(DeviceLocationAdapter, set.adapter)
    }

    @Test
    fun getLocations(){
        HelperTestFunction.nextBoolean.add(true)
        mackHeatmapDatabase.getLocations(ObservableList<LatLng>().observeOnce { assert(it.value.isNotEmpty()) }.then).observeOnce { assert(it.value) }

        val getlist = HelperTestFunction.getListEntityQueue.peek()!!

        assertNull(getlist.ids)
        assertNotNull(getlist.matcher)
        assertEquals(DatabaseConstant.CollectionConstant.LOCATION_COLLECTION, getlist.collection)
        assertNotNull(getlist.adapter)
    }
}