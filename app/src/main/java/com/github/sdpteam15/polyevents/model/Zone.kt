package com.github.sdpteam15.polyevents.model

import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class Zone (
    val zoneName: String,
    val location: String? = null
) {
    private constructor() : this (
        "", null
            )
}