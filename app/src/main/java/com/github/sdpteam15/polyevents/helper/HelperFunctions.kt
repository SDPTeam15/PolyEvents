package com.github.sdpteam15.polyevents.helper

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
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
    fun showToast(message: String, context: Context?) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    /**
     * Check if a permission was granted
     * (source : https://github.com/googlemaps/android-samples/blob/29ca74b9a3894121f179b9f36b0a51755e7231b0/ApiDemos/kotlin/app/src/gms/java/com/example/kotlindemos/PermissionUtils.kt)
     * @param grantPermissions : the permissions that were asked
     * @param grantResults : the granted permissions
     * @param permission : the permission we want to know whether it was granted
     * @return true if the permission was granted
     */
    fun isPermissionGranted(grantPermissions: Array<String>,
                            grantResults: IntArray,
                            permission: String): Boolean {
        for (a in grantPermissions.indices) {
            if (grantPermissions[a] == permission) {
                return PackageManager.PERMISSION_GRANTED == grantResults[a]
            }
        }
        return false
    }
}
