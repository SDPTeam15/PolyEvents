package com.github.sdpteam15.polyevents.util

import com.github.sdpteam15.polyevents.database.DatabaseConstant.USER_BIRTH_DATE
import com.github.sdpteam15.polyevents.database.DatabaseConstant.USER_EMAIL
import com.github.sdpteam15.polyevents.database.DatabaseConstant.USER_NAME
import com.github.sdpteam15.polyevents.database.DatabaseConstant.USER_PHONE
import com.github.sdpteam15.polyevents.database.DatabaseConstant.USER_UID
import com.github.sdpteam15.polyevents.database.DatabaseConstant.USER_USERNAME
import com.github.sdpteam15.polyevents.helper.HelperFunctions
import com.github.sdpteam15.polyevents.model.UserEntity
import com.google.firebase.Timestamp

/**
 * A class for converting between user entities in our code and
 * documents in the Firebase database. Not unlike the DTO (Data
 * transfer object) concept.
 *
 * IMPORTANT: This should be updated whenever we add, remove or update fields of UserEntity.
 */
// TODO: convert profiles list
object UserAdapter {
    /**
     * Convert a user entity to an intermediate mapping
     * of fields to their values, that we can pass to the document directly.
     * Firestore document keys are always strings.
     * @param user the entity we're converting
     * @return a hashmap of the entity fields to their values
     */
    fun toUserDocument(user: UserEntity): HashMap<String, Any?> {
        return hashMapOf(
            USER_UID to user.uid,
            USER_USERNAME to user.username,
            USER_NAME to user.name,
            // convert the localdate to LocalDateTime compatible to store in Firestore
            USER_BIRTH_DATE to user.birthDate?.atStartOfDay(),
            USER_EMAIL to user.email,
            USER_PHONE to user.telephone
        )
    }

    /**
     * Convert document data to a user entity in our model.
     * Data retrieved from Firestore documents are always of the form of a mutable mapping,
     * that maps strings - which are the names of the fields of our entity - to their values,
     * which can be of any type..
     * @param documentData this is the data we retrieve from the document.
     * @return the corresponding userEntity.
     */
    fun toUserEntity(documentData: MutableMap<String, Any?>): UserEntity {
        return UserEntity(
            uid = documentData.get(USER_UID) as String,
            username = documentData.get(USER_USERNAME) as String?,
            name = documentData.get(USER_NAME) as String?,
            birthDate = HelperFunctions.DateToLocalDateTime(
                (documentData.get(USER_BIRTH_DATE) as Timestamp?)?.toDate()
            )?.toLocalDate(),
            email = documentData.get(USER_EMAIL) as String?,
            telephone = documentData.get(USER_PHONE) as String?
        )
    }
}