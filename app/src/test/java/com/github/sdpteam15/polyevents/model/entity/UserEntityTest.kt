package com.github.sdpteam15.polyevents.model.entity

import com.github.sdpteam15.polyevents.helper.HelperFunctions
import org.junit.Test
import java.time.LocalDate
import kotlin.test.BeforeTest
import kotlin.test.assertEquals
import kotlin.test.assertNull

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
}