package com.github.sdpteam15.polyevents.view.adapter

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.github.sdpteam15.polyevents.R
import com.github.sdpteam15.polyevents.helper.HelperFunctions.showToast
import com.github.sdpteam15.polyevents.model.entity.Item
import com.github.sdpteam15.polyevents.model.observable.ObservableMap

/**
 * Recycler Adapter for the list of items
 * Shows each item with its available quantity
 * @param context context of parent view used to inflate new views
 * @param lifecycleOwner parent to enable observables to stop observing when the lifecycle is closed
 * @param availableItems Map of each available item grouped by types
 * @param mapSelectedItems Item counts for each item
 */
class ItemRequestAdapter(
    context: Context,
    lifecycleOwner: LifecycleOwner,
    private val availableItems: ObservableMap<String, ObservableMap<Item, Int>>,
    private val mapSelectedItems: ObservableMap<Item, Int>
) : RecyclerView.Adapter<ItemRequestAdapter.CustomViewHolder<*>>() {
    //private var isCategoryOpen = availableItems.keys(lifecycleOwner).then.groupOnce(lifecycleOwner) { it }.then.mapOnce(lifecycleOwner) { false }.then
    private var isCategoryOpen = mutableMapOf<String, Boolean>()
    private val inflater = LayoutInflater.from(context)

    init {
        //itemTypes.map(lifecycleOwner,isCategoryOpen) { Pair(it,false) }.then
        availableItems.observe(lifecycleOwner) {
            for (k in it.value.keys) {
                if (k !in isCategoryOpen) {
                    isCategoryOpen[k] = false
                }
            }
            notifyDataSetChanged()
        }
    }

    // Listener that update the map of selected items when the quantity is changed
    private val onItemQuantityChangeListener = { item: Item, newQuantity: Int ->
        when {
            mapSelectedItems.containsKey(item) and (newQuantity == 0) -> {
                mapSelectedItems.remove(item)
            }
            newQuantity > 0 -> {
                mapSelectedItems[item] = newQuantity
            }
        }
        Unit
    }

    abstract inner class CustomViewHolder<T>(private val view: View) :
        RecyclerView.ViewHolder(view) {
        abstract fun bind(item: T)
        abstract fun unbind()
    }

    /**
     * Adapted ViewHolder for each item type
     * Takes the corresponding item type "tab" view
     */
    inner class ItemTypeViewHolder(private val view: View) :
        CustomViewHolder<String>(view) {
        private val itemCategory = view.findViewById<TextView>(R.id.id_item_category)
        override fun bind(item: String) {
            itemCategory.text = item
            view.setOnClickListener {
                isCategoryOpen[item] = !isCategoryOpen[item]!!
                notifyDataSetChanged()
            }
        }

        override fun unbind() {
            //do nothing
        }

    }

    /**
     * Adapted ViewHolder for each item
     * Takes the corresponding item "tab" view
     */
    inner class ItemViewHolder(private val view: View) :
        CustomViewHolder<Pair<Item, Int>>(view) {

        private val itemName = view.findViewById<TextView>(R.id.id_item_name)
        private val itemQuantity = view.findViewById<EditText>(R.id.id_item_quantity)
        private lateinit var item: Pair<Item, Int>

        private val quantityTextWatcher = object : TextWatcher {
            private fun setNegativeQuantityToZero() {
                // Value set it negative, change it to 0
                // and inform the user
                itemQuantity.setText("0")
                showToast(
                    view.context.getString(R.string.item_quantity_positive_text),
                    view.context
                )
            }

            private fun lowerQuantityToMax(item: Pair<Item, Int>) {
                // The quantity set is too high, set it to the max quantity
                // available and inform the user
                val maxQuantity = item.second
                itemQuantity.setText(maxQuantity.toString())

                // Update the list with the max quantity available
                onItemQuantityChangeListener(item.first, maxQuantity)

                showToast(
                    view.context.getString(
                        R.string.max_item_quantity_text,
                        item.second.toString(), item.first.itemName
                    ), view.context
                )
            }

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
                } else {
                    mapSelectedItems.remove(item.first)
                }
            }
        }

        /**
         * Binds the value of the item to the layout of the item tab
         */
        override fun bind(item: Pair<Item, Int>) {
            this.item = item
            itemName.text =
                view.context.getString(
                    R.string.item_name_quantity_text,
                    item.first.itemName,
                    item.second
                )
            itemQuantity.setText(mapSelectedItems[item.first]?.toString() ?: "")
            itemQuantity.addTextChangedListener(quantityTextWatcher)
        }

        override fun unbind() {
            itemQuantity.removeTextChangedListener(quantityTextWatcher)
        }
    }


    override fun getItemCount(): Int {
        var count = 0
        for (isOpen in isCategoryOpen.entries) {
            count += 1 + (if (!isOpen.value) 0 else availableItems[isOpen.key]?.size ?: 0)
        }
        return count
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder<*> {
        return when (viewType) {
            ITEM_TYPE_HOLDER -> {
                val view = inflater.inflate(R.layout.card_material_item_category, parent, false)
                ItemTypeViewHolder(view)
            }
            ITEM_HOLDER -> {
                val view = inflater.inflate(R.layout.card_item, parent, false)
                ItemViewHolder(view)
            }
            else -> throw IllegalArgumentException("wrong itemType $viewType")
        }
    }

    override fun onViewRecycled(holder: CustomViewHolder<*>) {
        super.onViewRecycled(holder)
        holder.unbind()
    }

    override fun onBindViewHolder(holder: CustomViewHolder<*>, position: Int) {

        when (holder) {

            is ItemTypeViewHolder -> {
                var res = 0
                for (itemType in availableItems.keys) {
                    if (res++ == position) {
                        holder.bind(itemType)
                        return
                    }

                    if (isCategoryOpen[itemType] == true) {
                        for (item in availableItems[itemType]?.keys ?: listOf()) {
                            res++
                        }
                    }
                }
            }
            is ItemViewHolder -> {
                var res = 0
                for (itemType in availableItems.keys) {
                    res++
                    if (isCategoryOpen[itemType] == true) {
                        for (item in availableItems[itemType]?.keys ?: listOf()) {
                            if (res++ == position) {
                                holder.bind(Pair(item, availableItems[itemType]!![item]!!))
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
        for (itemType in availableItems.keys) {
            if (res++ == position) {
                return ITEM_TYPE_HOLDER
            }
            if (isCategoryOpen[itemType] == true) {
                for (item in availableItems[itemType]?.keys ?: listOf()) {
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
