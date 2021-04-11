package com.github.sdpteam15.polyevents.database

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.Query

interface Matcher {
    fun match(collection: CollectionReference): Query
}