package com.github.sdpteam15.polyevents.model

import com.google.firebase.firestore.IgnoreExtraProperties
import java.time.LocalDateTime

@IgnoreExtraProperties
data class Event(
    val eventName: String,
    val zoneName: String,
    val startTime: LocalDateTime? = null,
    val endTime: LocalDateTime? = null,
    val inventory: Map<String, Int>? = null
) {
    private constructor() : this(
        "", "", null, null, null
    )
}