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
import com.github.sdpteam15.polyevents.model.database.remote.DatabaseConstant
import com.github.sdpteam15.polyevents.model.entity.Event
import com.github.sdpteam15.polyevents.model.observable.ObservableList
import com.github.sdpteam15.polyevents.model.room.EventLocal
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// TODO: consider using repositories
// TODO: Firebase database objects are technically daos, consider refactoring?
// TODO: when user logs in, should fetch all info to store in local db
@Database(entities = [EventLocal::class], version = 1)
@TypeConverters(HelperFunctions.Converters::class)
abstract class LocalDatabase : RoomDatabase() {
    abstract fun eventDao(): EventDao

    companion object {
        private const val TAG = "LocalDatabase"
        @Volatile
        private var INSTANCE: LocalDatabase? = null

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
                        populateDatabase(database.eventDao())
                    }
                }
            }
        }

        /**
         * Populate the database in a new coroutine.
         */
        suspend fun populateDatabase(eventDao: EventDao) {
            // Start the app with a clean database every time.
            // Not needed if you only populate on creation.
            /*val ids = eventDao.getAll().map { it.eventId }
            com.github.sdpteam15.polyevents.model.database.remote.Database.currentDatabase.getListEntity(
                ObservableList<Event>().observeOnce {
                    scope.launch(Dispatchers.IO) {
                        eventDao.deleteAll()
                        for (e in it.value)
                            eventDao.insert(EventLocal.fromEvent(e))
                    }
                    Unit
                }.then,
                ids,
                null,
                DatabaseConstant.CollectionConstant.EVENT_COLLECTION
            )*/
            // TODO: populate the local database with that of the remote
            Log.d(TAG, "Populating the database")
            if (currentDatabase.currentUser != null) {

            }
            val eventsUserRegisteredTo = ObservableList<EventLocal>()
            //currentDatabase.eventDatabase!!.getEvents(
            // Sort by start time, events where current user is logged in
            //)
        }
    }
}
