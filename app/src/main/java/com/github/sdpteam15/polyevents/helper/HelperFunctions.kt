package com.github.sdpteam15.polyevents.helper

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.github.sdpteam15.polyevents.MainActivity
import com.github.sdpteam15.polyevents.R

class HelperFunctions private constructor(){
    companion object{
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