package com.github.sdpteam15.polyevents

import com.github.sdpteam15.polyevents.view.activity.MainActivity
import java.io.File

const val SPLIT_CHAR = ':'

const val IS_SENDING_LOCATION_ON = "isSendingLocationOn"
const val LOCATION_ID = "locationId"

//TODO replace by local cache
object Settings {
    var mainActivity: MainActivity? = null
    private var name: String? = null
        get() {
            if (field == null && mainActivity != null)
                field = mainActivity!!.applicationInfo.dataDir + "/Settings"
            return field
        }
    var isLoaded = false
        get() {
            if (!field && mainActivity != null) {
                var file = File(name)
                if (file.exists())
                    file.forEachLine {
                        val line = it.split(SPLIT_CHAR)
                        when (line[0]) {
                            IS_SENDING_LOCATION_ON -> isSendingLocationOn = line[1] == "true"
                            LOCATION_ID -> locationId = line[1]
                        }
                    }
                field = true
            }
            return field
        }
        set(value) {
            if (mainActivity != null) {
                File(name).printWriter().use {
                    it.println(IS_SENDING_LOCATION_ON + SPLIT_CHAR + if (isSendingLocationOn) "true" else "false")
                    it.println(LOCATION_ID + SPLIT_CHAR + locationId)
                }
                field = true
            }
        }


    private var isSendingLocationOn = true
    var IsSendingLocationOn: Boolean
        get() {
            isLoaded
            return isSendingLocationOn
        }
        set(value) {
            isLoaded
            isSendingLocationOn = value
            isLoaded = true
        }

    private var locationId = ""
    var LocationId: String
        get() {
            isLoaded
            return locationId
        }
        set(value) {
            isLoaded
            locationId = value
            isLoaded = true
        }
}