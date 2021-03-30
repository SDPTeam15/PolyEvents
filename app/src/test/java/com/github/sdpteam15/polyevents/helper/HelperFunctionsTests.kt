package com.github.sdpteam15.polyevents.helper

import org.junit.Test
import java.time.LocalDateTime
import kotlin.test.assertEquals

class HelperFunctionsTests {
    @Test
    fun testLocalDateTimeToDateConversionsAreEquivalent() {
        // NOTE!!: Some precision in nanoseconds of time is lost during the conversion
        val ldt = LocalDateTime.now()
        val date = HelperFunctions.LocalDateToTimeToDate(ldt)
        val ldtRetrieved = HelperFunctions.DateToLocalDateTime(date)
        assertEquals(ldt.month, ldtRetrieved!!.month)
        assertEquals(ldt.year, ldtRetrieved.year)
        assertEquals(ldt.dayOfYear, ldtRetrieved.dayOfYear)
        assertEquals(ldt.hour, ldtRetrieved.hour)
        assertEquals(ldt.minute, ldtRetrieved.minute)
        assertEquals(ldt.second, ldtRetrieved.second)
    }
}