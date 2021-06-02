package com.github.sdpteam15.polyevents.fakedatabase

import com.github.sdpteam15.polyevents.model.database.remote.objects.HeatmapDatabaseInterface
import com.github.sdpteam15.polyevents.model.observable.Observable
import com.github.sdpteam15.polyevents.model.observable.ObservableList
import com.github.sdpteam15.polyevents.model.room.UserSettings
import com.google.android.gms.maps.model.LatLng

object FakeDatabaseHeatmap : HeatmapDatabaseInterface {
    override fun setLocation(
        location: LatLng,
        userSettings: ObservableList<UserSettings>
    ): Observable<Boolean> = Observable(true)

    override fun getLocations(
        usersLocations: ObservableList<LatLng>
    ): Observable<Boolean> = Observable(true)
}