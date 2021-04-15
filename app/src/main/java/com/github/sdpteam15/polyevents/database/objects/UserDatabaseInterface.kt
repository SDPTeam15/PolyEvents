package com.github.sdpteam15.polyevents.database.objects

import com.github.sdpteam15.polyevents.database.Database
import com.github.sdpteam15.polyevents.database.observe.Observable
import com.github.sdpteam15.polyevents.model.UserEntity

interface UserDatabaseInterface {
    val currentUser: UserEntity?

    /**
     * Update the user information in the database
     * @param newValues : a map with the new value to set in the database
     * @param uid : the uid of the user from which we want to query the information
     * @param userAccess: the user object to use its permission
     * @return An observer that will be set to true if the communication with the DB is over and no error
     */
    fun updateUserInformation(
        newValues: Map<String, String>,
        uid: String,
        userAccess: UserEntity? = currentUser
    ): Observable<Boolean>

    /**
     * Register the user in the database with its basic information (uid, email, name)
     * @param user : user with all the requested information
     * @param userAccess: the user object to use its permission
     * @return An observer that will be set to true if the communication with the DB is over and no error
     */
    fun firstConnexion(
        user: UserEntity,
        userAccess: UserEntity? = currentUser
    ): Observable<Boolean>

    /**
     * Look in the database if the user already exists or not
     * @param isInDb : Will be set to true if in Database or to false otherwise
     * @param uid : user uid we want to check the existence
     * @param userAccess: the user object to use its permission
     * @return An observer that will be set to true if the communication with the DB is over and no error
     */
    fun inDatabase(
        isInDb: Observable<Boolean>,
        uid: String,
        userAccess: UserEntity? = currentUser
    ): Observable<Boolean>

    /**
     * Look in the database if the user already exists or not
     * @param user : live data that will be set with the user information from the database
     * @param uid : user uid we want to get the information
     * @param userAccess: the user object to use its permission
     * @return An observer that will be set to true if the communication with the DB is over and no error
     */
    fun getUserInformation(
        user: Observable<UserEntity>,
        uid: String? = currentUser?.uid,
        userAccess: UserEntity? = currentUser
    ): Observable<Boolean>

}