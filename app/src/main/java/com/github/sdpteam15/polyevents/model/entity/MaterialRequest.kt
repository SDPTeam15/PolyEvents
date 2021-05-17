package com.github.sdpteam15.polyevents.model.entity

import java.time.LocalDateTime

/**
 * Represents a material request from an event provider
 * @param requestId material request Id from firebase
 * @param items list of items to request
 * @param date date of the request
 * @param userId user who made the id
 * @param status material request status
 */
data class MaterialRequest(
    val requestId: String?,
    val items: Map<String, Int>,
    val time: LocalDateTime?,
    val userId: String,
    var status: Status
){
    enum class Status (private val status: String) {
        PENDING("pending"),
        ACCEPTED("accepted"),
        REFUSED("refused");

        override fun toString(): String {
            return status
        }

        companion object {
            private val map = Status.values().associateBy(Status::status)
            fun fromString(userRole: String) = map[userRole]
        }
    }


}