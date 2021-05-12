package com.github.sdpteam15.polyevents.model.map

enum class PolygonAction {
    RIGHT,
    DOWN,
    DIAG,
    MOVE,
    ROTATE,
    MARKER_START,
    MARKER_END
}

enum class MarkerDragMode {
    DRAG_START,
    DRAG,
    DRAG_END
}

data class IconBound(
    var leftBound: Int,
    var topBound: Int,
    var rightBound: Int,
    var bottomBound: Int
)

data class IconDimension(var width: Int, var height: Int)
data class IconAnchor(var anchorWidth: Float, var anchorHeight: Float)

enum class MapsFragmentMod {
    Visitor,
    EditZone,
    EditRoute
}