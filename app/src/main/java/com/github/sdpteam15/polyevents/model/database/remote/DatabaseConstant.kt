package com.github.sdpteam15.polyevents.model.database.remote

import com.github.sdpteam15.polyevents.model.database.remote.adapter.*

const val TEST_STR = "STR"

class StringWithID(val id: String, val string: String)

/**
 * Constants of collection names and attribute names.
 */
object DatabaseConstant {
    enum class CollectionConstant(
        val value: String,
        val adapter: AdapterInterface<out Any>
    ) {
        LOCATION_COLLECTION("locations", DeviceLocationAdapter),
        ZONE_COLLECTION("zones", ZoneAdapter),
        ITEM_COLLECTION("items", ItemEntityAdapter),
        EVENT_COLLECTION("events", EventAdapter),
        PROFILE_COLLECTION("profile", ProfileAdapter),
        USER_COLLECTION("users", UserAdapter),
        ITEM_TYPE_COLLECTION("itemTypes", ItemTypeAdapter),
        MATERIAL_REQUEST_COLLECTION("materialRequests", MaterialRequestAdapter),


        TEST_COLLECTION("test", object : AdapterInterface<StringWithID> {
            override fun toDocument(element: StringWithID): HashMap<String, Any?> =
                hashMapOf(TEST_STR to element.string)

            override fun fromDocument(
                document: MutableMap<String, Any?>,
                id: String
            ) = StringWithID(id, document[TEST_STR] as String)
        });


        override fun toString(): String = value
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
        EVENT_TAGS("tags"),
        EVENT_MAX_SLOTS("maxNumberOfSlots"),
        EVENT_LIMITED("limitedEvent"),
        EVENT_PARTICIPANTS("participants");

        override fun toString(): String = value
    }

    enum class ItemConstants(val value: String) {
        ITEM_DOCUMENT_ID("itemId"),
        ITEM_NAME("name"),
        ITEM_TYPE("itemType"),
        ITEM_COUNT("itemCount");

        override fun toString(): String = value
    }

    enum class ProfileConstants(val value: String) {
        PROFILE_ID("pid"),
        PROFILE_NAME("name"),
        PROFILE_RANK("rank"),
        PROFILE_USERS("users");

        override fun toString(): String = value
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
        USER_PROFILES("profiles");

        override fun toString(): String = value
    }

    enum class ZoneConstant(val value: String) {
        ZONE_DOCUMENT_ID("zoneId"),
        ZONE_NAME("zoneName"),
        ZONE_LOCATION("zoneLocation"),
        ZONE_DESCRIPTION("zoneDescription"),
        LAT_LONG_SEP("|"),
        POINTS_SEP("!"),
        AREAS_SEP("?");

        override fun toString(): String = value
    }

    enum class LocationConstant(val value: String) {
        LOCATIONS_DEVICE("device"),
        LOCATIONS_POINT_LATITUDE("latitude"),
        LOCATIONS_POINT_LONGITUDE("longitude"),
        LOCATIONS_TIME("time");

        override fun toString(): String = value
    }

    enum class MaterialRequestConstant(val value: String) {
        MATERIAL_REQUEST_ID("mid"),
        MATERIAL_REQUEST_TIME("time"),
        MATERIAL_REQUEST_LIST("item_list"),
        MATERIAL_REQUEST_USER_ID("user_id");

        override fun toString(): String = value
    }
}