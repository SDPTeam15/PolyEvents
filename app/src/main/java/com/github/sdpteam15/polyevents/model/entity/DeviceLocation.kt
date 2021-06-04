package com.github.sdpteam15.polyevents.model.entity

import com.google.android.gms.maps.model.LatLng
import java.time.LocalDateTime

/**
 * Entity representing the device location
 * @param device Device id
 * @param location The current location
 * @param time The time at which the current location is taken
 */
data class DeviceLocation(
    val device: String? = null,
    val location: LatLng,
    val time: LocalDateTime? = null,
)