package com.github.sdpteam15.polyevents.model.database.remote.objects

import android.annotation.SuppressLint
import com.github.sdpteam15.polyevents.model.database.remote.DatabaseConstant
import com.github.sdpteam15.polyevents.model.database.remote.DatabaseInterface
import com.github.sdpteam15.polyevents.model.observable.Observable
import com.github.sdpteam15.polyevents.model.room.UserSettings
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class UserSettingsDatabase(private val db: DatabaseInterface) :
    UserSettingsDatabaseInterface {
    @SuppressLint("StaticFieldLeak")
    var firestore: FirebaseFirestore? = null
        get() = field ?: Firebase.firestore

    override fun updateUserSettings(userSettings: UserSettings): Observable<Boolean> =
        db.setEntity(
            userSettings,
            db.currentUser!!.uid,
            DatabaseConstant.CollectionConstant.USER_SETTINGS_COLLECTION
        )

    override fun getUserSettings(
        id: String?,
        userSettingsObservable: Observable<UserSettings>
    ): Observable<Boolean> =
        db.getEntity(
            userSettingsObservable,
            id!!,
            DatabaseConstant.CollectionConstant.USER_SETTINGS_COLLECTION
        )
}