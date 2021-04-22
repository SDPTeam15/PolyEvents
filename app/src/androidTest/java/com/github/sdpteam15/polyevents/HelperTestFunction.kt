package com.github.sdpteam15.polyevents

import android.app.Activity
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry
import androidx.test.runner.lifecycle.Stage
import com.github.sdpteam15.polyevents.database.Database
import com.github.sdpteam15.polyevents.database.DatabaseInterface
import com.github.sdpteam15.polyevents.database.objects.ZoneDatabaseInterface
import com.github.sdpteam15.polyevents.database.observe.Observable
import com.github.sdpteam15.polyevents.database.observe.ObservableList
import com.github.sdpteam15.polyevents.model.Zone
import org.mockito.Mockito
import org.mockito.kotlin.anyOrNull

object HelperTestFunction {
    // Source : https://stackoverflow.com/questions/38737127/espresso-how-to-get-current-activity-to-test-fragments/58684943#58684943
    fun <T : Activity> getCurrentActivity(): T {
        var currentActivity: Activity? = null
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            run {
                currentActivity =
                    ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(
                        Stage.RESUMED
                    ).elementAtOrNull(0)
            }
        }
        return currentActivity as T
    }

    fun defaultMockDatabase(): DatabaseInterface{
        val database = Mockito.mock(DatabaseInterface::class.java)
        val zoneDatabase = Mockito.mock(ZoneDatabaseInterface::class.java)
        Mockito.`when`(database.zoneDatabase).thenAnswer{ zoneDatabase }
        Mockito.`when`(zoneDatabase.getAllZones(anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull())).thenAnswer{
            val res = it!!.arguments[2] as ObservableList<Zone>
            res.add(Zone("ID1", "Esplanade", "Espla", "a cool zone"), Database.currentDatabase)
            res.add(Zone("ID2", "Esplanade2", "Espla2", "a cool zone2"), Database.currentDatabase)
            res.add(Zone("ID3", "Esplanade3", "Espla3", "a cool zone3"), Database.currentDatabase)
            Observable(true, Database.currentDatabase)
        }
        return database
    }
}