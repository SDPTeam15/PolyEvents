package com.github.sdpteam15.polyevents.database.objects

import com.github.sdpteam15.polyevents.database.Database
import com.github.sdpteam15.polyevents.database.Matcher
import com.github.sdpteam15.polyevents.database.observe.Observable
import com.github.sdpteam15.polyevents.database.observe.ObservableList
import com.github.sdpteam15.polyevents.model.UserEntity
import com.github.sdpteam15.polyevents.model.UserProfile
import com.github.sdpteam15.polyevents.model.Zone

interface ZoneDatabaseInterface {
    val currentUser: UserEntity?
        get() = Database.currentDatabase.currentUser
    val currentProfile: UserProfile?
        get() = Database.currentDatabase.currentProfile

    /**
     * Store the newly created zone information in the database
     * @param zone the zone information we should insert
     * @param userAccess the user profile to use its permission
     * @return An observer that will be set to true if the communication with the DB is over and no error
     */
    fun createZone(
        zone: Zone,
        userAccess: UserEntity? = currentUser
    ): Observable<Boolean>

    /**
     * Get the zone information from the database
     * @param zoneId The id of the zone we want to get the information
     * @param zone live data that will be set with the zone information from the database
     * @param userAccess the user profile to use its permission
     * @return An observer that will be set to true if the communication with the DB is over and no error
     */
    fun getZoneInformation(
        zoneId: String,
        zone: Observable<Zone>,
        userAccess: UserEntity? = currentUser
    ): Observable<Boolean>

    /**
     * Update the zone information in the databae
     * @param zoneId The id of the zone we want to get the information
     * @param newZone The updated zone information we should store in the database
     * @param userAccess the user profile to use its permission
     * @return An observer that will be set to true if the communication with the DB is over and no error
     */
    fun updateZoneInformation(
        zoneId: String,
        newZone: Zone,
        userAccess: UserEntity? = currentUser
    ): Observable<Boolean>

    /**
     * Gets all the zones from the database
     * @param matcher matcher for the search
     * @param number maximum of result
     * @param zones live data that will be set with the list of zones from the database
     * @param userAccess the user profile to use its permission
     * @return An observer that will be set to true if the communication with the DB is over and no error
     */
    fun getAllZones(
        matcher: Matcher?,
        number: Long?,
        zones: ObservableList<Zone>,
        userAccess: UserEntity? = currentUser
    ): Observable<Boolean>

    fun deleteZone(zone: Zone, userAccess: UserEntity? = currentUser): Observable<Boolean>
}