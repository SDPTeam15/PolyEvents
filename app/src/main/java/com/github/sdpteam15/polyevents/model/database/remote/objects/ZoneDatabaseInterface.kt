package com.github.sdpteam15.polyevents.model.database.remote.objects

import com.github.sdpteam15.polyevents.model.database.remote.Database
import com.github.sdpteam15.polyevents.model.database.remote.matcher.Matcher
import com.github.sdpteam15.polyevents.model.entity.UserEntity
import com.github.sdpteam15.polyevents.model.entity.Zone
import com.github.sdpteam15.polyevents.model.observable.Observable
import com.github.sdpteam15.polyevents.model.observable.ObservableList

interface ZoneDatabaseInterface {
    val currentUser: UserEntity?
        get() = Database.currentDatabase.currentUser

    /**
     * Store the newly created zone information in the database
     * @param zone the zone information we should insert
     * @return An observer that will be set to true if the communication with the DB is over and no error
     */
    fun createZone(
        zone: Zone
    ): Observable<Boolean>

    /**
     * Get the zone information from the database
     * @param zoneId The id of the zone we want to get the information
     * @param zone live data that will be set with the zone information from the database
     * @return An observer that will be set to true if the communication with the DB is over and no error
     */
    fun getZoneInformation(
        zoneId: String,
        zone: Observable<Zone>
    ): Observable<Boolean>

    /**
     * Update the zone information in the databae
     * @param zoneId The id of the zone we want to get the information
     * @param newZone The updated zone information we should store in the database
     * @return An observer that will be set to true if the communication with the DB is over and no error
     */
    fun updateZoneInformation(
        zoneId: String,
        newZone: Zone
    ): Observable<Boolean>

    /**
     * Gets all the zones from the database
     * @param zones live data that will be set with the list of zones from the database
     * @param number maximum of result
     * @param matcher matcher for the search
     * @return An observer that will be set to true if the communication with the DB is over and no error
     */
    fun getAllZones(
        zones: ObservableList<Zone>,
        number: Long?  = null,
        matcher: Matcher? = null
    ): Observable<Boolean>

    /**
     * Deletes a zone from the database
     * @param zone zone to delete
     * @return An observer that will be set to true if the communication with the DB is over and no error
     */
    fun deleteZone(zone: Zone): Observable<Boolean>
}