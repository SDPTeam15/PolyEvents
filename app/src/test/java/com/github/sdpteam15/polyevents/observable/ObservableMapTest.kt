package com.github.sdpteam15.polyevents.observable

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.github.sdpteam15.polyevents.model.observable.Observable
import com.github.sdpteam15.polyevents.model.observable.ObservableMap
import org.junit.Test
import org.mockito.Mockito
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class ObservableMapTest {
    @Test
    fun lambdaIsUpdatedOnPut() {
        var isUpdateAdd = false
        var isUpdate = false
        val observable = ObservableMap<Int, Int>()
        val map: MutableMap<Int, Int> = observable


        val suppressorAdd = observable.observePut {
            assert(it.key < 5)
            isUpdateAdd = true
        }
        val suppressor = observable.observe { isUpdate = true }

        map.put(0, 0)

        assertEquals(true, isUpdateAdd)
        assertEquals(true, isUpdate)
        assertEquals(1, observable.size)
        assertEquals(0, observable[0])

        isUpdateAdd = false
        isUpdate = false

        suppressorAdd.remove()
        suppressor.remove()

        map.put(1, 1)

        assertEquals(false, isUpdateAdd)
        assertEquals(false, isUpdate)
        assertEquals(2, observable.size)
        assertEquals(0, observable[0])
        assertEquals(1, observable[1])
        assertEquals(0, observable.getObservable(0)!!.value)
        assertNull(observable.put(2, Observable()))

        observable.put(1, Observable(2))!!.observeOnce { assertEquals(2, it.value) }.then.postValue(
            0
        )
    }

    @Test
    fun lambdaIsUpdatedOnRemove() {
        var isUpdateRemove = false
        var isUpdate = false
        val observable = ObservableMap<Int, Int>()
        val map: MutableMap<Int, Int> = observable

        map.put(0, 0)
        map.put(1, 1)

        val suppressorRemove = observable.observeRemove { isUpdateRemove = true }
        val suppressor = observable.observe { isUpdate = true }

        map.remove(0)

        assertEquals(true, isUpdateRemove)
        assertEquals(true, isUpdate)
        assertEquals(1, observable.size)
        assertEquals(1, observable[1])

        isUpdateRemove = false
        isUpdate = false

        suppressorRemove.remove()
        suppressor.remove()

        map.remove(1)

        assertEquals(false, isUpdateRemove)
        assertEquals(false, isUpdate)
        assertEquals(0, observable.size)
    }

    @Test
    fun lambdaIsUpdatedOnItemUpdatedWithValue() {
        var isUpdateUpdate = false
        var isUpdate = false
        val observable = ObservableMap<Int, Int>()
        val map: MutableMap<Int, Int> = observable

        map.put(0, 0)

        val suppressorUpdate = observable.observeUpdate { isUpdateUpdate = true }
        val suppressor = observable.observe { isUpdate = true }

        map[0] = 1

        assertEquals(true, isUpdateUpdate)
        assertEquals(true, isUpdate)
        assertEquals(1, observable.size)
        assertEquals(1, observable[0])

        isUpdateUpdate = false
        isUpdate = false

        suppressorUpdate.remove()
        suppressor.remove()

        map[0] = 0

        assertEquals(false, isUpdateUpdate)
        assertEquals(false, isUpdate)
        assertEquals(1, observable.size)
        assertEquals(0, observable[0])
    }

    @Test
    fun lambdaIsUpdatedOnItemUpdatedWithObservable() {
        var isUpdateUpdate = false
        var isUpdate = false
        val observable = ObservableMap<Int, Int>()

        observable.put(0, 0)

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
    fun lambdaIsUpdatedWithLifecycleOwner() {
        val isUpdate = mutableListOf<Int>()
        val observable = ObservableMap<Int, Boolean>()
        val mockedLifecycleOwner = Mockito.mock(LifecycleOwner::class.java)
        val mockedLifecycle = Mockito.mock(Lifecycle::class.java)
        Mockito.`when`(mockedLifecycleOwner.lifecycle).thenReturn(mockedLifecycle)
        val map: MutableMap<Int, Boolean> = observable

        isUpdate.add(0)
        observable.observePutWhileTrue(mockedLifecycleOwner) { 1 > isUpdate[0]++ }
        isUpdate.add(0)
        observable.observeOnce(mockedLifecycleOwner) { isUpdate[1]++ }
        isUpdate.add(0)
        observable.observeRemove(mockedLifecycleOwner) { isUpdate[2]++ }
        isUpdate.add(0)
        observable.observeRemoveOnce(mockedLifecycleOwner) { isUpdate[3]++ }
        isUpdate.add(0)
        observable.observeRemoveWhileTrue(mockedLifecycleOwner) { 1 > isUpdate[4]++ }
        isUpdate.add(0)
        observable.observeUpdate(mockedLifecycleOwner) { isUpdate[5]++ }
        isUpdate.add(0)
        observable.observeUpdateOnce(mockedLifecycleOwner) { isUpdate[6]++ }
        isUpdate.add(0)
        observable.observeUpdateWhileTrue(mockedLifecycleOwner) { 1 > isUpdate[7]++ }
        isUpdate.add(0)
        observable.observeWhileTrue(mockedLifecycleOwner) { 1 > isUpdate[8]++ }
        isUpdate.add(0)
        observable.observePut(mockedLifecycleOwner) { isUpdate[9]++ }
        isUpdate.add(0)
        observable.observePutOnce(mockedLifecycleOwner) { isUpdate[10]++ }

        map.putAll(
            mutableMapOf(
                0 to true,
                1 to true,
                2 to true
            )
        )

        assert(observable.containsValue(true))
        assert(!observable.containsValue(false))
        assert(observable.containsObservable(observable.getObservable(0)!!))

        map[0] = false
        map[1] = false
        map[2] = false

        observable.clear()

        assertEquals("[2, 1, 3, 1, 2, 3, 1, 2, 2, 3, 1]", isUpdate.toString())
    }

    @Test
    fun mapIsImplemented() {
        val map: MutableMap<Int, Int> = ObservableMap()

        map.putAll(mutableMapOf(Pair(0, 0)))
        map.put(1, 1)
        map.put(2, 2)

        assert(!map.isEmpty())

        assertNotNull(map.values)
        assertNotNull(map.keys)

        assertNotNull(map.entries)
        for (e in map.entries) {
            assertNotNull(e.key)
            assertNotNull(e.value)
            assertNotNull(e.setValue(0))
        }
    }

    @Test
    fun map() {
        var updated = false
        val observableMap = ObservableMap<Int, Int>()
        val map: MutableMap<Int, Int> = observableMap
        val mappedObservableMap = observableMap.map { it.hashCode() }.then

        mappedObservableMap.observeOnce { updated = true }
        map.put(0, 0)
        assert(updated)
        updated = false
        map.put(1, 1)
        map.put(2, 2)

        mappedObservableMap.observeOnce { updated = true }
        map.remove(0)
        assert(updated)
        updated = false

        mappedObservableMap.observeOnce { updated = true }
        map.put(0, 0)
        assert(updated)
        updated = false

        mappedObservableMap.observeOnce { updated = true }
        map.putAll(mapOf(4 to 4))
        assert(updated)
        updated = false

        mappedObservableMap.observeOnce { updated = true }
        map[0] = 1
        assert(updated)
        updated = false

        mappedObservableMap.observeOnce { updated = true }
        map.clear()
        assert(updated)
        updated = false

        val mockedLifecycleOwner = Mockito.mock(LifecycleOwner::class.java)
        val mockedLifecycle = Mockito.mock(Lifecycle::class.java)
        Mockito.`when`(mockedLifecycleOwner.lifecycle).thenReturn(mockedLifecycle)

        assertNotNull(mappedObservableMap.mapWhileTrue(condition = { true }) { it })
        assertNotNull(
            mappedObservableMap.mapWhileTrue(
                mockedLifecycleOwner,
                condition = { true }) { it })
        assertNotNull(mappedObservableMap.map { it })
        assertNotNull(mappedObservableMap.map(mockedLifecycleOwner) { it })
        assertNotNull(mappedObservableMap.mapOnce { it })
        assertNotNull(mappedObservableMap.mapOnce(mockedLifecycleOwner) { it })
    }
}