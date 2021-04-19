package com.github.sdpteam15.polyevents.model

import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class EventAttendee(
    val userUid: String? = null,
    val eventId: String? = null
)
