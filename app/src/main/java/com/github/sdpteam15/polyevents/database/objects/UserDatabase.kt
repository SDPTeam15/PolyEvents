package com.github.sdpteam15.polyevents.database.objects

import android.annotation.SuppressLint
import com.github.sdpteam15.polyevents.database.DatabaseConstant
import com.github.sdpteam15.polyevents.database.DatabaseConstant.CollectionConstant.PROFILE_COLLECTION
import com.github.sdpteam15.polyevents.database.DatabaseConstant.CollectionConstant.USER_COLLECTION
import com.github.sdpteam15.polyevents.database.DatabaseConstant.UserConstants.USER_UID
import com.github.sdpteam15.polyevents.database.DatabaseInterface
import com.github.sdpteam15.polyevents.database.FirestoreDatabaseProvider
import com.github.sdpteam15.polyevents.database.observe.Observable
import com.github.sdpteam15.polyevents.database.observe.ObservableList
import com.github.sdpteam15.polyevents.model.UserEntity
import com.github.sdpteam15.polyevents.model.UserProfile
import com.github.sdpteam15.polyevents.util.ProfileAdapter
import com.github.sdpteam15.polyevents.util.UserAdapter
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class UserDatabase(private val db: DatabaseInterface) : UserDatabaseInterface {
    @SuppressLint("StaticFieldLeak")
    var firestore: FirebaseFirestore? = null
        get() = field ?: Firebase.firestore

    override var firstConnectionUser: UserEntity = UserEntity(uid = "DEFAULT")

    var profiles: MutableList<UserProfile> = mutableListOf()

    override fun updateUserInformation(
        newValues: Map<String, String>,
        uid: String,
        userAccess: UserProfile?
    ): Observable<Boolean> = FirestoreDatabaseProvider.thenDoSet(
        firestore!!.collection(USER_COLLECTION.value)
            .document(uid)
            .update(newValues as Map<String, Any>)
    )

    override fun firstConnexion(
        user: UserEntity,
        userAccess: UserProfile?
    ): Observable<Boolean> {
        firstConnectionUser = user

        return FirestoreDatabaseProvider.thenDoSet(
            firestore!!.collection(USER_COLLECTION.value)
                .document(user.uid)
                .set(firstConnectionUser)
        )
    }

    override fun inDatabase(
        isInDb: Observable<Boolean>,
        uid: String,
        userAccess: UserProfile?
    ): Observable<Boolean> = FirestoreDatabaseProvider.thenDoGet(
        firestore!!.collection(USER_COLLECTION.value)
            .whereEqualTo(USER_UID.value, uid)
            .limit(1)
            .get()
    ) { doc: QuerySnapshot ->
        if (doc.documents.size == 1) {
            isInDb.postValue(true)
        } else {
            isInDb.postValue(false)
        }
    }

    override fun getUserInformation(
        user: Observable<UserEntity>,
        uid: String?,
        userAccess: UserProfile?
    ): Observable<Boolean> = FirestoreDatabaseProvider.thenDoMultGet(
        firestore!!.collection(USER_COLLECTION.value)
            .document(uid!!)
            .get()
    ) {
        it.data?.let { it1 -> user.postValue(UserAdapter.fromDocument(it1, it.id), this) }
    }

    override fun addUserProfileAndAddToUser(
        profile: UserProfile,
        user: UserEntity,
        userAccess: UserEntity?
    ): Observable<Boolean> {
        val ended = Observable<Boolean>()

        profile.users.add(user.uid)
        val updater : () -> Unit = {
            user.profiles.add(profile.pid!!)
            db.setEntity(
                user,
                user.uid,
                USER_COLLECTION,
                UserAdapter
            ).observeOnce {
                ended.postValue(it.value, it.sender)
            }
        }

        if (profile.pid == null)
            db.addEntityAndGetId(
                profile,
                PROFILE_COLLECTION,
                ProfileAdapter
            ).observeOnce {
                if(it.value != "") {
                    profile.pid = it.value
                    updater()
                }
                else
                    ended.postValue(false, it.sender)
            }
        else
            db.setEntity(
                profile,
                profile.pid!!,
                PROFILE_COLLECTION,
                ProfileAdapter
            ).observeOnce {
                if(it.value)
                    updater()
                else
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
            USER_COLLECTION,
            UserAdapter
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
                        PROFILE_COLLECTION,
                        ProfileAdapter
                    )).observeOnce { end.postValue(it.value, it.sender) }
            } else
                end.postValue(it1.value, it1.sender)
        }
        return end
    }

    override fun updateProfile(profile: UserProfile, userAccess: UserEntity?): Observable<Boolean> =
        db.setEntity(
            profile,
            profile.pid!!,
            PROFILE_COLLECTION,
            ProfileAdapter
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
            PROFILE_COLLECTION,
            ProfileAdapter
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
            PROFILE_COLLECTION,
            UserAdapter
        )

    override fun getProfileById(
        profile: Observable<UserProfile>,
        pid: String,
        userAccess: UserEntity?
    ): Observable<Boolean> =
        db.getEntity(
            profile,
            pid,
            PROFILE_COLLECTION,
            ProfileAdapter
        )

    override fun removeProfile(profile: UserProfile, user: UserEntity?) =
        db.deleteEntity(
            profile.pid!!,
            PROFILE_COLLECTION
        )
}