package com.github.sdpteam15.polyevents.model

enum class UserRole(val userRole: String) {
    ADMIN("Admin"),
    ORGANIZER("Organizer"),
    STAFF("Staff"),
    PARTICIPANT("Participant");

    override fun toString(): String {
        return userRole
    }

    companion object {
        private val map = values().associateBy(UserRole::userRole)
        fun fromString(userRole: String) = map[userRole]
    }
}