package com.github.sdpteam15.polyevents.model.database.remote.matcher

/**
 * Contains data read from a document in the database as part of a query
 * @property data the data of the document
 * @property id the id of the document
 */
data class QueryDocumentSnapshot (
    val data : Map<String, Any?>,
    val id : String
)