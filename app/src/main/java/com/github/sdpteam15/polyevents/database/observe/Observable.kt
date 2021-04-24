package com.github.sdpteam15.polyevents.database.observe

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.github.sdpteam15.polyevents.helper.HelperFunctions.run
import java.util.logging.Level


/**
 * Observable live data of type T
 * @param
 */
class Observable<T>(value: T? = null, sender: Any? = null) {
    class AfterRemovable<U>(val then: U, val remove: () -> Boolean)
    companion object {
        fun <U> observeOnDestroy(
            lifecycle: LifecycleOwner,
            result: AfterRemovable<U>
        ): AfterRemovable<U> {
            //Anonymous class to observe the ON_STOP Event ao the Activity/Fragment
            val lifecycleObserver = object : LifecycleObserver {
                @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
                fun stopListener() = result.remove()
            }
            lifecycle.lifecycle.addObserver(lifecycleObserver)
            return result
        }
    }

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
    fun observeWhileTrue(observer: (UpdateArgs<T>) -> Boolean): AfterRemovable<Observable<T>> {
        observers.add(observer)
        if (updateArgs != null)
            run(Runnable {
                if (!observer(updateArgs!!))
                    observers.remove(observer)
            })
        return AfterRemovable(this, { leave(observer) })
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
     *  map to an other observable while it return true
     *  @param mapper mapper from the live data to the new one
     *  @param observable observer for the live data
     *  @return if the observer have been remove
     */
    fun <U> mapWhileTrue(
        observable: Observable<U> = Observable(),
        mapper: (T) -> Pair<U, Boolean>
    ) = AfterRemovable(
        observable,
        this.observeWhileTrue {
            val (v, r) = mapper(it.value)
            observable.postValue(v, it.sender)
            r
        }.remove
    )

    /**
     *  update to an other observable while it return true
     *  @param lifecycle lifecycle of the observer to automatically remove it from the observers when stopped
     *  @param mapper mapper from the live data to the new one
     *  @param observable observer for the live data
     *  @return if the observer have been remove
     */
    fun <U> mapWhileTrue(
        lifecycle: LifecycleOwner,
        observable: Observable<U> = Observable(),
        mapper: (T) -> Pair<U, Boolean>
    ) = observeOnDestroy(lifecycle, mapWhileTrue(observable, mapper))

    /**
     *  map to an other observable
     *  @param mapper mapper from the live data to the new one
     *  @param observable observer for the live data
     *  @return if the observer have been remove
     */
    fun <U> map(observable: Observable<U> = Observable(), mapper: (T) -> U) =
        mapWhileTrue(observable) { Pair(mapper(it), true) }

    /**
     *  update to an other observable
     *  @param lifecycle lifecycle of the observer to automatically remove it from the observers when stopped
     *  @param mapper mapper from the live data to the new one
     *  @param observable observer for the live data
     *  @return if the observer have been remove
     */
    fun <U> map(
        lifecycle: LifecycleOwner,
        observable: Observable<U> = Observable(),
        mapper: (T) -> U
    ) = observeOnDestroy(lifecycle, map(observable, mapper))

    /**
     *  map to an other observable once
     *  @param mapper mapper from the live data to the new one
     *  @param observable observer for the live data
     *  @return if the observer have been remove
     */
    fun <U> mapOnce(observable: Observable<U> = Observable(), mapper: (T) -> U) =
        mapWhileTrue(observable) { Pair(mapper(it), false) }

    /**
     *  update to an other observable once
     *  @param lifecycle lifecycle of the observer to automatically remove it from the observers when stopped
     *  @param mapper mapper from the live data to the new one
     *  @param observable observer for the live data
     *  @return if the observer have been remove
     */
    fun <U> mapOnce(
        lifecycle: LifecycleOwner,
        observable: Observable<U> = Observable(),
        mapper: (T) -> U
    ) = observeOnDestroy(lifecycle, mapOnce(observable, mapper))

        /**
     *  update to an other observable while it return true
     *  @param decider decider for the update
     *  @param observable observer for the live data
     */
    fun updateWhileTrue(
        observable: Observable<T>,
        decider: (T) -> Boolean
    ) = observeWhileTrue {
        observable.postValue(it.value, it.sender)
        decider(it.value)
    }

    /**
     *  update to an other observable while it return true
     *  @param lifecycle lifecycle of the observer to automatically remove it from the observers when stopped
     *  @param decider decider for the update
     *  @param observable observer for the live data
     */
    fun updateWhileTrue(
        lifecycle: LifecycleOwner,
        observable: Observable<T>,
        decider: (T) -> Boolean
    ) = observeOnDestroy(lifecycle, updateWhileTrue(observable, decider))

    /**
     *  update to an other observable
     *  @param observable observer for the live data
     */
    fun update(observable: Observable<T>) =
        updateWhileTrue(observable) { true }

    /**
     *  update to an other observable
     *  @param lifecycle lifecycle of the observer to automatically remove it from the observers when stopped
     *  @param observable observer for the live data
     */
    fun update(
        lifecycle: LifecycleOwner,
        observable: Observable<T>
    ) = observeOnDestroy(lifecycle, update(observable))

    /**
     *  update to an other observable once
     *  @param observable observer for the live data
     */
    fun updateOnce(observable: Observable<T>) =
        updateWhileTrue(observable) { false }

    /**
     *  update to an other observable once
     *  @param lifecycle lifecycle of the observer to automatically remove it from the observers when stopped
     *  @param observable observer for the live data
     */
    fun updateOnce(
        lifecycle: LifecycleOwner,
        observable: Observable<T>
    ) = observeOnDestroy(lifecycle, updateOnce(observable))

    /**
     * Post a new value
     * @param newValue the new value
     * @param sender The source of the event.
     */
    fun postValue(newValue: T, sender: Any? = null) : AfterRemovable<Observable<T>> {
        synchronized(this) { updateArgs = UpdateArgs(newValue, sender); }
        run(Runnable {
            val toRemove = mutableListOf<(UpdateArgs<T>) -> Boolean>()
            for (obs in observers)
                if (!obs(updateArgs!!))
                    toRemove.add(obs)
            for (obs in toRemove)
                leave(obs)
        })
        return AfterRemovable(this, { true })
    }
}