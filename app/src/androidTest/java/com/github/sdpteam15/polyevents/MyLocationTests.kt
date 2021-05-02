package com.github.sdpteam15.polyevents

import android.content.Intent
import android.os.Build
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import com.github.sdpteam15.polyevents.model.database.remote.Database
import com.github.sdpteam15.polyevents.model.database.remote.DatabaseInterface
import com.github.sdpteam15.polyevents.model.database.remote.FirestoreDatabaseProvider
import com.github.sdpteam15.polyevents.model.database.remote.login.UserLogin
import com.github.sdpteam15.polyevents.model.entity.UserEntity
import com.github.sdpteam15.polyevents.model.entity.UserProfile
import com.github.sdpteam15.polyevents.view.activity.MainActivity
import org.junit.Before
import org.mockito.Mockito

import org.junit.After

private const val lat = 42.52010210373032
private const val lng = 8.566237434744834
private const val zoom = 18f

class MyLocationTests {
    var mainActivity = ActivityScenarioRule(MainActivity::class.java)
    lateinit var scenario: ActivityScenario<MainActivity>


    lateinit var testUser: UserEntity

    val uid = "testUid"
    val username = "JohnDoe"
    val email = "John@Doe.com"
    lateinit var mockedDatabase: DatabaseInterface

    @Before
    fun setup() {
        mockedDatabase = HelperTestFunction.defaultMockDatabase()
        val mockedUserProfile = UserProfile("TestID", "TestName")
        Mockito.`when`(mockedDatabase.currentProfile).thenReturn(mockedUserProfile)

        Database.currentDatabase = mockedDatabase

        UserLogin.currentUserLogin.signOut()
        testUser = UserEntity(
            uid = uid,
            username = username,
            email = email
        )
        MainActivity.currentUser = testUser

        val intent = Intent(ApplicationProvider.getApplicationContext(), MainActivity::class.java)
        scenario = ActivityScenario.launch(intent)
        Espresso.onView(ViewMatchers.withId(R.id.ic_home)).perform(click())
        Espresso.onView(ViewMatchers.withId(R.id.id_fragment_admin_hub))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.btnRedirectZoneManagement)).perform(click())
        Intents.init()
    }


    @After
    fun teardown() {
        MainActivity.currentUser = null
        scenario.close()
        Intents.release()
        Database.currentDatabase = FirestoreDatabaseProvider
    }

    //Commented for now, everything works in local and with gradle, only cirrius fails (probably because it doesn't have access to the api key)
    /*
    @Test
    fun locationButtonIsDisplayed() {
        pressBack()
        var position = LatLng(lat, lng)
        var camera = CameraPosition(position, zoom, 0f, 0f)
        val mockedzzt = Mockito.mock(zzt::class.java)
        val m = Marker(mockedzzt)

        val mockedzzw = Mockito.mock(zzw::class.java)
        val p = Polygon(mockedzzw)

        val mockedMap = Mockito.mock(MapsInterface::class.java)
        GoogleMapHelper.map = mockedMap
        Mockito.`when`(mockedMap.cameraPosition).thenReturn(camera)
        Mockito.`when`(mockedMap.setMinZoomPreference(GoogleMapHelper.minZoom)).then {}

        Mockito.`when`(mockedMap.addPolygon(anyOrNull())).thenReturn(p)
        Mockito.`when`(mockedMap.addMarker(anyOrNull())).thenReturn(m)

        ZoneManagementActivity.inTest = true
        // Go to the map fragment
        onView(withId(R.id.ic_map)).perform(click())

        grantPermission()

        val imageButton = onView(
            Matchers.allOf(
                withId(R.id.id_location_button),
                withParent(withParent(withId(R.id.fl_wrapper))),
                isDisplayed()
            )
        )
        imageButton.check(matches(isDisplayed()))

        val imageButton2 = onView(
            Matchers.allOf(
                withId(R.id.id_locate_me_button),
                withParent(withParent(withId(R.id.fl_wrapper))),
                isDisplayed()
            )
        )
        imageButton2.check(matches(isDisplayed()))
    }
    */

    /**
     * Source : https://alexzh.com/ui-testing-of-android-runtime-permissions/
     */
    private fun grantPermission() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()

        val allowPermission = UiDevice.getInstance(instrumentation).findObject(
            UiSelector().text(
                when {
                    Build.VERSION.SDK_INT == 23 -> "Allow"
                    Build.VERSION.SDK_INT <= 28 -> "ALLOW"
                    Build.VERSION.SDK_INT == 29 -> "Allow only while using the app"
                    else -> "While using the app"
                }
            )
        )
        if (allowPermission.exists()) {
            allowPermission.click()
        }
    }
}
