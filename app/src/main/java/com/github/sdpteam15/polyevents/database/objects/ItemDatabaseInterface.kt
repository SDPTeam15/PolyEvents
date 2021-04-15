package com.github.sdpteam15.polyevents.database.objects

import com.github.sdpteam15.polyevents.model.UserEntity

interface ItemDatabaseInterface {
    val currentUser: UserEntity?
}