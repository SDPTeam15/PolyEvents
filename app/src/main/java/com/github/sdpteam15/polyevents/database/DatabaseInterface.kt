package com.github.sdpteam15.polyevents.database

import androidx.lifecycle.MutableLiveData
import com.github.sdpteam15.polyevents.activity.Activity
import com.github.sdpteam15.polyevents.user.Profile
import com.github.sdpteam15.polyevents.user.ProfileInterface
import com.github.sdpteam15.polyevents.user.UserInterface
import com.github.sdpteam15.polyevents.user.User
import java.util.*
import java.util.concurrent.Future
import kotlin.properties.ObservableProperty

/**
 * Database interface
 */
interface DatabaseInterface {
    /**
     * Get list of profile of a user uid
     * @param uid uid
     * @param user user for database access
     */
    fun getListProfile(uid : String = (User.CurrentUser as UserInterface).UID,
        user : UserInterface = User.CurrentUser as UserInterface) : List<ProfileInterface>

    /**
     * Add profile to a user
     * @param profile profile to add
     * @param uid uid
     * @param user user for database access
     */
    fun addProfile(profile: ProfileInterface, uid : String = (User.CurrentUser as UserInterface).UID,
                   user : UserInterface = User.CurrentUser as UserInterface) : Boolean

    /**
     * Remove profile from a user
     * @param profile profile to remove
     * @param uid uid
     * @param user user for database access
     */
    fun removeProfile(profile: ProfileInterface, uid : String = (User.CurrentUser as UserInterface).UID,
                   user : UserInterface = User.CurrentUser as UserInterface) : Boolean
    /*
    fun acceptMaterialReservation()

    fun deleteMaterialReservation()

    fun getAllMaterialReservationRequests()

    fun createMaterialReservation()

    fun createItem()

    fun updateEvent(userAccess: UserInterface = User.CurrentUser as UserInterface)

    fun createEvent(userAccess: UserInterface = User.CurrentUser as UserInterface)

    fun getEventList(activityListToModify: MutableLiveData<List<Activity>>)

    fun getUpcomingEvents(activityListToModify: MutableLiveData<List<Activity>>)
    */
    fun updateUserInformation(newValues: HashMap<String, String>, success: MutableLiveData<Boolean>, uid:String, userAccess: UserInterface = User.CurrentUser as UserInterface)

    fun getUserInformation(listener: MutableLiveData<User>, uid: String = (User.CurrentUser as UserInterface).UID, userAccess: UserInterface = User.CurrentUser as UserInterface)
}