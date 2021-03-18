package com.github.sdpteam15.polyevents.database.observe

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.github.sdpteam15.polyevents.helper.HelperFunctions.observeOnStop
import com.github.sdpteam15.polyevents.helper.HelperFunctions.run
import java.util.*

class ObservableList<T> {
    private val observersAdd = mutableSetOf<(T?) -> Unit>()
    private val observersRemove = mutableSetOf<(T?) -> Unit>()
    private val observersClear = mutableSetOf<() -> Unit>()
    private val observersItemUpdate = mutableSetOf<(T?, Int) -> Unit>()
    private val observers = mutableSetOf<(List<T?>) -> Unit>()


    private val values: MutableList<Observable<T>> = mutableListOf()
    private val removeItemObserver: MutableMap<Observable<T>, () -> Boolean> = mutableMapOf()

    val size get() = values.size

    /**
     * To list of T
     */
    val value: List<T?>
        get() {
            val v: MutableList<T?> = mutableListOf()
            for (observableValue in values)
                v.add(observableValue.value)
            return v
        }

    /**
     * Returns the element at the specified index in the list.
     * @return the element at the specified index in the list.
     */
    operator fun get(index: Int): T? = values[index].value

    /**
     * Returns the observable element at the specified index in the list.
     * @return the observable element at the specified index in the list.
     */
    fun getObservable(index: Int): Observable<T> = values[index]

    /**
     * Replaces the element at the specified position in this list with the specified element.
     * @param index position in this list
     * @param value new value
     */
    operator fun set(index: Int, value: T?) = values[index].postValue(value)

    /**
     * Replaces the element at the specified position in this list with the specified element.
     * @param index position in this list
     * @param value new value
     */
    operator fun set(index: Int, value: Observable<T>) {
        removeItemObserver[values[index]]!!()
        removeItemObserver.remove(values[index])
        values[index] = value
        removeItemObserver[value] = value.observe { itemUpdated(it, index) }
        itemUpdated(value.value, index)
    }

    /**
     * Add an observable.
     * @param observable observable to add.
     * @return observable added.
     */
    fun add(observable: Observable<T>): Observable<T> {
        values.add(observable)
        removeItemObserver[observable] =
            observable.observe { itemUpdated(it, values.indexOf(observable)) }
        itemAdded(observable.value)
        return observable
    }

    /**
     * Add an item.
     * @param item item to add.
     * @return observable added.
     */
    fun add(item: T): Observable<T> = add(Observable(item))

    /**
     * Remove an item.
     * @param item item to remove.
     * @return observable removed.
     */
    fun remove(observable: Observable<T>): Observable<T>? {
        if (!values.remove(observable))
            return null
        removeItemObserver[observable]!!()
        removeItemObserver.remove(observable)
        itemRemoved(observable.value)
        return observable
    }

    /**
     * Remove an item.
     * @param item item to remove.
     * @return observable removed.
     */
    fun remove(item: T): Observable<T>? {
        val observable: Observable<T>? = values.find { it.value == item }
        if (observable != null)
            return remove(observable)
        return null
    }

    /**
     * Clear all items.
     */
    fun clear() {
        for (item in values)
            removeItemObserver[item]!!()
        values.clear()
        removeItemObserver.clear()
        itemClear()
    }

    /**
     *  Add an observer for the live data additions
     *  @param observer observer for the live data additions
     *  @return a method to remove the observer
     */
    fun observeAdd(observer: (T?) -> Unit): () -> Boolean {
        observersAdd.add(observer)
        return { observersAdd.remove(observer) }
    }

    /**
     *  Add an observer for the live data additions
     *  @param observer lifecycle of the observer to automatically remove it from the observers when stopped
     *  @return a method to remove the observer
     */
    fun observeAdd(lifecycle: LifecycleOwner, observer: (T?) -> Unit): () -> Boolean =
        observeOnStop(lifecycle, observeAdd(observer))

    /**
     *  Add an observer for the live data removals
     *  @param observer observer for the live data removals
     *  @return a method to remove the observer
     */
    fun observeRemove(observer: (T?) -> Unit): () -> Boolean {
        observersRemove.add(observer)
        return { observersRemove.remove(observer) }
    }

    /**
     *  Add an observer for the live data removals
     *  @param observer lifecycle of the observer to automatically remove it from the observers when stopped
     *  @return a method to remove the observer
     */
    fun observeRemove(lifecycle: LifecycleOwner, observer: (T?) -> Unit): () -> Boolean =
        observeOnStop(lifecycle, observeRemove(observer))

    /**
     *  Add an observer for the live data clearing
     *  @param observer observer for the live data clearing
     *  @return a method to remove the observer
     */
    fun observeClear(observer: () -> Unit): () -> Boolean {
        observersClear.add(observer)
        return { observersClear.remove(observer) }
    }

    /**
     *  Add an observer for the live data clearing
     *  @param observer lifecycle of the observer to automatically remove it from the observers when stopped
     *  @return a method to remove the observer
     */
    fun observeClear(lifecycle: LifecycleOwner, observer: () -> Unit): () -> Boolean =
        observeOnStop(lifecycle, observeClear(observer))

    /**
     *  Add an observer for the live data clearing
     *  @param observer observer for the live data clearing
     *  @return a method to remove the observer
     */
    fun observeUpdate(observer: (T?, Int) -> Unit): () -> Boolean {
        observersItemUpdate.add(observer)
        return { observersItemUpdate.remove(observer) }
    }

    /**
     *  Add an observer for the live data clearing
     *  @param observer lifecycle of the observer to automatically remove it from the observers when stopped
     *  @return a method to remove the observer
     */
    fun observeUpdate(lifecycle: LifecycleOwner, observer: (T?, Int) -> Unit): () -> Boolean =
        observeOnStop(lifecycle, observeUpdate(observer))

    /**
     *  Add an observer for the live data
     *  @param observer observer for the live data
     *  @return a method to remove the observer
     */
    fun observe(observer: (List<T?>) -> Unit): () -> Boolean {
        observers.add(observer)
        return { observers.remove(observer) }
    }

    /**
     *  Add an observer for the live data
     *  @param observer lifecycle of the observer to automatically remove it from the observers when stopped
     *  @return a method to remove the observer
     */
    fun observe(lifecycle: LifecycleOwner, observer: (List<T?>) -> Unit): () -> Boolean =
        observeOnStop(lifecycle, observe(observer))

    private fun itemRemoved(value: T?) {
        run(Runnable {
            for (obs in observersRemove)
                obs(value);
            notifyUpdate()
        })
    }

    private fun itemAdded(value: T?) {
        run(Runnable {
            for (obs in observersAdd)
                obs(value);
            notifyUpdate()
        })
    }

    private fun itemUpdated(value: T?, index: Int) {
        run(Runnable {
            for (obs in observersItemUpdate)
                obs(value, index);
            notifyUpdate()
        })
    }

    private fun itemClear() {
        run(Runnable {
            for (obs in observersClear)
                obs();
            notifyUpdate()
        })
    }

    private fun notifyUpdate() {
        if (observers.isNotEmpty()) {
            val valueList = this.value
            for (obs in observers)
                obs(valueList)
        }
    }
}