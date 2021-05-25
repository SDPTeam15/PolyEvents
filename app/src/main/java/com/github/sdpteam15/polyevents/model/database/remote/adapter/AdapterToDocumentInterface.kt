package com.github.sdpteam15.polyevents.model.database.remote.adapter

/**
 * A interface for converting between user entities in our code and
 * documents in the database. Not unlike the DTO (Data
 * transfer object) concept.
 */
interface AdapterToDocumentInterface<T> {
    /**
     * Convert an entity to a map mapping fields keys (always string in the database) to their values that we can use directly in the database
     * @param element the entity we're converting
     * @return a map mapping entity fields to their values
     */
    fun toDocument(element: T?): Map<String, Any?>?
}