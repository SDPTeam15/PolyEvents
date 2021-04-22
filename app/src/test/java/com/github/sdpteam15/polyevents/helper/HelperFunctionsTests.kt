package com.github.sdpteam15.polyevents.helper

import android.Manifest
import android.content.pm.PackageManager
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import java.time.LocalDateTime
import kotlin.test.assertEquals

class HelperFunctionsTests {

    // Fake array of location permissions
    private val grantPermissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    @Test
    fun isPermissionGrantedIsTrueWhenGranted() {
        // Fake attributions of permissions
        val grantResults: IntArray =
            intArrayOf(PackageManager.PERMISSION_GRANTED, PackageManager.PERMISSION_DENIED)

        assertThat(
            HelperFunctions.isPermissionGranted(
                grantPermissions,
                grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION
            ), `is`(true)
        )
    }

    @Test
    fun isPermissionGrantedIsFalseWhenDenied() {
        // Fake attributions of permissions
        val grantResults: IntArray =
            intArrayOf(PackageManager.PERMISSION_DENIED, PackageManager.PERMISSION_GRANTED)

        assertThat(
            HelperFunctions.isPermissionGranted(
                grantPermissions,
                grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION
            ), `is`(false)
        )
    }

    @Test
    fun isPermissionGrantedIsFalseWhenNoPermissionMach() {
        // Fake attributions of permissions
        val grantResults: IntArray =
            intArrayOf(PackageManager.PERMISSION_DENIED, PackageManager.PERMISSION_GRANTED)

        assertThat(
            HelperFunctions.isPermissionGranted(
                grantPermissions,
                grantResults,
                Manifest.permission.ACCESS_NETWORK_STATE
            ), `is`(false)
        )
    }

    @Test
    fun testLocalDateTimeToDateConversionsAreEquivalent() {
        // NOTE!!: Some precision in nanoseconds of time is lost during the conversion
        val ldt = LocalDateTime.now()
        val date = HelperFunctions.localDateTimeToDate(ldt)
        val ldtRetrieved = HelperFunctions.dateToLocalDateTime(date)
        assertEquals(ldt.month, ldtRetrieved!!.month)
        assertEquals(ldt.year, ldtRetrieved.year)
        assertEquals(ldt.dayOfYear, ldtRetrieved.dayOfYear)
        assertEquals(ldt.hour, ldtRetrieved.hour)
        assertEquals(ldt.minute, ldtRetrieved.minute)
        assertEquals(ldt.second, ldtRetrieved.second)
    }
}