package com.github.sdpteam15.polyevents.database

/**
 * Constants of collection names and attribute names.
 */
object DatabaseConstant {
    enum class CollectionConstant(val value: String) {
        LOCATION_COLLECTION("locations"),
        ZONE_COLLECTION("zones"),
        ITEM_COLLECTION("items"),
        EVENT_COLLECTION("events"),
        PROFILE_COLLECTION("Profile"),
        USER_COLLECTION("users")
    }

    enum class EventConstant(val value: String) {
        EVENT_DOCUMENT_ID("eventId"),
        EVENT_NAME("eventName"),
        EVENT_ZONE_NAME("zoneName"),
        EVENT_DESCRIPTION("description"),
        EVENT_ORGANIZER("organizer"),
        EVENT_START_TIME("startTime"),
        EVENT_END_TIME("endTime"),
        EVENT_INVENTORY("inventory"),
        EVENT_ICON("icon"),
        EVENT_TAGS("tags")
    }

    enum class ItemConstants(val value: String) {
        ITEM_DOCUMENT_ID("itemId"),
        ITEM_NAME("name"),
        ITEM_TYPE("itemType"),
        ITEM_COUNT("itemCount")
    }

    enum class ProfileConstants(val value: String) {
        PROFILE_COLLECTION("profile"),
        PROFILE_ID("pid"),
        PROFILE_NAME("name"),
        PROFILE_RANK("rank"),
        PROFILE_USERS("users")
    }

    enum class UserConstants(val value: String) {
        USER_UID("uid"),
        USER_USERNAME("username"),
        USER_NAME("name"),
        USER_DISPLAY_NAME("displayName"),
        USER_EMAIL("email"),
        USER_AGE("age"),
        USER_TYPE("userType"),
        USER_BIRTH_DATE("birthDate"),
        USER_PHONE("telephone"),
        USER_PROFILES("profiles")
    }

    enum class ZoneConstant(val value: String) {
        ZONE_DOCUMENT_ID("zoneId"),
        ZONE_NAME("zoneName"),
        ZONE_LOCATION("zoneLocation"),
        ZONE_DESCRIPTION("zoneDescription"),
        LAT_LONG_SEP("|"),
        POINTS_SEP("!"),
        AREAS_SEP("?")
    }

    enum class LocationConstant(val value: String) {
        LOCATIONS_COLLECTION("locations"),
        LOCATIONS_POINT("point")
    }
}