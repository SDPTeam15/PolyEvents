package com.github.sdpteam15.polyevents.view.fragments.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.github.sdpteam15.polyevents.R
import com.github.sdpteam15.polyevents.helper.HelperFunctions
import com.github.sdpteam15.polyevents.model.entity.Event

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * create an instance of this fragment.
 */
class EventEditDifferenceFragment(
    val modifEvent: Event,
    val creation: Boolean,
    val origEvents: Event?
) : DialogFragment(R.layout.fragment_event_edit_difference) {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_event_edit_difference, container, false)
        setupFieldContents(view)
        setupFieldVisibility(view)
        return view
    }

    override fun onResume() {
        super.onResume()
        // Uncomment the following lines to manually set the height and the width of the DialogFragment
        val width = ConstraintLayout.LayoutParams.MATCH_PARENT
        val height = ConstraintLayout.LayoutParams.WRAP_CONTENT
        dialog!!.window!!.setLayout(width, height)
    }

    private fun setupFieldVisibility(view: View) {
        if(creation){
            view.findViewById<TextView>(R.id.tvOrigZone).visibility = View.INVISIBLE
            view.findViewById<TextView>(R.id.tvOrigEndDate).visibility = View.INVISIBLE
            view.findViewById<TextView>(R.id.tvOrigStartDate).visibility = View.INVISIBLE
            view.findViewById<TextView>(R.id.tvOrigLimited).visibility = View.INVISIBLE
            view.findViewById<TextView>(R.id.tvOrigName).visibility = View.INVISIBLE
        }
    }

    private fun setupFieldContents(view: View) {
        view.findViewById<TextView>(R.id.tvModZone).text = modifEvent.zoneName
        view.findViewById<TextView>(R.id.tvModDesc).text = modifEvent.description
        view.findViewById<TextView>(R.id.tvModEndDate).text =
            HelperFunctions.localDatetimeToString(modifEvent.endTime)
        view.findViewById<TextView>(R.id.tvModStartDate).text =
            HelperFunctions.localDatetimeToString(modifEvent.startTime)
        if (modifEvent.isLimitedEvent()) {
            view.findViewById<TextView>(R.id.tvModLimited).text =
                "Yes, " + modifEvent.getMaxNumberOfSlots().toString()+"."
        } else {
            view.findViewById<TextView>(R.id.tvModLimited).text = "No."
        }

        view.findViewById<TextView>(R.id.tvModName).text = modifEvent.eventName

        if (!creation) {
            view.findViewById<TextView>(R.id.tvOrigZone).text = origEvents!!.zoneName
            view.findViewById<TextView>(R.id.tvOrigDesc).text = origEvents.description
            view.findViewById<TextView>(R.id.tvOrigEndDate).text =
                HelperFunctions.localDatetimeToString(origEvents.endTime)
            view.findViewById<TextView>(R.id.tvOrigStartDate).text =
                HelperFunctions.localDatetimeToString(origEvents.startTime)
            if (origEvents.isLimitedEvent()) {
                view.findViewById<TextView>(R.id.tvModLimited).text =
                    "Yes, " + modifEvent.getMaxNumberOfSlots().toString()+"."
            } else {
                view.findViewById<TextView>(R.id.tvModLimited).text = "No."
            }

            view.findViewById<TextView>(R.id.tvOrigName).text = origEvents.eventName
        }
    }

    companion object {
        const val TAG = "EventEditDifferenceFragment"
    }
}