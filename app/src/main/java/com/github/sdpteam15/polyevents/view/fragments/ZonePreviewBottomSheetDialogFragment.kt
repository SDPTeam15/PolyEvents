package com.github.sdpteam15.polyevents.view.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import com.github.sdpteam15.polyevents.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

/**
 * Documentation on: https://developer.android.com/guide/topics/ui/dialogs.html#kotlin
 */
class ZonePreviewBottomSheetDialogFragment: BottomSheetDialogFragment() {
    /*override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }*/

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            // Get the layout inflater
            val inflater = requireActivity().layoutInflater

            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            builder.setView(inflater.inflate(R.layout.fragment_preview_zone_events_bottom_sheet, null))
                // Add action buttons
                .setPositiveButton("GO",
                    DialogInterface.OnClickListener { dialog, id ->
                        // sign in the user ...
                    })
                .setNegativeButton("NO",
                    DialogInterface.OnClickListener { dialog, id ->
                        getDialog()?.cancel()
                    })
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}