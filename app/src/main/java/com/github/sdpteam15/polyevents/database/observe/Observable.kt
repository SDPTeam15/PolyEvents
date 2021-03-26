package com.github.sdpteam15.polyevents.database.observe

import androidx.lifecycle.LifecycleOwner
import com.github.sdpteam15.polyevents.helper.HelperFunctions.observeOnStop
import com.github.sdpteam15.polyevents.helper.HelperFunctions.run
import java.util.*

/**
 * Observable live data of type T
 * @param
 */
class Observable<T>(value: T? = null, sender: Objects? = null) {
    constructor(value: T? = null) : this(value, null)
    constructor() : this(null)

    private val observers = mutableSetOf<(UpdateArgs<T>) -> Unit>()

    private var updateArgs: UpdateArgs<T>? = null

    init {
        if (value != null)
            updateArgs = UpdateArgs(value, sender)
    }

    /**
     * Current value
     */
    var value: T?
        get() = updateArgs?.value
        set(value) {
            if (value != null)
                postValue(value, null)
        }

    /**
     * Current sender
     */
    val sender: Objects?
        get() = updateArgs?.sender

    /**
     *  Add an observer for the live data
     *  @param observer observer for the live data
     *  @return a method to remove the observer
     */
    fun observe(observer: (UpdateArgs<T>) -> Unit): () -> Boolean {
        observers.add(observer)
        if (updateArgs != null)
            observer(updateArgs!!)
        return { observers.remove(observer) }
    }

    /**
     *  Add an observer for the live data
     *  @param observer lifecycle of the observer to automatically remove it from the observers when stopped
     *  @return a method to remove the observer
     */
    fun observe(lifecycle: LifecycleOwner, observer: (UpdateArgs<T>) -> Unit): () -> Boolean =
        observeOnStop(lifecycle, observe(observer))

    /**
     * Post a new value
     * @param newValue the new value
     * @param sender The source of the event.
     */
    fun postValue(newValue: T, sender: Objects? = null) {
        synchronized(this) { updateArgs = UpdateArgs(newValue, sender); }
        run(Runnable {
            for (obs in observers)
                obs(updateArgs!!);
        })
    }
}