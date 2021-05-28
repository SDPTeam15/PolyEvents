package com.github.sdpteam15.polyevents.fakedatabase

import com.github.sdpteam15.polyevents.model.database.remote.objects.UserDatabaseInterface
import com.github.sdpteam15.polyevents.model.entity.UserEntity
import com.github.sdpteam15.polyevents.model.entity.UserProfile
import com.github.sdpteam15.polyevents.model.observable.Observable
import com.github.sdpteam15.polyevents.model.observable.ObservableList
import com.google.firebase.firestore.auth.User

object FakeDatabaseUser : UserDatabaseInterface {
    lateinit var profiles: MutableList<UserProfile>
    var allUsers = mutableListOf<UserEntity>()
    override var firstConnectionUser: UserEntity = UserEntity(uid = "DEFAULT")
    override fun updateUserInformation(
        user: UserEntity,
        userAccess: UserProfile?
    ) = Observable(true, FakeDatabase)

    override fun firstConnexion(
        user: UserEntity
    ) = Observable(true, FakeDatabase)

    override fun inDatabase(
        isInDb: Observable<Boolean>,
        uid: String,
        userAccess: UserProfile?
    ): Observable<Boolean> {
        isInDb.postValue(true, FakeDatabase)
        return Observable(true, FakeDatabase)
    }

    override fun getUserInformation(
        user: Observable<UserEntity>,
        uid: String,
        userAccess: UserProfile?
    ):Observable<Boolean> {
        user.postValue(FakeDatabase.CURRENT_USER)
        return Observable(true,FakeDatabase)
    }

    override fun getListAllUsers(
        users: ObservableList<UserEntity>,
        userAccess: UserProfile?
    ): Observable<Boolean> {
        users.clear(FakeDatabase)
        users.addAll(allUsers, FakeDatabase)

        return Observable(true,FakeDatabase)
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