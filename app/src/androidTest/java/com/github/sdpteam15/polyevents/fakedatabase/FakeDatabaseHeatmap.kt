package com.github.sdpteam15.polyevents.fakedatabase

import com.github.sdpteam15.polyevents.database.objects.HeatmapDatabaseInterface
import com.github.sdpteam15.polyevents.database.observe.Observable
import com.github.sdpteam15.polyevents.model.UserEntity
import com.google.android.gms.maps.model.LatLng

object FakeDatabaseHeatmap : HeatmapDatabaseInterface {

    override fun setUserLocation(
        location: LatLng,
        userAccess: UserEntity?
    ): Observable<Boolean> {
        return Observable(true)
    }

    override fun getUsersLocations(
        usersLocations: Observable<List<LatLng>>,
        userAccess: UserEntity?
    ): Observable<Boolean> {
        // TODO : see whether we write a Python script that send fake data to our database
        usersLocations.postValue(listOf(LatLng(46.548823, 7.017012)))
        return Observable(true)
    }
}