package com.github.sdpteam15.polyevents.util

import com.github.sdpteam15.polyevents.model.UserEntity
import kotlin.collections.HashMap

/**
 * A class for converting between user entities in our code and
 * documents in the Firebase database. Not unlike the DTO (Data
 * transfer object) concept.
 *
 * IMPORTANT: This should be updated whenever we add, remove or update fields of UserEntity.
 */
class UserAdapter() {
    companion object {
        /**
         * Convert a user entity to an intermediate mapping
         * of fields to their values, that we can pass to the document directly.
         * Firestore document keys are always strings.
         * @param user the entity we're converting
         * @return a hashmap of the entity fields to their values
         */
        fun toUserDocument(user: UserEntity): HashMap<String, Any?> {
            return hashMapOf(
                    "googleId" to user.googleId,
                    "userType" to user.userType,
                    "username" to user.username,
                    "name" to user.name,
                    "age" to user.age,
                    "displayName" to user.displayName,
                    "email" to user.email
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
                    googleId = documentData.get("googleId") as String?,
                    userType = documentData.get("userType") as String?,
                    username = documentData.get("username") as String?,
                    name = documentData.get("name") as String?,
                    age = (documentData.get("age") as Long?)?.toInt(),
                    displayName = documentData.get("displayName") as String?,
                    email = documentData.get("email") as String?
            )
        }
    }
}