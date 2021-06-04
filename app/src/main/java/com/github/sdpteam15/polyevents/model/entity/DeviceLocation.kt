package com.github.sdpteam15.polyevents.model.entity

import com.google.android.gms.maps.model.LatLng
import java.time.LocalDateTime

/**
 * Entity representing the device location
 * @property device Device id
 * @property location The current location
 * @property time The time at which the current location is taken
 */
data class DeviceLocation(
    val device: String? = null,
    val location: LatLng,
    val time: LocalDateTime? = null,
)