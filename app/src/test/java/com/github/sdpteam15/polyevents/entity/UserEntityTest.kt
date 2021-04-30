package com.github.sdpteam15.polyevents.entity

import com.github.sdpteam15.polyevents.helper.HelperFunctions
import com.github.sdpteam15.polyevents.model.entity.UserEntity
import com.github.sdpteam15.polyevents.model.entity.UserProfile
import com.github.sdpteam15.polyevents.model.entity.UserRole
import org.junit.Test
import java.time.LocalDate
import kotlin.test.BeforeTest
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class UserEntityTest {
    lateinit var user: UserEntity

    val googleId = "googleId"
    val username = "JohnDoe"
    val name = "John Doe"
    val birthDate = LocalDate.of(1990, 12, 30)
    val email = "John@email.com"

    val adminProfile = UserProfile(
        pid = googleId,
        profileName = "adminProfile",
        userRole = UserRole.ADMIN
    )

    val currentDate = birthDate

    // TODO: add more profile related tests

    @BeforeTest
    fun setupUserEntity() {
        user = UserEntity(
            uid = googleId,
            username = username,
            birthDate = birthDate,
            name = name,
            email = email
        )
        user.addNewProfile(adminProfile)
    }

    @Test
    fun testUserAgeReturnedCorrectly() {
        val age = user.age
        assertEquals(
            HelperFunctions.calculateAge(birthDate, LocalDate.now()),
            age
        )
    }

    @Test
    fun testUserAgeWithNullBirthDate() {
        val userEntityWithoutBirthDate =
            UserEntity(
                uid = googleId,
                username = username,
                birthDate = null
            )
        assertNull(userEntityWithoutBirthDate.age)
    }

    @Test
    fun testUserProperties() {
        assertEquals(googleId, user.uid)
        assertEquals(username, user.username)
        assertEquals(name, user.name)
        assertEquals(birthDate, user.birthDate)
        assertEquals(email, user.email)
    }

    @Test
    fun testIfUserIsAdmin() {
        assertTrue(user.isAdmin())
    }

}