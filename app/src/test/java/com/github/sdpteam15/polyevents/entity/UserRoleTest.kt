package com.github.sdpteam15.polyevents.entity

import com.github.sdpteam15.polyevents.model.entity.UserRole
import org.junit.Assert.*
import org.junit.Test

class UserRoleTest {
    @Test
    fun testUserRoleEnumValueOf() {
        assertEquals(UserRole.ADMIN, UserRole.valueOf("ADMIN"))
    }

    @Test
    fun testUserRoleToString() {
        assertEquals("Admin", UserRole.ADMIN.toString())
    }

    @Test
    fun testGetUserRoleByValue() {
        assertEquals(UserRole.ADMIN, UserRole.fromString(UserRole.ADMIN.userRole))
        assertEquals(UserRole.ORGANIZER, UserRole.fromString(UserRole.ORGANIZER.userRole))
        assertEquals(UserRole.PARTICIPANT, UserRole.fromString(UserRole.PARTICIPANT.userRole))
        assertEquals(UserRole.STAFF, UserRole.fromString(UserRole.STAFF.userRole))
    }
}