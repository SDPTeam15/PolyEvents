package com.github.sdpteam15.polyevents.database.objects

import com.github.sdpteam15.polyevents.database.DatabaseInterface
import com.github.sdpteam15.polyevents.model.UserEntity
import com.google.firebase.firestore.FirebaseFirestore

class ItemDatabaseFirestoreTest {
    lateinit var user: UserEntity
    lateinit var mockedDatabase: FirebaseFirestore
    lateinit var database: DatabaseInterface
}