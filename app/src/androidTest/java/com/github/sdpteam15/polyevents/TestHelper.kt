package com.github.sdpteam15.polyevents

import android.app.Activity
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.ViewAssertion
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry
import androidx.test.runner.lifecycle.Stage
import org.hamcrest.CoreMatchers
import org.hamcrest.Matcher

object TestHelper {
    // Source : https://stackoverflow.com/questions/38737127/espresso-how-to-get-current-activity-to-test-fragments/58684943#58684943
    fun getCurrentActivity(): Activity? {
        var currentActivity: Activity? = null
        getInstrumentation().runOnMainSync { run { currentActivity = ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(
            Stage.RESUMED).elementAtOrNull(0) } }
        return currentActivity
    }
}

class RecyclerViewItemCountAssertion(expectedCount: Int) : ViewAssertion {
    private val matcher: Matcher<Int> = CoreMatchers.`is`(expectedCount)

    override fun check(view: View, noViewFoundException: NoMatchingViewException?) {
        if (noViewFoundException != null) {
            throw noViewFoundException
        }
        val recyclerView = view as RecyclerView
        val adapter = recyclerView.adapter
        ViewMatchers.assertThat(adapter!!.itemCount, matcher)
    }
}