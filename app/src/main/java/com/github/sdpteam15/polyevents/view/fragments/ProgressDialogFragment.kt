package com.github.sdpteam15.polyevents.view.fragments

import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.github.sdpteam15.polyevents.R
import com.github.sdpteam15.polyevents.model.observable.Observable

class ProgressDialogFragment(val observable: Observable<Boolean>? = null):
    DialogFragment(R.layout.fragment_progress_dialog) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        observable?.observeOnce(this) {
            dismiss()
        }
    }

    companion object {
        const val TAG = "ProgressDialogFragment"
    }
}