package com.github.sdpteam15.polyevents.model.database.remote.objects

import com.github.sdpteam15.polyevents.model.database.local.entity.UserSettings
import com.github.sdpteam15.polyevents.model.database.remote.Database
import com.github.sdpteam15.polyevents.model.entity.UserEntity
import com.github.sdpteam15.polyevents.model.observable.Observable
import com.github.sdpteam15.polyevents.model.observable.ObservableList
import com.google.android.gms.maps.model.LatLng

interface HeatmapDatabaseInterface {
    val currentUser: UserEntity?
        get() = Database.currentDatabase.currentUser

    /**
     * Update, or add if it was not already in the database, the current location
     * (provided by the GeoPoint) of the user in the database.
     * @param location current location of the user
     * @param userSettings an observable of user settings, to retrieve the location id, as well as updating
     * the location id of the user settings when done.
     * @return An observer that will be set to true if the communication with the DB is over and no error
     */
    fun setLocation(
        location: LatLng,
        userSettings: ObservableList<UserSettings>
    ): Observable<Boolean>

    /**
     * Fetch the current users locations.
     * @param usersLocations the list of users locations that will be set when the DB returns the information
     * @return An observer that will be set to true if the communication with the DB is over and no error
     */
    fun getLocations(
        usersLocations: ObservableList<LatLng>
    ): Observable<Boolean>
}