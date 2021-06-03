package com.github.sdpteam15.polyevents.model.database.remote.objects

import com.github.sdpteam15.polyevents.model.database.remote.Database
import com.github.sdpteam15.polyevents.model.entity.UserEntity
import com.github.sdpteam15.polyevents.model.entity.UserProfile
import com.github.sdpteam15.polyevents.model.observable.Observable
import com.github.sdpteam15.polyevents.model.observable.ObservableList

interface UserDatabaseInterface {
    val currentUser: UserEntity?
        get() = Database.currentDatabase.currentUser

    /**
     * Update the user information in the database
     * @param user the user from which we want to query the information
     * @return An observer that will be set to true if the communication with the DB is over and no error
     */
    fun updateUserInformation(
        user: UserEntity
    ): Observable<Boolean>

    /**
     * Register the user in the database with its basic information (uid, email, name)
     * @param user : user with all the requested information
     * @return An observer that will be set to true if the communication with the DB is over and no error
     */
    fun firstConnexion(
        user: UserEntity
    ): Observable<Boolean>

    /**
     * Look in the database if the user already exists or not
     * @param isInDb : Will be set to true if in Database or to false otherwise
     * @param uid : user uid we want to check the existence
     * @return An observer that will be set to true if the communication with the DB is over and no error
     */
    fun inDatabase(
        isInDb: Observable<Boolean>,
        uid: String
    ): Observable<Boolean>

    /**
     * Look in the database if the user already exists or not
     * @param user observable data that will be set with the user information from the database
     * @param uid user uid we want to get the information
     * @return An observer that will be set to true if the communication with the DB is over and no error
     */
    fun getUserInformation(
        user: Observable<UserEntity>,
        uid: String = currentUser!!.uid
    ): Observable<Boolean>

    /**
     * Get a list of all users in the database
     * @param users observable list that will contain all the user entity getted from the database
     * @return An observer that will be set to true if the communication with the DB is over and no error
     */
    fun getListAllUsers(
        users: ObservableList<UserEntity>
    ): Observable<Boolean>

    /**
     * add a UserProfile to a UserEntity
     * @param profile profile we want to add in the database
     * @param user user to add
     * @return An observer that will be set to true if the communication with the DB is over and no error
     */
    fun addUserProfileAndAddToUser(
        profile: UserProfile,
        user: UserEntity = currentUser!!
    ): Observable<Boolean>

    /**
     * remove a UserProfile from a UserEntity
     * @param profile profile to remove
     * @param user user for database access
     * @return if the operation succeed
     */
    fun removeProfileFromUser(
        profile: UserProfile,
        user: UserEntity
    ): Observable<Boolean>

    /**
     * Update profile
     * @param profile a map with the new value to set in the database
     * @return An observer that will be set to true if the communication with the DB is over and no error
     */
    fun updateProfile(
        profile: UserProfile
    ): Observable<Boolean>

    /**
     * Get list of profile of a user
     * @param profiles profile list
     * @param user user
     * @return An observer that will be set to true if the communication with the DB is over and no error
     */
    fun getUserProfilesList(
        profiles: ObservableList<UserProfile>,
        user: UserEntity
    ): Observable<Boolean>

    /**
     * Get list of user of a profile
     * @param users user list
     * @param profile profile
     * @return An observer that will be set to true if the communication with the DB is over and no error
     */
    fun getProfilesUserList(
        users: ObservableList<UserEntity>,
        profile: UserProfile
    ): Observable<Boolean>

    /**
     * Look in the database if the user already exists or not
     * @param profile live data that will be set with the find profile value
     * @param pid profile id we want to get
     * @return An observer that will be set to true if the communication with the DB is over and no error
     */
    fun getProfileById(
        profile: Observable<UserProfile>,
        pid: String
    ): Observable<Boolean>
}