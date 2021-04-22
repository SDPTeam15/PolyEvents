package com.github.sdpteam15.polyevents.util

import com.github.sdpteam15.polyevents.database.DatabaseConstant
import com.github.sdpteam15.polyevents.database.DatabaseConstant.LocationConstant
import com.github.sdpteam15.polyevents.helper.HelperFunctions
import com.github.sdpteam15.polyevents.model.DeviceLocation
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Timestamp

/**
 * A class for converting between device location entities in our code and
 * documents in the Firebase database. Not unlike the conversion to
 * DTO (Data transfer object) concept.
 *
 * IMPORTANT: This should be updated whenever we add, remove or update fields of Event.
 */
object DeviceLocationAdapter : AdapterInterface<DeviceLocation>  {
    override fun toDocument(element: DeviceLocation): HashMap<String, Any?> = hashMapOf(
        LocationConstant.LOCATIONS_DEVICE.value to element.device,
        LocationConstant.LOCATIONS_POINT.value + "/latitude"  to element.location.latitude,
        LocationConstant.LOCATIONS_POINT.value + "/longitude" to element.location.longitude,
        LocationConstant.LOCATIONS_TIME.value to HelperFunctions.LocalDateToTimeToDate(element.time)
    )

    override fun fromDocument(document: MutableMap<String, Any?>, id: String): DeviceLocation = DeviceLocation(
        device = document[LocationConstant.LOCATIONS_DEVICE.value] as String?,
        location = LatLng(
            document[LocationConstant.LOCATIONS_POINT.value + "/latitude"] as Double,
            document[LocationConstant.LOCATIONS_POINT.value + "/longitude"] as Double,
        ),
        time = HelperFunctions.DateToLocalDateTime(
            (document[DatabaseConstant.EventConstant.EVENT_START_TIME.value] as Timestamp?)?.toDate()
        )
    )
}