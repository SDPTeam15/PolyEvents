package com.github.sdpteam15.polyevents.database.observable

import com.github.sdpteam15.polyevents.database.observe.Observable
import com.github.sdpteam15.polyevents.database.observe.ObservableList
import org.junit.Test
import kotlin.test.assertEquals

class ObservableListTest {
    @Test
    fun lambdaIsUpdatedOnAddTest() {
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
    fun lambdaIsUpdatedOnRemoveTest() {
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
    fun lambdaIsUpdatedOnClearTest() {
        var isUpdateClear = false
        var isUpdate = false
        val observable = ObservableList<Int>()

        observable.add(0)
        observable.add(1)

        val suppressorClear = observable.observeClear { isUpdateClear = true }
        val suppressor = observable.observe { isUpdate = true }

        observable.clear()

        assertEquals(true, isUpdateClear)
        assertEquals(true, isUpdate)
        assertEquals(0, observable.size)

        isUpdateClear = false
        isUpdate = false

        suppressorClear()
        suppressor()

        observable.add(0)
        observable.add(1)
        observable.clear()

        assertEquals(false, isUpdateClear)
        assertEquals(false, isUpdate)
        assertEquals(0, observable.size)
    }

    @Test
    fun lambdaIsUpdatedOnItemUpdatedWithValueTest() {
        var isUpdateUpdate = false
        var isUpdate = false
        val observable = ObservableList<Int>()

        observable.add(0)

        val suppressorUpdate = observable.observeUpdate { _, _ -> isUpdateUpdate = true }
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
    fun lambdaIsUpdatedOnItemUpdatedWithObservableTest() {
        var isUpdateUpdate = false
        var isUpdate = false
        val observable = ObservableList<Int>()

        observable.add(0)

        val suppressorUpdate = observable.observeUpdate { _, _ -> isUpdateUpdate = true }
        val suppressor = observable.observe { isUpdate = true }

        observable[0] = Observable(1)

        assertEquals(true, isUpdateUpdate)
        assertEquals(true, isUpdate)
        assertEquals(1, observable.size)
        assertEquals(1, observable[0])

        isUpdateUpdate = false
        isUpdate = false

        suppressorUpdate()
        suppressor()

        observable[0] = Observable(0)

        assertEquals(false, isUpdateUpdate)
        assertEquals(false, isUpdate)
        assertEquals(1, observable.size)
        assertEquals(0, observable[0])
    }

    @Test
    fun RemoveTest(){
        val observable = ObservableList<Int>()
        observable.add(0)

        observable.remove(1)
        assertEquals(1, observable.size)
        observable.remove(Observable())
        assertEquals(1, observable.size)
    }
}