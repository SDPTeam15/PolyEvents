package com.github.sdpteam15.polyevents.database.objects

import com.github.sdpteam15.polyevents.database.Database
import com.github.sdpteam15.polyevents.database.observe.Observable
import com.github.sdpteam15.polyevents.database.observe.ObservableList
import com.github.sdpteam15.polyevents.model.UserEntity
import com.github.sdpteam15.polyevents.model.UserProfile

interface UserDatabaseInterface {
    /**
     * Map used in the firstConnection method. It's public to be able to use it in tests
     */
    var firstConnectionUser: UserEntity

    val currentUser: UserEntity?
        get() = Database.currentDatabase.currentUser
    val currentProfile: UserProfile?
        get() = Database.currentDatabase.currentProfile

    // TODO: Do we need userAccess for these methods? (Might do these with security rules)
    /**
     * Update the user information in the database
     * @param user the user from which we want to query the information
     * @param userAccess the user profile to use its permission
     * @return An observer that will be set to true if the communication with the DB is over and no error
     */
    fun updateUserInformation(
        user: UserEntity,
        userAccess: UserProfile? = currentProfile
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
     * @param userAccess: the user profile to use its permission
     * @return An observer that will be set to true if the communication with the DB is over and no error
     */
    fun inDatabase(
        isInDb: Observable<Boolean>,
        uid: String,
        userAccess: UserProfile? = currentProfile
    ): Observable<Boolean>

    /**
     * Look in the database if the user already exists or not
     * @param user observable data that will be set with the user information from the database
     * @param uid user uid we want to get the information
     * @param userAccess the user profile to use its permission
     * @return An observer that will be set to true if the communication with the DB is over and no error
     */
    fun getUserInformation(
        user: Observable<UserEntity>,
        uid: String = currentUser!!.uid,
        userAccess: UserProfile? = currentProfile
    ): Observable<Boolean>

    /**
     * add a UserProfile to a UserEntity
     * @param profile profile we want to add in the database
     * @param user user to add
     * @param userAccess user for database access
     * @return An observer that will be set to true if the communication with the DB is over and no error
     */
    fun addUserProfileAndAddToUser(
        profile: UserProfile,
        user: UserEntity = currentUser!!,
        userAccess: UserEntity? = currentUser
    ): Observable<Boolean>

    /**
     * remove a UserProfile from a UserEntity
     * @param profile profile to remove
     * @param user user for database access
     * @param userAccess the user profile to use its permission
     * @return if the operation succeed
    */
    fun removeProfileFromUser(
        profile: UserProfile,
        user: UserEntity,
        userAccess: UserProfile? = currentProfile
    ): Observable<Boolean>

    /**
     * Update profile
     * @param profile a map with the new value to set in the database
     * @param userAccess the user profile to use its permission
     * @return An observer that will be set to true if the communication with the DB is over and no error
     */
    fun updateProfile(
        profile: UserProfile,
        userAccess: UserEntity? = currentUser
    ): Observable<Boolean>

    /**
     * Get list of profile of a user
     * @param profiles profile list
     * @param user user
     * @param userAccess user for database access
     * @return An observer that will be set to true if the communication with the DB is over and no error
     */
    fun getUserProfilesList(
        profiles: ObservableList<UserProfile>,
        user: UserEntity,
        userAccess: UserEntity? = currentUser
    ): Observable<Boolean>

    /**
     * Get list of user of a profile
     * @param users user list
     * @param profile profile
     * @param userAccess user for database access
     * @return An observer that will be set to true if the communication with the DB is over and no error
     */
    fun getProfilesUserList(
        users: ObservableList<UserEntity>,
        profile: UserProfile,
        userAccess: UserEntity? = currentUser
    ): Observable<Boolean>

    /**
     * Look in the database if the user already exists or not
     * @param profile live data that will be set with the find profile value
     * @param pid profile id we want to get
     * @param userAccess the profile object to use its permission
     * @return An observer that will be set to true if the communication with the DB is over and no error
     */
    fun getProfileById(
        profile: Observable<UserProfile>,
        pid: String,
        userAccess: UserEntity? = currentUser
    ): Observable<Boolean>


    /**
     * Remove profile from a user
     * @param profile profile to remove
     * @param user user for database access
     * @return if the operation succeed
     */
    fun removeProfile(
        profile: UserProfile,
        user: UserEntity? = currentUser
    ): Observable<Boolean>

}