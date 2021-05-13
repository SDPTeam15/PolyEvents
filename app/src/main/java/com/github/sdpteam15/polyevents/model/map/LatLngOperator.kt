package com.github.sdpteam15.polyevents.model.map

import com.github.sdpteam15.polyevents.model.map.LatLngOperator.Polygon.Vertex
import com.github.sdpteam15.polyevents.model.map.LatLngOperator.PolygonOperationType.*
import com.google.android.gms.maps.model.LatLng
import java.util.*
import kotlin.math.abs
import kotlin.math.atan
import kotlin.math.sqrt

/**
 * Object containing methods to compute various metrics with LatLng
 * TODO? take into account the curvature of the earth to compute distances instead of considering latitude and longitude as cartesian coordinates
 */
object LatLngOperator {
    /**
     * Used to check if floating point values are close enough to be considered as equal
     */
    const val epsilon = 1e-10

    /**
     * Returns the coordinate-wise subtraction of the first point by the second one
     * @param point1 the first point
     * @param point2 the second point
     * @return the subtraction of point 1 by point 2
     */
    fun minus(point1: LatLng, point2: LatLng) =
        LatLng(point1.latitude - point2.latitude, point1.longitude - point2.longitude)

    /**
     * Returns the coordinate-wise addition of the first point by the second one
     * @param point1 the first point
     * @param point2 the second point
     * @return the addition of point 1 by point 2
     */
    fun plus(point1: LatLng, point2: LatLng) =
        LatLng(point1.latitude + point2.latitude, point1.longitude + point2.longitude)

    /**
     * Returns the coordinate-wise multiplication of the point by a scalar
     * @param point the point to multiply
     * @param nbr the scalar to multiply
     * @return the multiplication of the point by the given scalar
     */
    fun time(point: LatLng, nbr: Double) =
        LatLng(point.latitude * nbr, point.longitude * nbr)

    /**
     * Returns the coordinate-wise division of the point by a scalar
     * @param point the point to be divided
     * @param nbr the scalar to divide by
     * @return the division of the point by the given scalar
     */
    fun divide(point: LatLng, nbr: Double) =
        LatLng(point.latitude / nbr, point.longitude / nbr)

    /**
     * Returns the mean of the points
     * @param points list of points
     * @return the mean of the points
     */
    fun mean(points: List<LatLng>): LatLng {
        var latitude = 0.0
        var longitude = 0.0
        var nbr = 0
        for (point in points) {
            latitude += point.latitude
            longitude += point.longitude
            nbr += 1
        }
        return if (nbr != 0)
            LatLng(latitude / nbr, longitude / nbr)
        else
            LatLng(0.0, 0.0)
    }

    /**
     * Returns the angle in degrees between the horizontal x axis and the line passing through the two given points
     * @param start the first point
     * @param end the second point
     * @return the angle between the x axis and the line passing though start and end
     */
    fun angle(start: LatLng, end: LatLng) =
        (atan((start.latitude - end.latitude) / (start.longitude - end.longitude)) / Math.PI) * 180

    /**
     * Checks whether two angles are close enough to each other, i.e. if the difference between the lines described by the given angles is less than 20°.
     *
     * @param angle1 the first angle
     * @param angle2 the second angle
     * @return true if the angles are less than 20° apart, else return false
     */
    fun isTooParallel(angle1: Double, angle2: Double): Boolean {
        if (!(angle1 in -90.0..90.0 && angle2 in -90.0..90.0))
            throw IllegalArgumentException("angles must be in the range -90.0..90.0")
        var dif = abs(angle1 - angle2)
        dif = if (dif > 90) 180 - dif else dif
        return dif < 20
    }

    /**
     * Computes the scalar product between 2 points
     * @param point1 first point
     * @param point2 second point
     * @return distance between the points
     */
    fun scalar(point1: LatLng, point2: LatLng) =
        point1.latitude * point2.latitude + point1.longitude * point2.longitude

    /**
     * Computes the euclidean distance between 2 points
     * @param start first point
     * @param end second point
     * @return distance between the points
     */
    fun euclideanDistance(start: LatLng, end: LatLng): Double =
        euclideanDistance(start.longitude, start.latitude, end.longitude, end.latitude)

    /**
     * Computes the squared euclidean norm
     * @param point point
     */
    fun squaredNorm(point: LatLng) =
        squaredNorm(point.longitude, point.latitude)

    /**
     * Computes the euclidean norm
     * @param point point
     */
    fun norm(point: LatLng) =
        sqrt(squaredNorm(point))

    /**
     * Computes the squared euclidean norm
     * @param dx distance in x
     * @param dy distance in y
     */
    fun squaredNorm(dx: Double, dy: Double) =
        dx * dx + dy * dy

    /**
     * Computes the euclidean distance between 2 points
     * @param startX first point x coordinate
     * @param startY first point y coordinate
     * @param endX second point x coordinate
     * @param endY second point y coordinate
     * @return distance between the points
     */
    fun euclideanDistance(startX: Double, startY: Double, endX: Double, endY: Double): Double =
        sqrt(squaredEuclideanDistance(startX, startY, endX, endY))

    /**
     * Computes the squared euclidean distance between 2 points
     * @param startX first point x coordinate
     * @param startY first point y coordinate
     * @param endX second point x coordinate
     * @param endY second point y coordinate
     * @return squared euclidean distance between the points
     */
    fun squaredEuclideanDistance(
        startX: Double,
        startY: Double,
        endX: Double,
        endY: Double
    ): Double {
        val dx = startX - endX
        val dy = startY - endY
        return squaredNorm(dx, dy)
    }

    /**
     * Computes the squared euclidean distance between 2 points
     * @param start first point
     * @param end second point
     * @return squared euclidean distance between the points
     */
    fun squaredEuclideanDistance(start: LatLng, end: LatLng): Double =
        squaredEuclideanDistance(start.longitude, start.latitude, end.longitude, end.latitude)

    /**
     * Returns the intersection point between 2 segments
     * @param start1 start of the first segment
     * @param end1 end of the first segment
     * @param start2 start of the second segment
     * @param end2 end of the second segment
     * @return The intersection point of the two segments, null if the two segments do not intersect
     */
    fun getIntersection(start1: LatLng, end1: LatLng, start2: LatLng, end2: LatLng): LatLng? {

        val coefs = getIntersectionCoefficients(start1, end1, start2, end2) ?: return null
        return plus(start1, time(minus(end1, start1), coefs.first))
    }

    /**
     * Checks is a point is on a given segment
     * @param start the segment start
     * @param end the segment end
     * @param point the point to be checked if on the segment or not
     * @return true if the point is on the segment, false otherwise
     */
    fun isOnSegment(start: LatLng, end: LatLng, point: LatLng): Boolean {
        val a = minus(point, start)
        val b = minus(end, start)
        //if projection on the segment is (almost) the same, return checks if the point lies inside the boundaries formed by the two points
        return if (euclideanDistance(minus(point, start), project(a, b)) > epsilon) {
            false
        } else {

            ((point.latitude in start.latitude..end.latitude && point.longitude in start.longitude..end.longitude) ||
                    (point.latitude in start.latitude..end.latitude && point.longitude in end.longitude..start.longitude) ||
                    (point.latitude in end.latitude..start.latitude && point.longitude in end.longitude..start.longitude) ||
                    (point.latitude in end.latitude..start.latitude && point.longitude in start.longitude..end.longitude))
        }
    }

    /**
     * Computes the projection of the vector a on the vector b
     * @param a the vector to project
     * @param b the vector on which we want to project
     * @return the projection of a on b
     */
    fun project(a: LatLng, b: LatLng): LatLng {
        return time(b, scalar(a, divide(b, squaredNorm(b))))
    }

    /**
     * Checks if a point lies inside a Rectangle
     * https://math.stackexchange.com/questions/190111/how-to-check-if-a-point-is-inside-a-rectangle
     * @param point the point
     * @param rectangle the rectangle
     * @return true if the point is inside the rectangle, else false
     */
    fun isInRectangle(point: LatLng, rectangle: List<LatLng>): Boolean {
        val ab = minus(rectangle[1], rectangle[0])
        val ad = minus(rectangle[3], rectangle[0])
        val am = minus(point, rectangle[0])
        return (scalar(am, ab) in -epsilon..squaredNorm(ab) + epsilon &&
                scalar(am, ad) in -epsilon..squaredNorm(ad) + epsilon)
    }

    /**
     * Check if a point is very close to an other one (used to compensate floating point error)
     * @param p1 first point
     * @param p2 second point
     * @return true is the points are very close, else false
     */
    private fun isCloseTo(p1: LatLng, p2: LatLng): Boolean {
        return (euclideanDistance(p1, p2) < epsilon)
    }


    /**
     * Represents a polygon by a linked list of vertices.
     * @param points initial list of points
     */
    class Polygon(points: List<LatLng>) {
        // removes last vertex if it is the same as the first one
        private var vertices: List<Vertex> = points.dropLastWhile { points[0] == it }.map {
            Vertex(
                xy = it,
                next = null,
                prev = null,
                intersect = false,
                entry_exit = null,
                neighbor = null
            )
        }

        init {
            for (idx in vertices.indices) {
                vertices[idx].next = vertices[(idx + 1) % vertices.size]
                vertices[idx].prev = vertices[(idx - 1 + vertices.size) % vertices.size]
            }
        }

        val start = vertices[0]

        /**
         * Iterates over the polygon from start and going to the "next" Vertex at each iteration
         * @param function function to apply at each iteration
         */
        fun foreach(function: (Vertex) -> Unit) {
            var v = start
            do {
                function(v)
                v = v.next!!
            } while (v != start)
        }

        /**
         * Gets the list of LatLng from the polygon
         */
        fun toLatLongList(): MutableList<LatLng> {
            val list = mutableListOf<LatLng>()
            foreach { list.add(it.xy) }
            return list
        }


        /**
         * Represents a polygon vertex as a linked list element
         * @param xy latitude and longitude coordinates
         * @param next next linked list element
         * @param prev previous linked list element
         * @param intersect true if the point is an intersection point with an other polygon, else false
         * @param entry_exit null if not on an intersection point, true if the intersection is an entry point to the other polygon,
         *      regarding the "next" path of the current polygon, false if the intersection is an exit point from the other polygon.
         * @param neighbor if the point is an intersection, represents the copy of this point on the other polygon
         */
        data class Vertex(
            val xy: LatLng,
            var next: Vertex?,
            var prev: Vertex?,
            var intersect: Boolean,
            var entry_exit: Boolean?,
            var neighbor: Vertex?
        ) {
            override fun toString(): String {
                return xy.toString()
            }
        }
    }

    /**
     * Returns the coefficients between 0.0 and 1.0 representing the distance between the starts and the intersection point
     * @param start1 start of the first segment
     * @param end1 end of the first segment
     * @param start2 start of the second segment
     * @param end2 end of the second segment
     * @return null if no intersection, else returns the pair of ratios
     */
    private fun getIntersectionCoefficients(
        start1: LatLng,
        end1: LatLng,
        start2: LatLng,
        end2: LatLng
    ): Pair<Double, Double>? {
        val denom =
            (((start1.latitude - end1.latitude) * (start2.longitude - end2.longitude)) - ((start1.longitude - end1.longitude) * (start2.latitude - end2.latitude)))

        if (abs(denom) < epsilon) {
            //the lines are parallel
            return null
        }
        val t =
            ((start1.latitude - start2.latitude) * (start2.longitude - end2.longitude) - (start1.longitude - start2.longitude) * (start2.latitude - end2.latitude)) / denom
        val s =
            ((end1.latitude - start1.latitude) * (start1.longitude - start2.longitude) - (end1.longitude - start1.longitude) * (start1.latitude - start2.latitude)) / denom
        if (!(t in 0.0 - epsilon..1.0 + epsilon && s in 0.0 - epsilon..1.0 + epsilon)) {
            //the intersection point is outside the boundaries formed by the extremities of the segments
            return null
        }
        return Pair(t, s)
    }

    /**
     * creates a new intersection vertex between start and stop at a distance alpha (0 < alpha < 1)
     * @param start starting node
     * @param stop ending node
     * @param alpha distance between start and stop
     * @return new intersection vertex
     */
    private fun createIntersectionVertex(start: Vertex, stop: Vertex, alpha: Double) = Vertex(
        xy = plus(start.xy, time(minus(stop.xy, start.xy), alpha)),
        next = null,
        prev = null,
        intersect = true,
        entry_exit = null,
        neighbor = null
    )

    /**
     * Checks if the given point is in the given polygon
     * Draws a line from the point to infinity, if the ray crosses the polygon an even number of times, the point lies inside the polygon
     * @param point the point
     * @param polygon the polygon
     * @return true if the point is in the polygon, else retun false
     */
    private fun pointInsidePolygon(point: LatLng, polygon: Polygon): Boolean {
        // https://en.wikipedia.org/wiki/Point_in_polygon#:~:text=One%20simple%20way%20of%20finding,an%20even%20number%20of%20times.
        val maxLat = 90.0 // LatLng does not have a positive infinity for Lat, 90.0 is the max value
        var curVertex = polygon.start
        var nbOfIntersections = 0

        // keep track of the last intersection we crossed, to not count twice when the ray crosses a node
        var lastIntersection: LatLng?
        var currentIntersection: LatLng? = null
        do {
            //if point is on a segment, immediately we consider the point inside the polygon
            if (isOnSegment(curVertex.xy, curVertex.next!!.xy, point)) return true


            lastIntersection = currentIntersection
            currentIntersection = getIntersection(
                curVertex.xy,
                curVertex.next!!.xy,
                point,
                LatLng(
                    maxLat,
                    // trick to prevent vertical lines intersections
                    point.longitude + 0.123456
                )
            )

            if (
                currentIntersection != null &&
                if (lastIntersection != null) {
                    // don't count twice when the ray crosses a node
                    !isCloseTo(lastIntersection, currentIntersection)
                } else {
                    true
                }
            //if intersection add to the count
            ) {
                nbOfIntersections++
            }
            curVertex = curVertex.next!!
        } while (curVertex != polygon.start)
        return nbOfIntersections % 2 == 1

    }


    /**
     * Represents theoretical set operation we can make on polygons
     */
    enum class PolygonOperationType {
        UNION,          // A ∪ B
        INTERSECTION,   // A ∩ B
        DIFFERENCE      // A \ B
    }

    /**
     * Computes the theoretical set union between polygons (just like in Venn diagrams).
     * @param polygons List of polygons, each polygon is represented by a list of its corners (in LatLng)
     * @return a list of disjoint shapes. Each shape is a Pair,
     *      the first element is a single polygon representing the outside border of the shape,
     *      the second element is the list of disjoint holes (polygon) in that shape.
     */
    fun polygonsUnion(polygons: List<List<LatLng>>): MutableList<Pair<MutableList<LatLng>, MutableList<MutableList<LatLng>>?>> {
        //rectangles that have not been merged
        val remainingRectangles = polygons.toMutableList() //deep copy

        val finalShape =
            mutableListOf<Pair<MutableList<LatLng>, MutableList<MutableList<LatLng>>?>>()
        while (remainingRectangles.isNotEmpty()) {
            var outerShape = remainingRectangles.removeFirst().toMutableList()

            var holes = mutableListOf<MutableList<LatLng>>()

            val queuedRectangles = ArrayDeque(remainingRectangles)
            val notIntersecting = mutableListOf<List<LatLng>>()
            while (queuedRectangles.isNotEmpty()) {
                val rectangle = queuedRectangles.pollFirst()!!
                val unionShape = polygonOperation(
                    Polygon(outerShape),
                    Polygon(rectangle),
                    UNION
                )
                // if union is disjoint
                if (unionShape.size > 1) {
                    notIntersecting.add(rectangle)
                    continue
                }
                // there is an intersection
                while (notIntersecting.isNotEmpty()) {
                    queuedRectangles.addLast(notIntersecting.removeFirst())
                }
                remainingRectangles.remove(rectangle)
                val newHoles = unionShape[0].second ?: mutableListOf()
                for (hole in holes.toMutableList()) {
                    for (newHole in polygonOperation(
                        Polygon(hole),
                        Polygon(rectangle), DIFFERENCE
                    )) {
                        newHoles.add(newHole.first)
                    }
                }
                holes = newHoles

                outerShape = unionShape[0].first
            }

            if (holes.isEmpty()) {
                finalShape.add(Pair(outerShape, null))
            } else {
                finalShape.add(Pair(outerShape, holes))
            }
        }

        return finalShape
    }


    /**
     * Useful for the polygonOperation algorithm
     */
    private const val entry = true
    private const val exit = false

    /**
     * Computes theoretical set operations between the subject and the clip polygon
     * As the polygons can be non convex, the resulting shape may have holes, this is why we use this return type.
     * Inspired from : https://dl.acm.org/doi/abs/10.1145/274363.274364
     * @param subject "subject" polygon
     * @param clip "clip" polygon
     * @param polygonOperationType
     *      UNION :         subject ∪ clip
     *      INTERSECTION :  subject ∩ clip
     *      DIFFERENCE      subject \ clip
     * @return a list of disjoint shapes. Each shape is a Pair,
     *      the first element is a single polygon representing the outside border of the shape,
     *      the second element is the list of disjoint holes (polygon) in that shape.
     */
    fun polygonOperation(
        subject: Polygon,
        clip: Polygon,
        polygonOperationType: PolygonOperationType
    ): MutableList<Pair<MutableList<LatLng>, MutableList<MutableList<LatLng>>?>> {

        /** Phase 1 : find intersection points between subject and clip, and setup them in double linked lists
         * Each double linked list contains all the points of a polygon.
         * There is a link from one linked list to an other at each intersection point of the polygons so we can jump from one to the other
         */

        var curVertP1 = subject.start
        var curVertP2 = clip.start
        do {
            //we don't want to take into account when a point we used is too close
            if (!(isCloseTo(curVertP1.xy, curVertP2.xy) ||
                        isCloseTo(curVertP1.next!!.xy, curVertP2.xy) ||
                        isCloseTo(curVertP1.xy, curVertP2.next!!.xy) ||
                        isCloseTo(curVertP1.next!!.xy, curVertP2.next!!.xy))

            ) {

                val inter = getIntersectionCoefficients(
                    curVertP1.xy,
                    curVertP1.next!!.xy,
                    curVertP2.xy,
                    curVertP2.next!!.xy
                )
                if (inter != null) {
                    //create a vertex for each list at the intersection point
                    val v1 = createIntersectionVertex(curVertP1, curVertP1.next!!, inter.first)
                    val v2 = createIntersectionVertex(curVertP2, curVertP2.next!!, inter.second)
                    //link v1 and v2
                    v1.neighbor = v2
                    v2.neighbor = v1

                    //place v1 in first linked list, v2 in second linkedlist
                    v1.next = curVertP1.next
                    v1.prev = curVertP1
                    curVertP1.next!!.prev = v1
                    curVertP1.next = v1

                    v2.next = curVertP2.next
                    v2.prev = curVertP2
                    curVertP2.next!!.prev = v2
                    curVertP2.next = v2


                }
            }
            curVertP2 = curVertP2.next!!
            if (curVertP2 == clip.start) {
                curVertP1 = curVertP1.next!!
            }
        } while (curVertP1 != subject.start || curVertP2 != clip.start)

        val shape = checkForTrivialCases(subject, clip, polygonOperationType)
        if (shape != null) return shape

        /** Phase 2 : Set entry and exit points
         * Let's see polygons as directed graph represented by the linked lists in the "next" direction
         * At each intersection, if we enter in the other polygon, we place an "entry" marker,
         *      if we leave the other polygon interior, we place an "exit" marker
         */

        // list that keeps track of intersections on subject polygon, useful for Phase 3
        val subjectIntersections = mutableListOf<Vertex>()

        fun setEntriesExits(polygon1: Polygon, polygon2: Polygon) {
            var status = if (pointInsidePolygon(polygon1.start.xy, polygon2)) {
                exit
            } else {
                entry
            }
            polygon1.foreach {
                if (it.intersect) {
                    it.entry_exit = status
                    status = !status
                    // add all intersections of the first polygon for phase 3
                    if (polygon1 == subject) {
                        subjectIntersections.add(it)
                    }
                }
            }
        }
        setEntriesExits(subject, clip)
        setEntriesExits(clip, subject)

        /** Phase 3 Draw the resulting polygons and holes
         * We start at an intersection point and given the operation.
         * We follow the polygons and switch from one to the other to "draw" the resulting polygon
         * For union, at each intersection, we switch to the other polygon and we must follow the "outside" path
         * For intersection, at each intersection, we switch to the other polygon and we must follow the "inside" path
         * For difference, at each intersection, we switch to the other polygon and
         *      we follow the "outside" path of the subject polygon and "inside" path of the clip polygon
         *
         * The result is a list of disjoint polygons.
         */

        val polygons = mutableListOf<MutableList<LatLng>>()
        while (subjectIntersections.isNotEmpty()) {
            val firstIntersection = subjectIntersections.first()
            val newPolygon = mutableListOf(firstIntersection.xy)
            var current = firstIntersection
            var isOnSubject = true
            do {
                subjectIntersections.removeIf { isCloseTo(it.xy, current.xy) }
                if (when (polygonOperationType) {
                        UNION -> !current.entry_exit!!
                        INTERSECTION -> current.entry_exit!!
                        DIFFERENCE -> current.entry_exit!! != isOnSubject
                    }
                ) {
                    // draw the line over next values until the next intersection
                    do {
                        current = current.next!!
                        newPolygon.add(current.xy)
                    } while (!current.intersect)

                } else {
                    // draw the line over prev values until the next intersection
                    do {
                        current = current.prev!!
                        newPolygon.add(current.xy)
                    } while (!current.intersect)
                }
                // switch to the corresponding vertex on the other polygon
                current = current.neighbor!!
                isOnSubject = !isOnSubject
                // repeat until a polygon is closed
            } while (current != firstIntersection)
            polygons.add(newPolygon)
        }


        /** Phase 4 : Return in correct format
         * We must now return the correct values from our drawing of phase 3
         * If we are in UNION mode, there can only be a single shape with holes at this point
         * If we are in INTERSECTION mode, there can only be disjoint polygons without holes
         * If we are in DIFFERENCE mode, there can only be disjoint polygons without holes
         */

        /**
         * Used only in union mode, to find the enclosing outside polygon and its holes.
         * @param polygons list of polygons from phase 3
         * @return the enclosing shape with its holes
         */
        fun separateOutsidePolygonFromHoles(polygons: MutableList<MutableList<LatLng>>): Pair<MutableList<LatLng>, MutableList<MutableList<LatLng>>?> {
            if (polygons.size == 1) {
                return Pair(polygons[0], null)
            }
            fun findOutsidePolygon(polygons: MutableList<MutableList<LatLng>>): MutableList<LatLng> {
                var outsidePolygon = polygons[0]
                for (polygon in polygons.drop(1)) {
                    if (pointInsidePolygon(outsidePolygon[0], Polygon(polygon))) {
                        outsidePolygon = polygon
                    }
                }
                return outsidePolygon
            }

            val outside = findOutsidePolygon(polygons)
            polygons.remove(outside)
            return Pair(outside, polygons)
        }

        return when (polygonOperationType) {
            //union of 2 overlapping polygons can only give a single zone with holes inside
            UNION -> mutableListOf(
                separateOutsidePolygonFromHoles(
                    polygons
                )
            )
            //intersection of 2 polygons without holes can only give polygons without holes
            INTERSECTION, DIFFERENCE ->
                polygons.map { Pair(it, null) }.toMutableList()
        }
    }

    /**
     * Check if the resulting zones are one inside an other or disjoint
     * In this case we can return directly the trivial value relative to each operation
     * for example union of disjoint polygons is simply both polygons
     * or intersection of disjoint polygons is empty
     * or subtraction of a zone by an inner zone is the enclosing zone with the inner zone as its hole
     * @param subject subject polygon
     * @param clip clip polygon
     * @param polygonOperationType
     *      UNION :         subject ∪ clip
     *      INTERSECTION :  subject ∩ clip
     *      DIFFERENCE      subject \ clip
     * @return
     *
     */

    private fun checkForTrivialCases(
        subject: Polygon,
        clip: Polygon,
        polygonOperationType: PolygonOperationType
    ): MutableList<Pair<MutableList<LatLng>, MutableList<MutableList<LatLng>>?>>? {
        var existingIntersections = false
        var subjectInClip = true
        var clipInSubject = true
        var subjectAndClipSeparated = true
        subject.foreach {
            existingIntersections = existingIntersections || it.intersect
            if (pointInsidePolygon(it.xy, clip)) subjectAndClipSeparated = false
            else subjectInClip = false
        }
        clip.foreach {
            existingIntersections = existingIntersections || it.intersect
            if (pointInsidePolygon(it.xy, subject)) subjectAndClipSeparated = false
            else clipInSubject = false
        }

        if (subjectInClip && clipInSubject) {
            return when (polygonOperationType) {
                UNION, INTERSECTION -> mutableListOf(
                    Pair(
                        subject.toLatLongList(), null
                    )
                )
                DIFFERENCE -> mutableListOf()
            }
        }

        if (!existingIntersections) {
            when (polygonOperationType) {
                UNION -> {
                    if (subjectInClip) return mutableListOf(
                        Pair(
                            clip.toLatLongList(), null
                        )
                    )
                    if (clipInSubject) return mutableListOf(
                        Pair(
                            subject.toLatLongList(), null
                        )
                    )
                    if (subjectAndClipSeparated) return mutableListOf(
                        Pair(subject.toLatLongList(), null),
                        Pair(clip.toLatLongList(), null)
                    )
                }
                INTERSECTION -> {
                    if (subjectInClip) return mutableListOf(
                        Pair(
                            subject.toLatLongList(), null
                        )
                    )
                    if (clipInSubject) return mutableListOf(
                        Pair(
                            clip.toLatLongList(), null
                        )
                    )
                    if (subjectAndClipSeparated) return mutableListOf()
                }
                DIFFERENCE -> {
                    if (subjectInClip) return mutableListOf()
                    if (clipInSubject) return mutableListOf(
                        Pair(
                            subject.toLatLongList(), mutableListOf(clip.toLatLongList())
                        )
                    )
                    if (subjectAndClipSeparated) return mutableListOf(
                        Pair(
                            subject.toLatLongList(), null
                        )
                    )
                }
            }
        }
        return null
    }

}
