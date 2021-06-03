package com.github.sdpteam15.polyevents.view.fragments.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.DialogFragment
import com.github.sdpteam15.polyevents.R
import com.github.sdpteam15.polyevents.helper.HelperFunctions
import com.github.sdpteam15.polyevents.model.entity.Event


/**
 * Fragment that will show the difference between an event edit request and the original event
 * @param modifyEvent The proposed modification
 * @param creation True if it is an event creation, false otherwise
 * @param origEvents The original event or null if the request is an event creation
 */
class EventEditDifferenceFragment(
    private val modifyEvent: Event,
    private val creation: Boolean,
    private val origEvents: Event?
) : DialogFragment(R.layout.fragment_event_edit_difference) {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_event_edit_difference, container, false)
        setupFieldContents(view)
        setupFieldVisibility(view)
        return view
    }

    override fun onResume() {
        super.onResume()
        val width = ConstraintLayout.LayoutParams.MATCH_PARENT
        val height = ConstraintLayout.LayoutParams.WRAP_CONTENT
        dialog!!.window!!.setLayout(width, height)
    }

    /**
     * Method that will hide the textview related to the original event if the current request is event creation
     */
    private fun setupFieldVisibility(view: View) {
        if (creation) {
            view.findViewById<TextView>(R.id.tvOrigZone).visibility = View.INVISIBLE
            view.findViewById<TextView>(R.id.tvOrigDesc).visibility = View.INVISIBLE
            view.findViewById<TextView>(R.id.tvOrigEndDate).visibility = View.INVISIBLE
            view.findViewById<TextView>(R.id.tvOrigStartDate).visibility = View.INVISIBLE
            view.findViewById<TextView>(R.id.tvOrigLimited).visibility = View.INVISIBLE
            view.findViewById<TextView>(R.id.tvOrigName).visibility = View.INVISIBLE
            view.findViewById<TextView>(R.id.tvOrigTitle).visibility = View.INVISIBLE
            view.findViewById<TextView>(R.id.tvModTitle).text =
                getString(R.string.edit_fragment_creation_title)
        }
    }

    /**
     * Setup the content of the fields with events given in parameters
     */
    private fun setupFieldContents(view: View) {
        view.findViewById<TextView>(R.id.tvModZone).text = modifyEvent.zoneName
        view.findViewById<TextView>(R.id.tvModDesc).text = modifyEvent.description
        view.findViewById<TextView>(R.id.tvModEndDate).text =
            HelperFunctions.localDatetimeToString(modifyEvent.endTime)
        view.findViewById<TextView>(R.id.tvModStartDate).text =
            HelperFunctions.localDatetimeToString(modifyEvent.startTime)
        if (modifyEvent.isLimitedEvent()) {
            view.findViewById<TextView>(R.id.tvModLimited).text = getString(
                R.string.fragment_edit_limited_text,
                modifyEvent.getMaxNumberOfSlots().toString()
            )
        } else {
            view.findViewById<TextView>(R.id.tvModLimited).text =
                getString(R.string.fragment_edit_no_text)
        }

        view.findViewById<TextView>(R.id.tvModName).text = modifyEvent.eventName
        view.findViewById<Button>(R.id.btnCloseFragment).setOnClickListener {
            dismiss()
        }

        if (!creation) {
            view.findViewById<TextView>(R.id.tvOrigZone).text = origEvents!!.zoneName
            view.findViewById<TextView>(R.id.tvOrigDesc).text = origEvents.description
            view.findViewById<TextView>(R.id.tvOrigEndDate).text =
                HelperFunctions.localDatetimeToString(origEvents.endTime)
            view.findViewById<TextView>(R.id.tvOrigStartDate).text =
                HelperFunctions.localDatetimeToString(origEvents.startTime)
            if (origEvents.isLimitedEvent()) {
                view.findViewById<TextView>(R.id.tvOrigLimited).text = getString(
                    R.string.fragment_edit_limited_text,
                    origEvents.getMaxNumberOfSlots().toString()
                )
            } else {
                view.findViewById<TextView>(R.id.tvOrigLimited).text =
                    getString(R.string.fragment_edit_no_text)
            }

            view.findViewById<TextView>(R.id.tvOrigName).text = origEvents.eventName
        }
    }

    companion object {
        const val TAG = "EventEditDifferenceFragment"
    }
}