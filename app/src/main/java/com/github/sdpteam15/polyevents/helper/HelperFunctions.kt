package com.github.sdpteam15.polyevents.helper

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.github.sdpteam15.polyevents.MainActivity
import com.github.sdpteam15.polyevents.R

class HelperFunctions private constructor(){
    companion object{
        /**
         * Method that allows to switch the fragment in an activity
         * @param newFrag: the fragment we want to display (should be in the fragments app from MainActivity otherwise nothing happen)
         * @param activity: the activity in which a fragment is instantiate
         */
        fun changeFragment(activity: FragmentActivity?, newFrag: Fragment?){
            if(MainActivity.fragments.containsValue(newFrag)){
                activity?.supportFragmentManager?.beginTransaction()?.apply {
                    replace(R.id.fl_wrapper,newFrag as Fragment)
                    commit()
                }
            }
        }
    }
}