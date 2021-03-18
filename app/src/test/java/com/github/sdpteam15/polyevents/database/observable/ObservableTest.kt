package com.github.sdpteam15.polyevents.database.observable

import com.github.sdpteam15.polyevents.database.observe.Observable
import com.github.sdpteam15.polyevents.database.observe.Observer
import org.junit.Test
import org.mockito.Mockito.mock
import kotlin.test.assertEquals
import org.mockito.Mockito.`when` as When

class ObservableTest {
    @Test
    fun constructorTest() {
        assertEquals(null, Observable<Int>().value)
        assertEquals(null, Observable<Int>(null).value)
        assertEquals(0, Observable(0).value)
    }

    @Test
    fun observerIsUpdatedTest() {
        val mockedObserver = mock(Observer::class.java) as Observer<Boolean>
        var isUpdate = false
        When(mockedObserver.update(value = true)).thenAnswer {
            isUpdate = true
            Unit
        }
        val observable = Observable(false)
        val suppressor = observable.observe { isUpdate = true }
        observable.value = true

        assertEquals(true, isUpdate)
        assertEquals(true, observable.value)

        When(mockedObserver.update(value = false)).thenAnswer {
            isUpdate = true
            Unit
        }

        isUpdate = false
        suppressor()
        observable.value = false

        assertEquals(false, isUpdate)
        assertEquals(false, observable.value)
    }

    @Test
    fun lambdaIsUpdatedTest() {
        var isUpdate = false
        val observable = Observable(false)
        val suppressor = observable.observe { isUpdate = it ?: false }
        observable.value = true

        assertEquals(true, isUpdate)
        assertEquals(true, observable.value)

        isUpdate = false
        suppressor()
        observable.value = false

        assertEquals(false, isUpdate)
        assertEquals(false, observable.value)
    }
}