package com.github.sdpteam15.polyevents.database.observe

import android.annotation.SuppressLint
import androidx.arch.core.executor.ArchTaskExecutor
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import java.util.*

class ObservableList<T> {
    private val observersAdd = ArrayList<(T?) -> Unit>()
    private val observersRemove = ArrayList<(T?) -> Unit>()
    private val observersClear = ArrayList<() -> Unit>()
    private val observersItemUpdate = ArrayList<(T?, Int) -> Unit>()
    private val observers = ArrayList<(List<T?>) -> Unit>()


    private val values: MutableList<Observable<T>> = mutableListOf()
    private val removeItemObserver: MutableMap<Observable<T>, () -> Boolean> = mutableMapOf()

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
    operator fun get(index: Int) : T? = values[index].value

    /**
     * Replaces the element at the specified position in this list with the specified element.
     * @param index position in this list
     * @param value new value
     */
    operator fun set(index: Int, value: T?) = values[index].postValue(value)

    /**
     * Add an observable.
     * @param observable observable to add.
     * @return observable added.
     */
    fun add(observable: Observable<T>) : Observable<T>{
        removeItemObserver[observable] = observable.observe { itemUpdated(it, values.indexOf(observable)) }
        itemAdded(observable.value)
        return observable
    }

    /**
     * Add an item.
     * @param item item to add.
     * @return observable added.
     */
    fun add(item: T) : Observable<T> = add(Observable(item))

    /**
     * Remove an item.
     * @param item item to add.
     * @return observable added.
     */
    fun remove(observable: Observable<T>) : Observable<T>?{
        if(!values.remove(observable))
            return null
        removeItemObserver[observable]!!()
        removeItemObserver.remove(observable)
        itemRemoved(observable.value)
        return observable
    }

    /**
     * Clear all items.
     * @param item item to add.
     * @return observable added.
     */
    fun clear() {
        for(item in values)
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
     *  @param observer observer for the live data additions
     *  @return a method to remove the observer
     */
    fun observeAdd(observer: Observer<T?>): () -> Boolean =
        observeAdd { value: T? -> observer.update(value) }

    /**
     *  Add an observer for the live data additions
     *  @param observer lifecycle of the observer to automatically remove it from the observers when stopped
     *  @return a method to remove the observer
     */
    fun observeAdd(lifecycle: LifecycleOwner, observer: (T?) -> Unit) : () -> Boolean{
        val result = observeAdd(observer)

        //Anonymous class to observe the ON_STOP Event ao the Activity/Fragment
        val lifecycleObserver = object : LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
            fun stopListener() = result()
        }

        lifecycle.lifecycle.addObserver(lifecycleObserver)
        return result
    }

    /**
     *  Add an observer for the live data additions
     *  @param observer lifecycle of the observer to automatically remove it from the observers when stopped
     *  @return a method to remove the observer
     */
    fun observeAdd(lifecycle: LifecycleOwner, observer: Observer<T?>) : () -> Boolean =
        observeAdd(lifecycle) { value: T? -> observer.update(value) }

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
     *  @param observer observer for the live data removals
     *  @return a method to remove the observer
     */
    fun observeRemove(observer: Observer<T?>): () -> Boolean =
        observeRemove { value: T? -> observer.update(value) }

    /**
     *  Add an observer for the live data removals
     *  @param observer lifecycle of the observer to automatically remove it from the observers when stopped
     *  @return a method to remove the observer
     */
    fun observeRemove(lifecycle: LifecycleOwner, observer: (T?) -> Unit) : () -> Boolean{
        val result = observeRemove(observer)

        //Anonymous class to observe the ON_STOP Event ao the Activity/Fragment
        val lifecycleObserver = object : LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
            fun stopListener() = result()
        }

        lifecycle.lifecycle.addObserver(lifecycleObserver)
        return result
    }

    /**
     *  Add an observer for the live data removals
     *  @param observer lifecycle of the observer to automatically remove it from the observers when stopped
     *  @return a method to remove the observer
     */
    fun observeRemove(lifecycle: LifecycleOwner, observer: Observer<T?>) : () -> Boolean =
        observeRemove(lifecycle) { value: T? -> observer.update(value) }

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
     *  @param observer observer for the live data clearing
     *  @return a method to remove the observer
     */
    fun observeClear(observer: Observer<Unit>): () -> Boolean =
        observeClear { observer.update(Unit) }

    /**
     *  Add an observer for the live data clearing
     *  @param observer lifecycle of the observer to automatically remove it from the observers when stopped
     *  @return a method to remove the observer
     */
    fun observeClear(lifecycle: LifecycleOwner, observer: () -> Unit) : () -> Boolean{
        val result = observeClear(observer)

        //Anonymous class to observe the ON_STOP Event ao the Activity/Fragment
        val lifecycleObserver = object : LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
            fun stopListener() = result()
        }

        lifecycle.lifecycle.addObserver(lifecycleObserver)
        return result
    }

    /**
     *  Add an observer for the live data
     *  @param observer lifecycle of the observer to automatically remove it from the observers when stopped
     *  @return a method to remove the observer
     */
    fun observeClear(lifecycle: LifecycleOwner, observer: Observer<Unit>) : () -> Boolean =
        observeClear(lifecycle) { observer.update(Unit) }


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
     *  @param observer observer for the live data
     *  @return a method to remove the observer
     */
    fun observe(observer: Observer<List<T?>>): () -> Boolean =
        observe { value: List<T?> -> observer.update(value) }

    /**
     *  Add an observer for the live data
     *  @param observer lifecycle of the observer to automatically remove it from the observers when stopped
     *  @return a method to remove the observer
     */
    fun observe(lifecycle: LifecycleOwner, observer: (List<T?>) -> Unit) : () -> Boolean{
        val result = observe(observer)

        //Anonymous class to observe the ON_STOP Event ao the Activity/Fragment
        val lifecycleObserver = object : LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
            fun stopListener() = result()
        }

        lifecycle.lifecycle.addObserver(lifecycleObserver)
        return result
    }

    /**
     *  Add an observer for the live data
     *  @param observer lifecycle of the observer to automatically remove it from the observers when stopped
     *  @return a method to remove the observer
     */
    fun observe(lifecycle: LifecycleOwner, observer: Observer<List<T?>>) : () -> Boolean =
        observe(lifecycle) { value: List<T?> -> observer.update(value) }




    @SuppressLint("RestrictedApi")
    private fun itemRemoved(value: T?) {
        ArchTaskExecutor.getInstance().postToMainThread(Runnable {
            for (obs in observersRemove)
                obs(value);
            if (observers.isNotEmpty()) {
                val valueList = this.value
                for (obs in observers)
                    obs(valueList)
            }
        })
    }

    @SuppressLint("RestrictedApi")
    private fun itemAdded(value: T?) {
        ArchTaskExecutor.getInstance().postToMainThread(Runnable {
            for (obs in observersAdd)
                obs(value);
            if (observers.isNotEmpty()) {
                val valueList = this.value
                for (obs in observers)
                    obs(valueList)
            }
        })
    }

    @SuppressLint("RestrictedApi")
    private fun itemUpdated(value: T?, index: Int) {
        ArchTaskExecutor.getInstance().postToMainThread(Runnable {
            for (obs in observersItemUpdate)
                obs(value, index);
            if (observers.isNotEmpty()) {
                val valueList = this.value
                for (obs in observers)
                    obs(valueList)
            }
        })
    }

    @SuppressLint("RestrictedApi")
    private fun itemClear() {
        ArchTaskExecutor.getInstance().postToMainThread(Runnable {
            for (obs in observersClear)
                obs();
            if (observers.isNotEmpty()) {
                val valueList = this.value
                for (obs in observers)
                    obs(valueList)
            }
        })
    }
}