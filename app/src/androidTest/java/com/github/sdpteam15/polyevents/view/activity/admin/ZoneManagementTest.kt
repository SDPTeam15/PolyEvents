package com.github.sdpteam15.polyevents.view.activity.admin

import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import com.github.sdpteam15.polyevents.HelperTestFunction
import com.github.sdpteam15.polyevents.R
import com.github.sdpteam15.polyevents.TestHelper
import com.github.sdpteam15.polyevents.model.database.remote.Database
import com.github.sdpteam15.polyevents.model.database.remote.DatabaseInterface
import com.github.sdpteam15.polyevents.model.database.remote.FirestoreDatabaseProvider
import com.github.sdpteam15.polyevents.model.database.remote.login.UserLogin
import com.github.sdpteam15.polyevents.model.database.remote.objects.RouteDatabaseInterface
import com.github.sdpteam15.polyevents.model.database.remote.objects.ZoneDatabaseInterface
import com.github.sdpteam15.polyevents.model.entity.Zone
import com.github.sdpteam15.polyevents.model.map.GoogleMapHelper
import com.github.sdpteam15.polyevents.model.map.ZoneAreaMapHelper
import com.github.sdpteam15.polyevents.model.observable.Observable
import com.github.sdpteam15.polyevents.view.adapter.ZoneItemAdapter
import com.google.android.gms.internal.maps.zzt
import com.google.android.gms.internal.maps.zzw
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.Polygon
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.kotlin.anyOrNull
import org.mockito.Mockito.`when` as When

class ZoneManagementTest {
    lateinit var scenario2: ActivityScenario<ZoneManagementActivity>

    val uid = "testUid"
    val username = "JohnDoe"
    val email = "John@Doe.com"
    lateinit var mockedDatabase: DatabaseInterface
    lateinit var mockedZoneDatabase: ZoneDatabaseInterface
    val zoneId = "10"
    val zoneName = "Cool Zone name"
    val zoneDesc = "Cool zone desc"
    val zoneLoc = ""

    @Before
    fun setup() {
        mockedDatabase = HelperTestFunction.defaultMockDatabase()
        mockedZoneDatabase = mockedDatabase.zoneDatabase

        Database.currentDatabase = mockedDatabase

        UserLogin.currentUserLogin.signOut()

        val intent = Intent(
            ApplicationProvider.getApplicationContext(),
            ZoneManagementListActivity::class.java
        )
        ActivityScenario.launch<ZoneManagementListActivity>(intent)
        ZoneManagementActivity.inTest = false

        onView(withId(R.id.id_new_zone_button)).perform(click())

        onView(withId(R.id.id_zone_management_description_edittext))
            .perform(replaceText(""))
        onView(withId(R.id.id_zone_management_name_edittext))
            .perform(replaceText(""))
        ZoneManagementActivity.zoneObservable.postValue(Zone(location = "not null"))
    }

    @After
    fun teardown() {
        Database.currentDatabase = FirestoreDatabaseProvider
    }

    @Test
    fun pressCreateButtonDoesNothingWhenEmptyFields() {

        onView(withId(R.id.id_zone_management_description_edittext))
            .check(matches(withText("")))
        onView(withId(R.id.id_zone_management_name_edittext))
            .check(matches(withText("")))
        onView(withId(R.id.id_btn_manage)).perform(click())
        onView(withId(R.id.zone_management_activity)).check(
            matches(
                isDisplayed()
            )
        )
        onView(withId(R.id.id_zone_management_name_edittext))
            .perform(replaceText(zoneName))

        onView(withId(R.id.id_btn_manage)).perform(click())
        onView(withId(R.id.zone_management_activity))
            .check(matches(isDisplayed()))
    }

    @Test
    fun createWithCorrectInfoRedirectToCorrectActivity() {
        val obs = Observable<Boolean>()
        When(mockedZoneDatabase.createZone(ZoneManagementActivity.zone)).thenAnswer { _ ->
            obs
        }

        onView(withId(R.id.id_zone_management_name_edittext))
            .perform(replaceText(zoneName))
        onView(withId(R.id.id_zone_management_description_edittext))
            .perform(replaceText(zoneDesc))
        onView(withId(R.id.id_btn_manage)).perform(click())
        obs.postValue(true)

        onView(withId(R.id.zone_management_list_activity))
            .check(matches(isDisplayed()))
    }

    @Test
    @Suppress("UNCHECKED_CAST")
    fun updateInfoRedirectToCorrectActivityIfCorrect() {

        When(mockedDatabase.routeDatabase).thenAnswer {
            val mock = mock(RouteDatabaseInterface::class.java)
            When(mock.removeEdgeConnectedToZone(anyOrNull(), anyOrNull(), anyOrNull())).thenAnswer {
                Observable(true)
            }
            mock
        }

        val editingZone = "Zone ${GoogleMapHelper.uidZone++}"
        ZoneAreaMapHelper.editingZone = editingZone
        ZoneAreaMapHelper.zonesToArea[editingZone] = Pair(null, mutableListOf())

        val zoneInfo = Zone(editingZone, zoneName, zoneLoc, zoneDesc)

        val obs = Observable<Boolean>()
        When(
            mockedZoneDatabase.getZoneInformation(
                anyOrNull(),
                anyOrNull()
            )
        ).thenAnswer {
           (it.arguments[1] as Observable<Zone>).postValue(zoneInfo)
            Observable(true)
        }

        val intent = Intent(
            ApplicationProvider.getApplicationContext(),
            ZoneManagementListActivity::class.java
        )
        scenario2 = ActivityScenario.launch(intent)
        onView(withId(R.id.recycler_zones_list)).perform(
            RecyclerViewActions.actionOnItemAtPosition<ZoneItemAdapter.ItemViewHolder>(
                0, TestHelper.clickChildViewWithId(R.id.id_zone_modify_item)
            )
        )


        When(
            mockedZoneDatabase.updateZoneInformation(
                anyOrNull(),
                anyOrNull()
            )
        ).thenReturn(obs)
        onView(withId(R.id.id_zone_management_name_edittext))
            .perform(replaceText(zoneName))
        onView(withId(R.id.id_zone_management_description_edittext))
            .perform(replaceText("$zoneDesc 2"))
        onView(withId(R.id.id_zone_management_coordinates_edittext))
            .perform(replaceText("Set"))
        onView(withId(R.id.id_btn_manage)).perform(click())
        obs.postValue(true)

        Thread.sleep(500)
        onView(withId(R.id.zone_management_list_activity))
            .check(matches(isDisplayed()))
        scenario2.close()
        ZoneAreaMapHelper.editingZone = null
        ZoneAreaMapHelper.zonesToArea.clear()
        ZoneAreaMapHelper.areasPoints.clear()

    }

    @Test
    fun updateWithEmptyFieldsDoesNothing() {
        val zoneInfo = Zone(zoneId, zoneName, zoneLoc, zoneDesc)

        val obs = Observable<Boolean>()
        When(
            mockedZoneDatabase.getZoneInformation(
                anyOrNull(),
                anyOrNull()
            )
        ).thenAnswer {
            ZoneManagementActivity.zoneObservable.postValue(zoneInfo)
            Observable(true)
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


        onView(withId(R.id.id_zone_management_name_edittext))
            .perform(replaceText(zoneName))
        onView(withId(R.id.id_zone_management_description_edittext))
            .perform(replaceText(""))
        onView(withId(R.id.id_btn_manage)).perform(click())
        onView(withId(R.id.zone_management_activity))
            .check(matches(isDisplayed()))
    }

    @Test
    fun failToCreateStayOnActivity() {
        val obs = Observable<Boolean>()
        When(mockedZoneDatabase.createZone(ZoneManagementActivity.zone)).thenAnswer { _ ->
            obs
        }

        onView(withId(R.id.id_zone_management_name_edittext))
            .perform(replaceText("Hello"))
        onView(withId(R.id.id_zone_management_description_edittext))
            .perform(replaceText("Hello"))
        onView(withId(R.id.id_btn_manage)).perform(click())
        obs.postValue(false)

        onView(withId(R.id.zone_management_activity))
            .check(matches(isDisplayed()))
    }

    @Test
    fun failToUpdateStayOnActivity() {

        val zoneInfo = Zone(zoneId, zoneName, zoneLoc, zoneDesc)

        val obs = Observable<Boolean>()
        When(
            mockedZoneDatabase.getZoneInformation(
                anyOrNull(),
                anyOrNull()
            )
        ).thenAnswer {
            ZoneManagementActivity.zoneObservable.postValue(zoneInfo)
            Observable(true)
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


        onView(withId(R.id.id_zone_management_name_edittext))
            .perform(replaceText(zoneName))
        onView(withId(R.id.id_zone_management_description_edittext))
            .perform(replaceText(zoneDesc))
        onView(withId(R.id.id_btn_manage)).perform(click())
        obs.postValue(false)

        onView(withId(R.id.zone_management_activity))
            .check(matches(isDisplayed()))
        scenario2.close()
    }

    @Test
    fun clickOnDeleteButtonClearLocationAndSetCorrectText() {
        onView(withId(R.id.id_btn_delete_coordinates))
            .perform(click())
        assert(ZoneManagementActivity.zone.location == "")
        onView(withId(R.id.id_zone_management_coordinates_edittext))
            .check(matches(withText("Not set")))
    }

    @Test
    fun zoneIdSetterWorksProperly() {
        ZoneManagementActivity.zoneId = zoneId
        assert(ZoneManagementActivity.zoneId == zoneId)
    }

    @Test
    fun postValueWithNullLocationDisplayCorrectText() {
        val zoneWithNull =
            Zone(zoneId = zoneId, zoneName = zoneName, location = null, description = zoneDesc)
        ZoneManagementActivity.zoneObservable.postValue(zoneWithNull)
        onView(withId(R.id.id_zone_management_coordinates_edittext))
            .check(matches(withText("Not set")))
    }

    @Test
    fun btnManageCoordsCorrectlyActs() {
        ZoneManagementActivity.inTest = true
        onView(withId(R.id.id_zone_management_description_edittext))
            .perform(replaceText(zoneDesc))
        onView(withId(R.id.id_zone_management_name_edittext))
            .perform(replaceText(zoneName))
        onView(withId(R.id.id_btn_modify_coordinates))
            .perform(click())
        assert(ZoneManagementActivity.zone.zoneName == zoneName)
        assert(ZoneManagementActivity.zone.description == zoneDesc)
        onView(withId(R.id.id_framelayout_map_edit_zone))
            .check(matches(isDisplayed()))
    }

    @Test
    fun deleteCoordinatesDeleteFromGoogleMapHelper() {
        val arrayLngLat = arrayOf(4.10, 4.20, 4.30, 4.40, 4.50, 4.60, 4.70, 4.80)
        val arrayLngLat2 = arrayOf(5.10, 5.20, 5.30, 5.40, 5.50, 5.60, 5.70, 5.80)

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

        val mockedzzt = Mockito.mock(zzt::class.java)
        val m = Marker(mockedzzt)
        val mockedzzt2 = Mockito.mock(zzt::class.java)
        val m2 = Marker(mockedzzt2)


        val mockedzzw = Mockito.mock(zzw::class.java)
        val p = Polygon(mockedzzw)
        When(mockedzzw.points).thenReturn(listLngLat)

        val mockedzzw2 = Mockito.mock(zzw::class.java)
        val p2 = Polygon(mockedzzw2)
        When(mockedzzw2.points).thenReturn(listLngLat2)
        val nbAreas = 2
        val editingZone = "Zone ${GoogleMapHelper.uidZone++}"
        val area1 = GoogleMapHelper.uidArea++
        val area2 = GoogleMapHelper.uidArea++

        ZoneAreaMapHelper.areasPoints[area1] = Triple(editingZone, m, p)
        ZoneAreaMapHelper.areasPoints[area2] = Triple(editingZone, m2, p2)

        ZoneAreaMapHelper.editingZone = editingZone
        ZoneAreaMapHelper.zonesToArea[editingZone] = Pair(null, mutableListOf(area1, area2))

        assert(ZoneAreaMapHelper.zonesToArea[editingZone]!!.second.size == nbAreas)

        onView(withId(R.id.id_btn_delete_coordinates)).perform(click())
        assert(ZoneAreaMapHelper.zonesToArea[editingZone]!!.second.size == 0)

        ZoneAreaMapHelper.editingZone = null
        ZoneAreaMapHelper.zonesToArea.clear()
        ZoneAreaMapHelper.areasPoints.clear()

    }

}