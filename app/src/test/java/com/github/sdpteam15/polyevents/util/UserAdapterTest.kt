package com.github.sdpteam15.polyevents.util

import com.github.sdpteam15.polyevents.database.DatabaseConstant
import com.github.sdpteam15.polyevents.model.UserEntity
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class UserAdapterTest {
    lateinit var userEntity: UserEntity

    val googleId = "googleId"
    val userType = UserEntity.ADMIN
    val username = "JohnDoe"
    val name = "John Doe"
    val age = 29
    val displayName = "John"
    val email = "John@email.com"

    @Before
    fun setupUserEntity() {
        userEntity = UserEntity(googleId, userType, username,
            name, age, displayName, email)
    }

    @Test
    fun conversionOfUserEntityToDocumentPreservesData() {
        val document = UserAdapter.toUserDocument(userEntity)

        assertEquals(document[DatabaseConstant.USER_UID], googleId)
        assertEquals(document[DatabaseConstant.USER_TYPE], userType)
        assertEquals(document[DatabaseConstant.USER_USERNAME], username)
        assertEquals(document[DatabaseConstant.USER_AGE], age)
        assertEquals(document[DatabaseConstant.USER_NAME], name)
        assertEquals(document[DatabaseConstant.USER_DISPLAY_NAME], displayName)
        assertEquals(document[DatabaseConstant.USER_EMAIL], email)
    }

    @Test
    fun conversionOfDocumentToUserEntityPreservesData() {
        val userDocumentData : HashMap<String, Any?> = hashMapOf(
            DatabaseConstant.USER_UID to googleId,
            DatabaseConstant.USER_TYPE to userType,
            DatabaseConstant.USER_NAME to name,
            DatabaseConstant.USER_DISPLAY_NAME to displayName,
            DatabaseConstant.USER_EMAIL to email,
            // Numbers are always stored as Double in Firestore
            DatabaseConstant.USER_AGE to age.toLong(),
            DatabaseConstant.USER_USERNAME to username
        )

        assertEquals(userEntity, UserAdapter.toUserEntity(userDocumentData))
    }

    @Test
    fun testConversionWithNullValues() {
        val userEntityWithNullProperties = UserEntity(googleId = googleId)
        val document = UserAdapter.toUserDocument(userEntityWithNullProperties)
        assertEquals(document[DatabaseConstant.USER_UID], googleId)
        assertNull(document[DatabaseConstant.USER_DISPLAY_NAME])
    }
}