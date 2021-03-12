package com.github.sdpteam15.polyevents.database

import androidx.lifecycle.MutableLiveData
import com.github.sdpteam15.polyevents.user.Profile
import com.github.sdpteam15.polyevents.user.ProfileInterface
import com.github.sdpteam15.polyevents.user.User
import com.github.sdpteam15.polyevents.user.UserInterface
import java.util.HashMap
import kotlin.properties.ObservableProperty

/**
 * Fake Database
 */
class FakeDatabase :  DatabaseInterface {
    override fun getListProfile(uid: String, user: UserInterface): List<ProfileInterface> =
        emptyList()

    override fun addProfile(profile: ProfileInterface, uid: String, user: UserInterface): Boolean =
        true

    override fun removeProfile(
        profile: ProfileInterface,
        uid: String,
        user: UserInterface
    ): Boolean = true

    override fun updateUserInformation(
        newValues: HashMap<String, String>,
        success: MutableLiveData<Boolean>,
        uid: String,
        userAccess: UserInterface
    ) {
        TODO("Not yet implemented")
    }

    override fun getUserInformation(
        listener: MutableLiveData<User>,
        uid: String,
        userAccess: UserInterface
    ) {
        TODO("Not yet implemented")
    }
}