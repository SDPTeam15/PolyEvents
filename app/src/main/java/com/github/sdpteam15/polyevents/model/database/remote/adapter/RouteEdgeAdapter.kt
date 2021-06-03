package com.github.sdpteam15.polyevents.model.database.remote.adapter

import com.github.sdpteam15.polyevents.model.database.remote.DatabaseConstant.RouteConstant.*
import com.github.sdpteam15.polyevents.model.entity.RouteEdge

/**
 * A class for converting between route edge entities in our code and
 * documents in the Firebase database. Not unlike the conversion to
 * DTO (Data transfer object) concept.
 */
object RouteEdgeAdapter : AdapterInterface<RouteEdge> {
    override fun toDocumentWithoutNull(element: RouteEdge): HashMap<String, Any?> =
        hashMapOf(
            EDGE_ID.value to element.id,
            START_ID.value to element.startId,
            END_ID.value to element.endId
        )

    override fun fromDocument(document: Map<String, Any?>, id: String) = RouteEdge(
        id = id,
        startInitId = document[START_ID.value] as String?,
        endInitId = document[END_ID.value] as String?
    )
}