package com.github.sdpteam15.polyevents.model.database.remote.objects

import com.github.sdpteam15.polyevents.model.database.remote.Database
import com.github.sdpteam15.polyevents.model.observable.Observable
import com.github.sdpteam15.polyevents.model.observable.ObservableList
import com.github.sdpteam15.polyevents.model.entity.UserEntity
import com.github.sdpteam15.polyevents.model.entity.UserProfile
import com.google.android.gms.maps.model.LatLng

interface HeatmapDatabaseInterface {
    val currentUser: UserEntity?
        get() = Database.currentDatabase.currentUser
    val currentProfile: UserProfile?
        get() = Database.currentDatabase.currentProfile

    /**
     * Update, or add if it was not already in the database, the current location
     * (provided by the GeoPoint) of the user in the database.
     * @param location current location of the user
     * @return An observer that will be set to true if the communication with the DB is over and no error
     */
    fun setLocation(
        location: LatLng
    ): Observable<Boolean>

    /**
     * Fetch the current users locations.
     * @param usersLocations the list of users locations that will be set when the DB returns the information
     * @param userAccess the user profile to use its permission
     * @return An observer that will be set to true if the communication with the DB is over and no error
     */
    fun getLocations(
        usersLocations: ObservableList<LatLng>,
        userAccess: UserEntity? = null
    ): Observable<Boolean>
}