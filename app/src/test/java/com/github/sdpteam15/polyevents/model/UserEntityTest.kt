package com.github.sdpteam15.polyevents.model

import org.junit.Test
import kotlin.test.BeforeTest
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class UserEntityTest {
    lateinit var userEntity: UserEntity

    val googleId = "googleId"
    val userType = UserEntity.ADMIN
    val username = "JohnDoe"
    val name = "John Doe"
    val age = 29
    val displayName = "John"
    val email = "John@email.com"

    @BeforeTest
    fun setupUserEntity() {
        userEntity = UserEntity(googleId, userType, username,
        name, age, displayName, email)
    }

    @Test
    fun testUserProperties() {
        assertEquals(googleId, userEntity.googleId)
        assertEquals(userType, userEntity.userType)
        assertEquals(username, userEntity.username)
        assertEquals(name, userEntity.name)
        assertEquals(age, userEntity.age)
        assertEquals(displayName, userEntity.displayName)
        assertEquals(email, userEntity.email)
    }

    @Test
    fun testIfUserIsAdmin() {
        assertTrue(userEntity.isAdmin())
    }

}