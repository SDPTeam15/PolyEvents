package com.github.sdpteam15.polyevents.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RatingBar
import androidx.fragment.app.DialogFragment
import com.github.sdpteam15.polyevents.R
import com.github.sdpteam15.polyevents.helper.HelperFunctions
import com.github.sdpteam15.polyevents.model.database.remote.Database.currentDatabase
import com.github.sdpteam15.polyevents.model.entity.Rating
import com.github.sdpteam15.polyevents.model.observable.Observable
import com.github.sdpteam15.polyevents.view.service.ReviewHasChanged

/**
 * A Dialog Fragment that is displayed over an EventActivity, to leave a review for the event.
 */
class LeaveEventReviewFragment(val eventId: String?, val reviewHasChanged: ReviewHasChanged):
    DialogFragment(R.layout.fragment_leave_review) {

    private lateinit var leaveReviewDialogConfirmButton: Button
    private lateinit var userFeedbackDialogEditText: EditText
    private lateinit var leaveReviewDialogRatingBar: RatingBar
    private lateinit var leaveReviewDialogCancelButton: Button

    private var rated = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        if (view != null) {
            leaveReviewDialogConfirmButton = view.findViewById(R.id.leave_review_fragment_save_button)

            userFeedbackDialogEditText = view.findViewById(R.id.leave_review_fragment_feedback_text)

            leaveReviewDialogRatingBar = view.findViewById(R.id.leave_review_fragment_rating)
            leaveReviewDialogRatingBar.setOnRatingBarChangeListener { _, _, _ ->
                rated = true
            }

            leaveReviewDialogCancelButton = view.findViewById(R.id.leave_review_fragment_cancel_button)
            leaveReviewDialogCancelButton.setOnClickListener {
                // Dimiss the dialog if canceled
                dismiss()
            }

            leaveReviewDialogConfirmButton.setOnClickListener {
                onClickAdd()
            }

            val ratingObservable = Observable<Rating>()
            ratingObservable.observe(this) {
                val rating = it.value
                leaveReviewDialogRatingBar.rating = rating.rate!!
                if (rating.feedback != null) {
                    if (!rating.feedback.isEmpty()) {
                        userFeedbackDialogEditText.setText(rating.feedback)
                    }
                }
                leaveReviewDialogConfirmButton.setOnClickListener {
                    onClickUpdate(rating)
                }
            }

            currentDatabase.eventDatabase!!.getUserRatingFromEvent(
                userId = currentDatabase.currentUser!!.uid,
                eventId = eventId!!,
                returnedRating = ratingObservable,
                userAccess = null
            )
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        // Uncomment the following lines to manually set the height and the width of the DialogFragment
        /*val width = ConstraintLayout.LayoutParams.MATCH_PARENT
        val height = ConstraintLayout.LayoutParams.MATCH_PARENT
        dialog!!.window!!.setLayout(width, height)*/
    }

    /**
     * A listener to update a rating in the database. Used when we have an already found
     * rating for the current user.
     */
    private fun onClickUpdate(rating: Rating) {
        if (!rated) {
            // Check if user has rated, to avoid storing a rating with zero stars.
            HelperFunctions.showToast(getString(R.string.event_review_leave_rating_warning),
                context
            )
        } else {
            val ratingCopy = rating.copy(
                rate = leaveReviewDialogRatingBar.rating,
                feedback = userFeedbackDialogEditText.text.toString()
            )

            currentDatabase.eventDatabase!!.updateRating(
                ratingCopy
            ).observe(this) {
               showSuccessToastAndDismiss(it.value)
            }
        }
    }

    /**
     * A listener to save a new rating in the database.
     */
    private fun onClickAdd() {
        if (!rated) {
            // Check if user has rated, to avoid storing a rating with zero stars.
            HelperFunctions.showToast(getString(R.string.event_review_leave_rating_warning),
                context
            )
        } else {
            val rating = Rating(
                rate = leaveReviewDialogRatingBar.rating,
                feedback = userFeedbackDialogEditText.text.toString(),
                eventId = eventId,
                userId = currentDatabase.currentUser!!.uid
            )
            currentDatabase.eventDatabase!!.addRatingToEvent(
                rating
            ).observe(this) {
                showSuccessToastAndDismiss(it.value)
            }
        }
    }

    /**
     * Show a success or fail toast on the result of saving the rating to the database
     * @param resultStatus true if the rating was saved or updated successfully
     */
    private fun showSuccessToastAndDismiss(resultStatus: Boolean) {
        if (resultStatus) {
            HelperFunctions.showToast(getString(R.string.event_review_saved), context)
            reviewHasChanged.onLeaveReview()
        } else {
            HelperFunctions.showToast(getString(R.string.event_review_failed), context)
        }
        dismiss()
    }

    companion object {
        const val TAG = "LeaveEventReviewDialog"
    }
}