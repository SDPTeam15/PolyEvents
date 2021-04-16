package com.github.sdpteam15.polyevents

import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.*
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.*
import androidx.test.espresso.matcher.ViewMatchers.*
import com.github.sdpteam15.polyevents.admin.ZoneManagementActivity
import com.github.sdpteam15.polyevents.admin.ZoneManagementListActivity
import com.github.sdpteam15.polyevents.database.Database
import com.github.sdpteam15.polyevents.database.DatabaseInterface
import com.github.sdpteam15.polyevents.database.FirestoreDatabaseProvider
import com.github.sdpteam15.polyevents.database.objects.ZoneDatabaseInterface
import com.github.sdpteam15.polyevents.database.observe.Observable
import com.github.sdpteam15.polyevents.helper.GoogleMapHelper
import com.github.sdpteam15.polyevents.model.UserEntity
import com.github.sdpteam15.polyevents.model.UserProfile
import com.github.sdpteam15.polyevents.model.Zone
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.`when` as When

class ZoneManagementTest {
    lateinit var scenario: ActivityScenario<MainActivity>
    lateinit var scenario2: ActivityScenario<ZoneManagementActivity>


    lateinit var testUser: UserEntity

    val uid = "testUid"
    val username = "JohnDoe"
    val email = "John@Doe.com"
    lateinit var mockedDatabase: DatabaseInterface
    lateinit var mockedZoneDatabase:ZoneDatabaseInterface
    val zoneId = "IDZone"
    val zoneName = "Cool Zone name"
    val zoneDesc = "Cool zone desc"
    val zoneLoc = ""

    @Before
    fun setup() {
        mockedDatabase = Mockito.mock(DatabaseInterface::class.java)
        mockedZoneDatabase = Mockito.mock(ZoneDatabaseInterface::class.java)
        When(mockedDatabase.zoneDatabase).thenReturn(mockedZoneDatabase)
        
        val mockedUserProfile = UserProfile("TestID", "TestName")
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
        ZoneManagementActivity.inTest = false

        onView(withId(R.id.ic_home)).perform(click())
        onView(withId(R.id.id_fragment_admin_hub))
            .check(matches(isDisplayed()))
        onView(withId(R.id.btnRedirectZoneManagement))
            .perform(click())

        onView(withId(R.id.btnNewZone)).perform(click())

        onView(withId(R.id.zoneManagementDescription))
            .perform(replaceText(""))
        onView(withId(R.id.zoneManagementName))
            .perform(replaceText(""))
        ZoneManagementActivity.zoneObservable.postValue(Zone(location = "not null"))
    }

    @After
    fun teardown() {
        MainActivity.currentUser = null
        scenario.close()
        Database.currentDatabase = FirestoreDatabaseProvider
    }

    @Test
    fun pressCreateButtonDoesNothingWhenEmptyFields() {

        onView(withId(R.id.zoneManagementDescription))
            .check(matches(withText("")))
        onView(withId(R.id.zoneManagementName))
            .check(matches(withText("")))
        onView(withId(R.id.btnManage)).perform(click())
        onView(withId(R.id.zone_management_activity)).check(
            matches(
                isDisplayed()
            )
        )
        onView(withId(R.id.zoneManagementName))
            .perform(replaceText(zoneName))

        onView(withId(R.id.btnManage)).perform(click())
        onView(withId(R.id.zone_management_activity))
            .check(matches(isDisplayed()))
    }

    @Test
    fun createWithCorrectInfoRedirectToCorrectActivity() {
        val obs = Observable<Boolean>()
        When(mockedZoneDatabase.createZone(ZoneManagementActivity.zone)).thenAnswer { _ ->
            obs
        }

        onView(withId(R.id.zoneManagementName))
            .perform(replaceText(zoneName))
        onView(withId(R.id.zoneManagementDescription))
            .perform(replaceText(zoneDesc))
        onView(withId(R.id.btnManage)).perform(click())
        obs.postValue(true)

        onView(withId(R.id.zone_management_list_activity))
            .check(matches(isDisplayed()))
    }

    @Test
    fun updateInfoRedirectToCorrectActivityIfCorrect() {
        val zoneInfo = Zone(zoneId, zoneName, zoneLoc, zoneDesc)

        val obs = Observable<Boolean>()
        val obs2 = Observable<Boolean>()
        When(
            mockedZoneDatabase.getZoneInformation(
                ZoneManagementActivity.zoneId,
                ZoneManagementActivity.zoneObservable
            )
        ).thenAnswer {
            ZoneManagementActivity.zoneObservable.postValue(zoneInfo)
            obs2
        }


        val intent =
            Intent(ApplicationProvider.getApplicationContext(), ZoneManagementActivity::class.java)
        intent.putExtra(ZoneManagementListActivity.EXTRA_ID, zoneId)
        scenario2 = ActivityScenario.launch(intent)

        obs2.postValue(true)
        When(
            mockedZoneDatabase.updateZoneInformation(
                ZoneManagementActivity.zoneId,
                ZoneManagementActivity.zone
            )
        ).thenReturn(obs)
        onView(withId(R.id.zoneManagementName))
            .perform(replaceText(zoneName))
        onView(withId(R.id.zoneManagementDescription))
            .perform(replaceText(zoneDesc + " 2"))
        onView(withId(R.id.btnManage)).perform(click())
        obs.postValue(true)

        onView(withId(R.id.zone_management_list_activity))
            .check(matches(isDisplayed()))
        scenario2.close()
    }

    @Test
    fun updateWithEmptyFieldsDoesNothing() {
        val zoneInfo = Zone(zoneId, zoneName, zoneLoc, zoneDesc)

        val obs = Observable<Boolean>()
        val obs2 = Observable<Boolean>()
        When(
            mockedZoneDatabase.getZoneInformation(
                ZoneManagementActivity.zoneId,
                ZoneManagementActivity.zoneObservable
            )
        ).thenAnswer {
            ZoneManagementActivity.zoneObservable.postValue(zoneInfo)
            obs2
        }

        val intent =
            Intent(ApplicationProvider.getApplicationContext(), ZoneManagementActivity::class.java)
        intent.putExtra(ZoneManagementListActivity.EXTRA_ID, zoneId)
        scenario2 = ActivityScenario.launch(intent)


        When(
            mockedZoneDatabase.updateZoneInformation(
                ZoneManagementActivity.zoneId,
                ZoneManagementActivity.zone
            )
        ).thenAnswer { _ ->
            obs
        }

        obs2.postValue(true)

        onView(withId(R.id.zoneManagementName))
            .perform(replaceText(zoneName))
        onView(withId(R.id.zoneManagementDescription))
            .perform(replaceText(""))
        onView(withId(R.id.btnManage)).perform(click())
        onView(withId(R.id.zone_management_activity))
            .check(matches(isDisplayed()))
    }

    @Test
    fun failToCreateStayOnActivity() {
        val obs = Observable<Boolean>()
        When(mockedZoneDatabase.createZone(ZoneManagementActivity.zone)).thenAnswer { _ ->
            obs
        }

        onView(withId(R.id.zoneManagementName))
            .perform(replaceText("Hello"))
        onView(withId(R.id.zoneManagementDescription))
            .perform(replaceText("Hello"))
        onView(withId(R.id.btnManage)).perform(click())
        obs.postValue(false)

        onView(withId(R.id.zone_management_activity))
            .check(matches(isDisplayed()))
    }

    @Test
    fun failToUpdateStayOnActivity() {
        val zoneInfo = Zone(zoneId, zoneName, zoneLoc, zoneDesc)

        val obs = Observable<Boolean>()
        val obs2 = Observable<Boolean>()
        When(
            mockedZoneDatabase.getZoneInformation(
                ZoneManagementActivity.zoneId,
                ZoneManagementActivity.zoneObservable
            )
        ).thenAnswer {
            ZoneManagementActivity.zoneObservable.postValue(zoneInfo)
            obs2
        }

        val intent =
            Intent(ApplicationProvider.getApplicationContext(), ZoneManagementActivity::class.java)
        intent.putExtra(ZoneManagementListActivity.EXTRA_ID, zoneId)
        scenario2 = ActivityScenario.launch(intent)


        When(
            mockedZoneDatabase.updateZoneInformation(
                ZoneManagementActivity.zoneId,
                ZoneManagementActivity.zone
            )
        ).thenAnswer { _ ->
            obs
        }

        obs2.postValue(true)

        onView(withId(R.id.zoneManagementName))
            .perform(replaceText(zoneName))
        onView(withId(R.id.zoneManagementDescription))
            .perform(replaceText(zoneDesc))
        onView(withId(R.id.btnManage)).perform(click())
        obs.postValue(false)

        onView(withId(R.id.zone_management_activity))
            .check(matches(isDisplayed()))
        scenario2.close()
    }

    @Test
    fun clickOnDeleteButtonClearLocationAndSetCorrectText() {
        onView(withId(R.id.btnDeleteZoneCoordinates))
            .perform(click())
        assert(ZoneManagementActivity.zone.location=="")
        onView(withId(R.id.zoneManagementCoordinates))
            .check(matches(withText("Not set")))
    }

    @Test
    fun zoneIdSetterWorksProperly(){
        ZoneManagementActivity.zoneId=zoneId
        assert(ZoneManagementActivity.zoneId==zoneId)
    }

    @Test
    fun postValueWithNullLocationDisplayCorrectText(){
        val zoneWithNull = Zone(zoneId=zoneId,zoneName=zoneName,location = null,description = zoneDesc)
        ZoneManagementActivity.zoneObservable.postValue(zoneWithNull)
        onView(withId(R.id.zoneManagementCoordinates))
            .check(matches(withText("Not set")))
    }

    @Test
    fun btnManageCoordsCorrectlyActs(){
        ZoneManagementActivity.inTest=true
        onView(withId(R.id.zoneManagementDescription))
            .perform(replaceText(zoneDesc))
        onView(withId(R.id.zoneManagementName))
            .perform(replaceText(zoneName))
        onView(withId(R.id.btnModifyZoneCoordinates))
            .perform(click())
        assert(ZoneManagementActivity.zone.zoneName==zoneName)
        assert(ZoneManagementActivity.zone.description==zoneDesc)
        onView(withId(R.id.flMapEditZone))
            .check(matches(isDisplayed()))
    }

    @Test
    fun deleteCoordinatesDeleteFromGoogleMapHelper(){
        val arrayLngLat = arrayOf(4.10, 4.20, 4.30, 4.40, 4.50, 4.60, 4.70, 4.80)
        val arrayLngLat2 = arrayOf(5.10, 5.20, 5.30, 5.40, 5.50, 5.60, 5.70, 5.80)

        val map:MutableMap<Int,List<LatLng>> = mutableMapOf()

        val listLngLat: ArrayList<LatLng> = ArrayList()
        listLngLat.add(LatLng(arrayLngLat[0], arrayLngLat[1]))
        listLngLat.add(LatLng(arrayLngLat[2], arrayLngLat[3]))
        listLngLat.add(LatLng(arrayLngLat[4], arrayLngLat[5]))
        listLngLat.add(LatLng(arrayLngLat[6], arrayLngLat[7]))

        val listLngLat2: ArrayList<LatLng> = ArrayList()

        listLngLat2.add(LatLng(arrayLngLat2[0], arrayLngLat2[1]))
        listLngLat2.add(LatLng(arrayLngLat2[2], arrayLngLat2[3]))
        listLngLat2.add(LatLng(arrayLngLat2[4], arrayLngLat2[5]))
        listLngLat2.add(LatLng(arrayLngLat2[6], arrayLngLat2[7]))

        map[0] = listLngLat
        map[1] = listLngLat2
        map[2] = listLngLat2
        map[3] = listLngLat2
        map[4] = listLngLat2

        val initSize = map.size
        val nbModified = 3

        GoogleMapHelper.coordinates = map
        assert(GoogleMapHelper.coordinates.size==initSize)


        ZoneManagementActivity.nbModified=nbModified
        GoogleMapHelper.uid = initSize
        onView(withId(R.id.btnDeleteZoneCoordinates)).perform(click())
        assert(GoogleMapHelper.coordinates.size==initSize-nbModified)
        assert(GoogleMapHelper.uid==initSize-nbModified)
        assert(ZoneManagementActivity.nbModified==0)
    }

}