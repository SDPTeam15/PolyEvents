package com.github.sdpteam15.polyevents.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
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
    private val itemTypes : ObservableList<String>,
    val availableItems: Map<String, MutableList<Pair<Item, Int>>>,
    private val onItemQuantityChangeListener: (Item, Int) -> Unit
) : RecyclerView.Adapter<ItemRequestAdapter.ItemViewHolder>() {
    private var isCategoryOpen : MutableMap<String,Boolean> = mutableMapOf()
    init {
        for (itemType in itemTypes){
            isCategoryOpen[itemType] = false
        }
    }

    /**
     * Adapted ViewHolder for each item
     * Takes the corresponding item "tab" view
     */
    inner class ItemViewHolder(private val view: View, private val parent: ViewGroup) : RecyclerView.ViewHolder(view) {
/*
        private val itemName = view.findViewById<TextView>(R.id.id_item_name)
        private val itemQuantity = view.findViewById<EditText>(R.id.id_item_quantity)
*/


        /**
         * Binds the value of the item to the layout of the item tab
         */
        fun bind(itemType: String) {
            val items = mutableListOf<Pair<Item,Int>>()


            /*itemName.text =
                view.context.getString(
                    R.string.item_name_quantity_text,
                    item.first.itemName,
                    item.second
                )

            // Set initial quantity to 0
            itemQuantity.setText("0")
            itemQuantity.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {/* Do nothing */
                }

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {/* Do nothing*/
                }

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
            })*/
        }

        private fun setNegativeQuantityToZero() {
            // Value set it negative, change it to 0
            // and inform the user
            /*itemQuantity.setText("0")*/

            showToast(
                view.context.getString(R.string.item_quantity_positive_text),
                view.context
            )
        }

        private fun lowerQuantityToMax(item: Pair<Item, Int>) {
            // The quantity set is too high, set it to the max quantity
            // available and inform the user
            val maxQuantity = item.second
            /*itemQuantity.setText(maxQuantity.toString())
*/
            // Update the list with the max quantity available
            onItemQuantityChangeListener(item.first, maxQuantity)

            showToast(
                view.context.getString(
                    R.string.max_item_quantity_text,
                    item.second.toString(), item.first.itemName
                ), view.context
            )
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_material_item_category, parent, false)
        return ItemViewHolder(adapterLayout,parent)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val itemType = itemTypes[position]
        holder.bind(itemType)
    }

    override fun getItemCount(): Int {
        var count = 0
        for (isOpen in isCategoryOpen.entries){
            count += 1 + (if (isOpen.value) 0 else availableItems[isOpen.key]?.size ?: 0)
        }
        return count
    }
}