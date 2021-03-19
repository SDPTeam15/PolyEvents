package com.github.sdpteam15.polyevents.database

object DatabaseConstant {
    /**
     * User related constants
     */
    const val USER_COLLECTION = "users"
    const val USER_DOCUMENT_ID = "uid"
    const val USER_GOOGLE_ID = "googleId"
    const val USER_USERNAME = "username"
    const val USER_NAME = "name"
    const val USER_DISPLAY_NAME = "displayName"
    const val USER_EMAIL = "email"
    const val USER_AGE = "age"
    const val USER_TYPE =  "userType"
    const val USER_BIRTHDAY = "birthday"


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

    /**
     * Items related constants
     */
    const val ITEM_COLLECTION = "items"
    const val ITEM_DOCUMENT_ID = "itemId"
    const val ITEM_NAME = "name"


    /**
     * Area related constants
     */
    const val ZONE_COLLECTIOn = "zones"
    const val ZONE_DOCUMENT_ID = "zoneId"
    const val ZONE_NAME = "zoneName"
    const val ZONE_LOCATION = "location"
}