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
import com.github.sdpteam15.polyevents.database.observe.Observable
import com.github.sdpteam15.polyevents.helper.GoogleMapHelper
import com.github.sdpteam15.polyevents.model.UserEntity
import com.github.sdpteam15.polyevents.model.UserProfile
import com.github.sdpteam15.polyevents.model.Zone
import com.google.android.gms.internal.maps.zzt
import com.google.android.gms.internal.maps.zzw
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.Polygon
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
    val zoneId = "10"
    val zoneName = "Cool Zone name"
    val zoneDesc = "Cool zone desc"
    val zoneLoc = ""

    @Before
    fun setup() {
        mockedDatabase = Mockito.mock(DatabaseInterface::class.java)
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
        When(mockedDatabase.createZone(ZoneManagementActivity.zone)).thenAnswer { _ ->
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

        val editingZone = GoogleMapHelper.uidZone++
        GoogleMapHelper.editingZone = editingZone
        GoogleMapHelper.zonesToArea[editingZone] = Pair(null, mutableListOf())

        val zoneInfo = Zone("$editingZone", zoneName, zoneLoc, zoneDesc)

        val obs = Observable<Boolean>()
        val obs2 = Observable<Boolean>()
        When(
            mockedDatabase.getZoneInformation(
                ZoneManagementActivity.zoneId,
                ZoneManagementActivity.zoneObservable
            )
        ).thenAnswer {
            ZoneManagementActivity.zoneObservable.postValue(zoneInfo)
            obs2
        }


        val intent =
            Intent(ApplicationProvider.getApplicationContext(), ZoneManagementActivity::class.java)
        intent.putExtra(ZoneManagementListActivity.EXTRA_ID, "$editingZone")
        scenario2 = ActivityScenario.launch(intent)

        obs2.postValue(true)
        When(
            mockedDatabase.updateZoneInformation(
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
        GoogleMapHelper.editingZone = -1
        GoogleMapHelper.zonesToArea.clear()
        GoogleMapHelper.areasPoints.clear()
        /**/

    }

    @Test
    fun updateWithEmptyFieldsDoesNothing() {
        val zoneInfo = Zone(zoneId, zoneName, zoneLoc, zoneDesc)

        val obs = Observable<Boolean>()
        val obs2 = Observable<Boolean>()
        When(
            mockedDatabase.getZoneInformation(
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
            mockedDatabase.updateZoneInformation(
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
        When(mockedDatabase.createZone(ZoneManagementActivity.zone)).thenAnswer { _ ->
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
            mockedDatabase.getZoneInformation(
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
            mockedDatabase.updateZoneInformation(
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

        var listLngLat: ArrayList<LatLng> = ArrayList()
        listLngLat.add(LatLng(arrayLngLat[0], arrayLngLat[1]))
        listLngLat.add(LatLng(arrayLngLat[2], arrayLngLat[3]))
        listLngLat.add(LatLng(arrayLngLat[4], arrayLngLat[5]))
        listLngLat.add(LatLng(arrayLngLat[6], arrayLngLat[7]))

        var listLngLat2: ArrayList<LatLng> = ArrayList()

        listLngLat2.add(LatLng(arrayLngLat2[0], arrayLngLat2[1]))
        listLngLat2.add(LatLng(arrayLngLat2[2], arrayLngLat2[3]))
        listLngLat2.add(LatLng(arrayLngLat2[4], arrayLngLat2[5]))
        listLngLat2.add(LatLng(arrayLngLat2[6], arrayLngLat2[7]))

        val mockedzzt = Mockito.mock(zzt::class.java)
        var m = Marker(mockedzzt)
        val mockedzzt2 = Mockito.mock(zzt::class.java)
        var m2 = Marker(mockedzzt2)


        val mockedzzw = Mockito.mock(zzw::class.java)
        val p = Polygon(mockedzzw)
        When(mockedzzw.points).thenReturn(listLngLat)

        val mockedzzw2 = Mockito.mock(zzw::class.java)
        val p2 = Polygon(mockedzzw2)
        When(mockedzzw2.points).thenReturn(listLngLat2)
        val nbAreas = 2
        val editingZone = GoogleMapHelper.uidZone++
        val area1 = GoogleMapHelper.uidArea++
        val area2 = GoogleMapHelper.uidArea++

        GoogleMapHelper.areasPoints[area1] = Triple(editingZone, m, p)
        GoogleMapHelper.areasPoints[area2] = Triple(editingZone, m2, p2)

        GoogleMapHelper.editingZone = editingZone
        GoogleMapHelper.zonesToArea[editingZone] = Pair(null, mutableListOf(area1, area2))

        assert(GoogleMapHelper.zonesToArea[editingZone]!!.second.size==nbAreas)

        onView(withId(R.id.btnDeleteZoneCoordinates)).perform(click())
        assert(GoogleMapHelper.zonesToArea[editingZone]!!.second.size==0)

        GoogleMapHelper.editingZone = -1
        GoogleMapHelper.zonesToArea.clear()
        GoogleMapHelper.areasPoints.clear()

    }

}