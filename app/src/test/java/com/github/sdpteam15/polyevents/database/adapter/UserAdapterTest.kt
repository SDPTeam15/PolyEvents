package com.github.sdpteam15.polyevents.database.adapter

import com.github.sdpteam15.polyevents.helper.HelperFunctions
import com.github.sdpteam15.polyevents.model.database.remote.DatabaseConstant.UserConstants.*
import com.github.sdpteam15.polyevents.model.database.remote.adapter.UserAdapter
import com.github.sdpteam15.polyevents.model.entity.UserEntity
import com.google.firebase.Timestamp
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

class UserAdapterTest {
    lateinit var user: UserEntity

    private val googleId = "googleId"
    private val username = "JohnDoe"
    private val name = "John Doe"
    private val birthDate: LocalDate = LocalDate.of(1990, 12, 30)
    private val email = "John@email.com"
    private val telephone = "23456789"
    private val listProfile = ArrayList<String>()

    @Before
    fun setupUserEntity() {
        user = UserEntity(
            uid = googleId,
            username = username,
            birthDate = birthDate,
            name = name,
            email = email,
            telephone = telephone,
            profiles = listProfile
        )
    }

    @Test
    fun conversionOfUserEntityToDocumentPreservesData() {
        val document = UserAdapter.toDocumentWithoutNull(user)
        assertEquals(document[USER_UID.value], googleId)
        assertEquals(document[USER_USERNAME.value], username)
        assertEquals(
            document[USER_BIRTH_DATE.value],
            HelperFunctions.localDateTimeToDate(birthDate.atStartOfDay())
        )
        assertEquals(document[USER_NAME.value], name)
        assertEquals(document[USER_EMAIL.value], email)
        assertEquals(document[USER_PHONE.value], telephone)
    }

    @Test
    fun conversionOfDocumentToUserEntityPreservesData() {
        val birthDateTimeStamp =
            Timestamp(HelperFunctions.localDateTimeToDate(birthDate.atStartOfDay())!!)
        val userDocumentData: HashMap<String, Any?> = hashMapOf(
            USER_UID.value to googleId,
            USER_NAME.value to name,
            USER_EMAIL.value to email,
            USER_BIRTH_DATE.value to birthDateTimeStamp,
            USER_USERNAME.value to username,
            USER_PHONE.value to telephone,
            USER_PROFILES.value to listProfile
        )

        assertEquals(user, UserAdapter.fromDocument(userDocumentData, googleId))
    }

    @Test
    fun testConversionToUserEntityWithNullBirthDate() {
        val userDocumentData: HashMap<String, Any?> = hashMapOf(
            USER_UID.value to googleId,
            USER_NAME.value to name,
            USER_EMAIL.value to email,
            USER_BIRTH_DATE.value to null,
            USER_USERNAME.value to username,
            USER_PHONE.value to telephone,
            USER_PROFILES.value to listProfile
        )

        assertNull(UserAdapter.fromDocument(userDocumentData, googleId).age)
    }

    @Test
    fun testConversionWithNullValues() {
        val userEntityWithNullProperties = UserEntity(uid = googleId)
        val document = UserAdapter.toDocumentWithoutNull(userEntityWithNullProperties)
        assertEquals(document[USER_UID.value], googleId)
        assertNull(document[USER_NAME.value])
        assertNull(document[USER_AGE.value])
    }
}