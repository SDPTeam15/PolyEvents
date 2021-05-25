package com.github.sdpteam15.polyevents.view.activity

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import com.github.sdpteam15.polyevents.R
import com.github.sdpteam15.polyevents.helper.HelperFunctions
import com.github.sdpteam15.polyevents.model.database.remote.Database
import com.github.sdpteam15.polyevents.model.entity.Event
import com.github.sdpteam15.polyevents.model.observable.ObservableList
import com.github.sdpteam15.polyevents.model.observable.ObservableMap
import com.github.sdpteam15.polyevents.view.fragments.EXTRA_EVENT_ID
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList


class TimeTableActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    private lateinit var leftButton: ImageButton
    private lateinit var rightButton: ImageButton
    private lateinit var spinner: Spinner

    private var obsEventsMap = ObservableMap<String, ObservableMap<String, ObservableList<Event>>>()
    private var obsZoneNames = ObservableMap<String, String>()
    var selectedItem = 0

    private val displayedViews = ArrayList<View>()
    private val idViewToIdEvent = mutableMapOf<Int, String>()

    var nextId = 156
    val hourSizeDp = 100
    val lineHeightDp = 2
    var currentPadding = 30
    val linepaddingLeftDP = 2
    val nowLineHeightDP = 2

    //Event params
    var widthDP = 250

    private var idNowBar: View? = null
    private var selectedDate = LocalDateTime.now()
    private var selectedZone: String? = null


    val hourToLine: MutableMap<Int, Int> = mutableMapOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_time_table)

        leftButton = findViewById(R.id.id_change_zone_left)
        rightButton = findViewById(R.id.id_change_zone_right)

        rightButton.setOnClickListener {
            selectedDate = selectedDate.plusDays(1)
            setupDate()
            drawZoneEvents(selectedZone!!)
        }
        leftButton.setOnClickListener {
            selectedDate = selectedDate.minusDays(1)
            setupDate()
            drawZoneEvents(selectedZone!!)
        }
        spinner = findViewById(R.id.id_title_zone)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        setupSpinner()
        setupDate()

        generateTime(8, 23)
        getEventsFromDB()
        addNowBar()
    }

    private fun setupDate() {
        val date =
            selectedDate.format(DateTimeFormatter.ofPattern("EEEE, dd.MM.yyyy", Locale.FRENCH))
        findViewById<TextView>(R.id.id_date_timetable).text = date
    }

    /**
     * Generates the timeTable
     * @param start start time of the timeTable (included)
     * @param end end time of the timeTable (included)
     */
    fun generateTime(start: Int, end: Int) {
        if (start > end) {
            for (i in start..23) {
                addHour(i)
            }
            for (i in 0..end) {
                addHour(i)
            }
        } else {
            for (i in start..end) {
                addHour(i)
            }
        }
    }

    /**
     * Adds an hour to the timeTable
     * @param hour hour to add to the timeTable
     */
    fun addHour(hour: Int) {
        val tv = TextView(this)
        val currentIdText = nextId++
        val currentIdLine = nextId++
        hourToLine.put(hour, currentIdLine)

        tv.id = currentIdText
        tv.text = "${hour}:00"

        //Create a line
        val lign = View(this)
        lign.backgroundTintList = resources.getColorStateList(R.color.black, null)
        lign.id = currentIdLine

        //Create new parameters for the line (the line)
        val params = ViewGroup.LayoutParams(0, lineHeightDp.dpToPixelsInt(this))
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
            linepaddingLeftDP.dpToPixelsInt(this)
        )
        constraintSet.connect(
            currentIdLine,
            ConstraintSet.RIGHT,
            constraintLayout.id,
            ConstraintSet.RIGHT,
            0
        )

        //Apply all the constraints
        constraintSet.applyTo(constraintLayout)
        currentPadding += hourSizeDp
        addHalfHour(hour)
    }

    fun addHalfHour(hour: Int){
        if(hourToLine.size < 2)
            return
        val currentIdLineHalfHour = nextId++
        val idNextHour = hourToLine[hour]!!
        val idPreviousHour = hourToLine[hour-1]!!
        //Create a line
        val lignHalfHour = View(this)
        lignHalfHour.backgroundTintList = resources.getColorStateList(R.color.semi_black, null)
        lignHalfHour.id = currentIdLineHalfHour

        //Create new parameters for the line (the line)
        val paramsHalfHour = ViewGroup.LayoutParams(0, lineHeightDp.dpToPixelsInt(this))
        lignHalfHour.layoutParams = paramsHalfHour
        lignHalfHour.setBackgroundColor(Color.BLUE)

        val constraintLayout = findViewById<ConstraintLayout>(R.id.id_timetable_constraintlayout)

        constraintLayout.addView(lignHalfHour)

        val constraintSet = ConstraintSet()
        constraintSet.clone(constraintLayout)

        //Constraints for the line of half hours
        constraintSet.connect(
            currentIdLineHalfHour,
            ConstraintSet.TOP,
            idNextHour,
            ConstraintSet.TOP,
            0
        )
        constraintSet.connect(
            currentIdLineHalfHour,
            ConstraintSet.BOTTOM,
            idPreviousHour,
            ConstraintSet.BOTTOM,
            0
        )
        constraintSet.connect(
            currentIdLineHalfHour,
            ConstraintSet.LEFT,
            idNextHour,
            ConstraintSet.LEFT,
            8*linepaddingLeftDP.dpToPixelsInt(this)
        )
        constraintSet.connect(
            currentIdLineHalfHour,
            ConstraintSet.RIGHT,
            idNextHour,
            ConstraintSet.RIGHT,
            0
        )

        //Apply all the constraints
        constraintSet.applyTo(constraintLayout)
    }

    /**
     * Get all events from the DB
     */
    fun getEventsFromDB() {
        val requestObservable = ObservableList<Event>()
        requestObservable.group(this) { it.zoneId!! }.then.map(this, obsZoneNames) {
            it[0].zoneName!!
        }

        requestObservable.group(this) { it.zoneId!! }.then.map(this, obsEventsMap) {
            it.group(this) { e ->
                getDateFormat(e.startTime!!)
            }.then
        }

        Database.currentDatabase.eventDatabase!!.getEvents(
            null,
            null,
            eventList = requestObservable
        )
        .observe(this) {
            if (!it.value) {
                HelperFunctions.showToast(getString(R.string.fail_retrieve_events), this)
                finish()
            } else {
                setupSpinner()
                if (!obsZoneNames.isEmpty()) {
                    selectedZone = obsZoneNames.keys.toList()[0]
                    drawZoneEvents(selectedZone!!)
                }
            }
        }
    }

    private fun getDateFormat(date: LocalDateTime): String {
        return date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy", Locale.FRENCH))
    }


    /**
     * Changes the timetable element to have the events of the new selected zone
     * @param zoneId id of the newly selected zone
     */
    fun drawZoneEvents(zoneId: String) {
        clearDisplayedEvents()
        for (event in obsEventsMap[zoneId]!![getDateFormat(selectedDate)] ?: listOf()) {
            addEvent(event)
        }
        refreshNowBar()
    }


    /**
     * Set up the spinner with its elements and listener
     */
    fun setupSpinner() {
        val adapter =
            ArrayAdapter(this, R.layout.spinner_dropdown_item, obsZoneNames.values(this).then)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        spinner.setSelection(selectedItem)
        spinner.onItemSelectedListener = this
    }

    /**
     * Remove all displayed events that are on the timetable
     */
    fun clearDisplayedEvents() {
        val constraintLayout = findViewById<ConstraintLayout>(R.id.id_timetable_constraintlayout)
        for (event in displayedViews) {
            constraintLayout.removeView(event)
        }
        displayedViews.clear()
        idViewToIdEvent.clear()
    }

    /**
     * Adds an event to the timetable
     * @param event event to add
     */
    fun addEvent(event: Event) {
        val start = event.startTime
        val end = event.endTime
        start ?: return
        val idLine = hourToLine[start.hour] ?: return
        val duration = Duration.between(start, end)
        val height = ((duration.toMinutes() / 60.0) * hourSizeDp).toInt()
        val currentId = nextId++

        val constraintLayout = findViewById<ConstraintLayout>(R.id.id_timetable_constraintlayout)

        //Creation of the box for events
        val eventView2 =
            layoutInflater.inflate(R.layout.item_event_timetable, constraintLayout, false)
        eventView2.findViewById<TextView>(R.id.id_event_name_text).text = event.eventName
        eventView2.id = currentId
        eventView2.findViewById<CardView>(R.id.id_event_card).backgroundTintList =
            resources.getColorStateList(R.color.teal_200, null)

        val params = ViewGroup.LayoutParams(widthDP.dpToPixelsInt(this), height.dpToPixelsInt(this))
        eventView2.layoutParams = params
        constraintLayout.addView(eventView2)
        displayedViews.add(eventView2)
        idViewToIdEvent[currentId] = event.eventId!!

        eventView2.setOnClickListener {
            val intent = Intent(applicationContext, EventActivity::class.java).apply {
                putExtra(EXTRA_EVENT_ID, event.eventId)
            }
            startActivity(intent)
        }

        //Clone the current constraints of the constraintLayout
        val constraintSet = ConstraintSet()
        constraintSet.clone(constraintLayout)

        //Compute the margin top to have the view start at the right position to indicate start time
        val marginTop = ((start.minute / 60.0) * hourSizeDp).toInt()

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
            ConstraintSet.RIGHT,
            idLine,
            ConstraintSet.RIGHT,
            0
        )
        constraintSet.connect(
            currentId,
            ConstraintSet.TOP,
            idLine,
            ConstraintSet.BOTTOM,
            marginTop.dpToPixelsInt(this)
        )
        constraintSet.applyTo(constraintLayout)
    }

    /**
     * Adds the line that indicates the current time
     */
    private fun addNowBar() {
        val now = LocalDateTime.now()
        val nowH = now.hour
        val nowM = now.minute
        val idLine = hourToLine[nowH] ?: return

        val currentId = nextId++
        val constraintLayout = findViewById<ConstraintLayout>(R.id.id_timetable_constraintlayout)

        //Create now line
        val lign = View(this)
        lign.backgroundTintList = resources.getColorStateList(R.color.red, null)
        lign.id = currentId

        //Create new parameters for the line (the line)
        val params = ViewGroup.LayoutParams(0, nowLineHeightDP.dpToPixelsInt(this))
        lign.layoutParams = params
        lign.setBackgroundColor(Color.BLUE)

        constraintLayout.addView(lign)
        idNowBar = lign


        val constraintSet = ConstraintSet()
        constraintSet.clone(constraintLayout)
        val marginTop = ((nowM / 60.0) * hourSizeDp).toInt()
        //Constraints for the line
        constraintSet.connect(
            currentId,
            ConstraintSet.TOP,
            idLine,
            ConstraintSet.BOTTOM,
            marginTop.dpToPixelsInt(this)
        )
        constraintSet.connect(
            currentId,
            ConstraintSet.LEFT,
            idLine,
            ConstraintSet.LEFT,
            linepaddingLeftDP.dpToPixelsInt(this)
        )
        constraintSet.connect(
            currentId,
            ConstraintSet.RIGHT,
            constraintLayout.id,
            ConstraintSet.RIGHT,
            0
        )

        //Apply all the constraints
        constraintSet.applyTo(constraintLayout)


        //TODO : Maybe call the runnable itself instead of recreating each time
        val handler = Handler()
        val task = Runnable { refreshNowBar() }
        val period = (60 - now.second.toLong()) * 1000

        handler.postDelayed(task, period)
    }

    /**
     * Refreshes the display of the bar that indicates the current time
     */
    private fun refreshNowBar() {
        idNowBar ?: return
        val constraintLayout = findViewById<ConstraintLayout>(R.id.id_timetable_constraintlayout)
        constraintLayout.removeView(idNowBar)
        idNowBar = null
        addNowBar()
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, newPos: Int, p3: Long) {
        if (selectedItem != newPos) {
            selectedItem = newPos
            selectedZone = obsZoneNames.keys.toList()[newPos]
            drawZoneEvents(selectedZone!!)
        }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {

    }

    /**
     * Helper function to have size in pixels from dp
     * https://android--code.blogspot.com/2020/08/android-kotlin-convert-dp-to-pixels.html
     */
    fun Int.dpToPixelsInt(context: Context): Int = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), context.resources.displayMetrics
    ).toInt()
}