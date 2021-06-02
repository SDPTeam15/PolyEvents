package com.github.sdpteam15.polyevents.view.adapter

import android.content.Context
import android.widget.ArrayAdapter
import android.widget.Filter

/**
 * Custom adapter to display the list of profile rank into an AutoCompleteTextView
 * @param context The context in which the adapter will be used
 * @param resource The resource for which the adapter will be used
 * @param items The list of items in the adapter
 */
class ProfileRankListAdapter(context: Context, resource: Int, items: Array<String>) :
    ArrayAdapter<String>(context, resource, items) {
    override fun getFilter(): Filter {
        return CustomFilter()
    }
}

/**
 * Custom filter class that do not perform any filtering
 */
private class CustomFilter : Filter() {
    override fun performFiltering(constraint: CharSequence?): FilterResults? {
        return null
    }

    override fun publishResults(constraint: CharSequence?, results: FilterResults?) {}
}