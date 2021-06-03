package com.github.sdpteam15.polyevents.model.database.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.github.sdpteam15.polyevents.model.database.local.entity.NotificationUid
import com.github.sdpteam15.polyevents.model.database.local.entity.NotificationUid.Companion.SENTINEL_VALUE

@Dao
interface NotificationUidDao {
    @Query("SELECT * FROM notification_uid_table WHERE id = :id")
    suspend fun getNotificationUid(id: Int = SENTINEL_VALUE): List<NotificationUid>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(notificationUid: NotificationUid)
}