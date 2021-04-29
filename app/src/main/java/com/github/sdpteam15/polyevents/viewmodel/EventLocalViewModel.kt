package com.github.sdpteam15.polyevents.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.github.sdpteam15.polyevents.database.dao.EventDao
import com.github.sdpteam15.polyevents.database.observe.ObservableList
import com.github.sdpteam15.polyevents.model.room.EventLocal
import kotlinx.coroutines.launch

class EventLocalViewModel(private val eventDao: EventDao): ViewModel() {

    companion object {
        private const val TAG = "EventLocalViewModel"
    }

    // TODO: will need to update if user subscribes/unsubscribes from event (or if event is removed)
    fun getAllEvents(obs: ObservableList<EventLocal>) = viewModelScope.launch {
        val events = eventDao.getAll()
        obs.clear()
        obs.addAll(events)
    }

    /**
     * Launch a coroutine on the viewmodelscope to remove an event from the room database.
     * @param event the event to delete
     */
    fun delete(event: EventLocal) = viewModelScope.launch {
        eventDao.delete(event)
    }

    /**
     * Launch a coroutine on the viewmodelscope to insert an event in the room database.
     * @param event the event to insert
     */
    fun insert(event: EventLocal) = viewModelScope.launch {
        eventDao.insert(event)
        Log.d(TAG, "INSERTING EVENT INTO LOCAL DB")
    }
}

/**
 * A ViewModelProvider.Factory that gets as a parameter the dependencies needed to create
 * an EventViewModel.
 */
class EventLocalViewModelFactory(private val eventDao: EventDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EventLocalViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EventLocalViewModel(eventDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
