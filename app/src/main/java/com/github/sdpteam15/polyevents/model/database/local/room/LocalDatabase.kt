package com.github.sdpteam15.polyevents.model.database.local.room

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.github.sdpteam15.polyevents.helper.HelperFunctions
import com.github.sdpteam15.polyevents.model.database.local.dao.EventDao
import com.github.sdpteam15.polyevents.model.database.local.dao.GenericEntityDao
import com.github.sdpteam15.polyevents.model.database.local.dao.NotificationUidDao
import com.github.sdpteam15.polyevents.model.database.local.dao.UserSettingsDao
import com.github.sdpteam15.polyevents.model.database.local.entity.EventLocal
import com.github.sdpteam15.polyevents.model.database.local.entity.GenericEntity
import com.github.sdpteam15.polyevents.model.database.remote.Database.currentDatabase
import com.github.sdpteam15.polyevents.model.database.remote.DatabaseConstant
import com.github.sdpteam15.polyevents.model.entity.Event
import com.github.sdpteam15.polyevents.model.observable.Observable
import com.github.sdpteam15.polyevents.model.observable.ObservableList
import com.github.sdpteam15.polyevents.model.room.NotificationUid
import com.github.sdpteam15.polyevents.model.room.UserSettings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// TODO: consider using repositories
// TODO: Firebase database objects are technically daos, consider refactoring?
// TODO: when user logs in, should fetch all info to store in local db
@Database(
    entities = [EventLocal::class, UserSettings::class, NotificationUid::class, GenericEntity::class],
    version = 6, exportSchema = false
)
@TypeConverters(HelperFunctions.Converters::class)
abstract class LocalDatabase : RoomDatabase() {
    /**
     * Get the events dao (events to which the user is registered)
     */
    abstract fun eventDao(): EventDao

    /**
     * Get the user settings Dao
     */
    abstract fun userSettingsDao(): UserSettingsDao

    /**
     * Get the dao for the notification uid.
     */
    abstract fun notificationUidDao(): NotificationUidDao

    /**
     * Get the GenericEntity Dao
     */
    abstract fun genericEntityDao(): GenericEntityDao

    companion object {
        private const val TAG = "LocalDatabase"

        @Volatile
        private var INSTANCE: LocalDatabase? = null

        var eventsLocalObservable = ObservableList<Event>()
        var userSettingsObservable = Observable<UserSettings>()

        fun getDatabase(
            context: Context,
            scope: CoroutineScope
        ): LocalDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LocalDatabase::class.java,
                    "polyevents_database"
                )
                    // Wipes and rebuilds instead of migrating if no Migration object.
                    // Migration is not part of this codelab.
                    .fallbackToDestructiveMigration()
                    .addCallback(PolyEventsDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }

        /**
         * To delete all content and repopulate the database whenever the app is created,
         * we need to create a RoomDatabase.Callback and override onCreate().
         * Because you cannot do Room database operations on the UI thread,
         * onCreate() launches a coroutine on the IO Dispatcher.
         * (ref: https://developer.android.com/codelabs/android-room-with-a-view-kotlin#13)
         *
         * Note: Populating the database isn't related to a UI lifecycle, therefore you shouldn't
         * use a CoroutineScope like viewModelScope.  It's related to the app's lifecycle. Therefore
         * use application scope defined in Polyevents Application
         */
        private class PolyEventsDatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            /**
             * Override the onCreate method to populate the database.
             */
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                // If you want to keep the data through app restarts,
                // comment out the following line.
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateDatabaseWithUserEvents(database.eventDao(), scope)
                    }
                    scope.launch(Dispatchers.IO) {
                        populateDatabaseWithUserSettings(database.userSettingsDao(), scope)
                    }
                }
            }
        }

        /**
         * Populate the database in a new coroutine.
         */
        suspend fun populateDatabaseWithUserEvents(eventDao: EventDao, scope: CoroutineScope) {
            Log.d(TAG, "Populating the database with user's events")

            // TODO: need to clear notifications for events here or in MainActivity
            eventDao.deleteAll()
            if (currentDatabase.currentUser != null) {
                eventsLocalObservable.observe {
                    scope.launch(Dispatchers.IO) {
                        eventDao.insertAll(it.value.map { EventLocal.fromEvent(it) })
                    }
                }

                currentDatabase.eventDatabase!!
                    .getEvents(
                        eventList = eventsLocalObservable,
                        matcher = {
                            // Get all events to which the current user is registered to
                            // and order them by start date
                            it.whereArrayContains(
                                DatabaseConstant.EventConstant.EVENT_PARTICIPANTS.value,
                                currentDatabase.currentUser!!.uid
                                // TODO: why is orderby not working (need indices?)
                            )/*.orderBy(
                                            DatabaseConstant.EventConstant.EVENT_START_TIME.value,
                                            Query.Direction.ASCENDING
                                    )*/
                        },
                    )
                Log.d(TAG, "Finished retrieving from remote")
            }
        }

        suspend fun populateDatabaseWithUserSettings(
            userSettingsDao: UserSettingsDao, scope: CoroutineScope
        ) {
            if (currentDatabase.currentUser != null) {
                userSettingsObservable.observe {
                    scope.launch(Dispatchers.IO) {
                        userSettingsDao.insert(it.value)
                    }
                }

                currentDatabase.userSettingsDatabase!!.getUserSettings(
                    id = currentDatabase.currentUser!!.uid,
                    userSettingsObservable = userSettingsObservable
                )
            }
        }
    }
}
