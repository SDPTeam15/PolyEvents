package com.github.sdpteam15.polyevents.database.objects

import com.github.sdpteam15.polyevents.database.DatabaseConstant.CollectionConstant.PROFILE_COLLECTION
import com.github.sdpteam15.polyevents.database.DatabaseConstant.CollectionConstant.USER_COLLECTION
import com.github.sdpteam15.polyevents.database.DatabaseInterface
import com.github.sdpteam15.polyevents.database.observe.Observable
import com.github.sdpteam15.polyevents.database.observe.ObservableList
import com.github.sdpteam15.polyevents.model.UserEntity
import com.github.sdpteam15.polyevents.model.UserProfile

class UserDatabase(private val db: DatabaseInterface) : UserDatabaseInterface {
    override var firstConnectionUser: UserEntity = UserEntity(uid = "DEFAULT")

    var profiles: MutableList<UserProfile> = mutableListOf()

    override fun updateUserInformation(
        user: UserEntity,
        userAccess: UserProfile?
    ): Observable<Boolean> =
        db.setEntity(
            user,
            user.uid,
            USER_COLLECTION
        )

    override fun firstConnexion(
        user: UserEntity
    ): Observable<Boolean> {
        firstConnectionUser = user
        return db.setEntity(
            user,
            user.uid,
            USER_COLLECTION
        )
    }

    override fun inDatabase(
        isInDb: Observable<Boolean>,
        uid: String,
        userAccess: UserProfile?
    ) = db.getEntity(
        Observable(),
        uid,
        USER_COLLECTION
    ).updateOnce(isInDb).then

    override fun getUserInformation(
        user: Observable<UserEntity>,
        uid: String,
        userAccess: UserProfile?
    ) = db.getEntity(
        user,
        uid,
        USER_COLLECTION
    )

    override fun getListAllUsers(users: ObservableList<UserEntity>, userAccess: UserProfile?): Observable<Boolean> {
        TODO("Not yet implemented")
    }

    override fun addUserProfileAndAddToUser(
        profile: UserProfile,
        user: UserEntity,
        userAccess: UserEntity?
    ): Observable<Boolean> {
        val ended = Observable<Boolean>()

        profile.users.add(user.uid)

        (if (profile.pid == null) db.addEntityAndGetId(
            profile,
            PROFILE_COLLECTION
        ).mapOnce {
            if (it != "")
                profile.pid = it
            it != ""
        }.then
        else db.setEntity(
            profile,
            profile.pid!!,
            PROFILE_COLLECTION
        )).observeOnce {
            if (it.value) {
                user.profiles.add(profile.pid!!)
                db.setEntity(
                    user,
                    user.uid,
                    USER_COLLECTION
                ).updateOnce(ended)
            } else
                ended.postValue(false, it.sender)
        }
        return ended
    }

    override fun removeProfileFromUser(
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
            USER_COLLECTION
        ).observeOnce { it1 ->
            if (it1.value) {
                (if (profile.users.isEmpty())
                    db.deleteEntity(
                        profile.pid!!,
                        PROFILE_COLLECTION,
                    )
                else
                    db.setEntity(
                        profile,
                        profile.pid!!,
                        PROFILE_COLLECTION
                    )).updateOnce(end)
            } else
                end.postValue(it1.value, it1.sender)
        }
        return end
    }

    override fun updateProfile(profile: UserProfile, userAccess: UserEntity?) =
        db.setEntity(
            profile,
            profile.pid!!,
            PROFILE_COLLECTION
        )

    override fun getUserProfilesList(
        profiles: ObservableList<UserProfile>,
        user: UserEntity,
        userAccess: UserEntity?
    ): Observable<Boolean> =
        db.getListEntity(
            profiles,
            user.profiles,
            null,
            PROFILE_COLLECTION
        )

    override fun getProfilesUserList(
        users: ObservableList<UserEntity>,
        profile: UserProfile,
        userAccess: UserEntity?
    ): Observable<Boolean> =
        db.getListEntity(
            users,
            profile.users,
            null,
            USER_COLLECTION
        )

    override fun getProfileById(
        profile: Observable<UserProfile>,
        pid: String,
        userAccess: UserEntity?
    ): Observable<Boolean> =
        db.getEntity(
            profile,
            pid,
            PROFILE_COLLECTION
        )
}