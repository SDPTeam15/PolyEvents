package com.github.sdpteam15.polyevents.model.database.local.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.github.sdpteam15.polyevents.helper.HelperFunctions
import com.github.sdpteam15.polyevents.model.Scope
import com.github.sdpteam15.polyevents.model.database.local.dao.EventDao
import com.github.sdpteam15.polyevents.model.database.local.dao.GenericEntityDao
import com.github.sdpteam15.polyevents.model.database.local.dao.NotificationUidDao
import com.github.sdpteam15.polyevents.model.database.local.dao.UserSettingsDao
import com.github.sdpteam15.polyevents.model.database.local.entity.EventLocal
import com.github.sdpteam15.polyevents.model.database.local.entity.GenericEntity
import com.github.sdpteam15.polyevents.model.database.local.entity.NotificationUid
import com.github.sdpteam15.polyevents.model.database.local.entity.UserSettings
import com.github.sdpteam15.polyevents.model.database.remote.Database.currentDatabase
import com.github.sdpteam15.polyevents.model.database.remote.DatabaseConstant
import com.github.sdpteam15.polyevents.model.entity.Event
import com.github.sdpteam15.polyevents.model.observable.Observable
import com.github.sdpteam15.polyevents.model.observable.ObservableList
import kotlinx.coroutines.Dispatchers

/**
 * Local Database for the application. Uses Room persistence library running sqlite queries
 * on the device. We must specify the entities this local database manages as well as the version
 * number each time the data schema is modified. Must have methods to return dao (data access
 * objects) for each of the corresponding entities).
 * Consider using repositories in the future.
 */
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

        /**
         * Get the local database
         * @param context the context where we're getting the database from
         * @param scope the coroutine scope in which we launch the database callback on app creation
         */
        fun getDatabase(
            context: Context,
            scope: Scope
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
            private val scope: Scope
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
        suspend fun populateDatabaseWithUserEvents(eventDao: EventDao, scope: Scope) {

            // TODO: need to clear notifications for events here or in MainActivity
            eventDao.deleteAll()
            if (currentDatabase.currentUser != null) {
                eventsLocalObservable.observe {
                    scope.launch(Dispatchers.IO) {
                        eventDao.insertAll(it.value.map { EventLocal.fromEvent(it) })
                    }
                }

                currentDatabase.eventDatabase
                    .getEvents(
                        eventList = eventsLocalObservable.sortAndLimitFrom(null) {
                            it.startTime
                        },
                        matcher = {
                            // Get all events to which the current user is registered to
                            // and order them by start date
                            it.whereArrayContains(
                                DatabaseConstant.EventConstant.EVENT_PARTICIPANTS.value,
                                currentDatabase.currentUser!!.uid
                            )
                        },
                    )
            }
        }

        suspend fun populateDatabaseWithUserSettings(
            userSettingsDao: UserSettingsDao, scope: Scope
        ) {
            if (currentDatabase.currentUser != null) {
                userSettingsObservable.observe {
                    scope.launch(Dispatchers.IO) {
                        userSettingsDao.insert(it.value)
                    }
                }

                currentDatabase.userSettingsDatabase.getUserSettings(
                    id = currentDatabase.currentUser!!.uid,
                    userSettingsObservable = userSettingsObservable
                )
            }
        }
    }
}
