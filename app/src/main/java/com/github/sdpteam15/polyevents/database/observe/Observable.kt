package com.github.sdpteam15.polyevents.database.observe

import android.annotation.SuppressLint
import androidx.arch.core.executor.ArchTaskExecutor
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import java.util.*

/**
 * Observable live data of type T
 * @param
 */
open class Observable<T> (value: T? = null) {
    constructor() : this(null)
    protected val observers = ArrayList<(T?) -> Unit>()

    private var tempValue: T? = null

    init {
        tempValue = value
    }

    /**
     * Current value
     */
    var value: T?
        get() = tempValue
        set(value) {
            postValue(value)
        }

    /**
     *  Add an observer for the live data
     *  @param observer observer for the live data
     *  @return a method to remove the observer
     */
    fun observe(observer: (T?) -> Unit): () -> Boolean {
        observers.add(observer)
        return { observers.remove(observer) }
    }

    /**
     *  Add an observer for the live data
     *  @param observer observer for the live data
     *  @return a method to remove the observer
     */
    fun observe(observer: Observer<T>): () -> Boolean =
        observe { value: T? -> observer.update(value) }

    /**
     *  Add an observer for the live data
     *  @param observer lifecycle of the observer to automatically remove it from the observers when stopped
     *  @return a method to remove the observer
     */
    fun observe(lifecycle: LifecycleOwner, observer: (T?) -> Unit) : () -> Boolean{
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
    fun observe(lifecycle: LifecycleOwner, observer: Observer<T>) : () -> Boolean =
        observe(lifecycle) { value: T? -> observer.update(value) }

    /**
     * Post a new value
     * @param newValue the new value
     */
    @SuppressLint("RestrictedApi")
    open fun postValue(newValue: T?) {
        synchronized(this) { tempValue = newValue; }
        ArchTaskExecutor.getInstance().postToMainThread(Runnable {
            for (obs in observers)
                obs(value);
        })
    }
}