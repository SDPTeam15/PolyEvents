package com.github.sdpteam15.polyevents.util

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

        assertEquals(document["googleId"], googleId)
        assertEquals(document["userType"], userType)
        assertEquals(document["username"], username)
        assertEquals(document["age"], age)
        assertEquals(document["name"], name)
        assertEquals(document["displayName"], displayName)
        assertEquals(document["email"], email)
    }

    @Test
    fun conversionOfDocumentToUserEntityPreservesData() {
        val userDocumentData : HashMap<String, Any?> = hashMapOf(
            "googleId" to googleId,
            "userType" to userType,
            "name" to name,
            "displayName" to displayName,
            "email" to email,
            // Numbers are always stored as Double in Firestore
            "age" to age.toLong(),
            "username" to username
        )

        assertEquals(userEntity, UserAdapter.toUserEntity(userDocumentData))
    }

    @Test
    fun testConversionWithNullValues() {
        val userEntityWithNullProperties = UserEntity(googleId = googleId)
        val document = UserAdapter.toUserDocument(userEntityWithNullProperties)
        assertEquals(document["googleId"], googleId)
        assertNull(document["displayName"])
    }
}