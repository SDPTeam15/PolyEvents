package com.github.sdpteam15.polyevents.model.entity

import com.google.android.gms.maps.model.LatLng
import java.time.LocalDateTime

data class DeviceLocation(
    val device: String? = null,
    val location: LatLng,
    val time: LocalDateTime? = null,
)