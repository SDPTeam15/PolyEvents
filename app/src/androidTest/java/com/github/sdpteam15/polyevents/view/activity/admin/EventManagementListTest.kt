package com.github.sdpteam15.polyevents.view.activity.admin

import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers
import com.github.sdpteam15.polyevents.R
import com.github.sdpteam15.polyevents.model.database.remote.Database
import com.github.sdpteam15.polyevents.model.database.remote.DatabaseInterface
import com.github.sdpteam15.polyevents.model.database.remote.objects.ZoneDatabaseInterface
import com.github.sdpteam15.polyevents.model.entity.Zone
import com.github.sdpteam15.polyevents.model.observable.Observable
import com.github.sdpteam15.polyevents.model.observable.ObservableList
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.mockito.kotlin.anyOrNull

class EventManagementListTest {
    lateinit var mockedDatabase: DatabaseInterface
    lateinit var mockedZoneDB: ZoneDatabaseInterface
    lateinit var scenario: ActivityScenario<EventManagementListActivity>

    @Before
    fun setup() {
        val intent = Intent(
            ApplicationProvider.getApplicationContext(),
            EventManagementListActivity::class.java
        )
        scenario = ActivityScenario.launch(intent)

        mockedDatabase = Mockito.mock(DatabaseInterface::class.java)
        mockedZoneDB = Mockito.mock(ZoneDatabaseInterface::class.java)
        Mockito.`when`(mockedDatabase.zoneDatabase).thenReturn(mockedZoneDB)

        val obs = Observable<Boolean>()
        Mockito.`when`(
            mockedZoneDB.getAllZones(
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull()
            )
        ).thenAnswer {
            (it.arguments[2] as ObservableList<Zone>).add(
                Zone(
                    zoneName = "Test zone",
                    zoneId = "zoneId"
                )
            )
            obs
        }
        Database.currentDatabase = mockedDatabase
        obs.postValue(true)
    }

    @After
    fun teardown() {
        scenario.close()
    }

    @Test
    fun clickOnBtnCreateZoneLaunchCorrectActivityWithEmptyFields() {
        Intents.init()
        Espresso.onView(ViewMatchers.withId(R.id.btnNewEvent)).perform(ViewActions.click())
        Intents.intended(IntentMatchers.hasComponent(EventManagementActivity::class.java.name))
        Intents.release()
    }
}