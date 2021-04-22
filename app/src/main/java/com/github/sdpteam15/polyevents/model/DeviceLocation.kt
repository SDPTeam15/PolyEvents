package com.github.sdpteam15.polyevents.model

import com.google.android.gms.maps.model.LatLng

data class DeviceLocation (
    val location: LatLng,
    val device: String
)