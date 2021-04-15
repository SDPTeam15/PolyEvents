package com.github.sdpteam15.polyevents.database.objects

import android.annotation.SuppressLint
import com.github.sdpteam15.polyevents.database.Database
import com.github.sdpteam15.polyevents.database.DatabaseConstant
import com.github.sdpteam15.polyevents.database.FirestoreDatabaseProvider
import com.github.sdpteam15.polyevents.database.observe.Observable
import com.github.sdpteam15.polyevents.model.UserEntity
import com.github.sdpteam15.polyevents.model.Zone
import com.github.sdpteam15.polyevents.util.ZoneAdapter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

object ZoneDatabaseFirestore:ZoneDatabaseInterface {
    override val currentUser: UserEntity?
        get()= Database.currentDatabase.currentUser

    @SuppressLint("StaticFieldLeak")
    private var firestore: FirebaseFirestore? = null
        get() = field ?: Firebase.firestore

    override fun createZone(zone: Zone, userAccess: UserEntity?): Observable<Boolean> {
        return FirestoreDatabaseProvider.thenDoAdd(
            firestore!!
                .collection(DatabaseConstant.ZONE_COLLECTION)
                .add(ZoneAdapter.toZoneDocument(zone))
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
            zone.postValue(it.data?.let { it1 -> ZoneAdapter.toZoneEntity(it1, it.id) })
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
                .update(ZoneAdapter.toZoneDocument(newZone))
        )
    }
}