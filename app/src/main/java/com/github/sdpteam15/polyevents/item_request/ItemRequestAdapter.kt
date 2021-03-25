package com.github.sdpteam15.polyevents.item_request

import android.content.ClipData
import android.os.Build
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.github.sdpteam15.polyevents.R
import com.github.sdpteam15.polyevents.database.observe.ObservableList
import com.github.sdpteam15.polyevents.helper.HelperFunctions.showToast
import com.github.sdpteam15.polyevents.model.Item

/**
 * Adapts items to RecyclerView's ItemViewHolders
 * Takes :
 * - The list of available items to adapt
 * - A listener that will be triggered on click on a checkbox of an item view holder
 */
class ItemRequestAdapter(
    private val availableItems: ObservableList<Item>,
    private val onItemQuantityChangeListener: (String, Int) -> Unit
) : RecyclerView.Adapter<ItemRequestAdapter.ItemViewHolder>() {

    /**
     * Adapted ViewHolder for each item
     * Takes the corresponding item "tab" view
     */
    inner class ItemViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        private val itemName = view.findViewById<TextView>(R.id.id_item_name)
        private val itemQuantity = view.findViewById<EditText>(R.id.id_item_quantity)

        /**
         * Binds the value of the item to the layout of the item tab
         */
        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(item: Pair<String, Int>) {
            itemName.text =
                view.context.getString(R.string.item_name_quantity_text, item.first, item.second)

            // Set initial quantity to 0
            itemQuantity.setText("0")
            itemQuantity.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {/* Do nothing */}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {/* Do nothing*/}

                override fun afterTextChanged(s: Editable?) {
                    if (s.toString().isNotEmpty()) {
                        val value = s.toString().toInt()
                        when {
                            value < 0 -> setNegativeQuantityToZero()
                            value > item.second -> lowerQuantityToMax(item)
                            else -> {
                                // Update the list
                                onItemQuantityChangeListener(item.first, value)
                            }
                        }
                    }
                }
            })
        }

        private fun setNegativeQuantityToZero() {
            // Value set it negative, change it to 0
            // and inform the user
            itemQuantity.setText("0")

            showToast(view.context.getString(R.string.item_quantity_positive_text),
                view.context)
        }

        private fun lowerQuantityToMax(item: Pair<String, Int>) {
            // The quantity set is too high, set it to the max quantity
            // available and inform the user
            val maxQuantity = item.second
            itemQuantity.setText(maxQuantity.toString())

            // Update the list with the max quantity available
            onItemQuantityChangeListener(item.first, maxQuantity)

            showToast(view.context.getString(R.string.max_item_quantity_text,
                item.second.toString(), item.first), view.context)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.tab_item, parent, false)
        return ItemViewHolder(adapterLayout)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = availableItems[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return availableItems.size
    }
}