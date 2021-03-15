package com.github.sdpteam15.polyevents.database

import java.util.*

class  ObservableRequestAnswer {
    val responseObservers = ArrayList<ObserverRequestAnswer>()

    fun observe(newObs : ObserverRequestAnswer){
        responseObservers.add(newObs)
    }

    fun postValue(newValue: Boolean){
        for(obs in responseObservers){
            obs.update(newValue)
        }
        responseObservers.clear()
    }
}