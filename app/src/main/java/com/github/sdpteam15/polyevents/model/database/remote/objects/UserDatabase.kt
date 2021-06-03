package com.github.sdpteam15.polyevents.model.database.remote.objects

import com.github.sdpteam15.polyevents.model.database.remote.DatabaseConstant.CollectionConstant.PROFILE_COLLECTION
import com.github.sdpteam15.polyevents.model.database.remote.DatabaseConstant.CollectionConstant.USER_COLLECTION
import com.github.sdpteam15.polyevents.model.database.remote.DatabaseInterface
import com.github.sdpteam15.polyevents.model.entity.UserEntity
import com.github.sdpteam15.polyevents.model.entity.UserProfile
import com.github.sdpteam15.polyevents.model.entity.UserRole
import com.github.sdpteam15.polyevents.model.observable.Observable
import com.github.sdpteam15.polyevents.model.observable.ObservableList

class UserDatabase(private val db: DatabaseInterface) : UserDatabaseInterface {

    var profiles: MutableList<UserProfile> = mutableListOf()

    override fun updateUserInformation(
        user: UserEntity
    ): Observable<Boolean> =
        db.setEntity(
            user,
            user.uid,
            USER_COLLECTION
        )

    override fun firstConnexion(
        user: UserEntity
    ): Observable<Boolean> {
        return db.setEntity(
            user,
            user.uid,
            USER_COLLECTION
        ).observeOnce {
            if (it.value) {
                addUserProfileAndAddToUser(
                    UserProfile(
                        profileName = user.name,
                        userRole = UserRole.PARTICIPANT
                    ), user
                ).observeOnce {
                    user.loadSuccess = false
                    user.userProfiles
                    db.currentUser = user
                }
            }
        }.then
    }

    override fun inDatabase(
        isInDb: Observable<Boolean>,
        uid: String
    ) = db.getEntity(
        Observable(),
        uid,
        USER_COLLECTION
    ).updateOnce(isInDb).then

    override fun getUserInformation(
        user: Observable<UserEntity>,
        uid: String
    ) = db.getEntity(
        user,
        uid,
        USER_COLLECTION
    )

    override fun getListAllUsers(users: ObservableList<UserEntity>) =
        db.getListEntity(
            users,
            null,
            null,
            USER_COLLECTION
        )

    override fun addUserProfileAndAddToUser(
        profile: UserProfile,
        user: UserEntity
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
        user: UserEntity
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

    override fun updateProfile(profile: UserProfile) =
        db.setEntity(
            profile,
            profile.pid!!,
            PROFILE_COLLECTION
        ).observeOnce { if (db.currentUser != null) db.currentUser!!.loadSuccess = false }.then

    override fun getUserProfilesList(
        profiles: ObservableList<UserProfile>,
        user: UserEntity
    ): Observable<Boolean> =
        db.getListEntity(
            profiles,
            user.profiles,
            null,
            PROFILE_COLLECTION
        )

    override fun getProfilesUserList(
        users: ObservableList<UserEntity>,
        profile: UserProfile
    ): Observable<Boolean> =
        db.getListEntity(
            users,
            profile.users,
            null,
            USER_COLLECTION
        )

    override fun getProfileById(
        profile: Observable<UserProfile>,
        pid: String
    ): Observable<Boolean> =
        db.getEntity(
            profile,
            pid,
            PROFILE_COLLECTION
        )
}