package com.github.sdpteam15.polyevents.helper

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import androidx.arch.core.executor.ArchTaskExecutor
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.github.sdpteam15.polyevents.MainActivity
import com.github.sdpteam15.polyevents.R
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Period
import java.time.ZoneId
import java.util.*

object HelperFunctions {
    /**
     * Method that allows to switch the fragment in an event
     * @param newFrag: the fragment we want to display (should be in the fragments app from Mainevent otherwise nothing happen)
     * @param activity: the activity in which a fragment is instantiate
     */
    fun changeFragment(activity: FragmentActivity?, newFrag: Fragment?) {

        if (newFrag != null && MainActivity.fragments.containsValue(newFrag)) {
            activity?.supportFragmentManager?.beginTransaction()?.apply {
                replace(R.id.fl_wrapper, newFrag)
                commit()
            }
        }
    }

    /**
     * Method that allows to switch the fragment in an event
     * @param newFrag: the fragment we want to display (should be in the fragments app from MainEvent otherwise nothing happen)
     * @param activity: the activity in which a fragment is instantiate
     */
    fun refreshFragment(fragmentManager: FragmentManager?, frag: Fragment) {
        val ft: FragmentTransaction = fragmentManager!!.beginTransaction()
        ft.setReorderingAllowed(false)
        ft.detach(frag).attach(frag).commit()
    }

    @SuppressLint("RestrictedApi")
    fun run(runnable: Runnable) {
        try {
            ArchTaskExecutor.getInstance().postToMainThread(runnable)
        } catch (e: RuntimeException) {
            runnable.run()
        }
    }

    fun observeOnStop(lifecycle: LifecycleOwner, result: () -> Boolean): () -> Boolean {
        //Anonymous class to observe the ON_STOP Event ao the Activity/Fragment
        val lifecycleObserver = object : LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
            fun stopListener() = result()
        }
        lifecycle.lifecycle.addObserver(lifecycleObserver)
        return result
    }

    /**
     * Method that display a message as a Toast
     * @param message : the message to display
     * @param context : the context in which to show the toast
     */
    fun showToast(message: String, context: Context) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    /**
     * Helper function to convert a Date instance into the corresponding LocalDateTime. Note that
     * Google Cloud Firestore uses Timestamp which maps to Date and not
     * LocalDateTime.
     *
     * See https://stackoverflow.com/questions/19431234/converting-between-java-time-localdatetime-and-java-util-date
     * for more details
     *
     * @param date the Date instance to convert
     * @return the corresponding LocalDateTime
     */
    fun DateToLocalDateTime(date: Date?): LocalDateTime =
        LocalDateTime.ofInstant(date?.toInstant(), ZoneId.systemDefault())

    /**
     * Converts LocalDateTime to Date.
     * @param ldt the LocalDateTime instance
     *
     * @return the corresponding Date
     */
    fun LocalDateToTimeToDate(ldt: LocalDateTime?): Date =
        Date.from(ldt?.atZone(ZoneId.systemDefault())?.toInstant())

    /**
     * Calculates a person's age based on his birthDate and the current chosen date.
     * @param birthDate the birth date of a person
     * @param currentDate the current reference date based upon which we're calculating the age
     * @return the age of the person
     */
    fun calculateAge(birthDate: LocalDate, currentDate: LocalDate): Int =
        Period.between(birthDate, currentDate).getYears()
}
