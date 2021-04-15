package com.github.sdpteam15.polyevents.database.objects

import android.annotation.SuppressLint
import com.github.sdpteam15.polyevents.database.Database
import com.github.sdpteam15.polyevents.database.DatabaseConstant
import com.github.sdpteam15.polyevents.database.FirestoreDatabaseProvider
import com.github.sdpteam15.polyevents.database.observe.Observable
import com.github.sdpteam15.polyevents.model.UserEntity
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

object HeatmapDatabaseFirestore: HeatmapDatabaseInterface {
    @SuppressLint("StaticFieldLeak")
    private var firestore: FirebaseFirestore? = null
        get() = field ?: Firebase.firestore

    override val currentUser: UserEntity?
        get()= Database.currentDatabase.currentUser

    override fun setUserLocation(
        location: LatLng,
        userAccess: UserEntity?
    ): Observable<Boolean> {
        return FirestoreDatabaseProvider.thenDoSet(
            firestore!!.collection(DatabaseConstant.LOCATIONS_COLLECTION)
                .document(userAccess!!.uid)
                .set(
                    hashMapOf(
                        DatabaseConstant.LOCATIONS_POINT to GeoPoint(
                            location.latitude,
                            location.longitude
                        )
                    ),
                    SetOptions.merge()
                )
        )
    }

    override fun getUsersLocations(
        usersLocations: Observable<List<LatLng>>,
        userAccess: UserEntity?
    ): Observable<Boolean> = FirestoreDatabaseProvider.thenDoGet(
        firestore!!.collection(DatabaseConstant.LOCATIONS_COLLECTION)
            .get()
    ) { querySnapshot ->
        val locations = querySnapshot.documents.map {
            val geoPoint = it.data!![DatabaseConstant.LOCATIONS_POINT] as GeoPoint
            LatLng(geoPoint.latitude, geoPoint.longitude)
        }
        usersLocations.postValue(locations)
    }
}