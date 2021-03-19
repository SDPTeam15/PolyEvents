package com.github.sdpteam15.polyevents.util

import com.github.sdpteam15.polyevents.database.DatabaseConstant.USER_AGE
import com.github.sdpteam15.polyevents.database.DatabaseConstant.USER_DISPLAY_NAME
import com.github.sdpteam15.polyevents.database.DatabaseConstant.USER_EMAIL
import com.github.sdpteam15.polyevents.database.DatabaseConstant.USER_GOOGLE_ID
import com.github.sdpteam15.polyevents.database.DatabaseConstant.USER_NAME
import com.github.sdpteam15.polyevents.database.DatabaseConstant.USER_TYPE
import com.github.sdpteam15.polyevents.database.DatabaseConstant.USER_USERNAME
import com.github.sdpteam15.polyevents.model.UserEntity
import kotlin.collections.HashMap

/**
 * A class for converting between user entities in our code and
 * documents in the Firebase database. Not unlike the DTO (Data
 * transfer object) concept.
 *
 * IMPORTANT: This should be updated whenever we add, remove or update fields of UserEntity.
 */
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
            USER_GOOGLE_ID to user.googleId,
            USER_TYPE to user.userType,
            USER_USERNAME to user.username,
            USER_NAME to user.name,
            USER_AGE to user.age,
            USER_DISPLAY_NAME to user.displayName,
            USER_EMAIL to user.email
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
            googleId = documentData.get(USER_GOOGLE_ID) as String?,
            userType = documentData.get(USER_TYPE) as String?,
            username = documentData.get(USER_USERNAME) as String?,
            name = documentData.get(USER_NAME) as String?,
            age = (documentData.get(USER_AGE) as Long?)?.toInt(),
            displayName = documentData.get(USER_DISPLAY_NAME) as String?,
            email = documentData.get(USER_EMAIL) as String?
        )
    }
}