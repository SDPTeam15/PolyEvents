package com.github.sdpteam15.polyevents.database.objects

import com.github.sdpteam15.polyevents.database.DatabaseConstant
import com.github.sdpteam15.polyevents.database.DatabaseInterface
import com.github.sdpteam15.polyevents.database.FirestoreDatabaseProvider
import com.github.sdpteam15.polyevents.database.observe.Observable
import com.github.sdpteam15.polyevents.model.UserEntity
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.*
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mockito

private const val displayNameTest = "Test displayName"
private const val emailTest = "Test email"
private const val uidTest = "Test uid"

class HeatmapDatabaseFirestoreTest {
    lateinit var user: UserEntity
    lateinit var mockedDatabase: FirebaseFirestore
    lateinit var database: DatabaseInterface

    @Before
    fun setup() {
        user = UserEntity(
            uid = uidTest,
            name = displayNameTest,
            email = emailTest
        )

    }


    @Test
    fun setUserLocationCorrectlySet() {
        //mock the required class
        val mockedCollectionReference = Mockito.mock(CollectionReference::class.java)
        val mockedDocumentReference = Mockito.mock(DocumentReference::class.java)
        val mockedTask = Mockito.mock(Task::class.java) as Task<Void>

        val lat = 46.548823
        val lng = 7.017012
        val pointToAdd = LatLng(lat, lng)

        var latSet = 0.0
        var lngSet = 0.0

        Mockito.`when`(mockedDatabase.collection(DatabaseConstant.LOCATIONS_COLLECTION)).thenReturn(
            mockedCollectionReference
        )
        Mockito.`when`(mockedCollectionReference.document(uidTest))
            .thenReturn(mockedDocumentReference)
        Mockito.`when`(
            mockedDocumentReference.set(
                hashMapOf(
                    DatabaseConstant.LOCATIONS_POINT to GeoPoint(
                        pointToAdd.latitude,
                        pointToAdd.longitude
                    )
                ),
                SetOptions.merge()
            )
        ).thenReturn(mockedTask)

        Mockito.`when`(mockedTask.addOnSuccessListener(ArgumentMatchers.any())).thenAnswer {
            FirestoreDatabaseProvider.lastSetSuccessListener!!.onSuccess(null)

            latSet = lat
            lngSet = lng
            mockedTask
        }

        Mockito.`when`(mockedTask.addOnFailureListener(ArgumentMatchers.any())).thenAnswer {
            mockedTask
        }

        val result = FirestoreDatabaseProvider.setUserLocation(pointToAdd, user)
        MatcherAssert.assertThat(result.value, CoreMatchers.`is`(true))
        MatcherAssert.assertThat(latSet, CoreMatchers.`is`(lat))
        MatcherAssert.assertThat(lngSet, CoreMatchers.`is`(lng))
    }


    @Test
    fun getUsersLocationsReturnCorrectNumberOfLocations() {
        //Mock the needed classes
        val mockedCollectionReference = Mockito.mock(CollectionReference::class.java)
        val mockedTask = Mockito.mock(Task::class.java) as Task<QuerySnapshot>
        val mockedDocument = Mockito.mock(QuerySnapshot::class.java)

        val mockedDoc1 = Mockito.mock(DocumentSnapshot::class.java)
        val mockedDoc2 = Mockito.mock(DocumentSnapshot::class.java)
        val listMockedDocs = listOf(mockedDoc1, mockedDoc2)

        val lat1 = 46.548823
        val lng1 = 7.017012
        val lat2 = 46.548343
        val lng2 = 7.017892

        val locations = listOf(
            GeoPoint(lat1, lng1),
            GeoPoint(lat2, lng2)
        )
        val locationsLatLng = listOf(
            LatLng(lat1, lng1),
            LatLng(lat2, lng2)
        )

        val mapDoc1 = hashMapOf(
            DatabaseConstant.LOCATIONS_POINT to locations[0],
            DatabaseConstant.USER_UID to "1"
        )
        val mapDoc2 = hashMapOf(
            DatabaseConstant.LOCATIONS_POINT to locations[1],
            DatabaseConstant.USER_UID to "2"
        )
        Mockito.`when`(mockedDoc1.data).thenReturn(mapDoc1 as Map<String, Any>?)
        Mockito.`when`(mockedDoc2.data).thenReturn(mapDoc2 as Map<String, Any>?)

        //mock the needed method
        Mockito.`when`(mockedDatabase.collection(DatabaseConstant.LOCATIONS_COLLECTION)).thenReturn(
            mockedCollectionReference
        )
        Mockito.`when`(mockedCollectionReference.get()).thenReturn(mockedTask)

        Mockito.`when`(mockedDocument.documents).thenReturn(listMockedDocs)

        Mockito.`when`(mockedTask.addOnSuccessListener(ArgumentMatchers.any())).thenAnswer {
            FirestoreDatabaseProvider.lastQuerySuccessListener!!.onSuccess(mockedDocument)
            mockedTask
        }
        Mockito.`when`(mockedTask.addOnFailureListener(ArgumentMatchers.any())).thenAnswer {
            mockedTask
        }

        val locationsObs = Observable<List<LatLng>>()
        val result = FirestoreDatabaseProvider.getUsersLocations(locationsObs, user)

        // Assert that the DB successfully performed the query
        MatcherAssert.assertThat(result.value, CoreMatchers.`is`(true))

        MatcherAssert.assertThat(locationsObs.value, CoreMatchers.`is`(locationsLatLng))
    }

}