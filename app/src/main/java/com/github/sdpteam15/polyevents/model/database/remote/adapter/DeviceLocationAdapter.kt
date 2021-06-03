package com.github.sdpteam15.polyevents.model.database.remote.adapter

import com.github.sdpteam15.polyevents.helper.HelperFunctions
import com.github.sdpteam15.polyevents.helper.HelperFunctions.groupToLatLng
import com.github.sdpteam15.polyevents.helper.HelperFunctions.toLocalDateTime
import com.github.sdpteam15.polyevents.model.database.remote.DatabaseConstant.LocationConstant
import com.github.sdpteam15.polyevents.model.entity.DeviceLocation
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Timestamp

/**
 * A class for converting between device location entities in our code and
 * documents in the Firebase database. Not unlike the conversion to
 * DTO (Data transfer object) concept.
 */
object DeviceLocationAdapter : AdapterInterface<DeviceLocation> {
    override fun toDocumentWithoutNull(element: DeviceLocation): HashMap<String, Any?> =
        hashMapOf(
            LocationConstant.LOCATIONS_DEVICE.value to element.device,
            LocationConstant.LOCATIONS_POINT_LATITUDE.value to element.location.latitude,
            LocationConstant.LOCATIONS_POINT_LONGITUDE.value to element.location.longitude,
            LocationConstant.LOCATIONS_TIME.value to HelperFunctions.localDateTimeToDate(element.time)
        )

    override fun fromDocument(document: Map<String, Any?>, id: String): DeviceLocation =
        DeviceLocation(
            device = document[LocationConstant.LOCATIONS_DEVICE.value] as String?,
            location =
            (document[LocationConstant.LOCATIONS_POINT_LATITUDE.value] groupToLatLng document[LocationConstant.LOCATIONS_POINT_LONGITUDE.value])!! ,
            time = document[LocationConstant.LOCATIONS_TIME.value].toLocalDateTime()
        )
}