package com.github.sdpteam15.polyevents.model.database.remote.matcher

import com.github.sdpteam15.polyevents.model.observable.Observable

/**
 * A Query from a database
 */
interface Query {
    /**
     * Executes the query and returns the results as a QuerySnapshot.
     * @return A Task that will be resolved with the results of the {@code Query}.
     */
    fun get(): Observable<Pair<QuerySnapshot?, Exception?>>

    /**
     * Creates and returns a new Query that only returns the first matching documents up to
     * the specified number.
     *
     * @param limit The maximum number of items to return.
     * @return The created Query.
     */
    fun limit(limit: Long): Query

    /**
     * Creates and returns a new Query with the additional filter that documents must contain
     * the specified field and the value should be equal to the specified value.
     *
     * @param key The name of the field to compare
     * @param value The value for comparison
     * @return The created Query.
     */
    fun whereEqualTo(key: String, value: Any): Query

    /**
     * Creates and returns a new Query with the additional filter that documents must contain
     * the specified field and the value does not equal the specified value.
     *
     * A Query can have only one whereNotEqualTo() filter, and it cannot be
     * combined with whereNotIn().
     *
     * @param key The name of the field to compare
     * @param value The value for comparison
     * @return The created Query.
     */
    fun whereNotEqualTo(key: String, value: Any): Query

    /**
     * Creates and returns a new Query with the additional filter that documents must contain
     * the specified field, the value must be an array, and that the array must contain the provided
     * value.
     *
     * A Query can have only one whereArrayContains() filter and it cannot be
     * combined with whereArrayContainsAny().
     *
     * @param key The name of the field containing an array to search.
     * @param value The value that must be contained in the array
     * @return The created Query.
     */
    fun whereArrayContains(key: String, value: Any): Query

    /**
     * Creates and returns a new Query with the additional filter that documents must contain
     * the specified field and the value should be greater than the specified value.
     *
     * @param key The name of the field to compare
     * @param value The value for comparison
     * @return The created Query.
     */
    fun whereGreaterThan(key: String, value: Any): Query

    /**
     * Creates and returns a new Query with the additional filter that documents must contain
     * the specified field and the value should be less than the specified value.
     *
     * @param key The name of the field to compare
     * @param value The value for comparison
     * @return The created Query.
     */
    fun whereLessThan(key: String, value: Any): Query

    /**
     * Creates and returns a new Query with the additional filter that documents must contain
     * the specified field and the value should be greater than or equal to the specified value.
     *
     * @param key The name of the field to compare
     * @param value The value for comparison
     * @return The created Query.
     */
    fun whereGreaterThanOrEqualTo(key: String, value: Any): Query

    /**
     * Creates and returns a new Query with the additional filter that documents must contain
     * the specified field and the value should be less than or equal to the specified value.
     *
     * @param key The name of the field to compare
     * @param value The value for comparison
     * @return The created Query.
     */
    fun whereLessThanOrEqualTo(key: String, value: Any): Query

    /**
     * Creates and returns a new Query that's additionally sorted by the specified field.
     *
     * @param key The field to sort by.
     * @return The created Query.
     */
    @Deprecated("To remove because Firestore needs index")
    fun orderBy(key: String): Query
}