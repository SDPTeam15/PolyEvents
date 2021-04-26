package com.github.sdpteam15.polyevents.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
    context: Context,
    private val itemTypes: ObservableList<String>,
    val availableItems: Map<String, MutableList<Pair<Item, Int>>>,
    private val onItemQuantityChangeListener: (Item, Int) -> Unit
) : RecyclerView.Adapter<ItemRequestAdapter.CustomViewHolder<*>>() {
    private var isCategoryOpen: MutableMap<String, Boolean> = mutableMapOf()
    private val inflater = LayoutInflater.from(context)


    init {
        for (itemType in itemTypes) {
            isCategoryOpen[itemType] = false
        }
    }


    abstract inner class CustomViewHolder<T>(
        private val view: View,
        private val parent: ViewGroup
    ) : RecyclerView.ViewHolder(view) {
        abstract fun bind(item: T)
    }

    /**
     * Adapted ViewHolder for each item type
     * Takes the corresponding item type "tab" view
     */
    inner class ItemTypeViewHolder(private val view: View, private val parent: ViewGroup) :
        CustomViewHolder<String>(view, parent) {
        override fun bind(item: String) {

        }
    }

    /**
     * Adapted ViewHolder for each item
     * Takes the corresponding item "tab" view
     */
    inner class ItemViewHolder(private val view: View, private val parent: ViewGroup) :
        CustomViewHolder<Pair<Item,Int>>(view, parent) {
/*
        private val itemName = view.findViewById<TextView>(R.id.id_item_name)
        private val itemQuantity = view.findViewById<EditText>(R.id.id_item_quantity)
*/


        /**
         * Binds the value of the item to the layout of the item tab
         */
        override fun bind(item: Pair<Item,Int>) {
            val items = mutableListOf<Pair<Item, Int>>()


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
    //val adapterLayout = LayoutInflater.from(parent.context)
    //            .inflate(R.layout.card_material_item_category, parent, false)
    //        return ItemViewHolder(adapterLayout,parent)


    override fun getItemCount(): Int {
        var count = 0
        for (isOpen in isCategoryOpen.entries) {
            count += 1 + (if (isOpen.value) 0 else availableItems[isOpen.key]?.size ?: 0)
        }
        println("found $count items in $isCategoryOpen $availableItems")
        return count
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder<*> {
        return when (viewType) {
            ITEM_TYPE_HOLDER -> {
                val view = inflater.inflate(R.layout.card_material_item_category, parent, false)
                ItemTypeViewHolder(view, parent)
            }
            ITEM_HOLDER ->{
                val view = inflater.inflate(R.layout.card_material_item, parent, false)
                ItemViewHolder(view, parent)
            }
            else -> throw IllegalArgumentException("wrong itemtype $viewType")
        }
    }

    override fun onBindViewHolder(holder: CustomViewHolder<*>, position: Int) {

        when (holder) {

            is ItemTypeViewHolder -> {
                var res = 0
                for (itemType in itemTypes) {
                    if (res++ == position) {
                        holder.bind(itemType)
                        return
                    }
                    if (isCategoryOpen[itemType] == true) {
                        for (item in availableItems[itemType] ?: listOf()) {
                            res++
                        }
                    }
                }
            }
            is ItemViewHolder -> {
                var res = 0
                for (itemType in itemTypes) {
                    res++
                    if (isCategoryOpen[itemType] == true) {
                        for (item in availableItems[itemType] ?: listOf()) {
                            if(res++ == position){
                                holder.bind(item)
                                return
                            }
                        }
                    }
                }
            }
            else -> throw java.lang.IllegalArgumentException("invalid position")

        }
    }

    override fun getItemViewType(position: Int): Int {
        var res = 0
        println(position)
        for (itemType in itemTypes) {
            println(itemType)
            if (res++ == position) {
                return ITEM_TYPE_HOLDER
            }
            if (isCategoryOpen[itemType] == true) {
                for (item in availableItems[itemType] ?: listOf()) {
                    println(item)
                    if (res++ == position) {
                        return ITEM_HOLDER
                    }
                }
            }
        }
        //should never happen
        return -1
    }

    companion object {
        private const val ITEM_TYPE_HOLDER = 0
        private const val ITEM_HOLDER = 1
    }
}
