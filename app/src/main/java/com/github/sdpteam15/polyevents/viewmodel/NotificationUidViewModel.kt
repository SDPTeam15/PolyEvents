package com.github.sdpteam15.polyevents.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.github.sdpteam15.polyevents.model.database.local.dao.NotificationUidDao
import com.github.sdpteam15.polyevents.model.database.local.entity.NotificationUid
import com.github.sdpteam15.polyevents.model.database.local.entity.NotificationUid.Companion.SENTINEL_VALUE
import com.github.sdpteam15.polyevents.model.observable.ObservableList
import kotlinx.coroutines.launch

/**
 * A subclass of ViewModel responsible for managing Notification unique ids from local database.
 * Offers methods to get the current unique notification uid counter, as well as updating it.
 * @property notificationUidDao the data access object for the notification uid that the view model
 * uses to communicate with the local database
 */
class NotificationUidViewModel(private val notificationUidDao: NotificationUidDao) : ViewModel() {
    companion object {
        private const val TAG = "NotificationUidViewModel"
    }

    /**
     * Get the current notification uid stored in local cache
     * @param obs the observable to be updated with the notification uid stored
     * in local cache
     */
    fun getNotificationUid(obs: ObservableList<NotificationUid>) = viewModelScope.launch {
        obs.clear()
        obs.addAll(notificationUidDao.getNotificationUid(SENTINEL_VALUE))
    }

    /**
     * Update the notification uid stored in cache.
     * @param notificationUid the new notification uid
     */
    fun updateNotificationUid(notificationUid: NotificationUid) = viewModelScope.launch {
        notificationUidDao.insert(notificationUid)
    }
}

/**
 * A ViewModelProvider.Factory that gets as a parameter the dependencies needed to create
 * a NotificationUidViewModel.
 */
class NotificationUidViewModelFactory(private val notificationUidDao: NotificationUidDao) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NotificationUidViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NotificationUidViewModel(notificationUidDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}