package com.github.sdpteam15.polyevents

import android.os.Build
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import com.github.sdpteam15.polyevents.admin.ZoneManagementActivity
import com.github.sdpteam15.polyevents.helper.GoogleMapHelper
import com.github.sdpteam15.polyevents.helper.MapsInterface
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import org.hamcrest.Matchers
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito

private const val lat = 42.52010210373032
private const val lng = 8.566237434744834
private const val zoom = 18f

class MyLocationTests {
    // Have to take copy code from Mathieu's test in "GoogleMapHelperTest.kt"
    // (cannot put these tests in this class since they test the UI).
    lateinit var mockedMap: MapsInterface
    var position = LatLng(lat, lng)
    var camera = CameraPosition(position, zoom, 0f, 0f)

    @Rule
    @JvmField
    var mainActivity = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun goToMapFragment() {
        mockedMap = Mockito.mock(MapsInterface::class.java)
        GoogleMapHelper.map = mockedMap
        Mockito.`when`(mockedMap.cameraPosition).thenReturn(camera)
        Mockito.`when`(mockedMap.setMinZoomPreference(GoogleMapHelper.minZoom)).then {}
        ZoneManagementActivity.inTest = true
        // Go to the map fragment
        onView(withId(R.id.ic_map)).perform(click())
    }

    /*
    @Test
    fun locationButtonsAreDisplayed() {
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
