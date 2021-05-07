package com.github.sdpteam15.polyevents.model.database.remote.adapter

import com.github.sdpteam15.polyevents.model.entity.Rating

/**
 * A class for converting between rating entities in our code and
 * documents in the Firebase database. Not unlike the conversion to
 * DTO (Data transfer object) concept.
 */
object RatingAdapter: AdapterInterface<Rating> {
    override fun toDocument(element: Rating): HashMap<String, Any?> {
        TODO("Not yet implemented")
    }

    override fun fromDocument(document: MutableMap<String, Any?>, id: String): Rating {
        TODO("Not yet implemented")
    }
}