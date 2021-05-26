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
    val zoneId: String,
    var status: Status,
    var adminMessage: String?,
    var staffInChargeId: String?
){
    enum class Status (private val status: String) {
        PENDING("pending"),
        ACCEPTED("accepted"),
        REFUSED("refused"),
        DELIVERING("In delivery"),
        DELIVERED("Delivered"),
        RETURN_REQUESTED("Return requested"),
        RETURNING("In returning"),
        RETURNED("Returned")
        ;


        override fun toString(): String {
            return status
        }

        companion object {
            private val map = values().associateBy(Status::status)
            private val mapOrdinal =  map.mapKeys { it.value.ordinal }
            fun fromOrdinal(ordinal: Int) = mapOrdinal[ordinal]
        }
    }


}