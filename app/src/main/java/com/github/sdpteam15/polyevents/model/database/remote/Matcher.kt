package com.github.sdpteam15.polyevents.model.database.remote

import com.google.firebase.firestore.Query

/**
 * Filters a firebase collection given some conditions and returns the corresponding Query result
 * For example keep only the first 5 items from the collection.
 */
interface Matcher {
    //TODO make the Matcher generic for any DB
    fun match(collection: Query): Query
}