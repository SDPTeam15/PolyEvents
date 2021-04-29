package com.github.sdpteam15.polyevents.fakedatabase

import com.github.sdpteam15.polyevents.database.objects.HeatmapDatabaseInterface
import com.github.sdpteam15.polyevents.database.observe.Observable
import com.github.sdpteam15.polyevents.database.observe.ObservableList
import com.github.sdpteam15.polyevents.model.UserEntity
import com.google.android.gms.maps.model.LatLng

object FakeDatabaseHeatmap : HeatmapDatabaseInterface {
    override fun setLocation(location: LatLng): Observable<Boolean> = Observable(true)

    override fun getLocations(
        usersLocations: ObservableList<LatLng>,
        userAccess: UserEntity?
    ): Observable<Boolean> = Observable(true)
}