package com.github.sdpteam15.polyevents.database.objects

import com.github.sdpteam15.polyevents.database.DatabaseConstant.CollectionConstant.PROFILE_COLLECTION
import com.github.sdpteam15.polyevents.database.DatabaseConstant.CollectionConstant.USER_COLLECTION
import com.github.sdpteam15.polyevents.database.DatabaseInterface
import com.github.sdpteam15.polyevents.database.FirestoreDatabaseProvider
import com.github.sdpteam15.polyevents.database.HelperTestFunction
import com.github.sdpteam15.polyevents.database.observe.Observable
import com.github.sdpteam15.polyevents.database.observe.ObservableList
import com.github.sdpteam15.polyevents.model.UserEntity
import com.github.sdpteam15.polyevents.model.UserProfile
import com.github.sdpteam15.polyevents.util.ProfileAdapter
import com.github.sdpteam15.polyevents.util.UserAdapter
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.ktx.Firebase
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.kotlin.anyOrNull
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class UserDatabaseTest {
    lateinit var mackUserDatabase: UserDatabase
    lateinit var mockDatabaseInterface: DatabaseInterface

    @Before
    fun setup() {
        mockDatabaseInterface = HelperTestFunction.mockFor()
        mackUserDatabase = UserDatabase(mockDatabaseInterface)
        HelperTestFunction.clearQueue()
    }

    @Test
    fun updateUserInformation() {
        val user = UserEntity("uid")
        val userAccess = UserProfile()

        HelperTestFunction.nextBoolean.add(true)
        mackUserDatabase.updateUserInformation(user, userAccess)
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

        HelperTestFunction.nextBoolean.add(true)
        mackUserDatabase.firstConnexion(user).observeOnce { assert(it.value) }.then.postValue(false)

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

        HelperTestFunction.nextBoolean.add(true)
        mackUserDatabase.inDatabase(isInDb.observeOnce { assert(it.value) }.then, uid, userAccess)
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

        HelperTestFunction.nextBoolean.add(true)
        mackUserDatabase.getUserInformation(user, uid, userAccess)
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

        HelperTestFunction.nextString.add("pid")
        HelperTestFunction.nextBoolean.add(true)
        mackUserDatabase.addUserProfileAndAddToUser(profile, user, userAccess)
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

        HelperTestFunction.nextString.add("")
        mackUserDatabase.addUserProfileAndAddToUser(profile, user, userAccess)
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

        HelperTestFunction.nextString.add("pid")
        HelperTestFunction.nextBoolean.add(true)
        mackUserDatabase.addUserProfileAndAddToUser(profile, user, userAccess)
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

        HelperTestFunction.nextBoolean.add(true)
        HelperTestFunction.nextBoolean.add(true)
        mackUserDatabase.removeProfileFromUser(profile, user, userAccess)
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

        HelperTestFunction.nextBoolean.add(false)
        mackUserDatabase.removeProfileFromUser(profile, user, userAccess)
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

        HelperTestFunction.nextBoolean.add(true)
        HelperTestFunction.nextBoolean.add(true)
        mackUserDatabase.removeProfileFromUser(profile, user, userAccess)
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

        HelperTestFunction.nextBoolean.add(true)
        mackUserDatabase.updateProfile(profile, userAccess)
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

        HelperTestFunction.nextBoolean.add(true)
        mackUserDatabase.getUserProfilesList(profiles, user, userAccess)
            .observeOnce { assert(it.value) }.then.postValue(false)

        val getList = HelperTestFunction.getListEntityQueue.poll()!!

        assertEquals(profiles, getList.element)
        assertEquals(user.profiles, getList.ids)
        assertNull(getList.matcher)
        assertEquals(PROFILE_COLLECTION, getList.collection)
        assertEquals(ProfileAdapter, getList.adapter)
    }

    @Test
    fun getProfilesUserList() {
        val users = ObservableList<UserEntity>()
        val profile = UserProfile("pid", users = mutableListOf("uid"))
        val userAccess = UserEntity("uid")

        HelperTestFunction.nextBoolean.add(true)
        mackUserDatabase.getProfilesUserList(users, profile, userAccess)
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

        HelperTestFunction.nextBoolean.add(true)
        mackUserDatabase.getProfileById(profile, pid, userAccess)
            .observeOnce { assert(it.value) }.then.postValue(false)

        val get = HelperTestFunction.getEntityQueue.poll()!!

        assertEquals(profile, get.element)
        assertEquals(pid, get.id)
        assertEquals(PROFILE_COLLECTION, get.collection)
        assertEquals(ProfileAdapter, get.adapter)
    }

    @Test
    fun getListUsersWorksProperly() {
        val mockedDatabase= mock(FirebaseFirestore::class.java)
        FirestoreDatabaseProvider.firestore =mockedDatabase
        val mockedTask = mock(Task::class.java) as Task<QuerySnapshot>
        val mockedCollection = mock(CollectionReference::class.java)
        val mockedDocument = mock(QuerySnapshot::class.java)

        val userToBeAdded = mutableListOf<UserEntity>()
        val testUser = ObservableList<UserEntity>()
        userToBeAdded.add(UserEntity("uid1","username1","name1"))
        userToBeAdded.add(UserEntity("uid2","username2","name2"))
        userToBeAdded.add(UserEntity("uid3","username3","name3"))
        userToBeAdded.add(UserEntity("uid4","username4","name4"))
        userToBeAdded.add(UserEntity("uid5","username5","name5"))
        userToBeAdded.add(UserEntity("uid6","username6","name6"))
        userToBeAdded.add(UserEntity("uid7","username7","name7"))
        userToBeAdded.add(UserEntity("uid8","username8","name8"))
        userToBeAdded.add(UserEntity("uid9","username9","name9"))


        Mockito.`when`(mockedDatabase.collection(USER_COLLECTION.value)).thenReturn(mockedCollection)
        Mockito.`when`(mockedCollection.get()).thenAnswer {
            mockedTask
        }

        Mockito.`when`(mockedTask.addOnSuccessListener(anyOrNull())).thenAnswer {
            FirestoreDatabaseProvider.lastQuerySuccessListener!!.onSuccess(mockedDocument)
            //set method in hard to see if the success listener is successfully called
            testUser.addAll(userToBeAdded)
            mockedTask
        }
        val result = FirestoreDatabaseProvider.userDatabase!!.getListAllUsers(testUser)
        assert(result.value!!)
        for (user in userToBeAdded) {
            assert(user in testUser)
        }
        FirestoreDatabaseProvider.firestore=null
    }
}