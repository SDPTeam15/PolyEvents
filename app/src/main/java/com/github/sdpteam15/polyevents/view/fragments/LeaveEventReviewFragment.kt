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
            if (!rated) {
                // Check if user has rated, to avoid storing a rating with zero stars.
                HelperFunctions.showToast(getString(R.string.event_review_leave_rating_warning),
                    activity?.applicationContext
                )
            } else {
                Log.d(TAG, "Leaving a review")
                // TODO: Get current rating
                val rating = Rating(
                    rate = leaveReviewDialogRatingBar.rating,
                    feedback = userFeedbackDialogEditText.text.toString(),
                    eventId = eventId,
                    userId = currentDatabase.currentUser!!.uid
                )
                currentDatabase.eventDatabase?.addRatingToEvent(
                    rating
                )
                // TODO: show toast to confirm
                dismiss()
            }
        }

        /*val ratingObservable = Observable<Rating>()
        Log.d(TAG, "Here!")
        ratingObservable.observe(this) {
            Log.d(TAG, "Retrieved Rating for $eventId and ${currentDatabase.currentUser!!.uid}!")
            val rating = it.value
            leaveReviewDialogRatingBar.rating = rating.rate!!
            if (rating.feedback != null) {
                if (rating.feedback.isEmpty()) {
                    userFeedbackDialogEditText.setText("")
                } else {
                    userFeedbackDialogEditText.setText(rating.feedback)
                }
            }
        }

        Log.d(TAG, "Retrieving rating for $eventId and ${currentDatabase.currentUser!!.uid}!")
        val obs = currentDatabase.eventDatabase!!.getUserRatingFromEvent(
            userId = currentDatabase.currentUser!!.uid,
            eventId = eventId!!,
            returnedRating = ratingObservable,
            userAccess = null
        )

        obs.observe(this) {
            if(it.value) {
                Log.d(TAG, "Managed to retrieve rating for ${eventId} and ${currentDatabase.currentUser!!.uid}!")
            } else {
                Log.d(TAG, "Failed to retrieve rating for ${eventId} and ${currentDatabase.currentUser!!.uid}!")
            }
        }*/

        return view
    }

    override fun onResume() {
        super.onResume()
        // Uncomment the following lines to manually set the height and the width of the DialogFragment
        /*val width = ConstraintLayout.LayoutParams.MATCH_PARENT
        val height = ConstraintLayout.LayoutParams.MATCH_PARENT
        dialog!!.window!!.setLayout(width, height)*/
    }

    companion object {
        const val TAG = "LeaveEventReviewDialog"
    }
}