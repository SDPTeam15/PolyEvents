package com.github.sdpteam15.polyevents.database

import androidx.lifecycle.MutableLiveData
import com.github.sdpteam15.polyevents.event.Event
import com.github.sdpteam15.polyevents.user.Profile
import com.github.sdpteam15.polyevents.user.Profile.Companion.CurrentProfile
import com.github.sdpteam15.polyevents.user.ProfileInterface
import com.github.sdpteam15.polyevents.user.User
import com.github.sdpteam15.polyevents.user.UserInterface
import com.google.android.gms.tasks.Task
import java.util.*

const val NUMBER_UPCOMING_EVENTS = 3

/**
 * Database interface
 */
interface DatabaseInterface {

    /**
     * Current user of this database
     */
    val currentUser : DatabaseUserInterface?

    /**
     * Get list of profile of a user uid
     * @param uid uid
     * @param user user for database access
     * @return list of profile of a user uid
     */
    fun getListProfile(
        uid: String,
        user: UserInterface = currentUser as UserInterface
    ): List<ProfileInterface>

    /**
     * Add profile to a user
     * @param profile profile to add
     * @param uid uid
     * @param user user for database access
     * @return if the operation succeed
     */
    fun addProfile(
        profile: ProfileInterface, uid: String,
        user: UserInterface = currentUser as UserInterface
    ): Boolean

    /**
     * Remove profile from a user
     * @param profile profile to remove
     * @param uid uid
     * @param user user for database access
     * @return if the operation succeed
     */
    fun removeProfile(
        profile: ProfileInterface, uid: String = (currentUser as UserInterface).uid,
        user: UserInterface = currentUser as UserInterface
    ): Boolean

    /**
     * Update profile
     * @param event event to update
     * @param profile profile for database access
     * @return if the operation succeed
     */
    fun updateProfile(
        profile: ProfileInterface,
        user: UserInterface = currentUser as UserInterface
    ): Boolean

    /**
     * Get list of event
     * @param matcher matcher for the recherche
     * @param number maximum of result
     * @param profile profile for database access
     * @return list of event
     */
    fun getListEvent(
        matcher: String? = null, number: Int? = null,
        profile: ProfileInterface = CurrentProfile
    ): List<Event>

    /**
     * Query the upcoming events
     * @param number : the number of events to retrieve
     * @param profile profile for database access
     * @return List of events in upcoming order (closest first)
     */
    fun getUpcomingEvents(
        number: Int = NUMBER_UPCOMING_EVENTS,
        profile: ProfileInterface = CurrentProfile
    ): List<Event>

    /**
     * Get event from ID
     * @param id ID of the event
     * @param profile profile for database access
     * @return event corresponding to the given ID
     */
    fun getEventFromId(
        id: String,
        profile: ProfileInterface = CurrentProfile
    ): Event?

    /**
     * Update or request an update for an event
     * @param Event event to update
     * @param profile profile for database access
     */
    fun updateEvent(
        Event: Event,
        profile: ProfileInterface = CurrentProfile
    ): Boolean
    fun removeProfile(profile: ProfileInterface, success: MutableLiveData<Boolean>, uid : String = (User.currentUser as UserInterface).uid,
                   user : UserInterface = User.currentUser as UserInterface)
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
    fun updateUserInformation(newValues: HashMap<String, String>, success: MutableLiveData<Boolean>, uid:String, userAccess: UserInterface = User.currentUser as UserInterface)

    fun getUserInformation(listener: MutableLiveData<User>, uid: String = (User.currentUser as UserInterface).uid, userAccess: UserInterface = User.currentUser as UserInterface): MutableLiveData<Boolean>
}