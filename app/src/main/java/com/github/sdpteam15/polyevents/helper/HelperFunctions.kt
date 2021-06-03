package com.github.sdpteam15.polyevents.helper

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.arch.core.executor.ArchTaskExecutor
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.RequestPermissionsRequestCodeValidator
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.commit
import androidx.lifecycle.LifecycleOwner
import androidx.room.TypeConverter
import com.github.sdpteam15.polyevents.R
import com.github.sdpteam15.polyevents.model.observable.Observable
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Timestamp
import java.time.*
import java.time.format.DateTimeFormatter
import java.util.*

const val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1

object HelperFunctions {
    /**
     * Method that allows to switch the fragment in an event
     * @param newFrag: the fragment we want to display (should be in the fragments app from Mainevent otherwise nothing happen)
     * @param activity: the activity in which a fragment is instantiate
     * @param addToBackStack if set, add the fragment to the fragment backstack, so that the user
     * can go back to the current fragment on back button
     */
    fun changeFragment(
        activity: FragmentActivity?,
        newFrag: Fragment?,
        idFrameLayout: Int = R.id.fl_wrapper,
        addToBackStack: Boolean = false
    ) {
        if (newFrag != null) {
            // Create new fragment
            val fragmentManager = activity?.supportFragmentManager
            fragmentManager?.beginTransaction()
            fragmentManager?.commit {
                setReorderingAllowed(true)
                replace(idFrameLayout, newFrag)
                if (addToBackStack) {
                    addToBackStack(newFrag::class.java.simpleName)
                }
            }
        }
    }

    /**
     * Change Fragment while passing a bundle
     * @param newFrag: the fragment we want to display (should be in the fragments app from Mainevent otherwise nothing happen)
     * @param activity: the activity in which a fragment is instantiate
     * @param idFrameLayout the id of the fragment container in which the fragment will be instantiated
     * @param bundle the bundle to pass to the new fragment
     * @param addToBackStack if set, add the fragment to the fragment backstack, so that the user
     * can go back to the current fragment on back button
     */
    fun changeFragmentWithBundle(
        activity: FragmentActivity?,
        newFrag: Class<out Fragment>?,
        idFrameLayout: Int = R.id.fl_wrapper,
        bundle: Bundle? = null,
        addToBackStack: Boolean = false
    ) {
        if (newFrag != null) {
            // Create new fragment
            val fragmentManager = activity?.supportFragmentManager
            fragmentManager?.beginTransaction()
            fragmentManager?.commit {
                setReorderingAllowed(true)
                replace(idFrameLayout, newFrag, bundle)
                if (addToBackStack) {
                    addToBackStack(newFrag::class.java.simpleName)
                }
            }
        }
    }

    var end: Observable<Boolean>? = null

    /**
     * Asks for permission to use location
     */
    fun getLocationPermission(activity: Activity): Observable<Boolean> {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */

        if (ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED
        ) {
            return Observable(true)
        } else if (activity is RequestPermissionsRequestCodeValidator) {
            end = Observable<Boolean>()
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
            )
            return end!!
        } else
            return Observable(false)
    }

    fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION -> {
                end?.value = isPermissionGranted(
                    permissions,
                    grantResults,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
                end = null
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun getLoc(activity: Activity): Observable<LatLng?> {
        val end = Observable<LatLng?>()
        LocationServices.getFusedLocationProviderClient(activity).lastLocation.addOnSuccessListener {
            if (it != null)
                end.postValue(
                    LatLng(it.latitude, it.longitude)
                )
            else
                end.postValue(null)
        }.addOnFailureListener { end.postValue(null) }
        return end
    }

    @SuppressLint("RestrictedApi")
    fun run(runnable: Runnable) {
        try {
            ArchTaskExecutor.getInstance().postToMainThread(runnable)
        } catch (e: RuntimeException) {
            runnable.run()
        }
    }

    /**
     * wait that all Observable in list are updated
     * @param lifecycle lifecycle of the observer to automatically remove it from the observers when stopped
     * @param list list of observers to check the update
     * @return an Observable<Boolean> that wil be set to true once all observers in list are Updated
     */
    fun waitUpdate(lifecycle: LifecycleOwner, list: List<Observable<*>>): Observable<Boolean> {
        val ended = Observable<Boolean>()
        val done = MutableList(list.size) { false }
        for (index in list.indices)
            list[index].observeOnce(lifecycle) {
                synchronized(lifecycle) {
                    done[index] = true
                    if (done.reduce { a, b -> a && b })
                        ended.postValue(true)
                }
            }
        return ended
    }

    /**
     * Method that display a message as a Toast
     * @param message : the message to display
     * @param context : the context in which to show the toast
     */
    fun showToast(message: String, context: Context?) {
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
    fun dateToLocalDateTime(date: Any?): LocalDateTime? =
        when(date){
            is Timestamp -> LocalDateTime.ofInstant(date.toDate().toInstant(), ZoneId.systemDefault())
            is Date -> LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault())
            else -> null
        }

    /**
     * Convert
     * s LocalDateTime to Date.
     * @param ldt the LocalDateTime instance
     *
     * @return the corresponding Date
     */
    fun localDateTimeToDate(ldt: LocalDateTime?): Date? =
        ldt?.let { Date.from(it.atZone(ZoneId.systemDefault()).toInstant()) }

    /**
     * Calculates a person's age based on his birthDate and the current chosen date.
     * @param birthDate the birth date of a person
     * @param currentDate the current reference date based upon which we're calculating the age
     * @return the age of the person
     */
    fun calculateAge(birthDate: LocalDate, currentDate: LocalDate): Int =
        Period.between(birthDate, currentDate).years

    /**
     * Check if a permission was granted
     * (source : https://github.com/googlemaps/android-samples/blob/29ca74b9a3894121f179b9f36b0a51755e7231b0/ApiDemos/kotlin/app/src/gms/java/com/example/kotlindemos/PermissionUtils.kt)
     * @param grantPermissions : the permissions that were asked
     * @param grantResults : the granted permissions
     * @param permission : the permission we want to know whether it was granted
     * @return true if the permission was granted
     */
    fun isPermissionGranted(
        grantPermissions: Array<String>,
        grantResults: IntArray,
        permission: String
    ): Boolean {
        for (a in grantPermissions.indices) {
            if (grantPermissions[a] == permission) {
                return PackageManager.PERMISSION_GRANTED == grantResults[a]
            }
        }
        return false
    }

    /**
     *
     */
    fun localDatetimeToString(date: LocalDateTime?, textIfNull: String = ""): String {
        return date?.toString()?.replace("T", " ") ?: textIfNull
    }

    /**
     * Takes date instance with time and another date and returns the format as follows:
     * - if dateTime occurs the same day we return (e.g. "Today at 07:36")
     * - if dateTime occurs the day after the other date we return (e.g. "Tomorrow at 8:30"
     * - else return the date and time (e.g. July 26 at 23:00)
     * @param dateTime the LocalDateTime instance we're trying to format
     * @param other the other date, which is just a date without time, so we can compare days
     * @return the formatted date time with respect to the other date
     */
    fun formatDateTimeWithRespectToAnotherDate(dateTime: LocalDateTime?, other: LocalDate): String {
        var formatted = ""

        if (dateTime != null) {
            // First format the date time using the time formatter (e.g. 07:36)
            val timeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("k:mm")
            formatted = dateTime.format(timeFormatter)

            val dateTimeToLocalDate = dateTime.toLocalDate()

            if (dateTimeToLocalDate.equals(other)) {
                // If today
                formatted = "Today at $formatted"
            } else if (dateTimeToLocalDate.equals(other.plusDays(1L))) {
                // If tomorrow
                formatted = "Tomorrow at $formatted"
            } else {
                val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MMMM dd")
                // if not on the same day or day after, append the day and month as well
                formatted = "${dateTime.format(dateFormatter)} at $formatted"
            }
        }
        return formatted
    }

    /**
     * A class containing type converters for dealing with complex types, when persisting
     * in Room database.
     */
    object Converters {
        /**
         * https://www.baeldung.com/java-time-milliseconds
         */
        @TypeConverter
        fun fromLocalDateTime(value: LocalDateTime?): Long? {
            return value?.let {
                val zdt = ZonedDateTime.of(it, ZoneId.systemDefault())
                zdt.toInstant().toEpochMilli()
            }
        }

        /**
         * https://stackoverflow.com/questions/44883432/long-timestamp-to-localdatetime
         */
        @TypeConverter
        fun fromLong(value: Long?): LocalDateTime? {
            return value?.let {
                LocalDateTime.ofInstant(
                    Instant.ofEpochMilli(it),
                    TimeZone.getDefault().toZoneId()
                )
            }
        }

        @TypeConverter
        fun fromStringSet(value: Set<String>?): String? {
            return value?.joinToString(separator = ",")
        }

        @TypeConverter
        fun fromString(value: String?): MutableSet<String>? {
            return value?.split(",")?.toMutableSet()
        }
    }

    /**
    * if this object is not null apply run else return default
    * @param default default return
    * @param run the function to execute
    * @return if this object is not null apply run else return default
    */
    fun <S, T> S?.apply(default: T, run: (S) -> T) = if (this != null) run(this) else default

    /**
     * if this object is not null apply run else return default
     * @param default default return
     * @param run the function to execute
     * @return if this object is not null apply run else return default
     */
    fun <S, T> S?.apply(run: (S) -> T, default: Lazy<T>) =
        if (this != null) run(this) else default.value

    /**
     * if this object is not null apply run else return null
     * @param run the function to execute
     * @return if this object is apply do the run else return null
     */
    fun <S, T> S?.apply(run: (S) -> T?) = if (this != null) run(this) else null

    /**
     * Display an alert dialog with the given parameters
     * @param context the context of the current activity
     * @param title The title of the alert dialog
     * @param content The message of the alert dialog
     * @param yesContinuation Action that will be done if the yes button is pressed
     * @param noContinuation Action that will be done if the no button is pressed
     * @param yesButtonText The text for the "Yes" button (Yes by default)
     * @param noButtonText The text for the "No" button (No by default)
     */
    fun showAlertDialog(
        context: Context,
        title: String,
        content: String,
        yesContinuation: () -> Unit,
        noContinuation: () -> Unit = { },
        yesButtonText: String? = null,
        noButtonText: String? = null
    ) {
        val builder = AlertDialog.Builder(context)
        builder.setMessage(content)
            .setTitle(title)
            .setPositiveButton(yesButtonText ?: "Yes") { _, _ ->
                yesContinuation()
            }.setNegativeButton(noButtonText ?: "No") { _, _ ->
                noContinuation()
            }
        builder.show()
    }
}
