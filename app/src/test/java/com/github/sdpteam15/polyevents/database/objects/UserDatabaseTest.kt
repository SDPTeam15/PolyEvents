package com.github.sdpteam15.polyevents.database.objects

import com.github.sdpteam15.polyevents.database.HelperTestFunction
import com.github.sdpteam15.polyevents.model.database.remote.Database
import com.github.sdpteam15.polyevents.model.database.remote.DatabaseConstant.CollectionConstant.PROFILE_COLLECTION
import com.github.sdpteam15.polyevents.model.database.remote.DatabaseConstant.CollectionConstant.USER_COLLECTION
import com.github.sdpteam15.polyevents.model.database.remote.FirestoreDatabaseProvider
import com.github.sdpteam15.polyevents.model.database.remote.adapter.ProfileAdapter
import com.github.sdpteam15.polyevents.model.database.remote.adapter.UserAdapter
import com.github.sdpteam15.polyevents.model.database.remote.objects.UserDatabase
import com.github.sdpteam15.polyevents.model.entity.UserEntity
import com.github.sdpteam15.polyevents.model.entity.UserProfile
import com.github.sdpteam15.polyevents.model.observable.Observable
import com.github.sdpteam15.polyevents.model.observable.ObservableList
import com.github.sdpteam15.polyevents.view.PolyEventsApplication
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@Suppress("UNCHECKED_CAST", "TYPE_INFERENCE_ONLY_INPUT_TYPES_WARNING")
class UserDatabaseTest {
    lateinit var mockUserDatabase: UserDatabase

    @Before
    fun setup() {
        PolyEventsApplication.inTest = true
        val mockDatabaseInterface = HelperTestFunction.mockDatabaseInterface()
        mockUserDatabase = UserDatabase(mockDatabaseInterface)
        Database.currentDatabase = mockDatabaseInterface
        Mockito.`when`(mockDatabaseInterface.userDatabase).thenReturn(mockUserDatabase)

        HelperTestFunction.clearQueue()
    }

    @After
    fun teardown() {
        Database.currentDatabase = FirestoreDatabaseProvider
    }

    @Test
    fun updateUserInformation() {
        PolyEventsApplication.inTest = true
        val user = UserEntity("uid")

        HelperTestFunction.nextSetEntity { true }
        mockUserDatabase.updateUserInformation(user)
            .observeOnce { assert(it.value) }.then.postValue(false)

        val set = HelperTestFunction.lastSetEntity()!!

        assertEquals(user, set.element)
        assertEquals(user.uid, set.id)
        assertEquals(USER_COLLECTION, set.collection)
        assertEquals(UserAdapter, set.adapter)
    }

    @Test
    fun firstConnexion() {
        val user = UserEntity("uid")

        HelperTestFunction.nextSetEntity { true }
        HelperTestFunction.nextGetListEntity { true }

        mockUserDatabase.firstConnexion(user).observeOnce { assert(it.value) }.then.postValue(false)

        val set = HelperTestFunction.lastSetEntity()!!

        assertEquals(user, set.element)
        assertEquals(user.uid, set.id)
        assertEquals(USER_COLLECTION, set.collection)
        assertEquals(UserAdapter, set.adapter)
    }

    @Test
    fun inDatabase() {
        val isInDb = Observable<Boolean>()
        val uid = "uid"

        HelperTestFunction.nextGetEntity { true }
        mockUserDatabase.inDatabase(isInDb.observeOnce { assert(it.value) }.then, uid)
            .observeOnce { assert(it.value) }.then.postValue(false)

        val get = HelperTestFunction.lastGetEntity()!!

        assertNotNull(get.element)
        assertEquals(uid, get.id)
        assertEquals(USER_COLLECTION, get.collection)
        assertEquals(UserAdapter, get.adapter)
    }

    @Test
    fun getUserInformation() {
        val user = Observable<UserEntity>()
        val uid = "uid"
        HelperTestFunction.nextGetEntity { true }
        mockUserDatabase.getUserInformation(user, uid)
            .observeOnce { assert(it.value) }.then.postValue(false)

        val get = HelperTestFunction.lastGetEntity()!!

        assertEquals(user, get.element)
        assertEquals(uid, get.id)
        assertEquals(USER_COLLECTION, get.collection)
        assertEquals(UserAdapter, get.adapter)
    }

    @Test
    fun addUserProfileAndAddToUserWithANewProfile() {
        val profile = UserProfile()
        val user = UserEntity("uid")

        HelperTestFunction.nextAddEntityAndGetId { "pid" }
        HelperTestFunction.nextSetEntity { true }
        mockUserDatabase.addUserProfileAndAddToUser(profile, user)
            .observeOnce { assert(it.value) }.then.postValue(false)

        val add = HelperTestFunction.lastAddEntityAndGetId()!!
        val set = HelperTestFunction.lastSetEntity()!!

        assertEquals(profile, add.element)
        assertEquals(PROFILE_COLLECTION, add.collection)
        assertEquals(ProfileAdapter, add.adapter)

        assertEquals(user, set.element)
        assertEquals(user.uid, set.id)
        assertEquals(USER_COLLECTION, set.collection)
        assertEquals(UserAdapter, set.adapter)
    }

    @Test
    fun addUserProfileAndAddToUserWithANewProfileAndFail() {
        val profile = UserProfile()
        val user = UserEntity("uid")
        HelperTestFunction.nextAddEntityAndGetId { "" }
        mockUserDatabase.addUserProfileAndAddToUser(profile, user)
            .observeOnce { assert(!it.value) }.then.postValue(true)

        val add = HelperTestFunction.lastAddEntityAndGetId()!!

        assertEquals(profile, add.element)
        assertEquals(PROFILE_COLLECTION, add.collection)
        assertEquals(ProfileAdapter, add.adapter)
    }

    @Test
    fun addUserProfileAndAddToUser() {
        val profile = UserProfile("pid")
        val user = UserEntity("uid")

        HelperTestFunction.nextAddEntityAndGetId { "pid" }
        HelperTestFunction.nextSetEntity { true }
        mockUserDatabase.addUserProfileAndAddToUser(profile, user)
            .observeOnce { assert(it.value) }.then.postValue(false)

        val set1 = HelperTestFunction.lastSetEntity()!!
        val set2 = HelperTestFunction.lastSetEntity()!!

        assertEquals(profile, set1.element)
        assertEquals(profile.pid, set1.id)
        assertEquals(PROFILE_COLLECTION, set1.collection)
        assertEquals(ProfileAdapter, set1.adapter)

        assertEquals(user, set2.element)
        assertEquals(user.uid, set2.id)
        assertEquals(USER_COLLECTION, set2.collection)
        assertEquals(UserAdapter, set2.adapter)
    }

    @Test
    fun removeProfileFromUser() {
        val profile = UserProfile("pid")
        val user = UserEntity("uid")

        HelperTestFunction.nextSetEntity { true }
        HelperTestFunction.nextDeleteEntity { true }
        mockUserDatabase.removeProfileFromUser(profile, user)
            .observeOnce { assert(it.value) }.then.postValue(false)


        val set = HelperTestFunction.lastSetEntity()!!
        val del = HelperTestFunction.lastDeleteEntity()!!

        assertEquals(user, set.element)
        assertEquals(user.uid, set.id)
        assertEquals(USER_COLLECTION, set.collection)
        assertEquals(UserAdapter, set.adapter)

        assertEquals(profile.pid, del.id)
        assertEquals(PROFILE_COLLECTION, del.collection)
    }


    @Test
    fun removeProfileFromUserWithFail() {
        val profile = UserProfile("pid")
        val user = UserEntity("uid")

        HelperTestFunction.nextSetEntity { false }
        mockUserDatabase.removeProfileFromUser(profile, user)
            .observeOnce { assert(!it.value) }.then.postValue(true)


        val set = HelperTestFunction.lastSetEntity()!!

        assertEquals(user, set.element)
        assertEquals(user.uid, set.id)
        assertEquals(USER_COLLECTION, set.collection)
        assertEquals(UserAdapter, set.adapter)
    }

    @Test
    fun removeProfileFromUserWithoutDelete() {
        val profile = UserProfile("pid", users = mutableListOf("not uid"))
        val user = UserEntity("uid")

        HelperTestFunction.nextSetEntity { true }
        HelperTestFunction.nextSetEntity { true }
        mockUserDatabase.removeProfileFromUser(profile, user)
            .observeOnce { assert(it.value) }.then.postValue(false)


        val set1 = HelperTestFunction.lastSetEntity()!!
        val set2 = HelperTestFunction.lastSetEntity()!!

        assertEquals(user, set1.element)
        assertEquals(user.uid, set1.id)
        assertEquals(USER_COLLECTION, set1.collection)
        assertEquals(UserAdapter, set1.adapter)

        assertEquals(profile, set2.element)
        assertEquals(profile.pid, set2.id)
        assertEquals(PROFILE_COLLECTION, set2.collection)
        assertEquals(ProfileAdapter, set2.adapter)
    }

    @Test
    fun updateProfile() {
        val profile = UserProfile("pid")

        HelperTestFunction.nextSetEntity { true }
        mockUserDatabase.updateProfile(profile)
            .observeOnce { assert(it.value) }.then.postValue(false)

        val set = HelperTestFunction.lastSetEntity()!!

        assertEquals(profile, set.element)
        assertEquals(profile.pid, set.id)
        assertEquals(PROFILE_COLLECTION, set.collection)
        assertEquals(ProfileAdapter, set.adapter)
    }

    @Test
    fun getUserProfilesList() {
        val profiles = ObservableList<UserProfile>()
        val user = UserEntity("uid", profiles = mutableListOf("pid"))

        HelperTestFunction.nextGetListEntity { true }
        mockUserDatabase.getUserProfilesList(profiles, user)
            .observeOnce { assert(it.value) }.then.postValue(false)

        val getList = HelperTestFunction.lastGetListEntity()!!

        assertEquals(profiles, getList.element)
        assertEquals(user.profiles, getList.ids)
        assertNull(getList.matcher)
        assertEquals(PROFILE_COLLECTION, getList.collection)
        assertEquals(ProfileAdapter, getList.adapter)
    }

    @Test
    fun getUserLists() {
        val users = ObservableList<UserEntity>()

        HelperTestFunction.nextGetListEntity { true }
        mockUserDatabase.getListAllUsers(users)
            .observeOnce { assert(it.value) }.then.postValue(false)

        val getList = HelperTestFunction.lastGetListEntity()!!

        assertEquals(users, getList.element)
        assertNull(getList.matcher)
        assertEquals(USER_COLLECTION, getList.collection)
        assertEquals(UserAdapter, getList.adapter)
    }

    @Test
    fun getProfilesUserList() {
        val users = ObservableList<UserEntity>()
        val profile = UserProfile("pid", users = mutableListOf("uid"))

        HelperTestFunction.nextGetListEntity { true }
        mockUserDatabase.getProfilesUserList(users, profile)
            .observeOnce { assert(it.value) }.then.postValue(false)

        val getList = HelperTestFunction.lastGetListEntity()!!

        assertEquals(users, getList.element)
        assertEquals(profile.users, getList.ids)
        assertNull(getList.matcher)
        assertEquals(USER_COLLECTION, getList.collection)
        assertEquals(UserAdapter, getList.adapter)
    }

    @Test
    fun getProfileById() {
        val profile = Observable<UserProfile>()
        val pid = "pid"

        HelperTestFunction.nextGetEntity { true }
        mockUserDatabase.getProfileById(profile, pid)
            .observeOnce { assert(it.value) }.then.postValue(false)

        val get = HelperTestFunction.lastGetEntity()!!

        assertEquals(profile, get.element)
        assertEquals(pid, get.id)
        assertEquals(PROFILE_COLLECTION, get.collection)
        assertEquals(ProfileAdapter, get.adapter)
    }

}