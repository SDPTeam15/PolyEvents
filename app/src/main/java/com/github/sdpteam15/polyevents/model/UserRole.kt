package com.github.sdpteam15.polyevents.model

enum class UserRole(val userType: String) {
    ADMIN("admin"),
    ORGANIZER("organizer"),
    STAFF("staff"),
    PARTICIPANT("participant")
}