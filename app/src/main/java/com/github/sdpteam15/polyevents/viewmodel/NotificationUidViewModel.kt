package com.github.sdpteam15.polyevents.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.github.sdpteam15.polyevents.model.database.local.dao.NotificationUidDao
import com.github.sdpteam15.polyevents.model.observable.ObservableList
import com.github.sdpteam15.polyevents.model.room.NotificationUid
import com.github.sdpteam15.polyevents.model.room.NotificationUid.Companion.SENTINEL_VALUE
import kotlinx.coroutines.launch

class NotificationUidViewModel(private val notificationUidDao: NotificationUidDao) : ViewModel() {
    companion object {
        private const val TAG = "NotificationUidViewModel"
    }

    fun getNotificationUid(obs: ObservableList<NotificationUid>) = viewModelScope.launch {
        obs.clear()
        obs.addAll(notificationUidDao.getNotificationUid(SENTINEL_VALUE))
    }

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