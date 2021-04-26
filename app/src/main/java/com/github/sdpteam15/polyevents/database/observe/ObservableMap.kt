package com.github.sdpteam15.polyevents.database.observe

import androidx.lifecycle.LifecycleOwner
import com.github.sdpteam15.polyevents.helper.HelperFunctions

/**
 * Observable live map of type T
 * @param
 */
class ObservableMap<K, T> : MutableMap<K, T> {

    class UpdateKeyedValue<K, T>(val value: T, val key: K, val sender: Any?)
    class ObserversInfo<K, T>(
        val value: Observable.UpdateValue<Map<K, T>>,
        val info: Info,
        val args: Any?,
        val sender: Any?
    )

    enum class Info {
        put,
        putAll,
        remove,
        clear,
        itemUpdated
    }

    private val observersPut = mutableSetOf<(UpdateKeyedValue<K, T>) -> Boolean>()
    private val observersRemove = mutableSetOf<(UpdateKeyedValue<K, T>) -> Boolean>()
    private val observersItemUpdate = mutableSetOf<(UpdateKeyedValue<K, T>) -> Boolean>()
    private val observers = mutableSetOf<(ObserversInfo<K, T>) -> Boolean>()

    private val mapValues: MutableMap<K, Observable<T>> = mutableMapOf()
    private val removeItemObserver: MutableMap<Observable<T>, () -> Boolean> = mutableMapOf()

    override val size get() = mapValues.size

    override fun containsKey(key: K) = mapValues.containsKey(key)

    override operator fun get(key: K): T? = mapValues[key]?.value

    /**
     * Returns the observable element at the specified index in the list.
     * @return the observable element at the specified index in the list.
     */
    fun getObservable(key: K): Observable<T>? = mapValues[key]

    override fun containsValue(value: T): Boolean {
        for (e in mapValues.values)
            if (value?.equals(e.value) == true)
                return true
        return false
    }

    /**
     * Returns if the map maps one or more keys to the specified value.
     * @return if the map maps one or more keys to the specified value.
     */
    fun containsObservable(value: Observable<T>) = mapValues.containsValue(value)

    override fun isEmpty() = mapValues.isEmpty()

    private fun put(
        key: K,
        observable: Observable<T>,
        sender: Any? = null,
        notify: Boolean
    ): Observable<T>? {
        if (observable.value != null) {
            mapValues.put(key, observable)
            removeItemObserver[observable] =
                observable.observe(false) {
                    itemUpdated(
                        UpdateKeyedValue(
                            it.value,
                            key,
                            it.sender
                        )
                    )
                }.remove
            itemPuted(UpdateKeyedValue(observable.value!!, key, sender))
            if (notify) {
                notifyUpdate(sender, Info.put, Pair(key, observable))
            }
            return observable
        }
        return null
    }

    /**
     * Add an item.
     * @param key Key to add.
     * @param item Item to add.
     * @param sender The source of the event.
     * @return Observable added.
     */
    fun put(key: K, item: T, sender: Any?) = put(key, item, sender, true)

    private fun put(key: K, value: T, sender: Any? = null, notify: Boolean) =
        put(key, Observable(value), sender, notify)

    override fun put(key: K, value: T): T? = put(key, value, null)?.value

    /**
     * Add all items in the map.
     * @param from map to add.
     * @param sender The source of the event.
     */
    fun putAll(from: Map<out K, T>, sender: Any? = null): Boolean {
        var res = true
        for (key: K in from.keys) {
            res = res && (put(key, from[key]!!, sender, false) != null)
        }
        notifyUpdate(sender, Info.putAll, from)
        return res
    }

    override fun putAll(from: Map<out K, T>) {
        putAll(from, null)
    }

    /**
     * Remove an item.
     * @param key key of the item to remove.
     * @param sender The source of the event.
     * @return observable removed.
     */
    fun remove(key: K, sender: Any? = null) = remove(key, sender, true)
    private fun remove(
        key: K,
        sender: Any?,
        notify: Boolean
    ): Observable<T>? {
        val observable = mapValues.remove(key) ?: return null
        removeItemObserver[observable]!!()
        removeItemObserver.remove(observable)
        itemRemoved(UpdateKeyedValue(observable.value!!, key, sender))
        if (notify) {
            notifyUpdate(sender, Info.remove, key)
        }
        return observable
    }

    override fun remove(key: K): T? = remove(key, null)?.value


    override fun clear() = clear(null)

    /**
     * Clear all items.
     * @param sender The source of the event.
     */
    fun clear(sender: Any? = null) {
        for (key in mapValues.keys) {
            remove(key, sender, false)
        }
        notifyUpdate(sender, Info.clear)
    }

    override val entries: MutableSet<MutableMap.MutableEntry<K, T>>
        get() = mapValues.entries.map {
            object : MutableMap.MutableEntry<K, T>{
                override val key: K
                    get() = it.key
                override val value: T
                    get() = it.value.value!!
                override fun setValue(newValue: T): T {
                    val v = value
                    it.value.postValue(newValue, this)
                    return v
                }
            }
        } as MutableSet<MutableMap.MutableEntry<K, T>>
    override val keys: MutableSet<K>
        get() = mapValues.keys
    override val values: MutableCollection<T>
        get() = mapValues.values.map { it.value!! }.toMutableList()

    /**
     *  Add an observer for the live data additions while it return true
     *  @param observer observer for the live data additions
     *  @return a method to remove the observer
     */
    fun observePutWhileTrue(
        observer: (UpdateKeyedValue<K, T>) -> Boolean
    ): Observable.AfterRemovable<ObservableMap<K, T>> {
        observersPut.add(observer)
        return Observable.AfterRemovable(this, { leavePut(observer) })
    }

    /**
     *  Add an observer for the live data additions while it return true
     *  @param lifecycle lifecycle of the observer to automatically remove it from the observers when stopped
     *  @param observer observer for the live data additions
     *  @return a method to remove the observer
     */
    fun observePutWhileTrue(
        lifecycle: LifecycleOwner,
        observer: (UpdateKeyedValue<K, T>) -> Boolean
    ) =
        Observable.observeOnDestroy(lifecycle, observePutWhileTrue(observer))

    /**
     *  Add an observer for the live data additions
     *  @param observer observer for the live data additions
     *  @return a method to remove the observer
     */
    fun observePut(observer: (UpdateKeyedValue<K, T>) -> Unit) =
        observePutWhileTrue {
            observer(it)
            true
        }

    /**
     *  Add an observer for the live data additions
     *  @param lifecycle lifecycle of the observer to automatically remove it from the observers when stopped
     *  @param observer observer for the live data additions
     *  @return a method to remove the observer
     */
    fun observePut(
        lifecycle: LifecycleOwner,
        observer: (UpdateKeyedValue<K, T>) -> Unit
    ) =
        Observable.observeOnDestroy(lifecycle, observePut(observer))

    /**
     *  Add an observer for the live data additions once
     *  @param observer observer for the live data additions
     *  @return a method to remove the observer
     */
    fun observePutOnce(observer: (UpdateKeyedValue<K, T>) -> Unit) =
        observePutWhileTrue {
            observer(it)
            false
        }

    /**
     *  Add an observer for the live data additions once
     *  @param lifecycle lifecycle of the observer to automatically remove it from the observers when stopped
     *  @param observer observer for the live data additions
     *  @return a method to remove the observer
     */
    fun observePutOnce(
        lifecycle: LifecycleOwner,
        observer: (UpdateKeyedValue<K, T>) -> Unit
    ) =
        Observable.observeOnDestroy(lifecycle, observePutOnce(observer))

    /**
     *  remove an observer for the live data
     *  @param observer observer for the live data
     *  @return if the observer have been remove
     */
    fun leavePut(observer: (UpdateKeyedValue<K, T>) -> Boolean) = observersPut.remove(observer)

    /**
     *  Add an observer for the live data removals while it return true
     *  @param observer observer for the live data removals
     *  @return a method to remove the observer
     */
    fun observeRemoveWhileTrue(observer: (UpdateKeyedValue<K, T>) -> Boolean): Observable.AfterRemovable<ObservableMap<K, T>> {
        observersRemove.add(observer)
        return Observable.AfterRemovable(this, { leaveRemove(observer) })
    }

    /**
     *  Add an observer for the live data removals while it return true
     *  @param observer lifecycle of the observer to automatically remove it from the observers when stopped
     *  @return a method to remove the observer
     */
    fun observeRemoveWhileTrue(
        lifecycle: LifecycleOwner,
        observer: (UpdateKeyedValue<K, T>) -> Boolean
    ) =
        Observable.observeOnDestroy(lifecycle, observeRemoveWhileTrue(observer))

    /**
     *  Add an observer for the live data removals
     *  @param observer observer for the live data removals
     *  @return a method to remove the observer
     */
    fun observeRemove(observer: (UpdateKeyedValue<K, T>) -> Unit) = observeRemoveWhileTrue {
        observer(it)
        true
    }

    /**
     *  Add an observer for the live data removals
     *  @param lifecycle lifecycle of the observer to automatically remove it from the observers when stopped
     *  @param observer observer for the live data removals
     *  @return a method to remove the observer
     */
    fun observeRemove(lifecycle: LifecycleOwner, observer: (UpdateKeyedValue<K, T>) -> Unit) =
        Observable.observeOnDestroy(lifecycle, observeRemove(observer))

    /**
     *  Add an observer for the live data removals once
     *  @param observer observer for the live data removals
     *  @return a method to remove the observer
     */
    fun observeRemoveOnce(observer: (UpdateKeyedValue<K, T>) -> Unit) = observeRemoveWhileTrue {
        observer(it)
        false
    }

    /**
     *  Add an observer for the live data removals once
     *  @param lifecycle lifecycle of the observer to automatically remove it from the observers when stopped
     *  @param observer observer for the live data removals
     *  @return a method to remove the observer
     */
    fun observeRemoveOnce(
        lifecycle: LifecycleOwner,
        observer: (UpdateKeyedValue<K, T>) -> Unit
    ) = Observable.observeOnDestroy(lifecycle, observeRemoveOnce(observer))

    /**
     *  remove an observer for the live data
     *  @param observer observer for the live data
     *  @return if the observer have been remove
     */
    fun leaveRemove(observer: (UpdateKeyedValue<K, T>) -> Boolean) =
        observersRemove.remove(observer)

    /**
     *  Add an observer for the live data updating while it return tru
     *  @param observer observer for the live data updating
     *  @return a method to remove the observer
     */
    fun observeUpdateWhileTrue(
        observer: (UpdateKeyedValue<K, T>) -> Boolean
    ): Observable.AfterRemovable<ObservableMap<K, T>> {
        observersItemUpdate.add(observer)
        return Observable.AfterRemovable(this, { leaveUpdate(observer) })
    }

    /**
     *  Add an observer for the live data updating while it return tru
     *  @param lifecycle lifecycle of the observer to automatically remove it from the observers when stopped
     *  @param observer observer for the live data updating
     *  @return a method to remove the observer
     */
    fun observeUpdateWhileTrue(
        lifecycle: LifecycleOwner,
        observer: (UpdateKeyedValue<K, T>) -> Boolean
    ) = Observable.observeOnDestroy(lifecycle, observeUpdateWhileTrue(observer))

    /**
     *  Add an observer for the live data updating
     *  @param observer observer for the live data updating
     *  @return a method to remove the observer
     */
    fun observeUpdate(observer: (UpdateKeyedValue<K, T>) -> Unit) = observeUpdateWhileTrue {
        observer(it)
        true
    }

    /**
     *  Add an observer for the live data updating
     *  @param lifecycle lifecycle of the observer to automatically remove it from the observers when stopped
     *  @param observer observer for the live data updating
     *  @return a method to remove the observer
     */
    fun observeUpdate(
        lifecycle: LifecycleOwner,
        observer: (UpdateKeyedValue<K, T>) -> Unit
    ) = Observable.observeOnDestroy(lifecycle, observeUpdate(observer))

    /**
     *  Add an observer for the live data updating once
     *  @param observer observer for the live data updating
     *  @return a method to remove the observer
     */
    fun observeUpdateOnce(observer: (UpdateKeyedValue<K, T>) -> Unit) =
        observeUpdateWhileTrue {
            observer(it)
            false
        }

    /**
     *  Add an observer for the live data updating once
     *  @param lifecycle lifecycle of the observer to automatically remove it from the observers when stopped
     *  @param observer observer for the live data updating
     *  @return a method to remove the observer
     */
    fun observeUpdateOnce(
        lifecycle: LifecycleOwner,
        observer: (UpdateKeyedValue<K, T>) -> Unit
    ) = Observable.observeOnDestroy(lifecycle, observeUpdateOnce(observer))

    /**
     *  remove an observer for the live data
     *  @param observer observer for the live data
     *  @return if the observer have been remove
     */
    fun leaveUpdate(observer: (UpdateKeyedValue<K, T>) -> Boolean) =
        observersItemUpdate.remove(observer)

    /**
     *  Add an observer for the live data while it return true
     *  @param observer observer for the live data
     *  @return a method to remove the observer
     */
    fun observeWhileTrue(observer: (Observable.UpdateValue<Map<K, T>>) -> Boolean): Observable.AfterRemovable<ObservableMap<K, T>> {
        val resultObserver: (ObserversInfo<K, T>) -> Boolean = {
            observer(it.value)
        }
        observers.add(resultObserver)
        return Observable.AfterRemovable(this, { leave(resultObserver) })
    }

    /**
     *  Add an observer for the live data while it return true
     *  @param lifecycle lifecycle of the observer to automatically remove it from the observers when stopped
     *  @param observer observer for the live data
     *  @return a method to remove the observer
     */
    fun observeWhileTrue(
        lifecycle: LifecycleOwner,
        observer: (Observable.UpdateValue<Map<K, T>>) -> Boolean
    ) =
        Observable.observeOnDestroy(lifecycle, observeWhileTrue(observer))

    /**
     *  Add an observer for the live data
     *  @param observer observer for the live data
     *  @return a method to remove the observer
     */
    fun observe(observer: (Observable.UpdateValue<Map<K, T>>) -> Unit) = observeWhileTrue {
        observer(it)
        true
    }

    /**
     *  Add an observer for the live data
     *  @param lifecycle lifecycle of the observer to automatically remove it from the observers when stopped
     *  @param observer observer for the live data
     *  @return a method to remove the observer
     */
    fun observe(lifecycle: LifecycleOwner, observer: (Observable.UpdateValue<Map<K, T>>) -> Unit) =
        Observable.observeOnDestroy(lifecycle, observe(observer))

    /**
     *  Add an observer for the live data once
     *  @param observer observer for the live data
     *  @return a method to remove the observer
     */
    fun observeOnce(observer: (Observable.UpdateValue<Map<K, T>>) -> Unit) = observeWhileTrue {
        observer(it)
        false
    }

    /**
     *  Add an observer for the live data once
     *  @param lifecycle lifecycle of the observer to automatically remove it from the observers when stopped
     *  @param observer observer for the live data
     *  @return a method to remove the observer
     */
    fun observeOnce(
        lifecycle: LifecycleOwner,
        observer: (Observable.UpdateValue<Map<K, T>>) -> Unit
    ) =
        Observable.observeOnDestroy(lifecycle, observeOnce(observer))

    /**
     *  remove an observer for the live data
     *  @param observer observer for the live data
     *  @return if the observer have been remove
     */
    fun leave(observer: (ObserversInfo<K, T>) -> Boolean) = observers.remove(observer)

    /**
     *  map to an other observable while it return true
     *  @param observableList observer for the live data
     *  @param condition condition to continue to observe
     *  @param mapper mapper from the live data to the new one
     *  @return if the observer have been remove
     */
    fun <U> mapWhileTrue(
        observableMap: ObservableMap<K, U> = ObservableMap(),
        condition: () -> Boolean,
        mapper: (T) -> U
    ): Observable.AfterRemovable<ObservableMap<K, U>> {
        val result: (ObserversInfo<K, T>) -> Boolean =
            {
                when (it.info) {
                    Info.put -> {
                        val (key, observable) = it.args as Pair<K, Observable<T>>
                        observableMap.put(key, mapper(observable.value!!), it.sender)
                    }
                    Info.putAll -> {
                        val from = it.args as Map<out K, T>
                        val map = mutableMapOf<K, U>()
                        for (key in from.keys)
                            map.put(key, mapper(from[key]!!))
                        observableMap.putAll(map, it.sender)
                    }
                    Info.remove -> {
                        val key = it.args as K
                        observableMap.remove(key, it.sender)
                    }
                    Info.clear -> {
                        observableMap.clear(it.sender)
                    }
                    Info.itemUpdated -> {
                        val value = it.args as UpdateKeyedValue<K, T>
                        observableMap.getObservable(value.key)!!
                            .postValue(mapper(value.value), it.sender)
                    }
                }
                condition()
            }
        observers.add(result)
        return Observable.AfterRemovable(observableMap, { observers.remove(result) })
    }

    /**
     *  map to an other observable while it return true
     *  @param lifecycle lifecycle of the observer to automatically remove it from the observers when stopped
     *  @param observableMap observer for the live data
     *  @param condition condition to continue to observe
     *  @param mapper mapper from the live data to the new one
     *  @return if the observer have been remove
     */
    fun <U> mapWhileTrue(
        lifecycle: LifecycleOwner,
        observableMap: ObservableMap<K, U> = ObservableMap(),
        condition: () -> Boolean,
        mapper: (T) -> U
    ) = Observable.observeOnDestroy(lifecycle, mapWhileTrue(observableMap, condition, mapper))

    /**
     *  map to an other observable
     *  @param observableList observer for the live data
     *  @param mapper mapper from the live data to the new one
     *  @return if the observer have been remove
     */
    fun <U> map(
        observableMap: ObservableMap<K, U> = ObservableMap(),
        mapper: (T) -> U
    ) = mapWhileTrue(observableMap, { true }, mapper)

    /**
     *  map to an other observable
     *  @param lifecycle lifecycle of the observer to automatically remove it from the observers when stopped
     *  @param observableList observer for the live data
     *  @param mapper mapper from the live data to the new one
     *  @return if the observer have been remove
     */
    fun <U> map(
        lifecycle: LifecycleOwner,
        observableMap: ObservableMap<K, U> = ObservableMap(),
        mapper: (T) -> U
    ) = Observable.observeOnDestroy(lifecycle, map(observableMap, mapper))

    /**
     *  map to an other observable once
     *  @param observableList observer for the live data
     *  @param mapper mapper from the live data to the new one
     *  @return if the observer have been remove
     */
    fun <U> mapOnce(
        observableMap: ObservableMap<K, U> = ObservableMap(),
        mapper: (T) -> U
    ) = mapWhileTrue(observableMap, { false }, mapper)

    /**
     *  map to an other observable once
     *  @param lifecycle lifecycle of the observer to automatically remove it from the observers when stopped
     *  @param observableList observer for the live data
     *  @param mapper mapper from the live data to the new one
     *  @return if the observer have been remove
     */
    fun <U> mapOnce(
        lifecycle: LifecycleOwner,
        observableMap: ObservableMap<K, U> = ObservableMap(),
        mapper: (T) -> U
    ) = Observable.observeOnDestroy(lifecycle, mapOnce(observableMap, mapper))

    private fun itemPuted(value: UpdateKeyedValue<K, T>) {
        HelperFunctions.run(Runnable {
            val toRemove = mutableListOf<(UpdateKeyedValue<K, T>) -> Boolean>()
            for (obs in observersPut)
                if (!obs(value))
                    toRemove.add(obs)
            for (obs in toRemove)
                leavePut(obs)
        })
    }

    private fun itemRemoved(value: UpdateKeyedValue<K, T>) {
        HelperFunctions.run(Runnable {
            val toRemove = mutableListOf<(UpdateKeyedValue<K, T>) -> Boolean>()
            for (obs in observersRemove)
                if (!obs(value))
                    toRemove.add(obs)
            for (obs in toRemove)
                leaveRemove(obs)
        })
    }

    private fun itemUpdated(value: UpdateKeyedValue<K, T>) {
        HelperFunctions.run(Runnable {
            val toRemove = mutableListOf<(UpdateKeyedValue<K, T>) -> Boolean>()
            for (obs in observersItemUpdate)
                if (!obs(value))
                    toRemove.add(obs)
            for (obs in toRemove)
                leaveUpdate(obs)
            notifyUpdate(value.sender, Info.itemUpdated, value)
        })
    }

    private fun notifyUpdate(sender: Any? = null, info: Info, args: Any? = null) {
        if (observers.isNotEmpty()) {
            val valueList =
                ObserversInfo(
                    Observable.UpdateValue(this as MutableMap<K, T>, sender),
                    info,
                    args,
                    sender
                )
            val toRemove = mutableListOf<(ObserversInfo<K, T>) -> Boolean>()
            for (obs in observers)
                if (!obs(valueList))
                    toRemove.add(obs)
            for (obs in toRemove)
                leave(obs)
        }
    }
}