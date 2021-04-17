package com.github.sdpteam15.polyevents.database.observable

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.github.sdpteam15.polyevents.database.observe.Observable
import com.github.sdpteam15.polyevents.database.observe.ObservableList
import org.junit.Test
import org.mockito.Mockito
import kotlin.test.assertEquals

class ObservableListTest {
    @Test
    fun lambdaIsUpdatedOnAdd() {
        var isUpdateAdd = false
        var isUpdate = false
        val observable = ObservableList<Int>()


        val suppressorAdd = observable.observeAdd { isUpdateAdd = true }
        val suppressor = observable.observe { isUpdate = true }

        observable.add(0)

        assertEquals(true, isUpdateAdd)
        assertEquals(true, isUpdate)
        assertEquals(1, observable.size)
        assertEquals(0, observable[0])

        isUpdateAdd = false
        isUpdate = false

        suppressorAdd()
        suppressor()

        observable.add(1)

        assertEquals(false, isUpdateAdd)
        assertEquals(false, isUpdate)
        assertEquals(2, observable.size)
        assertEquals(0, observable[0])
        assertEquals(1, observable[1])
        assertEquals(0, observable.getObservable(0).value)
    }
    @Test
    fun lambdaIsUpdatedOnAddWithIndex() {
        var isUpdateAdd = false
        var isUpdate = false
        val observable = ObservableList<Int>()


        val suppressorAdd = observable.observeAddWithIndex { isUpdateAdd = true }
        val suppressor = observable.observe { isUpdate = true }

        observable.add(0, 0, sender)

        assertEquals(true, isUpdateAdd)
        assertEquals(true, isUpdate)
        assertEquals(1, observable.size)
        assertEquals(0, observable[0])

        isUpdateAdd = false
        isUpdate = false

        suppressorAdd()
        suppressor()

        observable.add(0, 1, sender)

        assertEquals(false, isUpdateAdd)
        assertEquals(false, isUpdate)
        assertEquals(2, observable.size)
        assertEquals(1, observable[0])
        assertEquals(0, observable[1])
        assertEquals(1, observable.getObservable(0).value)
    }

    @Test
    fun lambdaIsUpdatedOnRemove() {
        var isUpdateRemove = false
        var isUpdate = false
        val observable = ObservableList<Int>()

        observable.add(0)
        observable.add(1)

        val suppressorRemove = observable.observeRemove { isUpdateRemove = true }
        val suppressor = observable.observe { isUpdate = true }

        observable.remove(0)

        assertEquals(true, isUpdateRemove)
        assertEquals(true, isUpdate)
        assertEquals(1, observable.size)
        assertEquals(1, observable[0])

        isUpdateRemove = false
        isUpdate = false

        suppressorRemove()
        suppressor()

        observable.remove(1)

        assertEquals(false, isUpdateRemove)
        assertEquals(false, isUpdate)
        assertEquals(0, observable.size)
    }

    @Test
    fun lambdaIsUpdatedOnItemUpdatedWithValue() {
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

        suppressorUpdate()
        suppressor()

        observable[0] = 0

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

        suppressorUpdate()
        suppressor()

        observable[0] = 0

        assertEquals(false, isUpdateUpdate)
        assertEquals(false, isUpdate)
        assertEquals(1, observable.size)
        assertEquals(0, observable[0])
    }

    @Test
    fun Remove(){
        val observable = ObservableList<Int>()
        observable.add(0)

        observable.remove(1)
        assertEquals(1, observable.size)
        observable.remove(Observable())
        assertEquals(1, observable.size)
    }

    @Test
    fun lambdaIsUpdatedWithLifecycleOwner() {
        val isUpdate = MutableList<Int>(0) { 0 }
        val observable = ObservableList<Boolean>()
        val mockedLifecycleOwner = Mockito.mock(LifecycleOwner::class.java)
        val mockedLifecycle = Mockito.mock(Lifecycle::class.java)
        Mockito.`when`(mockedLifecycleOwner.lifecycle).thenReturn(mockedLifecycle)

        isUpdate.add(0)
        observable.observeAddOnce(mockedLifecycleOwner) { isUpdate[0]++ }
        isUpdate.add(0)
        observable.observeAddWhileTrue(mockedLifecycleOwner) { 1 > isUpdate[1]++ }
        isUpdate.add(0)
        observable.observeAddWithIndexWhileTrue(mockedLifecycleOwner) { 1 > isUpdate[2]++ }
        isUpdate.add(0)
        observable.observeOnce(mockedLifecycleOwner) { isUpdate[3]++ }
        isUpdate.add(0)
        observable.observeRemoveOnce(mockedLifecycleOwner) { isUpdate[4]++ }
        isUpdate.add(0)
        observable.observeRemoveWhileTrue(mockedLifecycleOwner) { 1 > isUpdate[5]++ }
        isUpdate.add(0)
        observable.observeUpdate(mockedLifecycleOwner) { isUpdate[6]++ }
        isUpdate.add(0)
        observable.observeUpdateOnce(mockedLifecycleOwner) { isUpdate[7]++ }
        isUpdate.add(0)
        observable.observeUpdateWhileTrue(mockedLifecycleOwner) { 1 > isUpdate[8]++ }
        isUpdate.add(0)
        observable.observeWhileTrue(mockedLifecycleOwner) { 1 > isUpdate[9]++ }
        isUpdate.add(0)
        observable.observeAddWithIndex(mockedLifecycleOwner) { isUpdate[10]++ }
        isUpdate.add(0)
        observable.observeAddWithIndexOnce(mockedLifecycleOwner) { isUpdate[11]++ }

        observable.add(true)
        observable.add(true)
        observable.add(true)

        observable.getObservable(0).postValue(false)
        observable.getObservable(1).postValue(false)
        observable.getObservable(2).postValue(false)

        observable.clear()

        assertEquals("[1, 2, 2, 1, 3, 2, 6, 6, 2, 2, 3, 1]", isUpdate.toString())
    }
}