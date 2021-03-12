package com.github.sdpteam15.polyevents.user

import com.github.sdpteam15.polyevents.event.Event
import com.github.sdpteam15.polyevents.database.DatabaseInterface

object FakeDatabase : DatabaseInterface {

    override fun getListProfile(uid: String, user: UserInterface): List<ProfileInterface> =
        emptyList()

    override fun addProfile(profile: ProfileInterface, uid: String, user: UserInterface): Boolean =
        true

    override fun removeProfile(
        profile: ProfileInterface,
        uid: String,
        user: UserInterface
    ): Boolean = true

    override fun updateProfile(profile: ProfileInterface, user: UserInterface): Boolean = true

    override fun getListEvent(
        matcher: String?,
        number: Int?,
        profile: ProfileInterface
    ): List<Event> = emptyList()

    override fun getUpcomingEvents(number: Int, profile: ProfileInterface): List<Event> =
        emptyList()


    override fun getEventFromId(id: String, profile: ProfileInterface): Event? = null

    override fun updateEvent(Activity: Event, profile: ProfileInterface): Boolean = true
}