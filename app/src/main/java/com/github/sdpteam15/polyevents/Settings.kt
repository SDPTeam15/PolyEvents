package com.github.sdpteam15.polyevents

import java.io.File

const val SPLIT_CHAR = ':'

const val IS_SENDING_LOCATION_ON = "isSendingLocationOn"
const val LOCATION_ID = "locationId"

object Settings {
    var mainActivity : MainActivity? = null
    private var name = ""
        get() {
            if(field == "")
                field = mainActivity!!.getApplicationInfo().dataDir + "/Settings"
            return field
        }
    var isLoaded = false
        get() {
            if (!field) {
                var file = File(name)
                if (file.exists())
                    file.forEachLine {
                        val line = it.split(SPLIT_CHAR)
                        when (line[0]) {
                            IS_SENDING_LOCATION_ON -> isSendingLocationOn = line[0] == "true"
                            LOCATION_ID -> locationId = line[0]
                        }
                    }
                field = true
            }
            return field
        }
        set(value) {
            File(name).printWriter().use {
                it.println(IS_SENDING_LOCATION_ON + SPLIT_CHAR + if(isSendingLocationOn) "true" else "false")
                it.println(LOCATION_ID + SPLIT_CHAR + locationId)
            }
            field = value
        }


    private var isSendingLocationOn = true
    var IsSendingLocationOn : Boolean
        get() {
            isLoaded
            return isSendingLocationOn
        }
        set(value){
            isLoaded
            isSendingLocationOn = value
            isLoaded = true
        }

    private var locationId = ""
    var LocationId : String
        get() {
            isLoaded
            return locationId
        }
        set(value){
            isLoaded
            locationId = value
            isLoaded = true
        }
}