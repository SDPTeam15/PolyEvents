package com.github.sdpteam15.polyevents.model.map

/**
 * Action on the polygon when editing
 */
enum class PolygonAction {
    RIGHT,
    DOWN,
    DIAG,
    MOVE,
    ROTATE,
    MARKER_START,
    MARKER_END
}

/**
 * Action on marker
 */
enum class MarkerDragMode {
    DRAG_START,
    DRAG,
    DRAG_END
}

/**
 * Data class for the bounds of the icon for the markers
 */
data class IconBound(
    var leftBound: Int,
    var topBound: Int,
    var rightBound: Int,
    var bottomBound: Int
)

/**
 * Data class for the dimension of the icon for the markers
 */
data class IconDimension(var width: Int, var height: Int)

/**
 * Data class for the anchor of the icon for the markers
 */
data class IconAnchor(var anchorWidth: Float, var anchorHeight: Float)

/**
 * Enum for the mode we access the map
 */
enum class MapsFragmentMod {
    Visitor,
    EditZone,
    EditRoute
}