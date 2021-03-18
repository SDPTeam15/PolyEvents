package com.github.sdpteam15.polyevents.database.observe

import androidx.lifecycle.LifecycleOwner
import com.github.sdpteam15.polyevents.helper.HelperFunctions.observeOnStop
import com.github.sdpteam15.polyevents.helper.HelperFunctions.run
import java.util.*

/**
 * Observable live data of type T
 * @param
 */
class Observable<T>(value: T? = null) {
    constructor() : this(null)

    private val observers = mutableSetOf<(T?) -> Unit>()

    private var tempValue: T? = null

    init {
        tempValue = value
    }

    /**
     * Current value
     */
    var value: T?
        get() = tempValue
        set(value) = postValue(value)

    /**
     *  Add an observer for the live data
     *  @param observer observer for the live data
     *  @return a method to remove the observer
     */
    fun observe(observer: (T?) -> Unit): () -> Boolean {
        observers.add(observer)
        if (value != null)
            observer(value)
        return { observers.remove(observer) }
    }

    /**
     *  Add an observer for the live data
     *  @param observer lifecycle of the observer to automatically remove it from the observers when stopped
     *  @return a method to remove the observer
     */
    fun observe(lifecycle: LifecycleOwner, observer: (T?) -> Unit): () -> Boolean =
        observeOnStop(lifecycle, observe(observer))

    /**
     * Post a new value
     * @param newValue the new value
     */
    fun postValue(newValue: T?) {
        synchronized(this) { tempValue = newValue; }
        run(Runnable {
            for (obs in observers)
                obs(value);
        })
    }
}