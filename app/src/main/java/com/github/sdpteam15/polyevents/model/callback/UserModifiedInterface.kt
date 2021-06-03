package com.github.sdpteam15.polyevents.model.callback

/**
 * Interface to notify that a profile has changed to refresh UI
 */
interface UserModifiedInterface {
    /**
     * Notify profile has changed
     */
    fun profileHasChanged()
}