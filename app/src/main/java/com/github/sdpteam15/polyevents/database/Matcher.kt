package com.github.sdpteam15.polyevents.database

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.Query

/**
 * Filters a firebase collection given some conditions and returns the corresponding Query result.git
 * For example keep only the first 5 items from the collection.
 */
interface Matcher {
    fun match(collection: CollectionReference): Query
}