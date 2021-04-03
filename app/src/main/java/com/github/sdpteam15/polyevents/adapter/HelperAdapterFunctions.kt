package com.github.sdpteam15.polyevents.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.sdpteam15.polyevents.R

object HelperAdapterFunctions {
    /**
     * Helper function to return the adapter layout from parent.
     * (Needed for refactoring purposes)
     * @param parent the parent layout
     * @param layout the layout to inflate
     *
     * @return the inflated view
     */
    fun createAdapterLayout(parent: ViewGroup, layout: Int): View {
        return LayoutInflater.from(parent.context)
            .inflate(layout, parent, false)
    }
}