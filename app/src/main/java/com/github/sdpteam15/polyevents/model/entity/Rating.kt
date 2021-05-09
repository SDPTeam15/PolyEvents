package com.github.sdpteam15.polyevents.model.entity

/**
 *  Represent a rating given by a user to an event
 *  @param ratingId Rating id
 *  @param rate The rate given by the user
 *  @param feedback The feedback given by the user
 *  @param eventId The event for which the user leaves the rating
 *  @param userId The user that leaves the rating
 *
 */
data class Rating(
    val ratingId: String? = null,
    val rate: Float? = null,
    val feedback: String? = null,
    val eventId: String? = null,
    val userId: String? = null
)
