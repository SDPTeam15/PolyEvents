package com.github.sdpteam15.polyevents.database.objects

import android.annotation.SuppressLint
import com.github.sdpteam15.polyevents.database.Database
import com.github.sdpteam15.polyevents.database.DatabaseConstant
import com.github.sdpteam15.polyevents.database.DatabaseConstant.CollectionConstant.TEST_COLLECTION
import com.github.sdpteam15.polyevents.database.DatabaseConstant.CollectionConstant.USER_COLLECTION
import com.github.sdpteam15.polyevents.database.DatabaseConstant.UserConstants.USER_UID
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

object UserDatabaseFirestore : UserDatabaseInterface {
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

        val lastFailureListener = OnFailureListener { ended.postValue(false, this) }

        val update: () -> Unit = {
            user.addNewProfile(profile)

            FirestoreDatabaseProvider.firestore!!.collection(USER_COLLECTION.value)
                .document(user.uid)
                .set(UserAdapter.toDocument(user))
                .addOnSuccessListener {
                    FirestoreDatabaseProvider.firestore!!.collection(DatabaseConstant.CollectionConstant.PROFILE_COLLECTION.value)
                        .document(profile.pid!!)
                        .set(ProfileAdapter.toDocument(profile))
                        .addOnSuccessListener { ended.postValue(true, this) }
                        .addOnFailureListener(lastFailureListener)
                }
                .addOnFailureListener(lastFailureListener)
        }

        if (profile.pid == null)
            FirestoreDatabaseProvider.firestore!!.collection(DatabaseConstant.CollectionConstant.PROFILE_COLLECTION.value)
                .add(ProfileAdapter.toDocument(profile))
                .addOnSuccessListener {
                    profile.pid = it.id
                    update()
                }
                .addOnFailureListener(lastFailureListener)
        else
            update()

        return ended
    }

    override fun removeProfileFromUser(
        profile: UserProfile,
        user: UserEntity,
        userAccess: UserProfile?
    ): Observable<Boolean> {
        user.profiles.remove(profile.pid)
        return Database.currentDatabase.setEntity(
            user,
            user.uid,
            USER_COLLECTION,
            UserAdapter,
            userAccess
        )
    }

    override fun updateProfile(profile: UserProfile, userAccess: UserEntity?): Observable<Boolean> =
        FirestoreDatabaseProvider.setEntity(
            profile,
            profile.pid!!,
            DatabaseConstant.CollectionConstant.PROFILE_COLLECTION,
            ProfileAdapter
        )

    override fun getUserProfilesList(
        profiles: ObservableList<UserProfile>,
        user: UserEntity,
        userAccess: UserEntity?
    ): Observable<Boolean> =
        FirestoreDatabaseProvider.getListEntity(
            profiles,
            user.profiles,
            DatabaseConstant.CollectionConstant.PROFILE_COLLECTION,
            ProfileAdapter
        )

    override fun getProfilesUserList(
        users: ObservableList<UserEntity>,
        profile: UserProfile,
        userAccess: UserEntity?
    ): Observable<Boolean> =
        FirestoreDatabaseProvider.getListEntity(
            users,
            profile.users,
            DatabaseConstant.CollectionConstant.PROFILE_COLLECTION,
            UserAdapter
        )

    override fun getProfileById(
        profile: Observable<UserProfile>,
        pid: String,
        userAccess: UserEntity?
    ): Observable<Boolean> = FirestoreDatabaseProvider.getEntity(
        profile,
        pid,
        DatabaseConstant.CollectionConstant.PROFILE_COLLECTION,
        ProfileAdapter
    )

    override fun removeProfile(profile: UserProfile, user: UserEntity?) =
        FirestoreDatabaseProvider.deleteEntity(
            profile.pid!!,
            DatabaseConstant.CollectionConstant.PROFILE_COLLECTION
        )
}