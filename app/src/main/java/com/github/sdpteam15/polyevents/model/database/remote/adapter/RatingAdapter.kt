package com.github.sdpteam15.polyevents.model.database.remote.adapter

import com.github.sdpteam15.polyevents.model.database.remote.DatabaseConstant.RatingConstant.*
import com.github.sdpteam15.polyevents.model.entity.Rating

/**
 * A class for converting between rating entities in our code and
 * documents in the Firebase database. Not unlike the conversion to
 * DTO (Data transfer object) concept.
 */
object RatingAdapter: AdapterInterface<Rating> {
    override fun toDocument(element: Rating): HashMap<String, Any?> =
        hashMapOf(
            RATING_DESCRIPTION.value to  element.feedback,
            RATING_SCORE.value to  element.rate,
            RATING_USER_ID.value to  element.userId,
            RATING_EVENT_ID.value to  element.eventId
        )

    override fun fromDocument(document: MutableMap<String, Any?>, id: String): Rating = Rating(
        ratingId = id,
        feedback = document[RATING_DESCRIPTION.value] as String,
        rate = document[RATING_SCORE.value] as Double,
        userId = document[RATING_USER_ID.value] as String,
        eventId = document[RATING_EVENT_ID.value] as String
    )
}