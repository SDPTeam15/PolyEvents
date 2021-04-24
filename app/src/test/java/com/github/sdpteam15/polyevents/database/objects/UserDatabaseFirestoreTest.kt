package com.github.sdpteam15.polyevents.database.objects

import com.github.sdpteam15.polyevents.database.DatabaseConstant.CollectionConstant.USER_COLLECTION
import com.github.sdpteam15.polyevents.database.DatabaseInterface
import com.github.sdpteam15.polyevents.database.HelperTestFunction
import com.github.sdpteam15.polyevents.database.observe.Observable
import com.github.sdpteam15.polyevents.model.UserEntity
import com.github.sdpteam15.polyevents.model.UserProfile
import com.github.sdpteam15.polyevents.util.UserAdapter
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

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

        val set = HelperTestFunction.setEntityQueue.peek()!!

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

        val set = HelperTestFunction.setEntityQueue.peek()!!

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

        val get = HelperTestFunction.getEntityQueue.peek()!!

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

        val get = HelperTestFunction.getEntityQueue.peek()!!

        assertEquals(user, get.element)
        assertEquals(uid, get.id)
        assertEquals(USER_COLLECTION, get.collection)
        assertEquals(UserAdapter, get.adapter)
    }
/*
    @Test
    fun addUserProfileAndAddToUser() {
        val profile = UserProfile()
        val user = UserEntity("uid")
        val userAccess = UserEntity("uid")

        HelperTestFunction.nextString.add("pid")
        HelperTestFunction.nextBoolean.add(true)
        mackUserDatabase.addUserProfileAndAddToUser(profile, user, userAccess)
            .observeOnce { assert(it.value) }.then.postValue(false)

        val get = HelperTestFunction.addEntityAndGetIdQueue.peek()!!

        assertEquals(user, get.element)
        assertEquals(uid, get.id)
        assertEquals(USER_COLLECTION, get.collection)
        assertEquals(UserAdapter, get.adapter)

    }
        ///
        val ended = Observable<Boolean>()

        profile.users.add(user.uid)
        val updater: () -> Unit = {
            user.profiles.add(profile.pid!!)
            db.setEntity(
                user,
                user.uid,
                USER_COLLECTION,
                UserAdapter
            ).updateOnce(ended)
        }

        if (profile.pid == null)
            db.addEntityAndGetId(
                profile,
                DatabaseConstant.CollectionConstant.PROFILE_COLLECTION,
                ProfileAdapter
            ).observeOnce {
                if (it.value != "") {
                    profile.pid = it.value
                    updater()
                } else
                    ended.postValue(false, it.sender)
            }
        else
            db.setEntity(
                profile,
                profile.pid!!,
                DatabaseConstant.CollectionConstant.PROFILE_COLLECTION,
                ProfileAdapter
            ).observeOnce {
                if (it.value)
                    updater()
                else
                    ended.postValue(false, it.sender)
            }
        return ended
    }

    fun removeProfileFromUser(
        profile: UserProfile,
        user: UserEntity,
        userAccess: UserProfile?
    ): Observable<Boolean> {
        user.profiles.remove(profile.pid!!)
        profile.users.remove(user.uid)
        val end = Observable<Boolean>()
        db.setEntity(
            user,
            user.uid,
            USER_COLLECTION,
            UserAdapter
        ).observeOnce { it1 ->
            if (it1.value) {
                (if (profile.users.isEmpty())
                    db.deleteEntity(
                        profile.pid!!,
                        DatabaseConstant.CollectionConstant.PROFILE_COLLECTION,
                    )
                else
                    db.setEntity(
                        profile,
                        profile.pid!!,
                        DatabaseConstant.CollectionConstant.PROFILE_COLLECTION,
                        ProfileAdapter
                    )).updateOnce(end)
            } else
                end.postValue(it1.value, it1.sender)
        }
        return end
    }

    fun updateProfile(profile: UserProfile, userAccess: UserEntity?) =
        db.setEntity(
            profile,
            profile.pid!!,
            DatabaseConstant.CollectionConstant.PROFILE_COLLECTION,
            ProfileAdapter
        )

    fun getUserProfilesList(
        profiles: ObservableList<UserProfile>,
        user: UserEntity,
        userAccess: UserEntity?
    ): Observable<Boolean> =
        db.getListEntity(
            profiles,
            user.profiles,
            null,
            DatabaseConstant.CollectionConstant.PROFILE_COLLECTION,
            ProfileAdapter
        )

    fun getProfilesUserList(
        users: ObservableList<UserEntity>,
        profile: UserProfile,
        userAccess: UserEntity?
    ): Observable<Boolean> =
        db.getListEntity(
            users,
            profile.users,
            null,
            DatabaseConstant.CollectionConstant.PROFILE_COLLECTION,
            UserAdapter
        )

    fun getProfileById(
        profile: Observable<UserProfile>,
        pid: String,
        userAccess: UserEntity?
    ): Observable<Boolean> =
        db.getEntity(
            profile,
            pid,
            DatabaseConstant.CollectionConstant.PROFILE_COLLECTION,
            ProfileAdapter
        )

    fun removeProfile(profile: UserProfile, user: UserEntity?) =
        db.deleteEntity(
            profile.pid!!,
            DatabaseConstant.CollectionConstant.PROFILE_COLLECTION
        )
    // */
}