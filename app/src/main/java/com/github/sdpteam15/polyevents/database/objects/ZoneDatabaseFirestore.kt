package com.github.sdpteam15.polyevents.database.objects

import android.annotation.SuppressLint
import com.github.sdpteam15.polyevents.database.Database
import com.github.sdpteam15.polyevents.database.DatabaseConstant
import com.github.sdpteam15.polyevents.database.FirestoreDatabaseProvider
import com.github.sdpteam15.polyevents.database.observe.Observable
import com.github.sdpteam15.polyevents.model.UserEntity
import com.github.sdpteam15.polyevents.model.UserProfile
import com.github.sdpteam15.polyevents.model.Zone
import com.github.sdpteam15.polyevents.util.ZoneAdapter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

object ZoneDatabaseFirestore : ZoneDatabaseInterface {
    @SuppressLint("StaticFieldLeak")
    var firestore: FirebaseFirestore? = null
        get() = field ?: Firebase.firestore

    override fun createZone(zone: Zone, userAccess: UserEntity?): Observable<Boolean> {
        return FirestoreDatabaseProvider.thenDoAdd(
            firestore!!
                .collection(DatabaseConstant.ZONE_COLLECTION)
                .add(ZoneAdapter.toDocument(zone))
        )
    }


    override fun getZoneInformation(
        zoneId: String,
        zone: Observable<Zone>,
        userAccess: UserEntity?
    ): Observable<Boolean> {
        return FirestoreDatabaseProvider.thenDoMultGet(
            firestore!!
                .collection(DatabaseConstant.ZONE_COLLECTION)
                .document(zoneId)
                .get()
        ) {
            it.data?.let { it1 -> zone.postValue(ZoneAdapter.fromDocument(it1, it.id),this) }
        }
    }

    override fun updateZoneInformation(
        zoneId: String,
        newZone: Zone,
        userAccess: UserEntity?
    ): Observable<Boolean> {
        return FirestoreDatabaseProvider.thenDoSet(
            firestore!!
                .collection(DatabaseConstant.ZONE_COLLECTION)
                .document(zoneId)
                .update(ZoneAdapter.toDocument(newZone))
        )
    }
}