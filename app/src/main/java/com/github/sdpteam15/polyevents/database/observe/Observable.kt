package com.github.sdpteam15.polyevents.database.observe

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.github.sdpteam15.polyevents.helper.HelperFunctions.run

/**
 * Data that notify a set of observers on a modification
 * When adding observers and passing a lifecycleOwner in the parameters, the added observer will not be notified if the lifecycle is destroyed.
 * This is useful for example when an activity is stopped, as we don't need to update data on this closed activity.
 * If we don't give a Lifecycle in parameters, the observer will keep beeing notified even if the activity is stopped.
 * To unsubscribe from the Observable, we take take the reference of the corresponding "remove" function
 * We get the "remove" function by taking the return value of the observe method, for example :
 *
 *      val ret : ThenOrRemove = observable.observe { ... }
 *      ...
 *      do something
 *      ...
 *      //on a special event stop observing
 *      ret.remove()
 *
 *  The chain operator "then" from this return function can be used to add multiple observers in a convenient way
 *  For example in an activity:
 *
 *      observable.map(this){ ... }.then.observe(this){ ... }.then.map(this){}.then.observeOnce(this){ ... }
 *
 * When setting a value, we use an "UpdateValue" object which is a pair containing the new value,
 * and eventually a reference to the sender object, which can be used when we need to know who sent the update.
 *
 * @param value the initial value of the data
 * @param sender object that modified the data
 */
class Observable<T>(value: T? = null, sender: Any? = null) {
    /**
     * The return value of each function that need to add a observer
     * @property then the reference of the Observable
     * @property remove a function  to remove the added observer
     */
    class ThenOrRemove<U>(val then: U, val remove: () -> Boolean)

    /**
     * Use an UpdateValue object each time we want to set a new value for the data.
     * It contains the new value and eventually the object that set the new value.
     * @property value the value
     * @property  sender object that modified the data
     */
    open class UpdateValue<T>(val value: T, val sender: Any?)

    companion object {
        /**
         * Remove the observer on when lifecycleOwner is destroy
         * @param lifecycle LifecycleOwner to observe
         * @param result value to return
         */
        fun <U> observeOnDestroy(
            lifecycle: LifecycleOwner,
            result: ThenOrRemove<U>
        ): ThenOrRemove<U> {
            //Anonymous class to observe the ON_STOP Event ao the Activity/Fragment
            val lifecycleObserver = object : LifecycleObserver {
                @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
                fun stopListener() = result.remove()
            }
            lifecycle.lifecycle.addObserver(lifecycleObserver)
            return result
        }
    }

    private val observers = mutableSetOf<(UpdateValue<T>) -> Boolean>()

    private var updateArgs: UpdateValue<T>? = null

    init {
        if (value != null)
            updateArgs = UpdateValue(value, sender)
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
     *  @param updateIfNotNull update if not null
     *  @param observer observer for the live data
     *  @return a method to remove the observer
     */
    fun observeWhileTrue(
        updateIfNotNull: Boolean = true,
        observer: (UpdateValue<T>) -> Boolean
    ): ThenOrRemove<Observable<T>> {
        observers.add(observer)
        if (updateIfNotNull && updateArgs != null)
            run(Runnable {
                if (!observer(updateArgs!!))
                    observers.remove(observer)
            })
        return ThenOrRemove(this, { leave(observer) })
    }

    /**
     *  Add an observer for the live data while it return true
     *  @param lifecycle lifecycle of the observer to automatically remove it from the observers when stopped
     *  @param updateIfNotNull update if not null
     *  @param observer observer for the live data
     *  @return a method to remove the observer
     */
    fun observeWhileTrue(
        lifecycle: LifecycleOwner,
        updateIfNotNull: Boolean = true,
        observer: (UpdateValue<T>) -> Boolean
    ) =
        observeOnDestroy(lifecycle, observeWhileTrue(updateIfNotNull, observer))

    /**
     *  Add an observer for the live data
     *  @param updateIfNotNull update if not null
     *  @param observer observer for the live data
     *  @return a method to remove the observer
     */
    fun observe(updateIfNotNull: Boolean = true, observer: (UpdateValue<T>) -> Unit) =
        observeWhileTrue(updateIfNotNull) {
            observer(it)
            true
        }

    /**
     *  Add an observer for the live data
     *  @param lifecycle lifecycle of the observer to automatically remove it from the observers when stopped
     *  @param updateIfNotNull update if not null
     *  @param observer observer for the live data
     *  @return a method to remove the observer
     */
    fun observe(
        lifecycle: LifecycleOwner,
        updateIfNotNull: Boolean = true,
        observer: (UpdateValue<T>) -> Unit
    ) =
        observeOnDestroy(lifecycle, observe(updateIfNotNull, observer))

    /**
     *  Add an observer for the live data once
     *  @param observer observer for the live data
     *  @param updateIfNotNull update if not null
     *  @return a method to remove the observer
     */
    fun observeOnce(updateIfNotNull: Boolean = true, observer: (UpdateValue<T>) -> Unit) =
        observeWhileTrue(updateIfNotNull) {
            observer(it)
            false
        }

    /**
     *  Add an observer for the live data once
     *  @param lifecycle lifecycle of the observer to automatically remove it from the observers when stopped
     *  @param updateIfNotNull update if not null
     *  @param observer observer for the live data
     *  @return a method to remove the observer
     */
    fun observeOnce(
        lifecycle: LifecycleOwner,
        updateIfNotNull: Boolean = true,
        observer: (UpdateValue<T>) -> Unit
    ) =
        observeOnDestroy(lifecycle, observeOnce(updateIfNotNull, observer))

    /**
     *  remove an observer for the live data
     *  @param observer observer for the live data
     *  @return if the observer have been remove
     */
    fun leave(observer: (UpdateValue<T>) -> Boolean): Boolean = observers.remove(observer)

    /**
     *  map to an other observable while it return true
     *  @param mapper mapper from the live data to the new one
     *  @param observable observer for the live data
     *  @return if the observer have been remove
     */
    fun <U> mapWhileTrue(
        observable: Observable<U> = Observable(),
        mapper: (T) -> Pair<U, Boolean>
    ) = ThenOrRemove(
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
     *  @param decider decider for the updat
     *  @param updateIfNotNull update if not null
     *  @param observable observer for the live data
     */
    fun updateWhileTrue(
        observable: Observable<T>,
        updateIfNotNull: Boolean = true,
        decider: (T) -> Boolean
    ) = observeWhileTrue(updateIfNotNull) {
        observable.postValue(it.value, it.sender)
        decider(it.value)
    }

    /**
     *  update to an other observable while it return true
     *  @param lifecycle lifecycle of the observer to automatically remove it from the observers when stopped
     *  @param decider decider for the update
     *  @param updateIfNotNull update if not null
     *  @param observable observer for the live data
     */
    fun updateWhileTrue(
        lifecycle: LifecycleOwner,
        observable: Observable<T>,
        updateIfNotNull: Boolean = true,
        decider: (T) -> Boolean
    ) = observeOnDestroy(lifecycle, updateWhileTrue(observable, updateIfNotNull, decider))

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
    fun updateOnce(observable: Observable<T>, updateIfNotNull: Boolean = false) =
        updateWhileTrue(observable, updateIfNotNull) { false }

    /**
     *  update to an other observable once
     *  @param lifecycle lifecycle of the observer to automatically remove it from the observers when stopped
     *  @param observable observer for the live data
     *  @param updateIfNotNull update if not null
     */
    fun updateOnce(
        lifecycle: LifecycleOwner,
        observable: Observable<T>,
        updateIfNotNull: Boolean = false,
    ) = observeOnDestroy(lifecycle, updateOnce(observable, updateIfNotNull))

    /**
     * Post a new value
     * @param newValue the new value
     * @param sender The source of the event.
     */
    fun postValue(newValue: T, sender: Any? = null): ThenOrRemove<Observable<T>> {
        synchronized(this) { updateArgs = UpdateValue(newValue, sender); }
        run(Runnable {
            for (obs in observers.toList())
                if (!obs(updateArgs!!))
                    leave(obs)
        })
        return ThenOrRemove(this, { true })
    }
}