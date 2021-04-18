package com.github.sdpteam15.polyevents.database

/**
 * Constants of collection names and attribute names.
 */
object DatabaseConstant {
    /**
     * User related constants
     */
    const val USER_COLLECTION = "users"
    const val USER_UID = "uid"
    const val USER_USERNAME = "username"
    const val USER_NAME = "name"
    const val USER_DISPLAY_NAME = "displayName"
    const val USER_EMAIL = "email"
    const val USER_AGE = "age"
    const val USER_TYPE = "userType"
    const val USER_BIRTH_DATE = "birthDate"
    const val USER_PHONE = "telephone"
    const val USER_PROFILES = "profiles"

    /**
     * Profile related constants
     */
    const val PROFILE_COLLECTION = "Profile"
    const val PROFILE_ID = "pid"
    const val PROFILE_NAME = "name"
    const val PROFILE_RANK = "rank"


    /**
     * Event related constants
     */
    const val EVENT_COLLECTION = "events"
    const val EVENT_DOCUMENT_ID = "eventId"
    const val EVENT_NAME = "eventName"
    const val EVENT_ZONE_NAME = "zoneName"
    const val EVENT_DESCRIPTION = "description"
    const val EVENT_ORGANIZER = "organizer"
    const val EVENT_START_TIME = "startTime"
    const val EVENT_END_TIME = "endTime"
    const val EVENT_INVENTORY = "inventory"
    const val EVENT_ICON = "icon"
    const val EVENT_TAGS = "tags"
    const val EVENT_MAX_SLOTS = "maxNumberOfSlots"

    /**
     * Items related constants
     */
    const val ITEM_COLLECTION = "items"
    const val ITEM_DOCUMENT_ID = "itemId"
    const val ITEM_NAME = "itemName"
    const val ITEM_TYPE = "itemType"
    const val ITEM_COUNT = "count"


    /**
     * Area related constants
     */
    const val ZONE_COLLECTION = "zones"
    const val ZONE_DOCUMENT_ID = "zoneId"
    const val ZONE_NAME = "zoneName"
    const val ZONE_LOCATION = "zoneLocation"
    const val ZONE_DESCRIPTION = "zoneDescription"
    const val LAT_LONG_SEP = "|"
    const val POINTS_SEP = "!"
    const val AREAS_SEP = "?"


    /**
     * Current locations related constants
     */
    const val LOCATIONS_COLLECTION = "locations"
    const val LOCATIONS_POINT = "point"
}