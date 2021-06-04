package com.github.sdpteam15.polyevents.model.database.remote.adapter

import com.github.sdpteam15.polyevents.helper.HelperFunctions
import com.github.sdpteam15.polyevents.helper.HelperFunctions.toLocalDateTime
import com.github.sdpteam15.polyevents.model.database.remote.DatabaseConstant.UserConstants.*
import com.github.sdpteam15.polyevents.model.entity.UserEntity

/**
 * A class for converting between user entities in our code and
 * documents in the Firebase database. Not unlike the DTO (Data
 * transfer object) concept.
 */
@Suppress("UNCHECKED_CAST")
object UserAdapter : AdapterInterface<UserEntity> {
    override fun toDocumentWithoutNull(element: UserEntity): HashMap<String, Any?> =
        hashMapOf(
            USER_USERNAME.value to element.username,
            USER_NAME.value to element.name,
            // convert the localdate to LocalDateTime compatible to store in Firestore
            // UPDATE: this will store the localdatetime as a hashmap, not a timestamp in Firestore
            // Best to convert to date to parse the birthdate as a Timestamp from Firestore when converting
            // from document
            USER_BIRTH_DATE.value to HelperFunctions.localDateTimeToDate(
                element.birthDate?.atStartOfDay()
            ),
            USER_EMAIL.value to element.email,
            USER_PROFILES.value to element.profiles.toList()
        )

    override fun fromDocument(document: Map<String, Any?>, id: String) = UserEntity(
        uid = id,
        username = document[USER_USERNAME.value] as String?,
        name = document[USER_NAME.value] as String?,
        birthDate = document[USER_BIRTH_DATE.value].toLocalDateTime()?.toLocalDate(),
        email = document[USER_EMAIL.value] as String?,
        profiles = (document[USER_PROFILES.value] as List<String>).toMutableSet()
    )
}