package com.github.sdpteam15.polyevents.model.database.local.dao

import androidx.room.*
import com.github.sdpteam15.polyevents.model.room.UserSettings

@Dao
interface UserSettingsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(userSettings: UserSettings)

    @Query("SELECT * FROM user_settings_table WHERE user_uid = :id")
    suspend fun get(id: Int = UserSettings.DEFAULT_ID): UserSettings
}