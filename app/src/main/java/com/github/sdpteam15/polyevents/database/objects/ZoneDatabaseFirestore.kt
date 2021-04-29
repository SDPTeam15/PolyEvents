package com.github.sdpteam15.polyevents.database.objects

import android.annotation.SuppressLint
import com.github.sdpteam15.polyevents.database.DatabaseConstant.CollectionConstant.ZONE_COLLECTION
import com.github.sdpteam15.polyevents.database.FirestoreDatabaseProvider
import com.github.sdpteam15.polyevents.database.Matcher
import com.github.sdpteam15.polyevents.database.observe.Observable
import com.github.sdpteam15.polyevents.database.observe.ObservableList
import com.github.sdpteam15.polyevents.model.UserEntity
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
                .collection(ZONE_COLLECTION.value)
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
                .collection(ZONE_COLLECTION.value)
                .document(zoneId)
                .get()
        ) {
            it.data?.let { it1 -> zone.postValue(ZoneAdapter.fromDocument(it1, it.id), this) }
        }
    }

    override fun updateZoneInformation(
        zoneId: String,
        newZone: Zone,
        userAccess: UserEntity?
    ): Observable<Boolean> {
        return FirestoreDatabaseProvider.thenDoSet(
            firestore!!
                .collection(ZONE_COLLECTION.value)
                .document(zoneId)
                .update(ZoneAdapter.toDocument(newZone))
        )
    }

    override fun getAllZones(
        matcher: Matcher?,
        number: Long?,
        zonesList: ObservableList<Zone>,
        userAccess: UserEntity?
    ): Observable<Boolean> {
        val task = FirestoreDatabaseProvider.firestore!!.collection(ZONE_COLLECTION.value)
        val query = matcher?.match(task)
        val v = if (query != null) {
            if (number != null) query.limit(number).get() else query.get()
        } else {
            if (number != null) task.limit(number).get() else task.get()
        }
        return FirestoreDatabaseProvider.thenDoGet(v) {
            zonesList.clear(this)
            for (d in it.documents) {
                val data = d.data
                if (data != null) {
                    val e: Zone = ZoneAdapter.fromDocument(data, d.id)
                    zonesList.add(e)
                }
            }
        }
    }

    override fun deleteZone(zone: Zone, userAccess: UserEntity?): Observable<Boolean> {
        return FirestoreDatabaseProvider.thenDoSet(
            FirestoreDatabaseProvider.firestore!!.collection(ZONE_COLLECTION.value)
                .document(zone.zoneId!!).delete()
        )
    }


}