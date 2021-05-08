package com.github.sdpteam15.polyevents.view.fragments

import android.os.Bundle
import android.util.Log
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

/**
 * A Dialog Fragment that is displayed over an EventActivity, to leave a review for the event.
 */
class LeaveEventReviewFragment(val eventId: String?):
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
        leaveReviewDialogConfirmButton = view!!.findViewById(R.id.leave_review_fragment_save_button)
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
            Log.d(TAG, "Retrieved Rating for $eventId and ${currentDatabase.currentUser!!.uid}!")
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

        return view
    }

    override fun onResume() {
        super.onResume()
        // Uncomment the following lines to manually set the height and the width of the DialogFragment
        /*val width = ConstraintLayout.LayoutParams.MATCH_PARENT
        val height = ConstraintLayout.LayoutParams.MATCH_PARENT
        dialog!!.window!!.setLayout(width, height)*/
    }

    private fun onClickUpdate(rating: Rating) {
        if (!rated) {
            // Check if user has rated, to avoid storing a rating with zero stars.
            HelperFunctions.showToast(getString(R.string.event_review_leave_rating_warning),
                context
            )
        } else {
            Log.d(TAG, "Leaving a review")
            val ratingCopy = rating.copy(
                rate = leaveReviewDialogRatingBar.rating,
                feedback = userFeedbackDialogEditText.text.toString()
            )

            currentDatabase.eventDatabase!!.updateRating(
                ratingCopy
            ).observe(this) {
               if (it.value) {
                   HelperFunctions.showToast(getString(R.string.event_review_saved), context)
               } else {
                   HelperFunctions.showToast(getString(R.string.event_review_failed), context)
               }
                dismiss()
            }
        }
    }

    private fun onClickAdd() {
        if (!rated) {
            // Check if user has rated, to avoid storing a rating with zero stars.
            HelperFunctions.showToast(getString(R.string.event_review_leave_rating_warning),
                context
            )
        } else {
            Log.d(TAG, "Leaving a review")
            val rating = Rating(
                rate = leaveReviewDialogRatingBar.rating,
                feedback = userFeedbackDialogEditText.text.toString(),
                eventId = eventId,
                userId = currentDatabase.currentUser!!.uid
            )
            currentDatabase.eventDatabase!!.addRatingToEvent(
                rating
            ).observe(this) {
                if (it.value) {
                    HelperFunctions.showToast(getString(R.string.event_review_saved), context)
                } else {
                    HelperFunctions.showToast(getString(R.string.event_review_failed), context)
                }
                dismiss()
            }
        }
    }

    companion object {
        const val TAG = "LeaveEventReviewDialog"
    }
}