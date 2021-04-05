package com.github.sdpteam15.polyevents.model

import com.google.firebase.firestore.IgnoreExtraProperties

/**
 * Entity model for a zone. Events occur inside a zone.
 *
 * @property zoneName the name of the zone
 * @property location the location of the zone
 *
 */
@IgnoreExtraProperties
data class Zone (
    val zoneName: String? = null,
    val location: String? = null,
    val description: String? = null
)