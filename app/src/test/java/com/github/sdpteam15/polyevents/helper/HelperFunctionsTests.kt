package com.github.sdpteam15.polyevents.helper

import android.Manifest
import android.content.pm.PackageManager
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test

class HelperFunctionsTests {

    // Fake array of location permissions
    private val grantPermissions = arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION,
        android.Manifest.permission.ACCESS_COARSE_LOCATION)

    @Test
    fun isPermissionGrantedIsTrueWhenGranted() {
        // Fake attributions of permissions
        val grantResults: IntArray = intArrayOf(PackageManager.PERMISSION_GRANTED, PackageManager.PERMISSION_DENIED)

        assertThat(HelperFunctions.isPermissionGranted(grantPermissions, grantResults, Manifest.permission.ACCESS_FINE_LOCATION), `is`(true))
    }

    @Test
    fun isPermissionGrantedIsFalseWhenDenied() {
        // Fake attributions of permissions
        val grantResults: IntArray = intArrayOf(PackageManager.PERMISSION_DENIED, PackageManager.PERMISSION_GRANTED)

        assertThat(HelperFunctions.isPermissionGranted(grantPermissions, grantResults, Manifest.permission.ACCESS_FINE_LOCATION), `is`(false))
    }

    @Test
    fun isPermissionGrantedIsFalseWhenNoPermissionMach() {
        // Fake attributions of permissions
        val grantResults: IntArray = intArrayOf(PackageManager.PERMISSION_DENIED, PackageManager.PERMISSION_GRANTED)

        assertThat(HelperFunctions.isPermissionGranted(grantPermissions, grantResults, Manifest.permission.ACCESS_NETWORK_STATE), `is`(false))
    }
}
