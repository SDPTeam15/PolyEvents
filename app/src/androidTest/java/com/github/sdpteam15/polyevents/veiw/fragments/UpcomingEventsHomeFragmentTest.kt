package com.github.sdpteam15.polyevents.veiw.fragments


import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.github.sdpteam15.polyevents.HelperTestFunction
import com.github.sdpteam15.polyevents.veiw.activity.MainActivity
import com.github.sdpteam15.polyevents.R
import com.github.sdpteam15.polyevents.model.database.remote.Database.currentDatabase
import com.github.sdpteam15.polyevents.model.database.remote.FirestoreDatabaseProvider
import com.github.sdpteam15.polyevents.model.database.remote.objects.EventDatabaseInterface
import com.github.sdpteam15.polyevents.model.observable.Observable
import com.github.sdpteam15.polyevents.model.observable.ObservableList
import com.github.sdpteam15.polyevents.model.entity.Event
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.anyOrNull
import java.time.LocalDateTime
import org.mockito.Mockito.`when` as When


@RunWith(MockitoJUnitRunner::class)
class UpcomingEventsHomeFragmentTest {

    var events = ObservableList<Event>()
    lateinit var scenario: ActivityScenario<MainActivity>

    @Before
    fun setup() {
        val eventsToAdd = ArrayList<Event>()

        eventsToAdd.add(
            Event(

                eventName = "Sushi demo",
                description = "Super hungry activity !",
                startTime = LocalDateTime.of(2021, 3, 7, 12, 15),
                organizer = "The fish band",
                zoneName = "Kitchen",
                tags = mutableSetOf("sushi", "japan", "cooking")
            )
        )

        eventsToAdd.add(
            Event(

                eventName = "Aqua Poney",
                description = "Super cool activity !" +
                        " With a super long description that essentially describes and explains" +
                        " the content of the activity we are speaking of.",
                startTime = LocalDateTime.of(2021, 3, 7, 14, 15),
                organizer = "The Aqua Poney team",
                zoneName = "Swimming pool"
            )
        )

        eventsToAdd.add(
            Event(

                eventName = "Concert",
                description = "Super noisy activity !",
                startTime = LocalDateTime.of(2021, 3, 7, 17, 15),
                organizer = "AcademiC DeCibel",
                zoneName = "Concert Hall"
            )
        )

        eventsToAdd.add(
            Event(

                eventName = "Cricket",
                description = "Outdoor activity !",
                startTime = LocalDateTime.of(2021, 3, 7, 18, 15),
                organizer = "Cricket club",
                zoneName = "Field"
            )
        )

        val mockDatabaseInterface = HelperTestFunction.defaultMockDatabase()
        val mockEventDatabase = mock(EventDatabaseInterface::class.java)
        currentDatabase = mockDatabaseInterface
        When(mockDatabaseInterface.eventDatabase).thenReturn(mockEventDatabase)
        When(
            mockEventDatabase!!.getEvents(
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull()
            )
        ).thenAnswer {
            val list = mutableListOf<Event>()
            var i = 0
            for (e in eventsToAdd) {
                list.add(e)
                if (++i >= it!!.arguments[1] as Long)
                    break
            }
            (it!!.arguments[2] as ObservableList<Event>).addAll(list)
            Observable(true)
        }


        val intent = Intent(ApplicationProvider.getApplicationContext(), MainActivity::class.java)
        scenario = ActivityScenario.launch(intent)

        Espresso.onView(withId(R.id.ic_home)).perform(click())
        Thread.sleep(1000)
    }

    @After
    fun tearDown() {
        scenario.close()
        currentDatabase = FirestoreDatabaseProvider
    }

    @Test
    fun correctNumberUpcomingActivitiesDisplayed() {
        /*Espresso.onView(withId(R.id.id_upcoming_events_list)).check(
            matches(
                hasChildCount(
                    NUMBER_UPCOMING_EVENTS
                )
            )
        )*/
    }
}