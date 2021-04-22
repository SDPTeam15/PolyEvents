package com.github.sdpteam15.polyevents.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.github.sdpteam15.polyevents.Settings.IsSendingLocationOn
import com.github.sdpteam15.polyevents.database.Database
import com.github.sdpteam15.polyevents.database.observe.UpdateArgs
import com.google.android.gms.maps.model.LatLng
import java.util.*

class Timer_Service : Service() {

    private var mTimer: Timer? = null
    var intent: Intent? = null

    private val observers = mutableSetOf<() -> Unit>()
    private val removeList = mutableListOf<() -> Unit>()

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        initServices()

        mTimer = Timer()
        mTimer!!.scheduleAtFixedRate(TimeDisplayTimerTask(), 30, NOTIFY_INTERVAL)
        intent = Intent(str_receiver)
    }

    internal inner class TimeDisplayTimerTask : TimerTask() {
        override fun run() {
            for (e in observers)
                e()
            intent
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        for (e in removeList)
            e()
        removeList.clear()
        mTimer!!.cancel()
    }

    private fun initServices() {
        addServices {
            if(IsSendingLocationOn)
                Database.currentDatabase.heatmapDatabase!!.setUserLocation(
                    LatLng(2.2,2.2)
                )
        }
    }

    fun addServices(service: () -> Unit) {
        observers.add(service)
        removeList.add {
            observers.remove(service)
            Unit
        }
    }

    companion object {
        lateinit var instance: Timer_Service
        var str_receiver = "receiver"
        const val NOTIFY_INTERVAL: Long = 1000
    }
}