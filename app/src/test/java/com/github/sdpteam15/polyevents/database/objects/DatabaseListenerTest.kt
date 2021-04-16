package com.github.sdpteam15.polyevents.database.objects

import com.github.sdpteam15.polyevents.database.*
import com.github.sdpteam15.polyevents.model.UserEntity
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.After
import org.junit.Test
import org.mockito.Mockito

class DatabaseListenerTest {
    lateinit var user: UserEntity
    lateinit var mockedDatabase: FirebaseFirestore
    lateinit var database: DatabaseInterface

    @After
    fun tearDown(){
        FirestoreDatabaseProvider.heatmapDatabase = null
        FirestoreDatabaseProvider.userDatabase = null
        FirestoreDatabaseProvider.itemDatabase = null
        FirestoreDatabaseProvider.materialRequestDatabase = null
        FirestoreDatabaseProvider.zoneDatabase = null
        FirestoreDatabaseProvider.eventDatabase = null
    }

    @Test
    fun databaseCorrectlySet(){
        val mockedHeatmap: HeatmapDatabaseInterface = Mockito.mock(HeatmapDatabaseInterface::class.java)
        val mockedUser: UserDatabaseInterface = Mockito.mock(UserDatabaseInterface::class.java)
        val mockedItem: ItemDatabaseInterface = Mockito.mock(ItemDatabaseInterface::class.java)
        val mockedMaterialRequest: MaterialRequestDatabaseInterface = Mockito.mock(MaterialRequestDatabaseInterface::class.java)
        val mockedZone: ZoneDatabaseInterface = Mockito.mock(ZoneDatabaseInterface::class.java)
        val mockedEvent: EventDatabaseInterface = Mockito.mock(EventDatabaseInterface::class.java)

        FirestoreDatabaseProvider.heatmapDatabase = mockedHeatmap
        FirestoreDatabaseProvider.userDatabase = mockedUser
        FirestoreDatabaseProvider.itemDatabase = mockedItem
        FirestoreDatabaseProvider.materialRequestDatabase = mockedMaterialRequest
        FirestoreDatabaseProvider.zoneDatabase = mockedZone
        FirestoreDatabaseProvider.eventDatabase = mockedEvent

        MatcherAssert.assertThat(
            FirestoreDatabaseProvider.heatmapDatabase,
            CoreMatchers.`is`(mockedHeatmap)
        )
        MatcherAssert.assertThat(
            FirestoreDatabaseProvider.userDatabase,
            CoreMatchers.`is`(mockedUser)
        )
        MatcherAssert.assertThat(
            FirestoreDatabaseProvider.itemDatabase,
            CoreMatchers.`is`(mockedItem)
        )
        MatcherAssert.assertThat(
            FirestoreDatabaseProvider.materialRequestDatabase,
            CoreMatchers.`is`(mockedMaterialRequest)
        )
        MatcherAssert.assertThat(
            FirestoreDatabaseProvider.zoneDatabase,
            CoreMatchers.`is`(mockedZone)
        )
        MatcherAssert.assertThat(
            FirestoreDatabaseProvider.eventDatabase,
            CoreMatchers.`is`(mockedEvent)
        )
    }
    @Test
    fun listenersAreCorrectlySet(){


        val lastQuerySuccessListener= OnSuccessListener<QuerySnapshot> { }
        val lastSetSuccessListener= OnSuccessListener<Void> { }
        val lastFailureListener= OnFailureListener {  }
        val lastGetSuccessListener= OnSuccessListener<DocumentSnapshot> { }
        val lastAddSuccessListener= OnSuccessListener<DocumentReference> { }

        FirestoreDatabaseProvider.lastQuerySuccessListener= lastQuerySuccessListener
        FirestoreDatabaseProvider.lastSetSuccessListener= lastSetSuccessListener
        FirestoreDatabaseProvider.lastFailureListener= lastFailureListener
        FirestoreDatabaseProvider.lastGetSuccessListener= lastGetSuccessListener
        FirestoreDatabaseProvider.lastAddSuccessListener= lastAddSuccessListener

        MatcherAssert.assertThat(
            FirestoreDatabaseProvider.lastQuerySuccessListener,
            CoreMatchers.`is`(lastQuerySuccessListener)
        )
        MatcherAssert.assertThat(
            FirestoreDatabaseProvider.lastSetSuccessListener,
            CoreMatchers.`is`(lastSetSuccessListener)
        )
        MatcherAssert.assertThat(
            FirestoreDatabaseProvider.lastFailureListener,
            CoreMatchers.`is`(lastFailureListener)
        )
        MatcherAssert.assertThat(
            FirestoreDatabaseProvider.lastGetSuccessListener,
            CoreMatchers.`is`(lastGetSuccessListener)
        )
        MatcherAssert.assertThat(
            FirestoreDatabaseProvider.lastAddSuccessListener,
            CoreMatchers.`is`(lastAddSuccessListener)
        )

    }
}