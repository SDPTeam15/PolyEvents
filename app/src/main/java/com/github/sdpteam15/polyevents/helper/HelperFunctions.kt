package com.github.sdpteam15.polyevents.helper

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.text.TextUtils.replace
import androidx.annotation.Nullable
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.github.sdpteam15.polyevents.R

class HelperFunctions private constructor(){
    companion object{
        fun startActivityAndTerminate(context: Context, target: Class<*>){
            startActivityWithoutTerminate(context,target)
            (context as? Activity)?.finish()
        }

        fun startActivityWithoutTerminate(context: Context, target: Class<*>){
            val loginIntent = Intent(context, target)
            context.startActivity(loginIntent)
        }

        fun changeFragment(activity: FragmentActivity?, newFrag: Fragment){
            activity?.supportFragmentManager?.beginTransaction()?.apply {
                replace(R.id.fl_wrapper,newFrag)
                commit()
            }
        }
    }
}