package com.github.sdpteam15.polyevents.util

/**
 * A class for converting between user entities in our code and
 * documents in the Firebase database. Not unlike the DTO (Data
 * transfer object) concept.
 */
interface AdapterInterface<T> {
    /**
     * Convert a user entity to an intermediate mapping
     * of fields to their values, that we can pass to the document directly.
     * Firestore document keys are always strings.
     * @param element the entity we're converting
     * @return a hashmap of the entity fields to their values
     */
    fun toDocument(element : T) : HashMap<String, Any?>

    /**
     * Convert document data to a user entity in our model.
     * Data retrieved from Firestore documents are always of the form of a mutable mapping,
     * that maps strings - which are the names of the fields of our entity - to their values,
     * which can be of any type..
     * @param document this is the data we retrieve from the document.
     * @return the corresponding userEntity.
     */
    fun fromDocument(document: MutableMap<String, Any?>, id : String) : T
}