package com.github.sdpteam15.polyevents.util

import com.github.sdpteam15.polyevents.database.DatabaseConstant
import com.github.sdpteam15.polyevents.helper.HelperFunctions
import com.github.sdpteam15.polyevents.model.UserEntity
import com.google.firebase.Timestamp
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.time.LocalDate
import kotlin.collections.HashMap

class UserAdapterTest {
    lateinit var user: UserEntity

    private val googleId = "googleId"
    private val username = "JohnDoe"
    private val name = "John Doe"
    private val birthDate: LocalDate = LocalDate.of(1990, 12, 30)
    private val email = "John@email.com"
    private val telephone = "23456789"

    @Before
    fun setupUserEntity() {
        user = UserEntity(
            uid = googleId,
            username = username,
            birthDate = birthDate,
            name = name,
            email = email,
            telephone = telephone
        )
    }

    @Test
    fun conversionOfUserEntityToDocumentPreservesData() {
        val document = UserAdapter.toDocument(user)

        assertEquals(document[DatabaseConstant.USER_UID], googleId)
        assertEquals(document[DatabaseConstant.USER_USERNAME], username)
        assertEquals(document[DatabaseConstant.USER_BIRTH_DATE], birthDate.atStartOfDay())
        assertEquals(document[DatabaseConstant.USER_NAME], name)
        assertEquals(document[DatabaseConstant.USER_EMAIL], email)
        assertEquals(document[DatabaseConstant.USER_PHONE], telephone)
    }

    @Test
    fun conversionOfDocumentToUserEntityPreservesData() {
        val birthDateTimeStamp =
                Timestamp(HelperFunctions.LocalDateToTimeToDate(birthDate.atStartOfDay())!!)
        val userDocumentData : HashMap<String, Any?> = hashMapOf(
                DatabaseConstant.USER_UID to googleId,
                DatabaseConstant.USER_NAME to name,
                DatabaseConstant.USER_EMAIL to email,
                DatabaseConstant.USER_BIRTH_DATE to birthDateTimeStamp,
                DatabaseConstant.USER_USERNAME to username,
                DatabaseConstant.USER_PHONE to telephone
        )

        assertEquals(user, UserAdapter.fromDocument(userDocumentData))
    }

    @Test
    fun testConversionToUserEntityWithNullBirthDate() {
        val userDocumentData : HashMap<String, Any?> = hashMapOf(
                DatabaseConstant.USER_UID to googleId,
                DatabaseConstant.USER_NAME to name,
                DatabaseConstant.USER_EMAIL to email,
                DatabaseConstant.USER_BIRTH_DATE to null,
                DatabaseConstant.USER_USERNAME to username,
                DatabaseConstant.USER_PHONE to telephone
        )

        assertNull(UserAdapter.fromDocument(userDocumentData).age)
    }

    @Test
    fun testConversionWithNullValues() {
        val userEntityWithNullProperties = UserEntity(uid = googleId)
        val document = UserAdapter.toDocument(userEntityWithNullProperties)
        assertEquals(document[DatabaseConstant.USER_UID], googleId)
        assertNull(document[DatabaseConstant.USER_NAME])
        assertNull(document[DatabaseConstant.USER_AGE])
    }
}