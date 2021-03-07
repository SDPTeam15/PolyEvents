package com.github.sdpteam15.polyevents.helper

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.github.sdpteam15.polyevents.user.loginActivity

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
    }
}