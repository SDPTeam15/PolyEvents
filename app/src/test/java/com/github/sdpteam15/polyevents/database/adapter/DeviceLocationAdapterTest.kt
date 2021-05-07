package com.github.sdpteam15.polyevents.database.adapter

import com.github.sdpteam15.polyevents.helper.HelperFunctions
import com.github.sdpteam15.polyevents.model.database.remote.DatabaseConstant
import com.github.sdpteam15.polyevents.model.database.remote.adapter.DeviceLocationAdapter
import com.github.sdpteam15.polyevents.model.entity.DeviceLocation
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Timestamp
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime

class DeviceLocationAdapterTest {
    val device = "device"
    val longitude = 42.0
    val latitude = 23.5
    val time = LocalDateTime.now()
    val timestamp = Timestamp.now()

    lateinit var deviceLocation: DeviceLocation
    lateinit var document: HashMap<String, Any?>

    @Before
    fun setupItem() {
        deviceLocation = DeviceLocation(
            device = device,
            location = LatLng(latitude, longitude),
            time = time
        )
        document = DeviceLocationAdapter.toDocument(deviceLocation)
    }


    @Test
    fun conversionOfDocumentToItemEntityPreservesData() {
        val deviceLocationDocument: HashMap<String, Any?> = hashMapOf(
            DatabaseConstant.LocationConstant.LOCATIONS_DEVICE.value to device,
            DatabaseConstant.LocationConstant.LOCATIONS_POINT_LATITUDE.value to latitude,
            DatabaseConstant.LocationConstant.LOCATIONS_POINT_LONGITUDE.value to longitude,
            DatabaseConstant.LocationConstant.LOCATIONS_TIME.value to timestamp
        )
        val obtainedLocation = DeviceLocationAdapter.fromDocument(deviceLocationDocument, "id")

        Assert.assertEquals(obtainedLocation, deviceLocation)
    }

    @Test
    fun conversionOfItemEntityToDocumentPreservesData() {
        Assert.assertEquals(
            document[DatabaseConstant.LocationConstant.LOCATIONS_DEVICE.value],
            device
        )
        Assert.assertEquals(
            document[DatabaseConstant.LocationConstant.LOCATIONS_POINT_LATITUDE.value],
            latitude
        )
        Assert.assertEquals(
            document[DatabaseConstant.LocationConstant.LOCATIONS_POINT_LONGITUDE.value],
            longitude
        )
        Assert.assertEquals(
            document[DatabaseConstant.LocationConstant.LOCATIONS_TIME.value].toString(),
            timestamp.toDate().toString()
        )
    }

}