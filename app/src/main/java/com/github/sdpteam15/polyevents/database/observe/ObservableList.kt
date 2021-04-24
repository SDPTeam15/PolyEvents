package com.github.sdpteam15.polyevents.database.observe

import androidx.lifecycle.LifecycleOwner
import com.github.sdpteam15.polyevents.helper.HelperFunctions.run


class ObservableList<T> : MutableList<T> {

    class UpdateIndexedValue<T>(val value: T, val index: Int, val sender: Any?)
    class ObserversInfo<T>(
        val value: Observable.UpdateValue<List<T>>,
        val info: Info,
        val args: Any?,
        val sender: Any?
    )

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

    private val observersAdd = mutableSetOf<(UpdateIndexedValue<T>) -> Boolean>()
    private val observersRemove = mutableSetOf<(UpdateIndexedValue<T>) -> Boolean>()
    private val observersItemUpdate = mutableSetOf<(UpdateIndexedValue<T>) -> Boolean>()
    private val observers = mutableSetOf<(ObserversInfo<T>) -> Boolean>()

    private val values: MutableList<Observable<T>> = mutableListOf()
    private val removeItemObserver: MutableMap<Observable<T>, () -> Boolean> = mutableMapOf()

    override val size get() = values.size

    /**
     * To list of T
     */
    val value: List<T>
        get() {
            val v: MutableList<T> = mutableListOf()
            for (observableValue in values) {
                v.add(observableValue.value!!)
            }
            return v
        }

    /**
     * Returns the element at the specified index in the list.
     * @return the element at the specified index in the list.
     */
    override operator fun get(index: Int): T = values[index].value!!

    /**
     * Returns the observable element at the specified index in the list.
     * @return the observable element at the specified index in the list.
     */
    fun getObservable(index: Int): Observable<T> = values[index]

    /**
     * Replaces the element at the specified position in this list with the specified element.
     * @param index position in this list
     * @param value new value
     * @param sender The source of the event.
     */
    fun set(index: Int, value: T, sender: Any?): T {
        val res = this[index]
        values[index].postValue(value, sender)
        return res
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
        sender: Any? = null,
        notify: Boolean
    ): Observable<T>? {
        if (observable.value != null) {
            if (!values.add(observable))
                return null
            val index = values.indexOf(observable)
            removeItemObserver[observable] =
                observable.observe(false) {
                    itemUpdated(
                        UpdateIndexedValue(
                            it.value,
                            index,
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
    private fun add(item: T, sender: Any? = null, notify: Boolean): Observable<T>? =
        add(Observable(item), sender)!!

    override fun addAll(elements: Collection<T>) = addAll(elements, null)

    /**
     * Add all items in the list.
     * @param items items list.
     * @param sender The source of the event.
     */
    fun addAll(items: Collection<T>, sender: Any? = null): Boolean {
        var res = true
        for (item: T in items) {
            res = res && (add(item, sender, false) != null)
        }
        notifyUpdate(sender, Info.addAll, items)
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
        sender: Any? = null,
        notify: Boolean
    ): Observable<T>? {
        if (!values.remove(observable)) {
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
        for (item: T in items) {
            res = res && (remove(item, sender, false) != null)
        }
        notifyUpdate(sender, Info.removeAll, items)
        return res
    }

    /**
     * Remove an item.
     * @param index index of the item to remove.
     * @param sender The source of the event.
     * @return observable removed.
     */
    fun removeAt( index: Int, sender: Any? = null) = removeAt(index, sender, true)
    private fun removeAt(
        index: Int,
        sender: Any?,
        notify: Boolean
    ): Observable<T>? {
        val observable = values.removeAt(index) ?: return null
        removeItemObserver[observable]!!()
        removeItemObserver.remove(observable)
        itemRemoved(UpdateIndexedValue(observable.value!!, index, sender))
        if (notify) {
            notifyUpdate(sender, Info.removeAt, index)
        }
        return observable
    }

    override fun removeAt(index: Int): T = removeAt(index, null)!!.value!!

    override fun clear() = clear(null)

    /**
     * Clear all items.
     * @param sender The source of the event.
     */
    fun clear(sender: Any? = null) {
        for (item in values.toList()) {
            remove(item, sender, false)
        }
        notifyUpdate(sender, Info.clear)
    }

    private fun find(item: T): Observable<T>? = values.find { it.value == item }

    override fun isEmpty() = values.isEmpty()
    override fun contains(element: T) = (find(element) != null)

    override fun indexOf(element: T): Int {
        val item = find(element) ?: return -1
        return values.indexOf(item)
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
        return values.lastIndexOf(item)
    }

    private fun add(
        index: Int,
        observable: Observable<T>,
        sender: Any? = null,
        notify: Boolean
    ): Observable<T>? {
        if (observable.value != null) {
            values.add(index, observable)
            removeItemObserver[observable] =
                observable.observe(false) {
                    itemUpdated(
                        UpdateIndexedValue(
                            it.value,
                            index,
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

    private fun add(index: Int, item: T, sender: Any? = null, notify: Boolean) =
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
    fun addAll(index: Int, items: Collection<T>, sender: Any? = null): Boolean {
        var res = true
        var i = 0
        for (item: T in items) {
            res = res && (add((i++ + index), item, sender, false) != null)
        }
        notifyUpdate(sender, Info.addAllIndex, Pair(index, items))
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
        for (item in values) {
            if (item.value !in items)
                toremove.add(item)
        }
        for (item in toremove)
            remove(item, sender, false)
        notifyUpdate(sender, Info.retainAll, items)
        return values.size != 0
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
    ): Observable.AfterRemovable<ObservableList<T>> {
        observersAdd.add(observer)
        return Observable.AfterRemovable(this, { leaveAdd(observer) })
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
    fun observeRemoveWhileTrue(observer: (UpdateIndexedValue<T>) -> Boolean): Observable.AfterRemovable<ObservableList<T>> {
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
    ): Observable.AfterRemovable<ObservableList<T>> {
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
    fun observeWhileTrue(observer: (Observable.UpdateValue<List<T>>) -> Boolean): Observable.AfterRemovable<ObservableList<T>> {
        val resultObserver: (ObserversInfo<T>) -> Boolean = {
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
    ): Observable.AfterRemovable<ObservableList<U>> {
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
                        val items = it.args as Collection<T>
                        observableList.addAll(items.map(mapper), it.sender)
                    }
                    Info.addAllIndex -> {
                        val (index, items) = it.args as Pair<Int, Collection<T>>
                        observableList.addAll(index, items.map(mapper), it.sender)
                    }
                    Info.remove -> {
                        val observable = it.args as Observable<T>
                        observableList.remove(mapper(observable.value!!), it.sender)
                    }
                    Info.removeAt -> {
                        val index = it.args as Int
                        observableList.removeAt(index, it.sender)
                    }
                    Info.removeAll -> {
                        val items = it.args as Collection<T>
                        observableList.removeAll(items.map(mapper), it.sender)
                    }
                    Info.retainAll -> {
                        val items = it.args as Collection<T>
                        observableList.retainAll(items.map(mapper), it.sender)
                    }
                    Info.clear -> {
                        observableList.clear(it.sender)
                    }
                    Info.itemUpdated -> {
                        val value = it.args as UpdateIndexedValue<T>
                        observableList.getObservable(value.index)
                            .postValue(mapper(value.value), it.sender)
                    }
                }
                condition()
            }
        observers.add(result)
        return Observable.AfterRemovable(observableList, { observers.remove(result) })
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

    private fun itemAdded(value: UpdateIndexedValue<T>) {
        run(Runnable {
            val toRemove = mutableListOf<(UpdateIndexedValue<T>) -> Boolean>()
            for (obs in observersAdd)
                if (!obs(value))
                    toRemove.add(obs)
            for (obs in toRemove)
                leaveAdd(obs)
        })
    }

    private fun itemRemoved(value: UpdateIndexedValue<T>) {
        run(Runnable {
            val toRemove = mutableListOf<(UpdateIndexedValue<T>) -> Boolean>()
            for (obs in observersRemove)
                if (!obs(value))
                    toRemove.add(obs)
            for (obs in toRemove)
                leaveRemove(obs)
        })
    }

    private fun itemUpdated(value: UpdateIndexedValue<T>) {
        run(Runnable {
            val toRemove = mutableListOf<(UpdateIndexedValue<T>) -> Boolean>()
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
                ObserversInfo(Observable.UpdateValue(this as List<T>, sender), info, args, sender)
            val toRemove = mutableListOf<(ObserversInfo<T>) -> Boolean>()
            for (obs in observers)
                if (!obs(valueList))
                    toRemove.add(obs)
            for (obs in toRemove)
                leave(obs)
        }
    }

    inner class ObservableListIterator(var index: Int) : MutableListIterator<T> {
        override fun hasNext() = values.size > index
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