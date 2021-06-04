package com.github.sdpteam15.polyevents.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.github.sdpteam15.polyevents.model.database.local.dao.EventDao
import com.github.sdpteam15.polyevents.model.database.local.entity.EventLocal
import com.github.sdpteam15.polyevents.model.observable.ObservableList
import kotlinx.coroutines.launch

class EventLocalViewModel(private val eventDao: EventDao) : ViewModel() {

    companion object {
        private const val TAG = "EventLocalViewModel"
    }

    fun getAllEvents(obs: ObservableList<EventLocal>) = viewModelScope.launch {
        val events = eventDao.getAll()
        obs.updateAll(events)
    }

    fun getEventById(eventId: String, obs: ObservableList<EventLocal>) = viewModelScope.launch {
        val eventLocal = eventDao.getEventById(eventId)
        if (eventLocal.isEmpty()) {
            obs.addAll(eventLocal)
        } else {
            obs.addAll(eventLocal)
        }
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
    }

    /**
     * Launch a coroutine on the viewmodel scope to get all user subscribed events. Events
     * to which you are subscribed are by definition limited.
     * @param obs the observable list to be updated with the events when they are
     * fetched from the local database
     */
    fun getAllSubscribedEvents(obs: ObservableList<EventLocal>) = viewModelScope.launch {
        val limitedEvents = eventDao.getEventsWhereLimited(true)
        obs.updateAll(limitedEvents)
    }

    /**
     * Launch a coroutine on the viewmodel scope to delete all user subscribed events.
     */
    fun deleteAllSubscribedEvents() = viewModelScope.launch {
        eventDao.deletedEventsWhereLimited(true)
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
