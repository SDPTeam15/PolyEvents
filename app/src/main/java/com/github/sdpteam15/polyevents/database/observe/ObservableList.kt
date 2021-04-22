package com.github.sdpteam15.polyevents.database.observe

import androidx.lifecycle.LifecycleOwner
import com.github.sdpteam15.polyevents.helper.HelperFunctions.observeOnDestroy
import com.github.sdpteam15.polyevents.helper.HelperFunctions.run

class IndexedValue<T>(val value: T, val index: Int)

class ObservableList<T> : MutableList<T> {
    private val observersAdd = mutableSetOf<(UpdateArgs<IndexedValue<T>>) -> Boolean>()
    private val observersRemove = mutableSetOf<(UpdateArgs<T>) -> Boolean>()
    private val observersItemUpdate = mutableSetOf<(UpdateArgs<IndexedValue<T>>) -> Boolean>()
    private val observers = mutableSetOf<(UpdateArgs<List<T>>) -> Boolean>()

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
                observable.observe {
                    itemUpdated(
                        UpdateArgs(
                            IndexedValue(
                                it.value,
                                index
                            ), it.sender
                        )
                    )
                }
            itemAdded(UpdateArgs(IndexedValue(observable.value!!, index), sender))
            if (notify) {
                notifyUpdate(sender)
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
        add(Observable(item), sender, notify)!!

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
        notifyUpdate(sender)
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
        itemRemoved(UpdateArgs(observable.value!!, sender))
        if (notify) {
            notifyUpdate(sender)
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
    private fun remove(item: T, sender: Any? = null, notify: Boolean): Observable<T>? {
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
    fun removeAll(items: Collection<T>, sender: Any? = null): Boolean {
        var res = true
        for (item: T in items) {
            res = res && (remove(item, sender, false) != null)
        }
        notifyUpdate(sender)
        return res
    }

    override fun removeAt(index: Int): T = values.removeAt(index).value!!

    override fun clear() = clear(null)

    /**
     * Clear all items.
     * @param sender The source of the event.
     */
    fun clear(sender: Any? = null) {
        for (item in values.toList()) {
            remove(item, sender, false)
        }
        notifyUpdate(sender)
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
                observable.observe {
                    itemUpdated(
                        UpdateArgs(
                            IndexedValue(
                                it.value,
                                index
                            ), it.sender
                        )
                    )
                }
            itemAdded(UpdateArgs(IndexedValue(observable.value!!, index), sender))
            if (notify) {
                notifyUpdate(sender)
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
        notifyUpdate(sender)
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
            if(item.value !in items)
                toremove.add(item)
        }
        for (item in toremove)
            remove(item, sender, false)
        notifyUpdate(sender)
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
    fun observeAddWithIndexWhileTrue(observer: (UpdateArgs<IndexedValue<T>>) -> Boolean): () -> Boolean {
        observersAdd.add(observer)
        return { leaveAdd(observer) }
    }

    /**
     *  Add an observer for the live data additions while it return true
     *  @param lifecycle lifecycle of the observer to automatically remove it from the observers when stopped
     *  @param observer observer for the live data additions
     *  @return a method to remove the observer
     */
    fun observeAddWithIndexWhileTrue(
        lifecycle: LifecycleOwner,
        observer: (UpdateArgs<IndexedValue<T>>) -> Boolean
    ) =
        observeOnDestroy(lifecycle, observeAddWithIndexWhileTrue(observer))

    /**
     *  Add an observer for the live data additions while it return true
     *  @param observer observer for the live data additions
     *  @return a method to remove the observer
     */
    fun observeAddWhileTrue(observer: (UpdateArgs<T>) -> Boolean) = observeAddWithIndexWhileTrue {
        observer(UpdateArgs(it.value.value, it.sender))
    }

    /**
     *  Add an observer for the live data additions while it return true
     *  @param lifecycle lifecycle of the observer to automatically remove it from the observers when stopped
     *  @param observer observer for the live data additions
     *  @return a method to remove the observer
     */
    fun observeAddWhileTrue(lifecycle: LifecycleOwner, observer: (UpdateArgs<T>) -> Boolean) =
        observeOnDestroy(lifecycle, observeAddWhileTrue(observer))

    /**
     *  Add an observer for the live data additions
     *  @param observer observer for the live data additions
     *  @return a method to remove the observer
     */
    fun observeAddWithIndex(observer: (UpdateArgs<IndexedValue<T>>) -> Unit) =
        observeAddWithIndexWhileTrue {
            observer(it)
            true
        }

    /**
     *  Add an observer for the live data additions
     *  @param lifecycle lifecycle of the observer to automatically remove it from the observers when stopped
     *  @param observer observer for the live data additions
     *  @return a method to remove the observer
     */
    fun observeAddWithIndex(
        lifecycle: LifecycleOwner,
        observer: (UpdateArgs<IndexedValue<T>>) -> Unit
    ) =
        observeOnDestroy(lifecycle, observeAddWithIndex(observer))

    /**
     *  Add an observer for the live data additions
     *  @param observer observer for the live data additions
     *  @return a method to remove the observer
     */
    fun observeAdd(observer: (UpdateArgs<T>) -> Unit) = observeAddWhileTrue {
        observer(it)
        true
    }

    /**
     *  Add an observer for the live data additions
     *  @param lifecycle lifecycle of the observer to automatically remove it from the observers when stopped
     *  @param observer observer for the live data additions
     *  @return a method to remove the observer
     */
    fun observeAdd(lifecycle: LifecycleOwner, observer: (UpdateArgs<T>) -> Unit) =
        observeOnDestroy(lifecycle, observeAdd(observer))

    /**
     *  Add an observer for the live data additions once
     *  @param observer observer for the live data additions
     *  @return a method to remove the observer
     */
    fun observeAddWithIndexOnce(observer: (UpdateArgs<IndexedValue<T>>) -> Unit) =
        observeAddWithIndexWhileTrue {
            observer(it)
            false
        }

    /**
     *  Add an observer for the live data additions once
     *  @param lifecycle lifecycle of the observer to automatically remove it from the observers when stopped
     *  @param observer observer for the live data additions
     *  @return a method to remove the observer
     */
    fun observeAddWithIndexOnce(
        lifecycle: LifecycleOwner,
        observer: (UpdateArgs<IndexedValue<T>>) -> Unit
    ) =
        observeOnDestroy(lifecycle, observeAddWithIndexOnce(observer))

    /**
     *  Add an observer for the live data additions once
     *  @param observer observer for the live data additions
     *  @return a method to remove the observer
     */
    fun observeAddOnce(observer: (UpdateArgs<T>) -> Unit) = observeAddWhileTrue {
        observer(it)
        false
    }

    /**
     *  Add an observer for the live data additions once
     *  @param lifecycle lifecycle of the observer to automatically remove it from the observers when stopped
     *  @param observer observer for the live data additions
     *  @return a method to remove the observer
     */
    fun observeAddOnce(lifecycle: LifecycleOwner, observer: (UpdateArgs<T>) -> Unit) =
        observeOnDestroy(lifecycle, observeAddOnce(observer))

    /**
     *  remove an observer for the live data
     *  @param observer observer for the live data
     *  @return if the observer have been remove
     */
    fun leaveAdd(observer: (UpdateArgs<IndexedValue<T>>) -> Boolean) = observersAdd.remove(observer)

    /**
     *  Add an observer for the live data removals while it return true
     *  @param observer observer for the live data removals
     *  @return a method to remove the observer
     */
    fun observeRemoveWhileTrue(observer: (UpdateArgs<T>) -> Boolean): () -> Boolean {
        observersRemove.add(observer)
        return { leaveRemove(observer) }
    }

    /**
     *  Add an observer for the live data removals while it return true
     *  @param observer lifecycle of the observer to automatically remove it from the observers when stopped
     *  @return a method to remove the observer
     */
    fun observeRemoveWhileTrue(
        lifecycle: LifecycleOwner,
        observer: (UpdateArgs<T>) -> Boolean
    ): () -> Boolean =
        observeOnDestroy(lifecycle, observeRemoveWhileTrue(observer))

    /**
     *  Add an observer for the live data removals
     *  @param observer observer for the live data removals
     *  @return a method to remove the observer
     */
    fun observeRemove(observer: (UpdateArgs<T>) -> Unit) = observeRemoveWhileTrue {
        observer(it)
        true
    }

    /**
     *  Add an observer for the live data removals
     *  @param lifecycle lifecycle of the observer to automatically remove it from the observers when stopped
     *  @param observer observer for the live data removals
     *  @return a method to remove the observer
     */
    fun observeRemove(lifecycle: LifecycleOwner, observer: (UpdateArgs<T>) -> Unit) =
        observeOnDestroy(lifecycle, observeRemove(observer))

    /**
     *  Add an observer for the live data removals once
     *  @param observer observer for the live data removals
     *  @return a method to remove the observer
     */
    fun observeRemoveOnce(observer: (UpdateArgs<T>) -> Unit) = observeRemoveWhileTrue {
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
        observer: (UpdateArgs<T>) -> Unit
    ) = observeOnDestroy(lifecycle, observeRemove(observer))

    /**
     *  remove an observer for the live data
     *  @param observer observer for the live data
     *  @return if the observer have been remove
     */
    fun leaveRemove(observer: (UpdateArgs<T>) -> Boolean) = observersRemove.remove(observer)

    /**
     *  Add an observer for the live data updating while it return tru
     *  @param observer observer for the live data updating
     *  @return a method to remove the observer
     */
    fun observeUpdateWhileTrue(observer: (UpdateArgs<IndexedValue<T>>) -> Boolean): () -> Boolean {
        observersItemUpdate.add(observer)
        return { leaveUpdate(observer) }
    }

    /**
     *  Add an observer for the live data updating while it return tru
     *  @param lifecycle lifecycle of the observer to automatically remove it from the observers when stopped
     *  @param observer observer for the live data updating
     *  @return a method to remove the observer
     */
    fun observeUpdateWhileTrue(
        lifecycle: LifecycleOwner,
        observer: (UpdateArgs<IndexedValue<T>>) -> Boolean
    ) = observeOnDestroy(lifecycle, observeUpdateWhileTrue(observer))

    /**
     *  Add an observer for the live data updating
     *  @param observer observer for the live data updating
     *  @return a method to remove the observer
     */
    fun observeUpdate(observer: (UpdateArgs<IndexedValue<T>>) -> Unit) = observeUpdateWhileTrue {
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
        observer: (UpdateArgs<IndexedValue<T>>) -> Unit
    ) = observeOnDestroy(lifecycle, observeUpdate(observer))

    /**
     *  Add an observer for the live data updating once
     *  @param observer observer for the live data updating
     *  @return a method to remove the observer
     */
    fun observeUpdateOnce(observer: (UpdateArgs<IndexedValue<T>>) -> Unit) =
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
        observer: (UpdateArgs<IndexedValue<T>>) -> Unit
    ) = observeOnDestroy(lifecycle, observeUpdate(observer))

    /**
     *  remove an observer for the live data
     *  @param observer observer for the live data
     *  @return if the observer have been remove
     */
    fun leaveUpdate(observer: (UpdateArgs<IndexedValue<T>>) -> Boolean) =
        observersItemUpdate.remove(observer)

    /**
     *  Add an observer for the live data while it return true
     *  @param observer observer for the live data
     *  @return a method to remove the observer
     */
    fun observeWhileTrue(observer: (UpdateArgs<List<T>>) -> Boolean): () -> Boolean {
        observers.add(observer)
        return { leave(observer) }
    }

    /**
     *  Add an observer for the live data while it return true
     *  @param lifecycle lifecycle of the observer to automatically remove it from the observers when stopped
     *  @param observer observer for the live data
     *  @return a method to remove the observer
     */
    fun observeWhileTrue(lifecycle: LifecycleOwner, observer: (UpdateArgs<List<T>>) -> Boolean) =
        observeOnDestroy(lifecycle, observeWhileTrue(observer))

    /**
     *  Add an observer for the live data
     *  @param observer observer for the live data
     *  @return a method to remove the observer
     */
    fun observe(observer: (UpdateArgs<List<T>>) -> Unit) = observeWhileTrue {
        observer(it)
        true
    }

    /**
     *  Add an observer for the live data
     *  @param lifecycle lifecycle of the observer to automatically remove it from the observers when stopped
     *  @param observer observer for the live data
     *  @return a method to remove the observer
     */
    fun observe(lifecycle: LifecycleOwner, observer: (UpdateArgs<List<T>>) -> Unit) =
        observeOnDestroy(lifecycle, observe(observer))

    /**
     *  Add an observer for the live data once
     *  @param observer observer for the live data
     *  @return a method to remove the observer
     */
    fun observeOnce(observer: (UpdateArgs<List<T>>) -> Unit) = observeWhileTrue {
        observer(it)
        false
    }

    /**
     *  Add an observer for the live data once
     *  @param lifecycle lifecycle of the observer to automatically remove it from the observers when stopped
     *  @param observer observer for the live data
     *  @return a method to remove the observer
     */
    fun observeOnce(lifecycle: LifecycleOwner, observer: (UpdateArgs<List<T>>) -> Unit) =
        observeOnDestroy(lifecycle, observeOnce(observer))

    /**
     *  remove an observer for the live data
     *  @param observer observer for the live data
     *  @return if the observer have been remove
     */
    fun leave(observer: (UpdateArgs<List<T>>) -> Boolean) = observers.remove(observer)

    private fun itemAdded(value: UpdateArgs<IndexedValue<T>>) {
        run(Runnable {
            val toRemove = mutableListOf<(UpdateArgs<IndexedValue<T>>) -> Boolean>()
            for (obs in observersAdd)
                if (!obs(value))
                    toRemove.add(obs)
            for (obs in toRemove)
                leaveAdd(obs)
        })
    }

    private fun itemRemoved(value: UpdateArgs<T>) {
        run(Runnable {
            val toRemove = mutableListOf<(UpdateArgs<T>) -> Boolean>()
            for (obs in observersRemove)
                if (!obs(value))
                    toRemove.add(obs)
            for (obs in toRemove)
                leaveRemove(obs)
        })
    }

    private fun itemUpdated(value: UpdateArgs<IndexedValue<T>>) {
        run(Runnable {
            val toRemove = mutableListOf<(UpdateArgs<IndexedValue<T>>) -> Boolean>()
            for (obs in observersItemUpdate)
                if (!obs(value))
                    toRemove.add(obs)
            for (obs in toRemove)
                leaveUpdate(obs)
            notifyUpdate(value.sender)
        })
    }

    fun notifyUpdate(sender: Any? = null) {
        if (observers.isNotEmpty()) {
            val valueList = this.value
            val toRemove = mutableListOf<(UpdateArgs<List<T>>) -> Boolean>()
            for (obs in observers)
                if (!obs(UpdateArgs(valueList, sender)))
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