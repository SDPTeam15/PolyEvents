package com.github.sdpteam15.polyevents.util

import com.github.sdpteam15.polyevents.database.DatabaseConstant.USER_BIRTH_DATE
import com.github.sdpteam15.polyevents.database.DatabaseConstant.USER_EMAIL
import com.github.sdpteam15.polyevents.database.DatabaseConstant.USER_NAME
import com.github.sdpteam15.polyevents.database.DatabaseConstant.USER_PHONE
import com.github.sdpteam15.polyevents.database.DatabaseConstant.USER_PROFILES
import com.github.sdpteam15.polyevents.database.DatabaseConstant.USER_UID
import com.github.sdpteam15.polyevents.database.DatabaseConstant.USER_USERNAME
import com.github.sdpteam15.polyevents.helper.HelperFunctions
import com.github.sdpteam15.polyevents.model.UserEntity
import com.google.firebase.Timestamp

/**
 * A class for converting between user entities in our code and
 * documents in the Firebase database. Not unlike the DTO (Data
 * transfer object) concept.
 */
object UserAdapter : AdapterInterface<UserEntity> {
    override fun toDocument(element: UserEntity): HashMap<String, Any?> = hashMapOf(
        USER_UID to element.uid,
        USER_USERNAME to element.username,
        USER_NAME to element.name,
        // convert the localdate to LocalDateTime compatible to store in Firestore
        USER_BIRTH_DATE to element.birthDate?.atStartOfDay(),
        USER_EMAIL to element.email,
        USER_PHONE to element.telephone,
        USER_PROFILES to element.profiles
    )

    override fun fromDocument(document: MutableMap<String, Any?>, id: String) = UserEntity(
        uid = id,
        username = document[USER_USERNAME] as String?,
        name = document[USER_NAME] as String?,
        birthDate = HelperFunctions.DateToLocalDateTime(
            (document[USER_BIRTH_DATE] as Timestamp?)?.toDate()
        )?.toLocalDate(),
        email = document[USER_EMAIL] as String?,
        telephone = document[USER_PHONE] as String?,
        profiles = document[USER_PROFILES] as MutableList<String>
    )
}