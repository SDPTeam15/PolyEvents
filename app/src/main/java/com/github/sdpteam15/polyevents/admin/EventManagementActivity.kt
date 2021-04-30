package com.github.sdpteam15.polyevents.admin

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.github.sdpteam15.polyevents.R
import com.github.sdpteam15.polyevents.database.Database.currentDatabase
import com.github.sdpteam15.polyevents.database.observe.Observable
import com.github.sdpteam15.polyevents.database.observe.ObservableList
import com.github.sdpteam15.polyevents.helper.HelperFunctions
import com.github.sdpteam15.polyevents.model.Event
import com.github.sdpteam15.polyevents.model.Zone
import java.time.LocalDateTime

class EventManagementActivity : AppCompatActivity() {
    private val zoneName = ArrayList<String>()
    private val mapIndexToId: MutableMap<Int, String> = mutableMapOf()
    private val zoneObserver = ObservableList<Zone>()
    private lateinit var dateStart: LocalDateTime
    private lateinit var dateEnd: LocalDateTime
    private lateinit var dialogStartDate: DatePickerDialog
    private lateinit var dialogEndDate: DatePickerDialog
    private lateinit var dialogStartTime: TimePickerDialog
    private lateinit var dialogEndTime: TimePickerDialog
    private var isCreation:Boolean = true
    private var curId = ""
    private val observableEvent = Observable<Event>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_management)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        val id = intent.getStringExtra(EventManagementListActivity.EVENT_ID_INTENT)!!
        isCreation = id== EventManagementListActivity.NEW_EVENT_ID
        curId = id
        dateStart = LocalDateTime.now()
        dateEnd = LocalDateTime.now()

        val spinner = findViewById<Spinner>(R.id.spinner_zone)
        val adapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, zoneName)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        setObservers(adapter)

        currentDatabase.zoneDatabase!!.getAllZones(null, null, zoneObserver).observe(this) {
            if (!it.value)
                HelperFunctions.showToast("Impossible to get zones from database", this)
        }

        setDateListener()
        setTimeListener()
        setButtonListener()
        manageButtonSetup()
    }

    private fun setButtonListener() {
        findViewById<Button>(R.id.btnStartDate).setOnClickListener {
            dialogStartDate.show()
        }
        findViewById<Button>(R.id.btnEndDate).setOnClickListener {
            dialogEndDate.show()
        }
        this.findViewById<Button>(R.id.btnStartTime).setOnClickListener {
            dialogStartTime.show()
        }
        findViewById<Button>(R.id.btnEndTime).setOnClickListener {
            dialogEndTime.show()
        }
    }

    private fun manageButtonSetup() {
        val btnManage = findViewById<Button>(R.id.btnManageEvent)
        if (isCreation) {
            btnManage.text = "Create event"
            btnManage.setOnClickListener {
                if (verifyCondition()){
                    currentDatabase.eventDatabase!!.createEvent(getInformation()).observe(this){
                        redirectOrDisplayError("Event successfully created","Unable to create the event, please try again", it.value)
                    }
                }
            }
        } else {
            btnManage.text = "Update event"
            btnManage.setOnClickListener {
                if (verifyCondition()) {
                    currentDatabase.eventDatabase!!.updateEvents(getInformation()).observe(this){
                        redirectOrDisplayError("Event successfully updated","Unable to update the event, please try again", it.value)
                    }
                }
            }
        }
    }

    private fun redirectOrDisplayError(msgSuccess:String, msgError:String, value:Boolean){
        if(value){
            HelperFunctions.showToast(msgSuccess,this)
            startActivity(Intent(this, EventManagementListActivity::class.java))
        }else{
            HelperFunctions.showToast(msgError,this)
        }
    }

    private fun getInformation(): Event {
        val eventId: String? = if (isCreation) null else curId

        val name = findViewById<EditText>(R.id.eventManagementNameField).text.toString()
        val desc = findViewById<EditText>(R.id.eventManagementDescriptionField).text.toString()
        val selectedZone = findViewById<Spinner>(R.id.spinner_zone).selectedItemPosition

        val zoneNa = zoneName[selectedZone]
        val zoneId = mapIndexToId[selectedZone]
        return Event(
            eventId = eventId,
            zoneId = zoneId,
            zoneName = zoneNa,
            startTime = this.dateStart,
            endTime = this.dateEnd,
            eventName = name,
            description = desc
        )
    }

    private fun setDateListener() {
        dialogEndDate = DatePickerDialog(
            this, { _: DatePicker, year: Int, month: Int, day: Int ->
                dateEnd =
                    LocalDateTime.of(
                        year,
                        month + 1,
                        day,
                        dateEnd.hour,
                        dateEnd.minute
                    )
                HelperFunctions.showToast("$year ${month + 1} $day", this)
            }, dateEnd.year, dateEnd.monthValue - 1, dateEnd.dayOfMonth
        )

        dialogStartDate = DatePickerDialog(
            this, { _: DatePicker, year: Int, month: Int, day: Int ->
                dateStart =
                    LocalDateTime.of(year, month + 1, day, dateStart.hour, dateStart.minute)
                HelperFunctions.showToast("$year ${month + 1} $day", this)
            }, dateStart.year, dateStart.monthValue - 1, dateStart.dayOfMonth
        )
    }

    private fun setTimeListener() {
        dialogStartTime = TimePickerDialog(
            this, { _: TimePicker, hour: Int, minute: Int ->
                dateEnd =
                    LocalDateTime.of(dateEnd.year, dateEnd.month, dateEnd.dayOfMonth, hour, minute)
            }, dateEnd.hour, dateEnd.minute, true
        )

        dialogEndTime = TimePickerDialog(
            this, { _: TimePicker, hour: Int, minute: Int ->
                dateEnd =
                    LocalDateTime.of(dateEnd.year, dateEnd.month, dateEnd.dayOfMonth, hour, minute)
            }, dateEnd.hour, dateEnd.minute, true
        )
    }

    private fun setObservers(adapter: ArrayAdapter<String>) {
        zoneObserver.observeAdd(this) {
            val name = it.value.zoneName!!
            zoneName.add(name)
            mapIndexToId[zoneName.indexOf(name)] = it.value.zoneId!!
            adapter.notifyDataSetChanged()
        }

        zoneObserver.observeRemove(this) {
            val name = it.value.zoneName!!
            mapIndexToId.remove(zoneName.indexOf(name))
            zoneName.remove(name)
            adapter.notifyDataSetChanged()
        }
    }

    private fun verifyCondition(): Boolean {

        var good = true
        if(findViewById<EditText>(R.id.eventManagementNameField).text.toString() == ""){
            good = false
            HelperFunctions.showToast("Please enter a name", this)
        }
        if(findViewById<EditText>(R.id.eventManagementDescriptionField).text.toString() == ""){
            good = false
            HelperFunctions.showToast("Please enter a description", this)
        }

        if(dateEnd.isBefore(dateStart)){
            good = false
            HelperFunctions.showToast("The end date is before the start date", this)
        }

        return good
    }

}
