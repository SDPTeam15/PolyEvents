package com.github.sdpteam15.polyevents.database.observe

/**
 * Observer of a live data
 */
interface Observer<T> {
    /**
     * update function when a new value occur
     * @param value the new value
     */
    fun update(value : T?)
}