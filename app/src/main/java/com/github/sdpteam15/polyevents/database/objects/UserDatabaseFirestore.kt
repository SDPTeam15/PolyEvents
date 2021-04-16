package com.github.sdpteam15.polyevents.database.objects

import android.annotation.SuppressLint
import com.github.sdpteam15.polyevents.database.Database
import com.github.sdpteam15.polyevents.database.DatabaseConstant
import com.github.sdpteam15.polyevents.database.FirestoreDatabaseProvider
import com.github.sdpteam15.polyevents.database.observe.Observable
import com.github.sdpteam15.polyevents.model.UserEntity
import com.github.sdpteam15.polyevents.util.UserAdapter
import com.github.sdpteam15.polyevents.util.ZoneAdapter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

object UserDatabaseFirestore: UserDatabaseInterface {
    @SuppressLint("StaticFieldLeak")
    private var firestore: FirebaseFirestore? = null
        get() = field ?: Firebase.firestore

    override val currentUser: UserEntity?
        get()= Database.currentDatabase.currentUser

    /**
     * Map used in the firstConnection method. It's public to be able to use it in tests
     */
    var firstConnectionUser: UserEntity = UserEntity(uid = "DEFAULT")

    override fun updateUserInformation(
        newValues: Map<String, String>,
        uid: String,
        userAccess: UserEntity?
    ): Observable<Boolean> = FirestoreDatabaseProvider.thenDoSet(
        firestore!!.collection(DatabaseConstant.USER_COLLECTION)
            .document(uid)
            .update(newValues as Map<String, Any>)
    )

    override fun firstConnexion(
        user: UserEntity,
        userAccess: UserEntity?
    ): Observable<Boolean> {
        firstConnectionUser = user

        return FirestoreDatabaseProvider.thenDoSet(
            firestore!!.collection(DatabaseConstant.USER_COLLECTION)
                .document(user.uid)
                .set(FirestoreDatabaseProvider.firstConnectionUser)
        )
    }

    override fun inDatabase(
        isInDb: Observable<Boolean>,
        uid: String,
        userAccess: UserEntity?
    ): Observable<Boolean> = FirestoreDatabaseProvider.thenDoGet(
        firestore!!.collection(DatabaseConstant.USER_COLLECTION)
            .whereEqualTo(DatabaseConstant.USER_UID, uid)
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
        userAccess: UserEntity?
    ): Observable<Boolean> = FirestoreDatabaseProvider.thenDoMultGet(
        firestore!!.collection(DatabaseConstant.USER_COLLECTION)
            .document(uid!!)
            .get()
    ) {
        it.data?.let { it1 -> user.postValue(UserAdapter.toUserEntity(it1),this) }
    }
}