package com.github.sdpteam15.polyevents.helper

import android.content.Context
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
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

    /**
     * Method that display a message as a Toast
     */
    fun showToast(message: String, context: Context) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }
}
