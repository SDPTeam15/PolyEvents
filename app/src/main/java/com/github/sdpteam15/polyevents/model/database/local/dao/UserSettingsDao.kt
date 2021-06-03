package com.github.sdpteam15.polyevents.model.database.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.github.sdpteam15.polyevents.model.database.local.entity.UserSettings

@Dao
interface UserSettingsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(userSettings: UserSettings)

    @Query("SELECT * FROM user_settings_table WHERE user_uid = :id")
    suspend fun get(id: String = UserSettings.DEFAULT_ID): List<UserSettings>
}