package com.github.sdpteam15.polyevents.database.adapter

import com.github.sdpteam15.polyevents.model.database.remote.DatabaseConstant
import com.github.sdpteam15.polyevents.model.database.remote.adapter.RatingAdapter
import com.github.sdpteam15.polyevents.model.entity.Rating
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import kotlin.test.assertNull

class RatingAdapterTest {
    val userID = "userId"
    val ratingId = "ratingID"
    val eventId = "eventId"
    val feedback = "feedback"
    val rate = 3.5F

    lateinit var rating: Rating

    @Before
    fun setupRating() {
        rating = Rating(
            ratingId, rate, feedback, eventId, userID

        )
    }

    @Test
    fun canCreateWithoutAnyValues() {
        val rating2 = Rating()
        assertNull(rating2.rate)
        assertNull(rating2.feedback)
        assertNull(rating2.userId)
        assertNull(rating2.eventId)
        assertNull(rating2.ratingId)
    }

    @Test
    fun conversionOfRatingDocumentPreservesData() {
        val document = RatingAdapter.toDocument(rating)

        Assert.assertEquals(document[DatabaseConstant.RatingConstant.RATING_USER_ID.value], userID)

        Assert.assertEquals(
            document[DatabaseConstant.RatingConstant.RATING_EVENT_ID.value],
            eventId
        )
        Assert.assertEquals(
            document[DatabaseConstant.RatingConstant.RATING_DESCRIPTION.value],
            feedback
        )
        Assert.assertEquals(document[DatabaseConstant.RatingConstant.RATING_SCORE.value], rate)

    }

    @Test
    fun conversionToRatingDocumentPreservesData() {
        val rat: HashMap<String, Any?> = hashMapOf(
            DatabaseConstant.RatingConstant.RATING_EVENT_ID.value to rating.eventId,
            DatabaseConstant.RatingConstant.RATING_USER_ID.value to rating.userId,
            DatabaseConstant.RatingConstant.RATING_DESCRIPTION.value to rating.feedback,
            DatabaseConstant.RatingConstant.RATING_SCORE.value to rating.rate!!.toDouble()
        )

        val obtainedRating =
            RatingAdapter.fromDocument(rat, ratingId)

        assert(obtainedRating.eventId == rating.eventId)
        assert(obtainedRating.userId == rating.userId)
        assert(obtainedRating.feedback == rating.feedback)
        assert(obtainedRating.rate == rating.rate)
    }


}
