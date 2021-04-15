package com.github.sdpteam15.polyevents.database.observable

import com.github.sdpteam15.polyevents.database.observe.Observable
import org.junit.Test
import kotlin.test.assertEquals

class ObservableTest {
    @Test
    fun constructorTest() {
        assertEquals(null, Observable<Int>().value)
        assertEquals(null, Observable<Int>(null).value)
        assertEquals(0, Observable(0).value)
    }

    @Test
    fun lambdaIsUpdatedTest() {
        var isUpdate = false
        val observable = Observable(false)
        val suppressor = observable.observe { isUpdate = it.value ?: false }
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