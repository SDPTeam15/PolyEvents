package com.github.sdpteam15.polyevents.map

import android.Manifest
import android.content.pm.PackageManager
import com.github.sdpteam15.polyevents.helper.HelperFunctions
import com.github.sdpteam15.polyevents.helper.HelperFunctions.formatDateTimeWithRespectToAnotherDate
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import kotlin.test.assertEquals
import kotlin.test.assertNull

class HelperFunctionsTests {

    // Fake array of location permissions
    private val grantPermissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    // using https://currentmillis.com/ on the above date
    val milliseconds: Long = 1638316861000L

    // 1st of December 2021, at 01:01:01
    val ldt = LocalDateTime.ofInstant(
        Instant.ofEpochMilli(milliseconds),
        ZoneId.systemDefault()
    )

    // test string set to string conversions
    val s = "hello,world,1,2,3"
    val set = mutableSetOf(
        "hello", "world", "1", "2", "3"
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


    @Test
    fun testConvertersLongToLocalDateTime() {
        assertEquals(ldt, HelperFunctions.Converters.fromLong(milliseconds))
        // Just checking localDateTime compares by content not reference
        assertEquals(
            LocalDateTime.of(
                2021, 12, 1, 1, 1, 1
            ), LocalDateTime.of(
                2021, 12, 1, 1, 1, 1
            )
        )

        assertNull(HelperFunctions.Converters.fromLong(null))
    }

    @Test
    fun testConvertersLocalDateTimeToLong() {
        assertEquals(milliseconds, HelperFunctions.Converters.fromLocalDateTime(ldt))
        assertNull(HelperFunctions.Converters.fromLocalDateTime(null))
    }

    @Test
    fun testConvertersStringSetToString() {
        assertEquals(
            HelperFunctions.Converters.fromStringSet(
                set
            ), s
        )
        assertNull(HelperFunctions.Converters.fromStringSet(null))
    }

    @Test
    fun testConvertersStringSetToSet() {
        assertEquals(
            HelperFunctions.Converters.fromString(
                s
            ), set
        )
        assertNull(HelperFunctions.Converters.fromString(null))
    }

    @Test
    fun testFormattedDateTime() {
        // 2nd of June 2021
        val currentDateTest = LocalDate.of(2021, 6, 2)

        val sameDayDateTime = LocalDateTime.of(2021, 6, 2, 7, 30, 30)
        assertEquals(
            formatDateTimeWithRespectToAnotherDate(sameDayDateTime, currentDateTest),
            "Today at 7:30"
        )

        val dayAfterDateTime = LocalDateTime.of(2021, 6, 3, 7, 30, 30)
        assertEquals(
            formatDateTimeWithRespectToAnotherDate(dayAfterDateTime, currentDateTest),
            "Tomorrow at 7:30"
        )

        val longAfterThatDateTime = LocalDateTime.of(2021, 12, 12, 7, 30, 40)
        assertEquals(
            formatDateTimeWithRespectToAnotherDate(longAfterThatDateTime, currentDateTest),
            "December 12 at 7:30"
        )
    }
}