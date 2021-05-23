package com.github.sdpteam15.polyevents.view.activity

import android.app.ActionBar
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.provider.SyncStateContract
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.marginRight
import com.github.sdpteam15.polyevents.R
import com.github.sdpteam15.polyevents.helper.HelperFunctions
import java.time.LocalDateTime


class TimeTableActivity : AppCompatActivity() {
    var nextId = 156
    val hourSizeDp = 100
    val lineHeightDp = 1
    var currentPadding = 30
    val hourToLine:MutableMap<Int, Int> = mutableMapOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_time_table)
        generateTime(16, 3)
        addEvent(LocalDateTime.of(2021, 5,23, 18, 30), 90.0)
        addEvent(LocalDateTime.of(2021, 5,23, 21, 30), 45.0)
    }

    fun addEvent(time: LocalDateTime, durationMinutes: Double){
        val idLine = hourToLine[time.hour]?:return
        val height = ((durationMinutes/60)*hourSizeDp).toInt()
        val width = 60
        val currentId = nextId++
        val event = View(this)
        event.backgroundTintList = resources.getColorStateList(R.color.purple_500, null)
        event.id = currentId
        event.setOnClickListener {
            HelperFunctions.showToast("Event click  : ${it.id}", this)
        }

        //Create new parameters for the line (the line)
        val params = ViewGroup.LayoutParams(width.dpToPixelsInt(this),height.dpToPixelsInt(this))
        event.layoutParams = params
        event.setBackgroundColor(Color.BLUE)
        val constraintLayout = findViewById<ConstraintLayout>(R.id.id_timetable_constraintlayout)
        constraintLayout.addView(event)

        val constraintSet = ConstraintSet()
        constraintSet.clone(constraintLayout)
        //Constraints for the time
        constraintSet.connect(
            currentId,
            ConstraintSet.LEFT,
            idLine,
            ConstraintSet.LEFT,
            0
        )
        constraintSet.connect(
            currentId,
            ConstraintSet.TOP,
            idLine,
            ConstraintSet.BOTTOM,
            0
        )
        constraintSet.applyTo(constraintLayout)
    }

    /**
     * Generates the timeTable
     * @param start start time of the timeTable (included)
     * @param end end time of the timeTable (included)
     */
    fun generateTime(start: Int, end: Int){
        if(start > end){
            for(i in start..23){
                addHour(i)
            }
            for(i in 0..end){
                addHour(i)
            }
        }else{
            for(i in start..end){
                addHour(i)
            }
        }
    }

    /**
     * Adds an hour to the timeTable
     * @param hour hour to add to the timeTable
     */
    fun addHour(hour: Int){
        val tv = TextView(this)
        val currentIdText = nextId++
        val currentIdLine = nextId++
        hourToLine.put(hour, currentIdLine)

        tv.id = currentIdText
        tv.text = "${hour}:00"

        //Create a
        val lign = View(this)
        lign.backgroundTintList = resources.getColorStateList(R.color.black, null)
        lign.id = currentIdLine

        //Create new parameters for the line (the line)
        val params = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,lineHeightDp.dpToPixelsInt(this))
        lign.layoutParams = params
        lign.setBackgroundColor(Color.BLUE)

        val constraintLayout = findViewById<ConstraintLayout>(R.id.id_timetable_constraintlayout)

        constraintLayout.addView(tv)
        constraintLayout.addView(lign)

        val constraintSet = ConstraintSet()
        constraintSet.clone(constraintLayout)

        //Constraints for the time
        constraintSet.connect(
            currentIdText,
            ConstraintSet.RIGHT,
            R.id.text_heure,
            ConstraintSet.RIGHT,
            0
        )
        constraintSet.connect(
            currentIdText,
            ConstraintSet.TOP,
            R.id.text_heure,
            ConstraintSet.TOP,
            currentPadding.dpToPixelsInt(this)
        )

        //Constraints for the line
        constraintSet.connect(
            currentIdLine,
            ConstraintSet.TOP,
            currentIdText,
            ConstraintSet.TOP,
            0
        )
        constraintSet.connect(
            currentIdLine,
            ConstraintSet.BOTTOM,
            currentIdText,
            ConstraintSet.BOTTOM,
            0
        )
        constraintSet.connect(
            currentIdLine,
            ConstraintSet.LEFT,
            currentIdText,
            ConstraintSet.RIGHT,
            0
        )

        //Apply all the constraints
        constraintSet.applyTo(constraintLayout)
        currentPadding += hourSizeDp
    }

    /**
     * Helper function to have size in pixels from dp
     * https://android--code.blogspot.com/2020/08/android-kotlin-convert-dp-to-pixels.html
     */
    fun Int.dpToPixelsInt(context: Context):Int = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,this.toFloat(),context.resources.displayMetrics
    ).toInt()
}