package com.github.sdpteam15.polyevents.database.observe

import androidx.lifecycle.LifecycleOwner
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
 * @param collection initial collection of the data
 * @param observable initial observable of the data
 * @param sender last object that modified the data
 */
class ObservableList<T>(
    collection: Collection<T>? = null,
    observable: Observable<T>? = null,
    sender: Any? = null
) : MutableList<T> {


    private val observersAdd = mutableSetOf<(UpdateIndexedValue<T>) -> Boolean>()
    private val observersRemove = mutableSetOf<(UpdateIndexedValue<T>) -> Boolean>()
    private val observersItemUpdate = mutableSetOf<(UpdateIndexedValue<T>) -> Boolean>()
    private val observers = mutableSetOf<(ObserversInfo<T>) -> Boolean>()

    private var listValues: MutableList<Observable<T>>
    private var removeItemObserver: MutableMap<Observable<T>, () -> Boolean>

    init {
        listValues = mutableListOf()
        removeItemObserver = mutableMapOf()

        if (collection != null)
            addAll(collection, sender)
        if (observable != null)
            add(observable, sender)
    }

    open class UpdateIndexedValue<T>(value: T, val index: Int, sender: Any?) :
        Observable.UpdateValue<T>(value, sender)

    class ObserversInfo<T>(
        value: List<T>,
        val info: Info,
        val args: Any?,
        sender: Any?
    ) : Observable.UpdateValue<List<T>>(value, sender)

    enum class Info {
        add,
        addIndex,
        addAll,
        addAllIndex,
        remove,
        removeAt,
        removeAll,
        retainAll,
        clear,
        itemUpdated
    }

    override val size get() = listValues.size

    /**
     * Returns the element at the specified index in the list.
     * @return the element at the specified index in the list.
     */
    override operator fun get(index: Int): T = listValues[index].value!!

    /**
     * Returns the observable element at the specified index in the list.
     * @return the observable element at the specified index in the list.
     */
    fun getObservable(index: Int): Observable<T> = listValues[index]

    /**
     * Replaces the element at the specified position in this list with the specified element.
     * @param index position in this list
     * @param value new value
     * @param sender The source of the event.
     */
    fun set(index: Int, value: T, sender: Any?): T {
        listValues[index].postValue(value, sender)
        return value
    }

    override operator fun set(index: Int, element: T): T = set(index, element, null)

    override fun add(element: T) = add(element, null) != null

    /**
     * Add an observable.
     * @param observable Observable to add.
     * @param sender The source of the event.
     * @return Observable added.
     */
    fun add(observable: Observable<T>, sender: Any? = null): Observable<T>? =
        add(observable, sender, true)

    private fun add(
        observable: Observable<T>,
        sender: Any?,
        notify: Boolean
    ): Observable<T>? {
        if (observable.value != null) {
            if (!listValues.add(observable))
                return null
            val index = listValues.indexOf(observable)
            removeItemObserver[observable] =
                observable.observe(false) {
                    itemUpdated(
                        observable,
                        UpdateIndexedValue(
                            it.value,
                            listValues.indexOf(observable),
                            it.sender
                        )
                    )
                }.remove
            itemAdded(UpdateIndexedValue(observable.value!!, index, sender))
            if (notify) {
                notifyUpdate(sender, Info.add, observable)
            }
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
    fun add(item: T, sender: Any? = null): Observable<T>? = add(Observable(item), sender, true)!!
    private fun add(item: T, sender: Any?, notify: Boolean): Observable<T>? =
        add(Observable(item), sender, notify)!!

    override fun addAll(elements: Collection<T>) = addAll(elements, null)

    /**
     * Add all items in the list.
     * @param items items list.
     * @param sender The source of the event.
     */
    fun addAll(items: Collection<T>, sender: Any?): Boolean {
        var res = true
        val added = mutableListOf<Observable<T>>()
        for (item: T in items) {
            val ads = add(item, sender, false)
            if (ads != null)
                added.add(ads)
            else
                res = false
        }
        notifyUpdate(sender, Info.addAll, added)
        return res
    }

    override fun remove(element: T) = remove(element, null) != null

    /**
     * Remove an item.
     * @param observable item to remove.
     * @param sender The source of the event.
     * @return observable removed.
     */
    fun remove(observable: Observable<T>, sender: Any? = null) =
        remove(observable, sender, true)

    private fun remove(
        observable: Observable<T>,
        sender: Any?,
        notify: Boolean
    ): Observable<T>? {
        if (!listValues.remove(observable)) {
            return null
        }
        removeItemObserver[observable]!!()
        removeItemObserver.remove(observable)
        itemRemoved(UpdateIndexedValue(observable.value!!, -1, sender))
        if (notify) {
            notifyUpdate(sender, Info.remove, observable)
        }
        return observable
    }

    /**
     * Remove an item.
     * @param item item to remove.
     * @param sender The source of the event.
     * @return observable removed.
     */
    fun remove(item: T, sender: Any? = null): Observable<T>? = remove(item, sender, true)
    private fun remove(item: T, sender: Any?, notify: Boolean): Observable<T>? {
        val observable: Observable<T>? = find(item)
        if (observable != null) {
            return remove(observable, sender, notify)
        }
        return null
    }

    override fun removeAll(elements: Collection<T>) = removeAll(elements, null)

    /**
     * Add all items in the list.
     * @param items items list.
     * @param sender The source of the event.
     */
    fun removeAll(items: Collection<T>, sender: Any?): Boolean {
        var res = true

        val removeed = mutableListOf<Observable<T>>()
        for (item: T in items) {
            val rms = remove(item, sender, false)
            if (rms != null)
                removeed.add(rms)
            else
                res = false
        }
        notifyUpdate(sender, Info.removeAll, Pair(items, removeed))
        return res
    }

    /**
     * Remove an item.
     * @param index index of the item to remove.
     * @param sender The source of the event.
     * @return observable removed.
     */
    fun removeAt(index: Int, sender: Any? = null) = removeAt(index, sender, true)
    private fun removeAt(
        index: Int,
        sender: Any?,
        notify: Boolean
    ): Observable<T>? {
        val observable = listValues.removeAt(index) ?: return null
        removeItemObserver[observable]!!()
        removeItemObserver.remove(observable)
        itemRemoved(UpdateIndexedValue(observable.value!!, index, sender))
        if (notify) {
            notifyUpdate(sender, Info.removeAt, Pair(index, observable))
        }
        return observable
    }

    override fun removeAt(index: Int): T = removeAt(index, null)!!.value!!

    override fun clear() = clear(null)

    /**
     * Clear all items.
     * @param sender The source of the event.
     */
    fun clear(sender: Any?) {
        for (item in listValues.toMutableList()) {
            remove(item, sender, false)
        }
        notifyUpdate(sender, Info.clear)
    }

    private fun find(item: T): Observable<T>? = listValues.find { it.value == item }

    override fun isEmpty() = listValues.isEmpty()

    fun contains(item: Observable<T>) = listValues.contains(item)

    override fun contains(element: T) = (find(element) != null)

    override fun indexOf(element: T): Int {
        val item = find(element) ?: return -1
        return listValues.indexOf(item)
    }

    override fun containsAll(elements: Collection<T>): Boolean {
        for (element in elements) {
            if (!this.contains(element)) {
                return false
            }
        }
        return true
    }

    override fun lastIndexOf(element: T): Int {
        val item = find(element) ?: return -1
        return listValues.lastIndexOf(item)
    }

    private fun add(
        index: Int,
        observable: Observable<T>,
        sender: Any?,
        notify: Boolean
    ): Observable<T>? {
        if (observable.value != null) {
            listValues.add(index, observable)
            removeItemObserver[observable] =
                observable.observe(false) {
                    itemUpdated(
                        observable,
                        UpdateIndexedValue(
                            it.value,
                            listValues.indexOf(observable),
                            it.sender
                        )
                    )
                }.remove
            itemAdded(UpdateIndexedValue(observable.value!!, index, sender))
            if (notify) {
                notifyUpdate(sender, Info.addIndex, Pair(index, observable))
            }
            return observable
        }
        return null
    }

    /**
     * Add an item.
     * @param index Index to add.
     * @param item Item to add.
     * @param sender The source of the event.
     * @return Observable added.
     */
    fun add(index: Int, item: T, sender: Any?) = add(index, item, sender, true)

    private fun add(index: Int, item: T, sender: Any?, notify: Boolean) =
        add(index, Observable(item), sender, notify)

    override fun add(index: Int, element: T) {
        add(index, element, null)
    }

    /**
     * Add all items in the list.
     * @param index index in list.
     * @param items items list.
     * @param sender The source of the event.
     */
    fun addAll(index: Int, items: Collection<T>, sender: Any?): Boolean {
        var res = true

        var i = 0
        val added = mutableListOf<Observable<T>>()
        for (item: T in items) {
            val ads = add((i++ + index), item, sender, false)
            if (ads != null)
                added.add(ads)
            else
                res = false
        }
        notifyUpdate(sender, Info.addAllIndex, Pair(index, added))
        return res
    }

    override fun addAll(index: Int, elements: Collection<T>) = addAll(index, elements, null)

    /**
     * Retains only the elements in this collection that are contained in the specified collection.
     * @param items items list.
     * @param sender The source of the event.
     * @return true if any element was removed from the collection.
     */
    fun retainAll(items: Collection<T>, sender: Any?): Boolean {
        var i = 0
        val toremove = mutableListOf<Observable<T>>()
        for (item in listValues) {
            if (item.value !in items)
                toremove.add(item)
        }
        for (item in toremove)
            remove(item, sender, false)
        notifyUpdate(sender, Info.retainAll, Pair(items, toremove))
        return listValues.size != 0
    }

    override fun retainAll(elements: Collection<T>): Boolean =
        retainAll(elements, null)

    override fun subList(fromIndex: Int, toIndex: Int): MutableList<T> {
        TODO("Not yet implemented")
    }

    /**
     *  Add an observer for the live data additions while it return true
     *  @param observer observer for the live data additions
     *  @return a method to remove the observer
     */
    fun observeAddWhileTrue(
        observer: (UpdateIndexedValue<T>) -> Boolean
    ): Observable.ThenOrRemove<ObservableList<T>> {
        observersAdd.add(observer)
        return Observable.ThenOrRemove(this, { leaveAdd(observer) })
    }

    /**
     *  Add an observer for the live data additions while it return true
     *  @param lifecycle lifecycle of the observer to automatically remove it from the observers when stopped
     *  @param observer observer for the live data additions
     *  @return a method to remove the observer
     */
    fun observeAddWhileTrue(
        lifecycle: LifecycleOwner,
        observer: (UpdateIndexedValue<T>) -> Boolean
    ) =
        Observable.observeOnDestroy(lifecycle, observeAddWhileTrue(observer))

    /**
     *  Add an observer for the live data additions
     *  @param observer observer for the live data additions
     *  @return a method to remove the observer
     */
    fun observeAdd(observer: (UpdateIndexedValue<T>) -> Unit) =
        observeAddWhileTrue {
            observer(it)
            true
        }

    /**
     *  Add an observer for the live data additions
     *  @param lifecycle lifecycle of the observer to automatically remove it from the observers when stopped
     *  @param observer observer for the live data additions
     *  @return a method to remove the observer
     */
    fun observeAdd(
        lifecycle: LifecycleOwner,
        observer: (UpdateIndexedValue<T>) -> Unit
    ) =
        Observable.observeOnDestroy(lifecycle, observeAdd(observer))

    /**
     *  Add an observer for the live data additions once
     *  @param observer observer for the live data additions
     *  @return a method to remove the observer
     */
    fun observeAddOnce(observer: (UpdateIndexedValue<T>) -> Unit) =
        observeAddWhileTrue {
            observer(it)
            false
        }

    /**
     *  Add an observer for the live data additions once
     *  @param lifecycle lifecycle of the observer to automatically remove it from the observers when stopped
     *  @param observer observer for the live data additions
     *  @return a method to remove the observer
     */
    fun observeAddOnce(
        lifecycle: LifecycleOwner,
        observer: (UpdateIndexedValue<T>) -> Unit
    ) =
        Observable.observeOnDestroy(lifecycle, observeAddOnce(observer))

    /**
     *  remove an observer for the live data
     *  @param observer observer for the live data
     *  @return if the observer have been remove
     */
    fun leaveAdd(observer: (UpdateIndexedValue<T>) -> Boolean) = observersAdd.remove(observer)

    /**
     *  Add an observer for the live data removals while it return true
     *  @param observer observer for the live data removals
     *  @return a method to remove the observer
     */
    fun observeRemoveWhileTrue(observer: (UpdateIndexedValue<T>) -> Boolean): Observable.ThenOrRemove<ObservableList<T>> {
        observersRemove.add(observer)
        return Observable.ThenOrRemove(this, { leaveRemove(observer) })
    }

    /**
     *  Add an observer for the live data removals while it return true
     *  @param observer lifecycle of the observer to automatically remove it from the observers when stopped
     *  @return a method to remove the observer
     */
    fun observeRemoveWhileTrue(
        lifecycle: LifecycleOwner,
        observer: (UpdateIndexedValue<T>) -> Boolean
    ) =
        Observable.observeOnDestroy(lifecycle, observeRemoveWhileTrue(observer))

    /**
     *  Add an observer for the live data removals
     *  @param observer observer for the live data removals
     *  @return a method to remove the observer
     */
    fun observeRemove(observer: (UpdateIndexedValue<T>) -> Unit) = observeRemoveWhileTrue {
        observer(it)
        true
    }

    /**
     *  Add an observer for the live data removals
     *  @param lifecycle lifecycle of the observer to automatically remove it from the observers when stopped
     *  @param observer observer for the live data removals
     *  @return a method to remove the observer
     */
    fun observeRemove(lifecycle: LifecycleOwner, observer: (UpdateIndexedValue<T>) -> Unit) =
        Observable.observeOnDestroy(lifecycle, observeRemove(observer))

    /**
     *  Add an observer for the live data removals once
     *  @param observer observer for the live data removals
     *  @return a method to remove the observer
     */
    fun observeRemoveOnce(observer: (UpdateIndexedValue<T>) -> Unit) = observeRemoveWhileTrue {
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
        observer: (UpdateIndexedValue<T>) -> Unit
    ) = Observable.observeOnDestroy(lifecycle, observeRemoveOnce(observer))

    /**
     *  remove an observer for the live data
     *  @param observer observer for the live data
     *  @return if the observer have been remove
     */
    fun leaveRemove(observer: (UpdateIndexedValue<T>) -> Boolean) =
        observersRemove.remove(observer)

    /**
     *  Add an observer for the live data updating while it return tru
     *  @param observer observer for the live data updating
     *  @return a method to remove the observer
     */
    fun observeUpdateWhileTrue(
        observer: (UpdateIndexedValue<T>) ->
        Boolean
    ): Observable.ThenOrRemove<ObservableList<T>> {
        observersItemUpdate.add(observer)
        return Observable.ThenOrRemove(this, { leaveUpdate(observer) })
    }

    /**
     *  Add an observer for the live data updating while it return tru
     *  @param lifecycle lifecycle of the observer to automatically remove it from the observers when stopped
     *  @param observer observer for the live data updating
     *  @return a method to remove the observer
     */
    fun observeUpdateWhileTrue(
        lifecycle: LifecycleOwner,
        observer: (UpdateIndexedValue<T>) -> Boolean
    ) = Observable.observeOnDestroy(lifecycle, observeUpdateWhileTrue(observer))

    /**
     *  Add an observer for the live data updating
     *  @param observer observer for the live data updating
     *  @return a method to remove the observer
     */
    fun observeUpdate(observer: (UpdateIndexedValue<T>) -> Unit) = observeUpdateWhileTrue {
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
        observer: (UpdateIndexedValue<T>) -> Unit
    ) = Observable.observeOnDestroy(lifecycle, observeUpdate(observer))

    /**
     *  Add an observer for the live data updating once
     *  @param observer observer for the live data updating
     *  @return a method to remove the observer
     */
    fun observeUpdateOnce(observer: (UpdateIndexedValue<T>) -> Unit) =
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
        observer: (UpdateIndexedValue<T>) -> Unit
    ) = Observable.observeOnDestroy(lifecycle, observeUpdateOnce(observer))

    /**
     *  remove an observer for the live data
     *  @param observer observer for the live data
     *  @return if the observer have been remove
     */
    fun leaveUpdate(observer: (UpdateIndexedValue<T>) -> Boolean) =
        observersItemUpdate.remove(observer)

    /**
     *  Add an observer for the live data while it return true
     *  @param observer observer for the live data
     *  @return a method to remove the observer
     */
    fun observeWhileTrue(observer: (Observable.UpdateValue<List<T>>) -> Boolean): Observable.ThenOrRemove<ObservableList<T>> {
        observers.add(observer)
        return Observable.ThenOrRemove(this, { leave(observer) })
    }

    /**
     *  Add an observer for the live data while it return true
     *  @param lifecycle lifecycle of the observer to automatically remove it from the observers when stopped
     *  @param observer observer for the live data
     *  @return a method to remove the observer
     */
    fun observeWhileTrue(
        lifecycle: LifecycleOwner,
        observer: (Observable.UpdateValue<List<T>>) -> Boolean
    ) =
        Observable.observeOnDestroy(lifecycle, observeWhileTrue(observer))

    /**
     *  Add an observer for the live data
     *  @param observer observer for the live data
     *  @return a method to remove the observer
     */
    fun observe(observer: (Observable.UpdateValue<List<T>>) -> Unit) = observeWhileTrue {
        observer(it)
        true
    }

    /**
     *  Add an observer for the live data
     *  @param lifecycle lifecycle of the observer to automatically remove it from the observers when stopped
     *  @param observer observer for the live data
     *  @return a method to remove the observer
     */
    fun observe(lifecycle: LifecycleOwner, observer: (Observable.UpdateValue<List<T>>) -> Unit) =
        Observable.observeOnDestroy(lifecycle, observe(observer))

    /**
     *  Add an observer for the live data once
     *  @param observer observer for the live data
     *  @return a method to remove the observer
     */
    fun observeOnce(observer: (Observable.UpdateValue<List<T>>) -> Unit) = observeWhileTrue {
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
        observer: (Observable.UpdateValue<List<T>>) -> Unit
    ) =
        Observable.observeOnDestroy(lifecycle, observeOnce(observer))

    /**
     *  remove an observer for the live data
     *  @param observer observer for the live data
     *  @return if the observer have been remove
     */
    fun leave(observer: (ObserversInfo<T>) -> Boolean) = observers.remove(observer)

    /**
     *  map to an other observable while it return true
     *  @param observableList observer for the live data
     *  @param condition condition to continue to observe
     *  @param mapper mapper from the live data to the new one
     *  @return if the observer have been remove
     */
    fun <U> mapWhileTrue(
        observableList: ObservableList<U> = ObservableList(),
        condition: () -> Boolean,
        mapper: (T) -> U
    ): Observable.ThenOrRemove<ObservableList<U>> {
        val result: (ObserversInfo<T>) -> Boolean =
            {
                when (it.info) {
                    Info.add -> {
                        val observable = it.args as Observable<T>
                        observableList.add(mapper(observable.value!!), it.sender)
                    }
                    Info.addIndex -> {
                        val (index, observable) = it.args as Pair<Int, Observable<T>>
                        observableList.add(index, mapper(observable.value!!), it.sender)
                    }
                    Info.addAll -> {
                        val items = it.args as MutableList<Observable<T>>

                        observableList.addAll(items.map { mapper(it.value!!) }, it.sender)
                    }
                    Info.addAllIndex -> {
                        val (index, items) = it.args as Pair<Int, MutableList<Observable<T>>>
                        observableList.addAll(index, items.map { mapper(it.value!!) }, it.sender)
                    }
                    Info.remove -> {
                        val observable = it.args as Observable<T>
                        observableList.remove(mapper(observable.value!!), it.sender)
                    }
                    Info.removeAt -> {
                        val (index, observable) = it.args as Pair<Int, Observable<T>>
                        observableList.removeAt(index, it.sender)
                    }
                    Info.removeAll -> {
                        val (items, observables) = it.args as Pair<Collection<T>, MutableList<Observable<T>>>
                        observableList.removeAll(items.map(mapper), it.sender)
                    }
                    Info.retainAll -> {
                        val (items, observables) = it.args as Pair<Collection<T>, MutableList<Observable<T>>>
                        observableList.retainAll(items.map(mapper), it.sender)
                    }
                    Info.clear -> {
                        observableList.clear(it.sender)
                    }
                    Info.itemUpdated -> {
                        val (observable, value) = it.args as Pair<Observable<T>, UpdateIndexedValue<T>>
                        observableList.getObservable(value.index)
                            .postValue(mapper(value.value), it.sender)
                    }
                }
                condition()
            }
        observers.add(result)
        return Observable.ThenOrRemove(observableList, { observers.remove(result) })
    }

    /**
     *  map to an other observable while it return true
     *  @param lifecycle lifecycle of the observer to automatically remove it from the observers when stopped
     *  @param observableList observer for the live data
     *  @param condition condition to continue to observe
     *  @param mapper mapper from the live data to the new one
     *  @return if the observer have been remove
     */
    fun <U> mapWhileTrue(
        lifecycle: LifecycleOwner,
        observableList: ObservableList<U> = ObservableList(),
        condition: () -> Boolean,
        mapper: (T) -> U
    ) = Observable.observeOnDestroy(lifecycle, mapWhileTrue(observableList, condition, mapper))

    /**
     *  map to an other observable
     *  @param observableList observer for the live data
     *  @param mapper mapper from the live data to the new one
     *  @return if the observer have been remove
     */
    fun <U> map(
        observableList: ObservableList<U> = ObservableList(),
        mapper: (T) -> U
    ) = mapWhileTrue(observableList, { true }, mapper)

    /**
     *  map to an other observable
     *  @param lifecycle lifecycle of the observer to automatically remove it from the observers when stopped
     *  @param observableList observer for the live data
     *  @param mapper mapper from the live data to the new one
     *  @return if the observer have been remove
     */
    fun <U> map(
        lifecycle: LifecycleOwner,
        observableList: ObservableList<U> = ObservableList(),
        mapper: (T) -> U
    ) = Observable.observeOnDestroy(lifecycle, map(observableList, mapper))

    /**
     *  map to an other observable once
     *  @param observableList observer for the live data
     *  @param mapper mapper from the live data to the new one
     *  @return if the observer have been remove
     */
    fun <U> mapOnce(
        observableList: ObservableList<U> = ObservableList(),
        mapper: (T) -> U
    ) = mapWhileTrue(observableList, { false }, mapper)

    /**
     *  map to an other observable once
     *  @param lifecycle lifecycle of the observer to automatically remove it from the observers when stopped
     *  @param observableList observer for the live data
     *  @param mapper mapper from the live data to the new one
     *  @return if the observer have been remove
     */
    fun <U> mapOnce(
        lifecycle: LifecycleOwner,
        observableList: ObservableList<U> = ObservableList(),
        mapper: (T) -> U
    ) = Observable.observeOnDestroy(lifecycle, mapOnce(observableList, mapper))

    /**
     *  group by mapper to an other observable while it return true
     *  @param observableMap observer for the live data
     *  @param condition condition to continue to observe
     *  @param mapper mapper from the live data to the new one
     *  @return if the observer have been remove
     */
    fun <U> groupWhileTrue(
        observableMap: ObservableMap<U, ObservableList<T>> = ObservableMap(),
        condition: () -> Boolean,
        mapper: (T) -> U
    ): Observable.ThenOrRemove<ObservableMap<U, ObservableList<T>>> {
        observableMap.clear()
        val addLambda: (ObserversInfo<T>, Observable<T>) -> Unit = { it, observable ->
            val key = mapper(observable.value!!)
            if (observableMap.containsKey(key)) {
                val o = observableMap.getObservable(key)!!
                o.value!!.add(observable, it.sender)
                o.postValue(o.value!!, it.sender)
            } else
                observableMap.put(
                    key,
                    ObservableList(observable = observable, sender = it.sender),
                    it.sender
                )
        }
        val removeLambda: (ObserversInfo<T>, Observable<T>) -> Unit = { it, observable ->
            val key = mapper(observable.value!!)
            observableMap[key]!!.remove(observable, it.sender)
            if (observableMap[key]!!.isEmpty())
                observableMap.remove(key)
            else {
                val o = observableMap.getObservable(key)!!
                o.postValue(o.value!!, it.sender)
            }
        }

        val result: (ObserversInfo<T>) -> Boolean =
            {
                when (it.info) {
                    Info.add -> addLambda(it, it.args as Observable<T>)
                    Info.addIndex -> addLambda(it, (it.args as Pair<Int, Observable<T>>).second)
                    Info.addAll -> {
                        val items = it.args as MutableList<Observable<T>>
                        for (item in items)
                            addLambda(it, item)
                    }
                    Info.addAllIndex -> {
                        val (_, items) = it.args as Pair<Int, MutableList<Observable<T>>>
                        for (item in items)
                            addLambda(it, item)
                    }
                    Info.remove -> removeLambda(it, it.args as Observable<T>)
                    Info.removeAt -> removeLambda(it, (it.args as Pair<Int, Observable<T>>).second)
                    Info.removeAll -> {
                        val (_, observables) = it.args as Pair<Collection<T>, MutableList<Observable<T>>>
                        for (observable in observables)
                            removeLambda(it, observable)
                    }
                    Info.retainAll -> {
                        val (_, observables) = it.args as Pair<Collection<T>, MutableList<Observable<T>>>
                        for (observable in observables)
                            removeLambda(it, observable)
                    }
                    Info.clear -> {
                        observableMap.clear(it.sender)
                    }
                    Info.itemUpdated -> {
                        val (observable, value) = it.args as Pair<Observable<T>, UpdateIndexedValue<T>>
                        var from: U? = null
                        for (key in observableMap.keys)
                            if (observableMap[key]!!.contains(observable)) {
                                from = key
                                break
                            }
                        val to = mapper(value.value)
                        if (from!! != to) {
                            observableMap[from]!!.remove(observable, it.sender)
                            if (observableMap[from]!!.isEmpty())
                                observableMap.remove(from)
                            else {
                                val o = observableMap.getObservable(from)!!
                                o.postValue(o.value!!, it.sender)
                            }
                            addLambda(it, observable)
                        } else {
                            val o = observableMap.getObservable(to)!!
                            o.postValue(o.value!!, it.sender)
                        }
                    }
                }
                condition()
            }
        observers.add(result)
        return Observable.ThenOrRemove(observableMap, { observers.remove(result) })
    }

    /**
     *  group by mapper to an other observable while it return true
     *  @param lifecycle lifecycle of the observer to automatically remove it from the observers when stopped
     *  @param observableMap observer for the live data
     *  @param condition condition to continue to observe
     *  @param mapper mapper from the live data to the new one
     *  @return if the observer have been remove
     */
    fun <U> groupWhileTrue(
        lifecycle: LifecycleOwner,
        observableMap: ObservableMap<U, ObservableList<T>> = ObservableMap(),
        condition: () -> Boolean,
        mapper: (T) -> U
    ) = Observable.observeOnDestroy(lifecycle, groupWhileTrue(observableMap, condition, mapper))

    /**
     *  group by mapper to an other observable
     *  @param observableMap observer for the live data
     *  @param mapper mapper from the live data to the new one
     *  @return if the observer have been remove
     */
    fun <U> group(
        observableMap: ObservableMap<U, ObservableList<T>> = ObservableMap(),
        mapper: (T) -> U
    ) = groupWhileTrue(observableMap, { true }, mapper)

    /**
     *  group by mapper to an other observable
     *  @param lifecycle lifecycle of the observer to automatically remove it from the observers when stopped
     *  @param observableMap observer for the live data
     *  @param mapper mapper from the live data to the new one
     *  @return if the observer have been remove
     */
    fun <U> group(
        lifecycle: LifecycleOwner,
        observableMap: ObservableMap<U, ObservableList<T>> = ObservableMap(),
        mapper: (T) -> U
    ) = Observable.observeOnDestroy(lifecycle, group(observableMap, mapper))

    /**
     *  group by mapper to an other observable once
     *  @param observableMap observer for the live data
     *  @param mapper mapper from the live data to the new one
     *  @return if the observer have been remove
     */
    fun <U> groupOnce(
        observableMap: ObservableMap<U, ObservableList<T>> = ObservableMap(),
        mapper: (T) -> U
    ) = groupWhileTrue(observableMap, { false }, mapper)

    /**
     *  group by mapper to an other observable once
     *  @param lifecycle lifecycle of the observer to automatically remove it from the observers when stopped
     *  @param observableMap observer for the live data
     *  @param mapper mapper from the live data to the new one
     *  @return if the observer have been remove
     */
    fun <U> groupOnce(
        lifecycle: LifecycleOwner,
        observableMap: ObservableMap<U, ObservableList<T>> = ObservableMap(),
        mapper: (T) -> U
    ) = Observable.observeOnDestroy(lifecycle, group(observableMap, mapper))

    private fun itemAdded(value: UpdateIndexedValue<T>) {
        run(Runnable {
            for (obs in observersAdd.toList())
                if (!obs(value))
                leaveAdd(obs)
        })
    }

    private fun itemRemoved(value: UpdateIndexedValue<T>) {
        run(Runnable {
            for (obs in observersRemove.toList())
                if (!obs(value))
                    leaveRemove(obs)
        })
    }

    private fun itemUpdated(observable: Observable<T>, value: UpdateIndexedValue<T>) {
        run(Runnable {
            for (obs in observersItemUpdate.toList())
                if (!obs(value))
                    leaveUpdate(obs)
            notifyUpdate(value.sender, Info.itemUpdated, Pair(observable, value))
        })
    }

    private fun notifyUpdate(sender: Any? = null, info: Info, args: Any? = null) {
        if (observers.isNotEmpty()) {
            val valueList =
                ObserversInfo(this as List<T>, info, args, sender)
            for (obs in observers.toList())
                if (!obs(valueList))
                    leave(obs)
        }
    }

    /**
     * An iterator over a mutable collection that supports indexed access. Provides the ability
     * to add, modify and remove elements while iterating.
     */
    inner class ObservableListIterator(var index: Int) : MutableListIterator<T> {
        override fun hasNext() = listValues.size > index
        override fun next() = get(index++)
        override fun nextIndex() = index + 1

        override fun hasPrevious() = index > 0
        override fun previous() = get(--index)
        override fun previousIndex() = index


        override fun add(element: T) = add(index++, element)

        override fun remove() {
            if (hasPrevious()) {
                remove(getObservable(--index), this)
            }
        }

        override fun set(element: T) {
            set(index, element)
        }
    }

    override fun iterator(): MutableIterator<T> = ObservableListIterator(0)
    override fun listIterator(): MutableListIterator<T> = ObservableListIterator(0)
    override fun listIterator(index: Int): MutableListIterator<T> = ObservableListIterator(index)
}