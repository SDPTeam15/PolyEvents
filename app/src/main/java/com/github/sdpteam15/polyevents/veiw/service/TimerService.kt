package com.github.sdpteam15.polyevents.veiw.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.github.sdpteam15.polyevents.model.observable.Observable
import java.util.*

const val SERVICE_PERIOD = 30L

/**
 * Service that notifies a list of task at constant time interval [SERVICE_PERIOD]s.
 */
class TimerService : Service() {

    private var mTimer: Timer? = null

    private val observers = mutableSetOf<() -> Unit>()

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        instance.postValue(this, this)

        mTimer = Timer()
        mTimer!!.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                for (e in observers)
                    e()
            }
        }, 0, SERVICE_PERIOD * 1000)
    }

    override fun onDestroy() {
        super.onDestroy()
        observers.clear()
        mTimer!!.cancel()
    }

    /**
     * add a task
     */
    fun addTask(task: () -> Unit) {
        observers.add(task)
    }

    companion object {
        val instance = Observable<TimerService>()
    }
}