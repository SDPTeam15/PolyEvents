package com.github.sdpteam15.polyevents.fakedatabase

import com.github.sdpteam15.polyevents.database.objects.UserDatabaseFirestore
import com.github.sdpteam15.polyevents.database.objects.UserDatabaseInterface
import com.github.sdpteam15.polyevents.database.observe.Observable
import com.github.sdpteam15.polyevents.database.observe.ObservableList
import com.github.sdpteam15.polyevents.model.UserEntity
import com.github.sdpteam15.polyevents.model.UserProfile

object FakeDatabaseUser:UserDatabaseInterface {
    lateinit var profiles: MutableList<UserProfile>

    private fun initProfiles() {
        profiles = mutableListOf()
    }

   fun getProfilesList(uid: String, user: UserEntity?): List<UserProfile> =
        profiles


    fun addProfile(profile: UserProfile, uid: String, user: UserEntity?): Boolean =
        profiles.add(profile)


    override fun updateUserInformation(
        newValues: Map<String, String>,
        uid: String,
        userAccess: UserEntity?
    ): Observable<Boolean> =
        Observable(true, this)

    override fun firstConnexion(
        user: UserEntity,
        userAccess: UserEntity?
    ): Observable<Boolean> =
        Observable(true, this)

    override fun inDatabase(
        isInDb: Observable<Boolean>,
        uid: String,
        userAccess: UserEntity?
    ): Observable<Boolean> {
        isInDb.postValue(true, this)
        return Observable(true, this)
    }

    override fun getUserInformation(
        user: Observable<UserEntity>,
        uid: String?,
        userAccess: UserEntity?
    ): Observable<Boolean> {
        TODO("Not yet implemented")
    }

    override fun addUserProfileAndAddToUser(
        profile: UserProfile,
        user: UserEntity,
        userAccess: UserEntity?
    ): Observable<Boolean> {
        profile.pid = FakeDatabase.generateRandomKey()
        user.profiles.add(profile.pid!!)
        return Observable(profile.pid != null)
    }

    override fun updateProfile(profile: UserProfile, userAccess: UserEntity?): Observable<Boolean> {
        TODO("Not yet implemented")
    }

    override fun getUserProfilesList(
        profiles: ObservableList<UserProfile>,
        user: UserEntity,
        userAccess: UserEntity?
    ): Observable<Boolean> {

        TODO("Not yet implemented")
    }

    override fun getProfilesUserList(
        users: ObservableList<UserEntity>,
        profile: UserProfile,
        userAccess: UserEntity?
    ): Observable<Boolean> {
        TODO("Not yet implemented")
    }

    override fun getProfileById(
        profile: Observable<UserProfile>,
        pid: String,
        userAccess: UserEntity?
    ): Observable<Boolean> {
        TODO("Not yet implemented")
    }

    override fun removeProfile(profile: UserProfile, user: UserEntity?): Observable<Boolean> {
        TODO("Not yet implemented")
    }


}