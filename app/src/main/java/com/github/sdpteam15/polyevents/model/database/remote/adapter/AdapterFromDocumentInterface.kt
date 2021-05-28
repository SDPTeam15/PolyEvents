package com.github.sdpteam15.polyevents.model.database.remote.adapter

/**
 * A interface for converting between entities in our code and
 * documents in the database. Not unlike the DTO (Data
 * transfer object) concept.
 */
interface AdapterFromDocumentInterface<T> {
    /**
     * Convert document data to a entity in our model.
     * Data retrieved from the database are always a mutable map that maps strings (names of the fields of our entity) to their values,
     * which can be of any type..
     * @param document this is the data we retrieve from the document.
     * @return the corresponding entity.
     */
    fun fromDocument(document: Map<String, Any?>, id: String): T?
}