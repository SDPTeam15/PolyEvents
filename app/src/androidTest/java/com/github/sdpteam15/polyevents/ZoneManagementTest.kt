package com.github.sdpteam15.polyevents

import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.github.sdpteam15.polyevents.admin.ZoneManagementActivity
import com.github.sdpteam15.polyevents.admin.ZoneManagementListActivity
import com.github.sdpteam15.polyevents.database.Database
import com.github.sdpteam15.polyevents.database.DatabaseInterface
import com.github.sdpteam15.polyevents.database.FirestoreDatabaseProvider
import com.github.sdpteam15.polyevents.database.observe.Observable
import com.github.sdpteam15.polyevents.model.UserEntity
import com.github.sdpteam15.polyevents.model.UserProfile
import com.github.sdpteam15.polyevents.model.Zone
import com.google.firebase.auth.FirebaseAuth
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.`when` as When

class ZoneManagementTest {
    var mainActivity = ActivityScenarioRule(MainActivity::class.java)
    var zoneManagement = ActivityScenarioRule(ZoneManagementActivity::class.java)
    lateinit var scenario : ActivityScenario<MainActivity>
    lateinit var scenario2: ActivityScenario<ZoneManagementActivity>


    lateinit var testUser: UserEntity

    val uid = "testUid"
    val username = "JohnDoe"
    val email = "John@Doe.com"
    lateinit var mockedDatabase: DatabaseInterface
    val zoneId = "IDZone"
    val zoneName = "Cool Zone name"
    val zoneDesc = "Cool zone desc"
    val zoneLoc = ""

    @Before
    fun setup() {
        mockedDatabase = Mockito.mock(DatabaseInterface::class.java)
        val mockedUserProfile = UserProfile("TestID","TestName")
        When(mockedDatabase.currentProfile).thenReturn(mockedUserProfile)

        testUser = UserEntity(
            uid = uid,
            username = username,
            email = email
        )

        Database.currentDatabase = mockedDatabase
        When(mockedDatabase.currentUser).thenReturn(testUser)

        FirebaseAuth.getInstance().signOut()

        MainActivity.currentUser = testUser

        val intent = Intent(ApplicationProvider.getApplicationContext(), MainActivity::class.java)
        scenario = ActivityScenario.launch(intent)

        Espresso.onView(ViewMatchers.withId(R.id.ic_home)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.id_fragment_admin_hub))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.btnRedirectZoneManagement)).perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withId(R.id.btnNewZone)).perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withId(R.id.zoneManagementDescription)).perform(ViewActions.replaceText(""))
        Espresso.onView(ViewMatchers.withId(R.id.zoneManagementName)).perform(ViewActions.replaceText(""))
    }

    @After
    fun teardown() {
        MainActivity.currentUser = null
        scenario.close()
        Database.currentDatabase = FirestoreDatabaseProvider
    }

    @Test
    fun pressCreateButtonDoesNothingWhenEmptyFields(){

        Espresso.onView(ViewMatchers.withId(R.id.zoneManagementDescription))
            .check(ViewAssertions.matches(ViewMatchers.withText("")))
        Espresso.onView(ViewMatchers.withId(R.id.zoneManagementName))
            .check(ViewAssertions.matches(ViewMatchers.withText("")))
        Espresso.onView(ViewMatchers.withId(R.id.btnManage)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.zone_management_activity)).check(
            ViewAssertions.matches(
                ViewMatchers.isDisplayed()
            ))
        Espresso.onView(ViewMatchers.withId(R.id.zoneManagementName)).perform(ViewActions.replaceText(zoneName))

        Espresso.onView(ViewMatchers.withId(R.id.btnManage)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.zone_management_activity)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun createWithCorrectInfoRedirectToCorrectActivity(){
        val obs = Observable<Boolean>()
        When(mockedDatabase.createZone(ZoneManagementActivity.zone)).thenAnswer { _ ->
            obs
        }

        Espresso.onView(ViewMatchers.withId(R.id.zoneManagementName)).perform(ViewActions.replaceText(zoneName))
        Espresso.onView(ViewMatchers.withId(R.id.zoneManagementDescription)).perform(ViewActions.replaceText(zoneDesc))
        Espresso.onView(ViewMatchers.withId(R.id.btnManage)).perform(ViewActions.click())
        obs.postValue(true)

        Espresso.onView(ViewMatchers.withId(R.id.zone_management_list_activity)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun updateInfoRedirectToCorrectActivityIfCorrect(){
        val zoneInfo = Zone(zoneId, zoneName, zoneLoc,zoneDesc)

        val obs = Observable<Boolean>()
        val obs2 = Observable<Boolean>()
        When(mockedDatabase.getZoneInformation(ZoneManagementActivity.zoneId, ZoneManagementActivity.zoneObservable)).thenAnswer{
            ZoneManagementActivity.zoneObservable.postValue(zoneInfo)
            obs2
        }

        When(mockedDatabase.updateZoneInformation(ZoneManagementActivity.zoneId, ZoneManagementActivity.zone)).thenAnswer { _ ->
            obs
        }

        val intent = Intent(ApplicationProvider.getApplicationContext(), ZoneManagementActivity::class.java)
        intent.putExtra(ZoneManagementListActivity.EXTRA_ID, zoneId)
        scenario2 = ActivityScenario.launch(intent)

        obs2.postValue(true)

        Espresso.onView(ViewMatchers.withId(R.id.zoneManagementName)).perform(ViewActions.replaceText(zoneName))
        Espresso.onView(ViewMatchers.withId(R.id.zoneManagementDescription)).perform(ViewActions.replaceText(zoneDesc+" 2"))
        Espresso.onView(ViewMatchers.withId(R.id.btnManage)).perform(ViewActions.click())
        obs.postValue(true)

        Espresso.onView(ViewMatchers.withId(R.id.zone_management_list_activity)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        scenario2.close()
    }

    @Test
    fun updateWithEmptyFieldsDoesNothing(){
        val zoneInfo = Zone(zoneId, zoneName, zoneLoc,zoneDesc)

        val obs = Observable<Boolean>()
        val obs2 = Observable<Boolean>()
        When(mockedDatabase.getZoneInformation(ZoneManagementActivity.zoneId, ZoneManagementActivity.zoneObservable)).thenAnswer{
            ZoneManagementActivity.zoneObservable.postValue(zoneInfo)
            obs2
        }

        val intent = Intent(ApplicationProvider.getApplicationContext(), ZoneManagementActivity::class.java)
        intent.putExtra(ZoneManagementListActivity.EXTRA_ID,zoneId)
        scenario2 = ActivityScenario.launch(intent)


        When(mockedDatabase.updateZoneInformation(ZoneManagementActivity.zoneId, ZoneManagementActivity.zone)).thenAnswer { _ ->
            obs
        }

        obs2.postValue(true)

        Espresso.onView(ViewMatchers.withId(R.id.zoneManagementName)).perform(ViewActions.replaceText(zoneName))
        Espresso.onView(ViewMatchers.withId(R.id.zoneManagementDescription)).perform(ViewActions.replaceText(""))
        Espresso.onView(ViewMatchers.withId(R.id.btnManage)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.zone_management_activity)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun failToCreateStayOnActivity(){
        val obs = Observable<Boolean>()
        When(mockedDatabase.createZone(ZoneManagementActivity.zone)).thenAnswer { _ ->
            obs
        }

        Espresso.onView(ViewMatchers.withId(R.id.zoneManagementName)).perform(ViewActions.replaceText("Hello"))
        Espresso.onView(ViewMatchers.withId(R.id.zoneManagementDescription)).perform(ViewActions.replaceText("Hello"))
        Espresso.onView(ViewMatchers.withId(R.id.btnManage)).perform(ViewActions.click())
        obs.postValue(false)

        Espresso.onView(ViewMatchers.withId(R.id.zone_management_activity)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun failToUpdateStayOnActivity(){
        val zoneInfo = Zone(zoneId, zoneName, zoneLoc,zoneDesc)

        val obs = Observable<Boolean>()
        val obs2 = Observable<Boolean>()
        When(mockedDatabase.getZoneInformation(ZoneManagementActivity.zoneId, ZoneManagementActivity.zoneObservable)).thenAnswer{
            ZoneManagementActivity.zoneObservable.postValue(zoneInfo)
            obs2
        }

        val intent = Intent(ApplicationProvider.getApplicationContext(), ZoneManagementActivity::class.java)
        intent.putExtra(ZoneManagementListActivity.EXTRA_ID,zoneId)
        scenario2 = ActivityScenario.launch(intent)


        When(mockedDatabase.updateZoneInformation(ZoneManagementActivity.zoneId, ZoneManagementActivity.zone)).thenAnswer { _ ->
            obs
        }

        obs2.postValue(true)

        Espresso.onView(ViewMatchers.withId(R.id.zoneManagementName)).perform(ViewActions.replaceText(zoneName))
        Espresso.onView(ViewMatchers.withId(R.id.zoneManagementDescription)).perform(ViewActions.replaceText(zoneDesc))
        Espresso.onView(ViewMatchers.withId(R.id.btnManage)).perform(ViewActions.click())
        obs.postValue(true)

        Espresso.onView(ViewMatchers.withId(R.id.zone_management_list_activity)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        scenario2.close()
    }

}