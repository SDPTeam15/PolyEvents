package com.github.sdpteam15.polyevents.fakedatabase

import com.github.sdpteam15.polyevents.model.database.remote.objects.UserDatabaseInterface
import com.github.sdpteam15.polyevents.model.entity.UserEntity
import com.github.sdpteam15.polyevents.model.entity.UserProfile
import com.github.sdpteam15.polyevents.model.observable.Observable
import com.github.sdpteam15.polyevents.model.observable.ObservableList

object FakeDatabaseUser : UserDatabaseInterface {
    lateinit var profiles: MutableList<UserProfile>
    var allUsers = mutableListOf<UserEntity>()
    override fun updateUserInformation(
        user: UserEntity
    ) = Observable(true, FakeDatabase)

    override fun firstConnexion(
        user: UserEntity
    ) = Observable(true, FakeDatabase)

    override fun inDatabase(
        isInDb: Observable<Boolean>,
        uid: String
    ): Observable<Boolean> {
        isInDb.postValue(true, FakeDatabase)
        return Observable(true, FakeDatabase)
    }

    override fun getUserInformation(
        user: Observable<UserEntity>,
        uid: String
    ): Observable<Boolean> {
        user.postValue(FakeDatabase.CURRENT_USER)
        return Observable(true, FakeDatabase)
    }

    override fun getListAllUsers(
        users: ObservableList<UserEntity>
    ): Observable<Boolean> {
        users.clear(FakeDatabase)
        users.addAll(allUsers, FakeDatabase)

        return Observable(true, FakeDatabase)
    }

    override fun addUserProfileAndAddToUser(
        profile: UserProfile,
        user: UserEntity
    ): Observable<Boolean> {
        profile.pid = FakeDatabase.generateRandomKey()
        user.profiles.add(profile.pid!!)
        return Observable(profile.pid != null)
    }

    override fun removeProfileFromUser(
        profile: UserProfile,
        user: UserEntity
    ): Observable<Boolean> {
        TODO("Not yet implemented")
    }

    override fun updateProfile(profile: UserProfile): Observable<Boolean> {
        TODO("Not yet implemented")
    }

    override fun getUserProfilesList(
        profiles: ObservableList<UserProfile>,
        user: UserEntity
    ): Observable<Boolean> {

        TODO("Not yet implemented")
    }

    override fun getProfilesUserList(
        users: ObservableList<UserEntity>,
        profile: UserProfile
    ): Observable<Boolean> {
        TODO("Not yet implemented")
    }

    override fun getProfileById(
        profile: Observable<UserProfile>,
        pid: String
    ): Observable<Boolean> {
        TODO("Not yet implemented")
    }
}