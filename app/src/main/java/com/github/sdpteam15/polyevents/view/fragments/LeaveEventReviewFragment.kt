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

class LeaveEventReviewFragment: DialogFragment(R.layout.fragment_leave_review) {

    private lateinit var leaveReviewDialogConfirmButton: Button
    private lateinit var userFeedbackDialogEditText: EditText
    private lateinit var leaveReviewDialogRatingBar: RatingBar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        leaveReviewDialogConfirmButton = view!!.findViewById(R.id.leave_review_fragment_save_button)
        userFeedbackDialogEditText = view.findViewById(R.id.leave_review_fragment_feedback_text)
        leaveReviewDialogRatingBar = view.findViewById(R.id.leave_review_fragment_rating)
        leaveReviewDialogConfirmButton.setOnClickListener {
            // TODO: Save review
            Log.d(TAG, "Ok left review")
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

    companion object {
        const val TAG = "LeaveEventReviewDialog"
    }
}