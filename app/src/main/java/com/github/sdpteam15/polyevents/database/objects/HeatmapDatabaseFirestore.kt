package com.github.sdpteam15.polyevents.database.objects

import android.annotation.SuppressLint
import com.github.sdpteam15.polyevents.Settings
import com.github.sdpteam15.polyevents.database.DatabaseConstant.LocationConstant.LOCATIONS_COLLECTION
import com.github.sdpteam15.polyevents.database.DatabaseConstant.LocationConstant.LOCATIONS_POINT
import com.github.sdpteam15.polyevents.database.DatabaseConstant.LocationConstant.LOCATIONS_TIME
import com.github.sdpteam15.polyevents.database.FirestoreDatabaseProvider
import com.github.sdpteam15.polyevents.database.observe.Observable
import com.github.sdpteam15.polyevents.model.UserEntity
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

object HeatmapDatabaseFirestore : HeatmapDatabaseInterface {
    @SuppressLint("StaticFieldLeak")
    var firestore: FirebaseFirestore? = null
        get() = field ?: Firebase.firestore

    override fun setLocation(
        location: LatLng
    ): Observable<Boolean> {
        if(Settings.LocationId == "") {
            val ended = Observable<Boolean>()
            FirestoreDatabaseProvider.addEntity(

            ).observeOnce {  }
            return
        }
        else

    }

    override fun getUsersLocations(
        usersLocations: Observable<List<LatLng>>,
        userAccess: UserEntity?
    ): Observable<Boolean> = FirestoreDatabaseProvider.thenDoGet(
        firestore!!.collection(LOCATIONS_COLLECTION.value)
            .get()
    ) { querySnapshot ->
        val locations = querySnapshot.documents.map {
            val geoPoint = it.data!![LOCATIONS_POINT.value] as GeoPoint
            LatLng(geoPoint.latitude, geoPoint.longitude)
        }
        usersLocations.postValue(locations)
    }
}