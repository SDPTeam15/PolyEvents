package com.github.sdpteam15.polyevents.fakedatabase

import com.github.sdpteam15.polyevents.database.objects.UserDatabaseInterface
import com.github.sdpteam15.polyevents.database.observe.Observable
import com.github.sdpteam15.polyevents.database.observe.ObservableList
import com.github.sdpteam15.polyevents.model.UserEntity
import com.github.sdpteam15.polyevents.model.UserProfile

object FakeDatabaseUser : UserDatabaseInterface {
    lateinit var profiles: MutableList<UserProfile>

    override var firstConnectionUser: UserEntity = UserEntity(uid = "DEFAULT")
    override fun updateUserInformation(
        user: UserEntity,
        userAccess: UserProfile?
    ) = Observable(true, this)

    override fun firstConnexion(
        user: UserEntity
    ) = Observable(true, this)

    override fun inDatabase(
        isInDb: Observable<Boolean>,
        uid: String,
        userAccess: UserProfile?
    ): Observable<Boolean> {
        isInDb.postValue(true, this)
        return Observable(true, this)
    }

    override fun getUserInformation(
        user: Observable<UserEntity>,
        uid: String,
        userAccess: UserProfile?
    ) = Observable(true, this)

    override fun getListAllUsers(
        users: ObservableList<UserEntity>,
        userAccess: UserProfile?
    ): Observable<Boolean> {
        return Observable(true)
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

    override fun removeProfileFromUser(
        profile: UserProfile,
        user: UserEntity,
        userAccess: UserProfile?
    ): Observable<Boolean> {
        TODO("Not yet implemented")
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
}