package com.github.sdpteam15.polyevents.veiw

import android.app.Application
import com.github.sdpteam15.polyevents.model.database.local.room.LocalDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

// TODO: consider instantiating Firebase database here
class PolyEventsApplication: Application() {
    // No need to cancel this scope as it'll be torn down with the process
    val applicationScope = CoroutineScope(SupervisorJob())

    // Using by lazy so the database and the repository are only created when they're needed
    // rather than when the application starts
    val database by lazy { LocalDatabase.getDatabase(this, applicationScope) }
}