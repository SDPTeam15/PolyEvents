package com.github.sdpteam15.polyevents.database

import com.github.sdpteam15.polyevents.database.observe.Observable
import org.junit.Test

class btest {
    @Test
    fun test(){
        val b = Observable<Boolean>()
        b.observe { bool : Boolean ->
            assert(bool)
        }
        b.postValue(true)


    }

}