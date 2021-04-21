package com.github.sdpteam15.polyevents.database.observable

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.github.sdpteam15.polyevents.database.observe.Observable
import org.junit.Test
import org.mockito.Mockito.mock
import kotlin.test.assertEquals
import org.mockito.Mockito.`when` as When

const val sender = "sender"

class ObservableTest {
    @Test
    fun constructor() {
        assertEquals(null, Observable<Int>().value)
        assertEquals(null, Observable<Int>().sender)
        assertEquals(null, Observable<Int>(null).value)
        assertEquals(null, Observable<Int>(sender = sender).sender)
        assertEquals(null, Observable<Int>(null, sender).sender)
        assertEquals(null, Observable<Int>(null).sender)
        assertEquals(0, Observable(0).value)
        assertEquals(sender, Observable(0, sender).sender)
    }

    @Test
    fun lambdaIsUpdated() {
        var isUpdate = false
        val observable = Observable<Boolean>()
        val suppressor = observable.observe { isUpdate = it.value }
        observable.postValue(true, sender)

        assertEquals(true, isUpdate)
        assertEquals(true, observable.value)
        assertEquals(sender, observable.sender)

        isUpdate = false
        suppressor()
        observable.postValue(false, sender)

        assertEquals(false, isUpdate)
        assertEquals(false, observable.value)
        assertEquals(sender, observable.sender)

        isUpdate = false
        observable.postValue(true, sender)
        observable.observeOnce { isUpdate = it.value }

        assertEquals(true, isUpdate)
        assertEquals(true, observable.value)
        assertEquals(sender, observable.sender)

        isUpdate = false
        observable.postValue(false, sender)

        assertEquals(false, isUpdate)
        assertEquals(false, observable.value)
        assertEquals(sender, observable.sender)
    }

    @Test
    fun lambdaIsUpdatedWithLifecycleOwner() {
        var isUpdate = false
        val observable = Observable<Boolean>()
        val mockedLifecycleOwner = mock(LifecycleOwner::class.java)
        val mockedLifecycle = mock(Lifecycle::class.java)
        When(mockedLifecycleOwner.lifecycle).thenReturn(mockedLifecycle)

        var suppressor = observable.observe(mockedLifecycleOwner) { isUpdate = it.value ?: false }
        observable.postValue(true, sender)
        assert(isUpdate)
        assert(suppressor())
        isUpdate = false

        suppressor = observable.observeOnce(mockedLifecycleOwner) { isUpdate = it.value ?: false }
        observable.postValue(true, sender)
        assert(isUpdate)
        assert(!suppressor())
        isUpdate = false

        suppressor = observable.observeWhileTrue(mockedLifecycleOwner) {
            isUpdate = it.value ?: false
            true
        }
        observable.postValue(true, sender)
        assert(isUpdate)
        assert(suppressor())
    }
}