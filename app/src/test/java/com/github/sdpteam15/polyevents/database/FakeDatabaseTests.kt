package com.github.sdpteam15.polyevents.database

import com.github.sdpteam15.polyevents.database.Database.currentDatabase
import com.google.firebase.firestore.FirebaseFirestore
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import kotlin.test.assertEquals

class FakeDatabaseTests {
    @Test
    fun toDo(){
        currentDatabase = FakeDatabase

        assertEquals(currentDatabase.getEventFromId("0"),FakeDatabase.events!!.get(0))
    }
}