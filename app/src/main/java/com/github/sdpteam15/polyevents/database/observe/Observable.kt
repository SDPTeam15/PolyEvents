package com.github.sdpteam15.polyevents.database.observe

import androidx.lifecycle.LifecycleOwner
import com.github.sdpteam15.polyevents.helper.HelperFunctions.observeOnDestroy
import com.github.sdpteam15.polyevents.helper.HelperFunctions.run

/**
 * Observable live data of type T
 * @param
 */
class Observable<T>(value: T? = null, sender: Any? = null) {
    constructor(value: T? = null) : this(value, null)
    constructor() : this(null)

    private val observers = mutableSetOf<(UpdateArgs<T>) -> Boolean>()

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
    val sender: Any?
        get() = updateArgs?.sender

    /**
     *  Add an observer for the live data while it return true
     *  @param observer observer for the live data
     *  @return a method to remove the observer
     */
    fun observeWhileTrue(observer: (UpdateArgs<T>) -> Boolean): () -> Boolean {
        observers.add(observer)
        if (updateArgs != null)
            run(Runnable {
                observer(updateArgs!!)
            })
        return { leave(observer) }
    }

    /**
     *  Add an observer for the live data while it return true
     *  @param lifecycle lifecycle of the observer to automatically remove it from the observers when stopped
     *  @param observer observer for the live data
     *  @return a method to remove the observer
     */
    fun observeWhileTrue(lifecycle: LifecycleOwner, observer: (UpdateArgs<T>) -> Boolean) =
        observeOnDestroy(lifecycle, observeWhileTrue(observer))

    /**
     *  Add an observer for the live data
     *  @param observer observer for the live data
     *  @return a method to remove the observer
     */
    fun observe(observer: (UpdateArgs<T>) -> Unit) = observeWhileTrue {
        observer(it)
        true
    }

    /**
     *  Add an observer for the live data
     *  @param lifecycle lifecycle of the observer to automatically remove it from the observers when stopped
     *  @param observer observer for the live data
     *  @return a method to remove the observer
     */
    fun observe(lifecycle: LifecycleOwner, observer: (UpdateArgs<T>) -> Unit) =
        observeOnDestroy(lifecycle, observe(observer))

    /**
     *  Add an observer for the live data once
     *  @param observer observer for the live data
     *  @return a method to remove the observer
     */
    fun observeOnce(observer: (UpdateArgs<T>) -> Unit) = observeWhileTrue {
        observer(it)
        false
    }

    /**
     *  Add an observer for the live data once
     *  @param lifecycle lifecycle of the observer to automatically remove it from the observers when stopped
     *  @param observer observer for the live data
     *  @return a method to remove the observer
     */
    fun observeOnce(lifecycle: LifecycleOwner, observer: (UpdateArgs<T>) -> Unit) =
        observeOnDestroy(lifecycle, observeOnce(observer))

    /**
     *  remove an observer for the live data
     *  @param observer observer for the live data
     *  @return if the observer have been remove
     */
    fun leave(observer: (UpdateArgs<T>) -> Boolean): Boolean = observers.remove(observer)

    /**
     * Post a new value
     * @param newValue the new value
     * @param sender The source of the event.
     */
    fun postValue(newValue: T, sender: Any? = null) {
        synchronized(this) { updateArgs = UpdateArgs(newValue, sender); }
        run(Runnable {
            val toRemove = mutableListOf<(UpdateArgs<T>) -> Boolean>()
            for (obs in observers)
                if (!obs(updateArgs!!))
                    toRemove.add(obs)
            for (obs in toRemove)
                    leave(obs)
        })
    }
}