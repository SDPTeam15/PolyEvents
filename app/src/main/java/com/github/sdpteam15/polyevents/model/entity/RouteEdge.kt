package com.github.sdpteam15.polyevents.model.entity

/**
 * TODO
 */
data class RouteEdge(
    val id: String?,
    val start: RouteNode,
    val end: RouteNode
) {
    val weight: Double
        get() {
            TODO()
        }

}