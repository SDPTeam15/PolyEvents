package com.github.sdpteam15.polyevents.database.observe

import androidx.lifecycle.LifecycleOwner
import com.github.sdpteam15.polyevents.helper.HelperFunctions.observeOnStop
import com.github.sdpteam15.polyevents.helper.HelperFunctions.run
import java.util.*

class IndexedValue<T> (val value :T, val index: Int)

class ObservableList<T>{
    private val observersAdd = mutableSetOf<(UpdateArgs<T>) -> Unit>()
    private val observersRemove = mutableSetOf<(UpdateArgs<T>) -> Unit>()
    private val observersItemUpdate = mutableSetOf<(UpdateArgs<IndexedValue<T>>) -> Unit>()
    private val observers = mutableSetOf<(UpdateArgs<List<T>>) -> Unit>()

    private val values: MutableList<Observable<T>> = mutableListOf()
    private val removeItemObserver: MutableMap<Observable<T>, () -> Boolean> = mutableMapOf()

    val size get() = values.size

    /**
     * To list of T
     */
    val value: List<T>
        get() {
            val v: MutableList<T> = mutableListOf()
            for (observableValue in values)
                v.add(observableValue.value!!)
            return v
        }

    /**
     * Returns the element at the specified index in the list.
     * @return the element at the specified index in the list.
     */
    operator fun get(index: Int): T = values[index].value!!

    /**
     * Returns the observable element at the specified index in the list.
     * @return the observable element at the specified index in the list.
     */
    fun getObservable(index: Int): Observable<T> = values[index]

    /**
     * Replaces the element at the specified position in this list with the specified element.
     * @param index position in this list
     * @param value new value
     * @param sender The source of the event.
     */
    fun set(index: Int, value: T, sender : Any?) = values[index].postValue(value, sender)

    operator fun set(index: Int, value: T) = set(index, value, null)

    /**
     * Add an observable.
     * @param observable Observable to add.
     * @param sender The source of the event.
     * @return Observable added.
     */
    fun add(observable: Observable<T>, sender : Any? = null): Observable<T>? = add(observable, sender, true)
    private fun add(observable: Observable<T>, sender : Any? = null, notify : Boolean): Observable<T>? {
        if (observable.value != null) {
            values.add(observable)
            removeItemObserver[observable] =
                observable.observe { itemUpdated(UpdateArgs(IndexedValue(it.value, values.indexOf(observable)), it.sender)) }
            itemAdded(UpdateArgs(observable.value!!, sender))
            notifyUpdate(sender)
            return observable
        }
        return null
    }

    /**
     * Add an item.
     * @param item Item to add.
     * @param sender The source of the event.
     * @return Observable added.
     */
    fun add(item: T, sender : Any? = null): Observable<T> = add(Observable(item), sender)!!
    private fun add(item: T, sender : Any? = null, notify : Boolean): Observable<T> = add(Observable(item), sender, notify)!!

    /**
     * Add all items in the list.
     * @param items items list.
     * @param sender The source of the event.
     */
    fun addAll(items: List<T>, sender : Any? = null) {
        for (item: T in items)
            add(item, sender, false)
        notifyUpdate(sender)
    }


    /**
     * Remove an item.
     * @param observable item to remove.
     * @param sender The source of the event.
     * @return observable removed.
     */
    fun remove(observable: Observable<T>, sender : Any? = null): Observable<T>? =
        remove(observable, sender, true)
    private fun remove(observable: Observable<T>, sender : Any? = null, notify : Boolean): Observable<T>? {
        if (!values.remove(observable))
            return null
        removeItemObserver[observable]!!()
        removeItemObserver.remove(observable)
        itemRemoved(UpdateArgs(observable.value!!, sender))
        if(notify)
            notifyUpdate(sender)
        return observable
    }

    /**
     * Remove an item.
     * @param item item to remove.
     * @param sender The source of the event.
     * @return observable removed.
     */
    fun remove(item: T, sender : Any? = null): Observable<T>? {
        val observable: Observable<T>? = values.find { it.value == item }
        if (observable != null)
            return remove(observable, sender)
        return null
    }

    /**
     * Clear all items.
     * @param sender The source of the event.
     */
    fun clear(sender : Any? = null) {
        for (item in values)
            remove(item, sender, false)
        notifyUpdate(sender)
    }

    /**
     *  Add an observer for the live data additions
     *  @param observer observer for the live data additions
     *  @return a method to remove the observer
     */
    fun observeAdd(observer: (UpdateArgs<T>) -> Unit): () -> Boolean {
        observersAdd.add(observer)
        return { observersAdd.remove(observer) }
    }

    /**
     *  Add an observer for the live data additions
     *  @param observer lifecycle of the observer to automatically remove it from the observers when stopped
     *  @return a method to remove the observer
     */
    fun observeAdd(lifecycle: LifecycleOwner, observer: (UpdateArgs<T>) -> Unit): () -> Boolean =
        observeOnStop(lifecycle, observeAdd(observer))

    /**
     *  Add an observer for the live data removals
     *  @param observer observer for the live data removals
     *  @return a method to remove the observer
     */
    fun observeRemove(observer: (UpdateArgs<T>) -> Unit): () -> Boolean {
        observersRemove.add(observer)
        return { observersRemove.remove(observer) }
    }

    /**
     *  Add an observer for the live data removals
     *  @param observer lifecycle of the observer to automatically remove it from the observers when stopped
     *  @return a method to remove the observer
     */
    fun observeRemove(lifecycle: LifecycleOwner, observer: (UpdateArgs<T>) -> Unit): () -> Boolean =
        observeOnStop(lifecycle, observeRemove(observer))

    /**
     *  Add an observer for the live data clearing
     *  @param observer observer for the live data clearing
     *  @return a method to remove the observer
     */
    fun observeUpdate(observer: (UpdateArgs<IndexedValue<T>>) -> Unit): () -> Boolean {
        observersItemUpdate.add(observer)
        return { observersItemUpdate.remove(observer) }
    }

    /**
     *  Add an observer for the live data clearing
     *  @param observer lifecycle of the observer to automatically remove it from the observers when stopped
     *  @return a method to remove the observer
     */
    fun observeUpdate(lifecycle: LifecycleOwner, observer: (UpdateArgs<IndexedValue<T>>) -> Unit): () -> Boolean =
        observeOnStop(lifecycle, observeUpdate(observer))

    /**
     *  Add an observer for the live data
     *  @param observer observer for the live data
     *  @return a method to remove the observer
     */
    fun observe(observer: (UpdateArgs<List<T>>) -> Unit): () -> Boolean {
        observers.add(observer)
        return { observers.remove(observer) }
    }

    /**
     *  Add an observer for the live data
     *  @param observer lifecycle of the observer to automatically remove it from the observers when stopped
     *  @return a method to remove the observer
     */
    fun observe(lifecycle: LifecycleOwner, observer: (UpdateArgs<List<T>>) -> Unit): () -> Boolean =
        observeOnStop(lifecycle, observe(observer))

    private fun itemAdded(value: UpdateArgs<T>) {
        run(Runnable {
            for (obs in observersAdd)
                obs(value)
        })
    }

    private fun itemRemoved(value: UpdateArgs<T>) {
        run(Runnable {
            for (obs in observersRemove)
                obs(value)
        })
    }

    private fun itemUpdated(value: UpdateArgs<IndexedValue<T>>) {
        run(Runnable {
            for (obs in observersItemUpdate)
                obs(value)
            notifyUpdate(value.sender)
        })
    }

    private fun notifyUpdate(sender: Any?) {
        if (observers.isNotEmpty()) {
            val valueList = this.value
            for (obs in observers)
                obs(UpdateArgs(valueList, sender))
        }
    }
}