package com.github.sdpteam15.polyevents.database.objects

import com.github.sdpteam15.polyevents.database.Database
import com.github.sdpteam15.polyevents.model.UserEntity

object ItemDatabaseFirestore: ItemDatabaseInterface {
    override val currentUser: UserEntity?
        get()= Database.currentDatabase.currentUser
}