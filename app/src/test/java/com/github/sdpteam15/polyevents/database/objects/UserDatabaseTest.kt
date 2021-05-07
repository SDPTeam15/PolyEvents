package com.github.sdpteam15.polyevents.database.objects

import com.github.sdpteam15.polyevents.database.HelperTestFunction
import com.github.sdpteam15.polyevents.model.database.remote.DatabaseConstant.CollectionConstant.PROFILE_COLLECTION
import com.github.sdpteam15.polyevents.model.database.remote.DatabaseConstant.CollectionConstant.USER_COLLECTION
import com.github.sdpteam15.polyevents.model.database.remote.adapter.ProfileAdapter
import com.github.sdpteam15.polyevents.model.database.remote.adapter.UserAdapter
import com.github.sdpteam15.polyevents.model.database.remote.objects.UserDatabase
import com.github.sdpteam15.polyevents.model.entity.UserEntity
import com.github.sdpteam15.polyevents.model.entity.UserProfile
import com.github.sdpteam15.polyevents.model.observable.Observable
import com.github.sdpteam15.polyevents.model.observable.ObservableList
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@Suppress("UNCHECKED_CAST", "TYPE_INFERENCE_ONLY_INPUT_TYPES_WARNING")
class UserDatabaseTest {
    lateinit var mockUserDatabase: UserDatabase

    @Before
    fun setup() {
        val mockDatabaseInterface = HelperTestFunction.mockFor()
        mockUserDatabase = UserDatabase(mockDatabaseInterface)
        HelperTestFunction.clearQueue()
    }

    @Test
    fun updateUserInformation() {
        val user = UserEntity("uid")
        val userAccess = UserProfile()

        HelperTestFunction.nextBoolean(true)
        mockUserDatabase.updateUserInformation(user, userAccess)
            .observeOnce { assert(it.value) }.then.postValue(false)

        val set = HelperTestFunction.setEntityQueue.poll()!!

        assertEquals(user, set.element)
        assertEquals(user.uid, set.id)
        assertEquals(USER_COLLECTION, set.collection)
        assertEquals(UserAdapter, set.adapter)
    }

    @Test
    fun firstConnexion() {
        val user = UserEntity("uid")

        HelperTestFunction.nextBoolean(true)
        mockUserDatabase.firstConnexion(user).observeOnce { assert(it.value) }.then.postValue(false)

        val set = HelperTestFunction.setEntityQueue.poll()!!

        assertEquals(user, set.element)
        assertEquals(user.uid, set.id)
        assertEquals(USER_COLLECTION, set.collection)
        assertEquals(UserAdapter, set.adapter)
    }

    @Test
    fun inDatabase() {
        val isInDb = Observable<Boolean>()
        val uid = "uid"
        val userAccess = UserProfile()

        HelperTestFunction.nextBoolean(true)
        mockUserDatabase.inDatabase(isInDb.observeOnce { assert(it.value) }.then, uid, userAccess)
            .observeOnce { assert(it.value) }.then.postValue(false)

        val get = HelperTestFunction.getEntityQueue.poll()!!

        assertNotNull(get.element)
        assertEquals(uid, get.id)
        assertEquals(USER_COLLECTION, get.collection)
        assertEquals(UserAdapter, get.adapter)
    }

    @Test
    fun getUserInformation() {
        val user = Observable<UserEntity>()
        val uid = "uid"
        val userAccess = UserProfile()

        HelperTestFunction.nextBoolean(true)
        mockUserDatabase.getUserInformation(user, uid, userAccess)
            .observeOnce { assert(it.value) }.then.postValue(false)

        val get = HelperTestFunction.getEntityQueue.poll()!!

        assertEquals(user, get.element)
        assertEquals(uid, get.id)
        assertEquals(USER_COLLECTION, get.collection)
        assertEquals(UserAdapter, get.adapter)
    }

    @Test
    fun addUserProfileAndAddToUserWithANewProfile() {
        val profile = UserProfile()
        val user = UserEntity("uid")
        val userAccess = UserEntity("uid")

        HelperTestFunction.nextString("pid")
        HelperTestFunction.nextBoolean(true)
        mockUserDatabase.addUserProfileAndAddToUser(profile, user, userAccess)
            .observeOnce { assert(it.value) }.then.postValue(false)

        val add = HelperTestFunction.addEntityAndGetIdQueue.poll()!!
        val set = HelperTestFunction.setEntityQueue.poll()!!

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
        val userAccess = UserEntity("uid")

        HelperTestFunction.nextString("")
        mockUserDatabase.addUserProfileAndAddToUser(profile, user, userAccess)
            .observeOnce { assert(!it.value) }.then.postValue(true)

        val add = HelperTestFunction.addEntityAndGetIdQueue.poll()!!

        assertEquals(profile, add.element)
        assertEquals(PROFILE_COLLECTION, add.collection)
        assertEquals(ProfileAdapter, add.adapter)
    }

    @Test
    fun addUserProfileAndAddToUser() {
        val profile = UserProfile("pid")
        val user = UserEntity("uid")
        val userAccess = UserEntity("uid")

        HelperTestFunction.nextString("pid")
        HelperTestFunction.nextBoolean(true)
        mockUserDatabase.addUserProfileAndAddToUser(profile, user, userAccess)
            .observeOnce { assert(it.value) }.then.postValue(false)

        val set1 = HelperTestFunction.setEntityQueue.poll()!!
        val set2 = HelperTestFunction.setEntityQueue.poll()!!

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
        val userAccess = UserProfile()

        HelperTestFunction.nextBoolean(true)
        HelperTestFunction.nextBoolean(true)
        mockUserDatabase.removeProfileFromUser(profile, user, userAccess)
            .observeOnce { assert(it.value) }.then.postValue(false)


        val set = HelperTestFunction.setEntityQueue.poll()!!
        val del = HelperTestFunction.deleteEntityQueue.poll()!!

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
        val userAccess = UserProfile()

        HelperTestFunction.nextBoolean(false)
        mockUserDatabase.removeProfileFromUser(profile, user, userAccess)
            .observeOnce { assert(!it.value) }.then.postValue(true)


        val set = HelperTestFunction.setEntityQueue.poll()!!

        assertEquals(user, set.element)
        assertEquals(user.uid, set.id)
        assertEquals(USER_COLLECTION, set.collection)
        assertEquals(UserAdapter, set.adapter)
    }

    @Test
    fun removeProfileFromUserWithoutDelete() {
        val profile = UserProfile("pid", users = mutableListOf("not uid"))
        val user = UserEntity("uid")
        val userAccess = UserProfile()

        HelperTestFunction.nextBoolean(true)
        HelperTestFunction.nextBoolean(true)
        mockUserDatabase.removeProfileFromUser(profile, user, userAccess)
            .observeOnce { assert(it.value) }.then.postValue(false)


        val set1 = HelperTestFunction.setEntityQueue.poll()!!
        val set2 = HelperTestFunction.setEntityQueue.poll()!!

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
        val userAccess = UserEntity("uid")

        HelperTestFunction.nextBoolean(true)
        mockUserDatabase.updateProfile(profile, userAccess)
            .observeOnce { assert(it.value) }.then.postValue(false)

        val set = HelperTestFunction.setEntityQueue.poll()!!

        assertEquals(profile, set.element)
        assertEquals(profile.pid, set.id)
        assertEquals(PROFILE_COLLECTION, set.collection)
        assertEquals(ProfileAdapter, set.adapter)
    }

    @Test
    fun getUserProfilesList() {
        val profiles = ObservableList<UserProfile>()
        val user = UserEntity("uid", profiles = mutableListOf("pid"))
        val userAccess = UserEntity("uid")

        HelperTestFunction.nextBoolean(true)
        mockUserDatabase.getUserProfilesList(profiles, user, userAccess)
            .observeOnce { assert(it.value) }.then.postValue(false)

        val getList = HelperTestFunction.getListEntityQueue.poll()!!

        assertEquals(profiles, getList.element)
        assertEquals(user.profiles, getList.ids)
        assertNull(getList.matcher)
        assertEquals(PROFILE_COLLECTION, getList.collection)
        assertEquals(ProfileAdapter, getList.adapter)
    }
    @Test
    fun getUserLists() {
        val users = ObservableList<UserEntity>()
        val userAccess = UserProfile("uid")

        HelperTestFunction.nextBoolean(true)
        mockUserDatabase.getListAllUsers(users,  userAccess = userAccess)
            .observeOnce { assert(it.value) }.then.postValue(false)

        val getList = HelperTestFunction.getListEntityQueue.poll()!!

        assertEquals(users, getList.element)
        assertNull(getList.matcher)
        assertEquals(USER_COLLECTION, getList.collection)
        assertEquals(UserAdapter, getList.adapter)
    }

    @Test
    fun getProfilesUserList() {
        val users = ObservableList<UserEntity>()
        val profile = UserProfile("pid", users = mutableListOf("uid"))
        val userAccess = UserEntity("uid")

        HelperTestFunction.nextBoolean(true)
        mockUserDatabase.getProfilesUserList(users, profile, userAccess)
            .observeOnce { assert(it.value) }.then.postValue(false)

        val getList = HelperTestFunction.getListEntityQueue.poll()!!

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
        val userAccess = UserEntity("uid")

        HelperTestFunction.nextBoolean(true)
        mockUserDatabase.getProfileById(profile, pid, userAccess)
            .observeOnce { assert(it.value) }.then.postValue(false)

        val get = HelperTestFunction.getEntityQueue.poll()!!

        assertEquals(profile, get.element)
        assertEquals(pid, get.id)
        assertEquals(PROFILE_COLLECTION, get.collection)
        assertEquals(ProfileAdapter, get.adapter)
    }

}