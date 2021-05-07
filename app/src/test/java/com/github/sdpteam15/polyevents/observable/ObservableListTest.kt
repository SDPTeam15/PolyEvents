package com.github.sdpteam15.polyevents.observable

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.github.sdpteam15.polyevents.model.observable.Observable
import com.github.sdpteam15.polyevents.model.observable.ObservableList
import org.junit.Test
import org.mockito.Mockito
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class ObservableListTest {
    @Test
    fun lambdaIsUpdatedOnAdd() {
        var isUpdateAdd = false
        var isUpdate = false
        val observable = ObservableList<Int>()
        val list: MutableList<Int> = observable


        val suppressorAdd = observable.observeAdd {
            assert(it.index < 5)
            isUpdateAdd = true
        }
        val suppressor = observable.observe { isUpdate = true }

        list.add(0, 0)

        assertEquals(true, isUpdateAdd)
        assertEquals(true, isUpdate)
        assertEquals(1, observable.size)
        assertEquals(0, observable[0])

        isUpdateAdd = false
        isUpdate = false

        suppressorAdd.remove()
        suppressor.remove()

        list.add(0, 1)

        assertEquals(false, isUpdateAdd)
        assertEquals(false, isUpdate)
        assertEquals(2, observable.size)
        assertEquals(1, observable[0])
        assertEquals(0, observable[1])
        assertEquals(1, observable.getObservable(0).value)
        assertNull(observable.add(Observable()))
    }

    @Test
    fun lambdaIsUpdatedOnRemove() {
        var isUpdateRemove = false
        var isUpdate = false
        val observable = ObservableList<Int>()
        val list: MutableList<Int> = observable

        list.add(0)
        list.add(1)

        val suppressorRemove = observable.observeRemove { isUpdateRemove = true }
        val suppressor = observable.observe { isUpdate = true }

        list.remove(0)

        assertEquals(true, isUpdateRemove)
        assertEquals(true, isUpdate)
        assertEquals(1, observable.size)
        assertEquals(1, observable[0])

        isUpdateRemove = false
        isUpdate = false

        suppressorRemove.remove()
        suppressor.remove()

        list.remove(1)

        assertEquals(false, isUpdateRemove)
        assertEquals(false, isUpdate)
        assertEquals(0, observable.size)
    }

    @Test
    fun lambdaIsUpdatedOnItemUpdatedWithValue() {
        var isUpdateUpdate = false
        var isUpdate = false
        val observable = ObservableList<Int>()
        val list: MutableList<Int> = observable

        list.add(0)

        val suppressorUpdate = observable.observeUpdate { isUpdateUpdate = true }
        val suppressor = observable.observe { isUpdate = true }

        list[0] = 1

        assertEquals(true, isUpdateUpdate)
        assertEquals(true, isUpdate)
        assertEquals(1, observable.size)
        assertEquals(1, observable[0])

        isUpdateUpdate = false
        isUpdate = false

        suppressorUpdate.remove()
        suppressor.remove()

        list[0] = 0

        assertEquals(false, isUpdateUpdate)
        assertEquals(false, isUpdate)
        assertEquals(1, observable.size)
        assertEquals(0, observable[0])
    }

    @Test
    fun lambdaIsUpdatedOnItemUpdatedWithObservable() {
        var isUpdateUpdate = false
        var isUpdate = false
        val observable = ObservableList<Int>()

        observable.add(0)

        val suppressorUpdate = observable.observeUpdate { isUpdateUpdate = true }
        val suppressor = observable.observe { isUpdate = true }

        observable[0] = 1

        assertEquals(true, isUpdateUpdate)
        assertEquals(true, isUpdate)
        assertEquals(1, observable.size)
        assertEquals(1, observable[0])

        isUpdateUpdate = false
        isUpdate = false

        suppressorUpdate.remove()
        suppressor.remove()

        observable[0] = 0

        assertEquals(false, isUpdateUpdate)
        assertEquals(false, isUpdate)
        assertEquals(1, observable.size)
        assertEquals(0, observable[0])
    }

    @Test
    fun remove() {
        val observable = ObservableList<Int>()
        observable.add(0)

        observable.remove(1)
        assertEquals(1, observable.size)
        observable.remove(Observable())
        assertEquals(1, observable.size)
    }

    @Test
    fun lambdaIsUpdatedWithLifecycleOwner() {
        val isUpdate = mutableListOf<Int>()
        val observable = ObservableList<Boolean>()
        val mockedLifecycleOwner = Mockito.mock(LifecycleOwner::class.java)
        val mockedLifecycle = Mockito.mock(Lifecycle::class.java)
        Mockito.`when`(mockedLifecycleOwner.lifecycle).thenReturn(mockedLifecycle)
        val list: MutableList<Boolean> = observable

        isUpdate.add(0)
        observable.observeAddWhileTrue(mockedLifecycleOwner) { 1 > isUpdate[0]++ }
        isUpdate.add(0)
        observable.observeOnce(mockedLifecycleOwner) { isUpdate[1]++ }
        isUpdate.add(0)
        observable.observeRemoveOnce(mockedLifecycleOwner) { isUpdate[2]++ }
        isUpdate.add(0)
        observable.observeRemoveWhileTrue(mockedLifecycleOwner) { 1 > isUpdate[3]++ }
        isUpdate.add(0)
        observable.observeUpdate(mockedLifecycleOwner) { isUpdate[4]++ }
        isUpdate.add(0)
        observable.observeUpdateOnce(mockedLifecycleOwner) { isUpdate[5]++ }
        isUpdate.add(0)
        observable.observeUpdateWhileTrue(mockedLifecycleOwner) { 1 > isUpdate[6]++ }
        isUpdate.add(0)
        observable.observeWhileTrue(mockedLifecycleOwner) { 1 > isUpdate[7]++ }
        isUpdate.add(0)
        observable.observeAdd(mockedLifecycleOwner) { isUpdate[8]++ }
        isUpdate.add(0)
        observable.observeAddOnce(mockedLifecycleOwner) { isUpdate[9]++ }

        list.addAll(MutableList(3) { true })

        list[0] = false
        list[1] = false
        list[2] = false

        observable.clear()

        assertEquals("[2, 1, 1, 2, 3, 1, 2, 2, 3, 1]", isUpdate.toString())
    }

    @Test
    fun listIsImplemented() {
        assertEquals(1, ObservableList(listOf(0)).size)

        val list: MutableList<Int> = ObservableList()

        assertFailsWith<NotImplementedError> { list.subList(0,0) }

        list.add(0)
        list.add(1)
        list.add(2)

        assert(list.containsAll(mutableListOf(0)))
        assert(list.indexOf(1) == 1)
        assert(list.lastIndexOf(1) == 1)
        assert(list.contains(0))
        assert(list.containsAll(mutableListOf(0)))
        assert(list.retainAll(mutableListOf(0)))
        assert(list.removeAll(mutableListOf(0)))
        assert(list.isEmpty())

        list.add(0)
        list.add(1)
        list.add(2)

        val iterator = list.iterator() as MutableListIterator<Int>

        assert(iterator.hasNext())
        assertNotNull(iterator.nextIndex())
        assertNotNull(iterator.next())

        iterator.set(-1)
        assertEquals(-1, list[1])

        assert(iterator.hasPrevious())
        assertNotNull(iterator.previousIndex())
        assertNotNull(iterator.previous())


        iterator.add(-1)
        assertEquals(-1, list[0])

        iterator.remove()
        assertEquals(0, list[0])
    }

    @Test
    fun map(){
        var updated = false
        val observableList = ObservableList<Int>()
        val list: MutableList<Int> = observableList
        val mappedObservableList = observableList.map{it.hashCode()}.then

        assertFailsWith<NotImplementedError> { list.subList(0,0) }

        mappedObservableList.observeOnce { updated = true }
        list.add(0)
        assert(updated)
        updated = false
        list.add(1)
        list.add(2)

        mappedObservableList.observeOnce { updated = true }
        assert(list.retainAll(mutableListOf(0)))
        assert(updated)
        updated = false

        mappedObservableList.observeOnce { updated = true }
        assert(list.removeAll(mutableListOf(0)))
        assert(updated)
        updated = false

        mappedObservableList.observeOnce { updated = true }
        list.addAll(listOf(0, 1, 2))
        assert(updated)
        updated = false

        mappedObservableList.observeOnce { updated = true }
        list.remove(0)
        assert(updated)
        updated = false

        mappedObservableList.observeOnce { updated = true }
        list.removeAt(0)
        assert(updated)
        updated = false

        mappedObservableList.observeOnce { updated = true }
        list.add(0, 0)
        assert(updated)
        updated = false

        mappedObservableList.observeOnce { updated = true }
        list.addAll(0, listOf(4))
        assert(updated)
        updated = false

        mappedObservableList.observeOnce { updated = true }
        list[0] = 1
        assert(updated)
        updated = false

        mappedObservableList.observeOnce { updated = true }
        list.clear()
        assert(updated)
        updated = false

        val mockedLifecycleOwner = Mockito.mock(LifecycleOwner::class.java)
        val mockedLifecycle = Mockito.mock(Lifecycle::class.java)
        Mockito.`when`(mockedLifecycleOwner.lifecycle).thenReturn(mockedLifecycle)

        assertNotNull(observableList.mapWhileTrue(condition = {true}) { it })
        assertNotNull(observableList.mapWhileTrue(mockedLifecycleOwner, condition = {true}) { it })
        assertNotNull(observableList.map{ it })
        assertNotNull(observableList.map(mockedLifecycleOwner) { it })
        assertNotNull(observableList.mapOnce{ it })
        assertNotNull(observableList.mapOnce(mockedLifecycleOwner) { it })
    }

    @Test
    fun group(){
        var updated = false
        val observableList = ObservableList<Int>()
        val list: MutableList<Int> = observableList
        val mappedObservableMap = observableList.group{ it % 2 }.then

        assertFailsWith<NotImplementedError> { list.subList(0,0) }

        mappedObservableMap.observeOnce { updated = true }
        list.add(0)
        assert(updated)
        updated = false
        list.add(1)
        list.add(2)

        mappedObservableMap.observeOnce { updated = true }
        assert(list.retainAll(mutableListOf(0)))
        assert(updated)
        updated = false

        mappedObservableMap.observeOnce { updated = true }
        assert(list.removeAll(mutableListOf(0)))
        assert(updated)
        updated = false

        mappedObservableMap.observeOnce { updated = true }
        list.addAll(listOf(0, 1, 2))
        assert(updated)
        updated = false

        mappedObservableMap.observeOnce {
            updated = true
        }
        list.remove(0)
        assert(updated)
        updated = false

        mappedObservableMap.observeOnce { updated = true }
        list.removeAt(0)
        assert(updated)
        updated = false

        mappedObservableMap.observeOnce { updated = true }
        list.add(0, 0)
        assert(updated)
        updated = false

        mappedObservableMap.observeOnce { updated = true }
        list.addAll(0, listOf(4))
        assert(updated)
        updated = false

        mappedObservableMap.observeOnce { updated = true }
        list[0] = 1
        assert(updated)
        updated = false

        mappedObservableMap.observeOnce { updated = true }
        list[0] = 3
        assert(updated)
        updated = false

        mappedObservableMap.observeOnce { updated = true }
        list.clear()
        assert(updated)
        updated = false

        val mockedLifecycleOwner = Mockito.mock(LifecycleOwner::class.java)
        val mockedLifecycle = Mockito.mock(Lifecycle::class.java)
        Mockito.`when`(mockedLifecycleOwner.lifecycle).thenReturn(mockedLifecycle)

        assertNotNull(observableList.groupWhileTrue(condition = {true}) { it % 2 })
        assertNotNull(observableList.groupWhileTrue(mockedLifecycleOwner, condition = {true}) { it % 2 })
        assertNotNull(observableList.group{ it % 2 })
        assertNotNull(observableList.group(mockedLifecycleOwner) { it % 2 })
        assertNotNull(observableList.groupOnce{ it % 2})
        assertNotNull(observableList.groupOnce(mockedLifecycleOwner) { it % 2 })
    }
}