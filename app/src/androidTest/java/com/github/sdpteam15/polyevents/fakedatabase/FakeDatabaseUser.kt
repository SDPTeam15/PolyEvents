package com.github.sdpteam15.polyevents.fakedatabase

import com.github.sdpteam15.polyevents.model.database.remote.objects.UserDatabaseInterface
import com.github.sdpteam15.polyevents.model.entity.UserEntity
import com.github.sdpteam15.polyevents.model.entity.UserProfile
import com.github.sdpteam15.polyevents.model.observable.Observable
import com.github.sdpteam15.polyevents.model.observable.ObservableList

object FakeDatabaseUser : UserDatabaseInterface {
    lateinit var profiles: MutableList<UserProfile>
    var allUsers = mutableListOf<UserEntity>()
    override var firstConnectionUser: UserEntity = UserEntity(uid = "DEFAULT")
    override fun updateUserInformation(
        user: UserEntity
    ): Observable<Boolean> {
        allUsers.add(user)
        return Observable(true, FakeDatabase)
    }

    override fun firstConnexion(
        user: UserEntity
    ): Observable<Boolean> {
        allUsers.add(user)
        return Observable(true, FakeDatabase)
    }

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
        return Observable(profile.pid != null, FakeDatabase)
    }

    override fun removeProfileFromUser(
        profile: UserProfile,
        user: UserEntity
    ): Observable<Boolean> {
        profiles.remove(profile)
        return Observable(true, FakeDatabase)
    }

    override fun updateProfile(profile: UserProfile): Observable<Boolean> {
        profiles[profiles.indexOfFirst { it.pid == profile.pid }] = profile
        return Observable(true, FakeDatabase)
    }

    override fun getUserProfilesList(
        profiles: ObservableList<UserProfile>,
        user: UserEntity
    ): Observable<Boolean> {
        profiles.addAll(this.allUsers.first { it.uid == user.uid }.userProfiles)
        return Observable(true, FakeDatabase)
    }

    override fun getProfilesUserList(
        users: ObservableList<UserEntity>,
        profile: UserProfile
    ): Observable<Boolean> {
        users.addAll(profiles.first { it.pid == profile.pid }.userEntity)
        return Observable(true, FakeDatabase)
    }

    override fun getProfileById(
        profile: Observable<UserProfile>,
        pid: String
    ): Observable<Boolean> {
        profile.postValue(profiles.first { it.pid == pid })
        return Observable(true,FakeDatabase)
    }
}