package com.github.sdpteam15.polyevents.view.activity

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import com.github.sdpteam15.polyevents.R


class TimeTableActivity : AppCompatActivity() {
    var nextId = 156
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_time_table)
        addHour()
    }

    fun addHour(){
        val tv = TextView(this)
        val currentId = nextId++
        tv.id = currentId
        tv.text = "15:00"

        val constraintLayout = findViewById<ConstraintLayout>(R.id.id_timetable_constraintlayout)

        constraintLayout.addView(tv)
        val constraintSet = ConstraintSet()
        constraintSet.clone(constraintLayout)
        constraintSet.connect(
            currentId,
            ConstraintSet.RIGHT,
            R.id.text_heure,
            ConstraintSet.RIGHT,
            0
        )
        constraintSet.connect(
            currentId,
            ConstraintSet.TOP,
            R.id.text_heure,
            ConstraintSet.TOP,
            300
        )
        constraintSet.applyTo(constraintLayout)
    }
}