package com.github.sdpteam15.polyevents.model.database.remote.matcher

/**
 * Filters a firebase collection given some conditions and returns the corresponding Query result
 * For example keep only the first 5 items from the collection. Presented as
 * a Functional (SAM) interface.
 */
fun interface Matcher { fun match(collection: Query): Query }