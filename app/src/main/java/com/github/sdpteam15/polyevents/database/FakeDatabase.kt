package com.github.sdpteam15.polyevents.database

import com.github.sdpteam15.polyevents.user.ProfileInterface
import com.github.sdpteam15.polyevents.user.UserInterface

/**
 * Fake Database
 */
class FakeDatabase :  DatabaseInterface{
    override fun getListProfile(uid: String, user: UserInterface): List<ProfileInterface>
        = emptyList()
    override fun addProfile(profile: ProfileInterface, uid: String, user: UserInterface): Boolean
        = true
    override fun removeProfile( profile: ProfileInterface, uid: String, user: UserInterface ): Boolean
        = true
}