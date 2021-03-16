package com.github.sdpteam15.polyevents.item_request

import android.os.Build
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import com.github.sdpteam15.polyevents.R

/**
 * Adapts items to RecyclerView's ItemViewHolders
 * Takes :
 * - The list of available items to adapt
 * - A listener that will be triggered on click on a checkbox of an item view holder
 */
class ItemRequestAdapter(
    private val availableItems: List<Pair<String, Int>>,
    private val onItemCheckChangeListener: (Pair<String, Int>, Boolean) -> Unit,
    private val onItemQuantityChangeListener: (String, Int) -> Unit
) : RecyclerView.Adapter<ItemRequestAdapter.ItemViewHolder>() {

    /**
     * Adapted ViewHolder for each item
     * Takes the corresponding item "tab" view
     */
    inner class ItemViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        private val itemName = view.findViewById<TextView>(R.id.id_item_name)
        private val itemCheckBox = view.findViewById<CheckBox>(R.id.id_item_requested)
        private val itemQuantity = view.findViewById<EditText>(R.id.id_item_quantity)

        /**
         * Binds the value of the item to the layout of the item tab
         */
        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(item: Pair<String, Int>) {
            itemName.text = item.first
            itemCheckBox.setOnClickListener { view ->
                val isChecked = (view as CheckBox).isChecked
                onItemCheckChangeListener(item, isChecked)
            }
            itemQuantity.setText(item.second.toString())
            itemQuantity.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                    // Do nothing
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    // Do nothing
                }

                override fun afterTextChanged(s: Editable?) {
                    if(s.toString().isNotEmpty()) {
                        val value = s.toString().toInt()
                        if (value < 0) {
                            itemQuantity.setText("0")

                            Toast.makeText(view.context, view.context.getString(R.string.item_quantity_positive_text), Toast.LENGTH_LONG)
                                .show()
                        } else if (value > item.second) {
                            val maxQuantity = item.second.toString()
                            itemQuantity.setText(maxQuantity)

                            Toast.makeText(view.context, view.context.getString(R.string.max_item_quantity_text, maxQuantity, itemName.text), Toast.LENGTH_LONG)
                                .show()
                        }
                        // Update the list
                        onItemQuantityChangeListener(item.first, value)
                    }
                }
            })
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