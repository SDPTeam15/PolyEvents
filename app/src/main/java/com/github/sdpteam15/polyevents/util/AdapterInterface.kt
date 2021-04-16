package com.github.sdpteam15.polyevents.util

/**
 * A class for converting between user entities in our code and
 * documents in the database. Not unlike the DTO (Data
 * transfer object) concept.
 */
interface AdapterInterface<T> {
    /**
     * Convert an entity to a map mapping fields keys (always string in the database) to their values that we can use directly in the database
     * @param element the entity we're converting
     * @return a hashmap mapping entity fields to their values
     */
    fun toDocument(element : T) : HashMap<String, Any?>

    /**
     * Convert document data to a user entity in our model.
     * Data retrieved from the database are always a mutable map that maps strings (names of the fields of our entity) to their values,
     * which can be of any type..
     * @param document this is the data we retrieve from the document.
     * @return the corresponding userEntity.
     */
    fun fromDocument(document: MutableMap<String, Any?>, id : String) : T
}