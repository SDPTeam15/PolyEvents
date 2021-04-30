package com.github.sdpteam15.polyevents.view.activity.admin

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.github.sdpteam15.polyevents.R
import com.github.sdpteam15.polyevents.helper.HelperFunctions
import com.github.sdpteam15.polyevents.model.database.remote.Database.currentDatabase
import com.github.sdpteam15.polyevents.model.entity.Event
import com.github.sdpteam15.polyevents.model.entity.Zone
import com.github.sdpteam15.polyevents.model.observable.Observable
import com.github.sdpteam15.polyevents.model.observable.ObservableList
import java.time.LocalDateTime

class EventManagementActivity : AppCompatActivity() {

    companion object {
        const val MIN_PART_NB = 1
        const val emptyPartNb = "0"
        const val typeStart = "start"
        const val typeEnd = "END"
        private const val defaultPartNb = "10"
        val dateStart = Observable<LocalDateTime>()
        val dateEnd = Observable<LocalDateTime>()

        fun postValueDate(type: String, year: Int, month: Int, day: Int, hour: Int, minute: Int) {
            if (type == typeStart) {
                dateStart.postValue(
                    LocalDateTime.of(
                        year,
                        month,
                        day,
                        hour,
                        minute
                    )
                )
            } else {
                dateEnd.postValue(
                    LocalDateTime.of(
                        year,
                        month,
                        day,
                        hour,
                        minute
                    )
                )
            }
        }
    }

    private val zoneName = ArrayList<String>()
    private val mapIndexToId: MutableMap<Int, String> = mutableMapOf()
    private val zoneObserver = ObservableList<Zone>()
    private lateinit var dialogStartDate: DatePickerDialog
    private lateinit var dialogEndDate: DatePickerDialog
    private lateinit var dialogStartTime: TimePickerDialog
    private lateinit var dialogEndTime: TimePickerDialog
    private var isCreation: Boolean = true
    private var curId = ""
    private val observableEvent = Observable<Event>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_management)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        val id = intent.getStringExtra(EventManagementListActivity.EVENT_ID_INTENT)!!
        isCreation = id == EventManagementListActivity.NEW_EVENT_ID
        curId = id

        setupDateListener()
        dateStart.postValue(LocalDateTime.now().withSecond(0).withNano(0))
        dateEnd.postValue(LocalDateTime.now().withSecond(0).withNano(0))

        val adapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, zoneName)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        findViewById<Spinner>(R.id.spinner_zone).adapter = adapter

        zoneObserver.observeAdd(this) {
            val name = it.value.zoneName!!
            zoneName.add(name)
            mapIndexToId[zoneName.indexOf(name)] = it.value.zoneId!!
            adapter.notifyDataSetChanged()
        }

        currentDatabase.zoneDatabase!!.getAllZones(null, null, zoneObserver).observe(this) {
            if (!it.value) {
                HelperFunctions.showToast(getString(R.string.failed_get_zones), this)
                startActivity(Intent(this, EventManagementListActivity::class.java))
            }
        }

        setDateListener()
        setTimeListener()
        setButtonListener()
        setupSwitchListener()
        setupViewInActivity(false)
        manageButtonSetup()
    }

    private fun setupDateListener() {
        dateStart.observe(this) {
            updateTextDate(typeStart)
        }
        dateEnd.observe(this) {
            updateTextDate(typeEnd)
        }
    }

    private fun setupSwitchListener() {
        val nbpart = findViewById<EditText>(R.id.etNbPart)
        findViewById<Switch>(R.id.swtLimitedEvent).setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                nbpart.visibility = View.VISIBLE
                nbpart.setText(defaultPartNb)
            } else {
                nbpart.visibility = View.INVISIBLE
                nbpart.setText(emptyPartNb)
            }
        }
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

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private fun setupViewInActivity(onCallback: Boolean) {
        val btnManage = findViewById<Button>(R.id.btnManageEvent)
        val switch = findViewById<Switch>(R.id.swtLimitedEvent)
        val descET = findViewById<EditText>(R.id.eventManagementDescriptionField)
        val nameET = findViewById<EditText>(R.id.eventManagementNameField)
        val nbpart = findViewById<EditText>(R.id.etNbPart)

        if (!onCallback) {
            btnManage.text = getString(R.string.create_event_btn_text)
            nameET.setText("")
            descET.setText("")
            switch.isChecked = false
            nbpart.visibility = View.INVISIBLE
            nbpart.setText(emptyPartNb)
        } else {
            val event = observableEvent.value!!
            btnManage.text = getString(R.string.update_event_btn_text)
            nameET.setText(event.eventName)
            descET.setText(event.description)

            nbpart.setText(emptyPartNb)
            nbpart.visibility = View.INVISIBLE

            if (event.isLimitedEvent()) {
                nbpart.setText(event.getMaxNumberOfSlots()!!.toString())
                nbpart.visibility = View.VISIBLE
            }
            switch.isChecked = event.isLimitedEvent()
            dateStart.postValue(event.startTime!!)
            dateEnd.postValue(event.endTime!!)
            updateTextDate(typeEnd)
            updateTextDate(typeStart)
        }
    }

    private fun manageButtonSetup() {
        val btnManage = findViewById<Button>(R.id.btnManageEvent)
        if (isCreation) {
            btnManage.setOnClickListener {
                if (verifyCondition()) {
                    currentDatabase.eventDatabase!!.createEvent(getInformation()).observe(this) {
                        redirectOrDisplayError(
                            getString(R.string.event_creation_success),
                            getString(R.string.event_creation_failed),
                            it.value
                        )
                    }
                }
            }
        } else {
            btnManage.setOnClickListener {
                if (verifyCondition()) {
                    currentDatabase.eventDatabase!!.updateEvents(getInformation()).observe(this) {
                        redirectOrDisplayError(
                            getString(R.string.event_update_success),
                            getString(R.string.failed_to_update_event_info),
                            it.value
                        )
                    }
                }
            }
            currentDatabase.eventDatabase!!.getEventFromId(curId, observableEvent).observe(this) {
                if (it.value) {
                    setupViewInActivity(true)
                } else {
                    HelperFunctions.showToast(  getString(R.string.failed_get_event_information), this)
                    startActivity(Intent(this, EventManagementListActivity::class.java))
                }
            }
        }
    }

    private fun updateTextDate(type: String) {
        if (typeStart == type) {
            findViewById<EditText>(R.id.et_start_date).setText(dateStart.value!!.toString())
        } else {
            findViewById<EditText>(R.id.et_end_date).setText(dateEnd.value!!.toString())
        }
    }

    private fun redirectOrDisplayError(msgSuccess: String, msgError: String, value: Boolean) {
        if (value) {
            HelperFunctions.showToast(msgSuccess, this)
            startActivity(Intent(this, EventManagementListActivity::class.java))
        } else {
            HelperFunctions.showToast(msgError, this)
        }
    }

    private fun getInformation(): Event {
        val eventId: String? = if (isCreation) null else curId

        val name = findViewById<EditText>(R.id.eventManagementNameField).text.toString()
        val desc = findViewById<EditText>(R.id.eventManagementDescriptionField).text.toString()
        val selectedZone = findViewById<Spinner>(R.id.spinner_zone).selectedItemPosition
        val limitedEvent = findViewById<Switch>(R.id.swtLimitedEvent).isChecked
        val nbParticipant = findViewById<EditText>(R.id.etNbPart).text.toString().toInt()

        val zoneNa = zoneName[selectedZone]
        val zoneId = mapIndexToId[selectedZone]
        return Event(
            eventId = eventId,
            zoneId = zoneId,
            zoneName = zoneNa,
            startTime = dateStart.value,
            endTime = dateEnd.value,
            eventName = name,
            description = desc,
            limitedEvent = limitedEvent,
            maxNumberOfSlots = nbParticipant
        )
    }

    private fun setDateListener() {
        dialogEndDate = DatePickerDialog(
            this, { _: DatePicker, year: Int, month: Int, day: Int ->
                postValueDate(
                    typeEnd,
                    year,
                    month + 1,
                    day,
                    dateEnd.value!!.hour,
                    dateEnd.value!!.minute
                )
            }, dateEnd.value!!.year, dateEnd.value!!.monthValue - 1, dateEnd.value!!.dayOfMonth
        )

        dialogStartDate = DatePickerDialog(
            this,
            { _: DatePicker, year: Int, month: Int, day: Int ->
                postValueDate(
                    typeStart,
                    year,
                    month + 1,
                    day,
                    dateStart.value!!.hour,
                    dateStart.value!!.minute
                )
            },
            dateStart.value!!.year,
            dateStart.value!!.monthValue - 1,
            dateStart.value!!.dayOfMonth
        )
    }

    private fun setTimeListener() {
        dialogStartTime = TimePickerDialog(
            this, { _: TimePicker, hour: Int, minute: Int ->
                postValueDate(
                    typeStart,
                    dateStart.value!!.year,
                    dateStart.value!!.monthValue,
                    dateStart.value!!.dayOfMonth,
                    hour,
                    minute
                )
            },
            dateStart.value!!.hour,
            dateStart.value!!.minute,
            true
        )

        dialogEndTime = TimePickerDialog(
            this, { _: TimePicker, hour: Int, minute: Int ->
                postValueDate(
                    typeEnd,
                    dateEnd.value!!.year,
                    dateEnd.value!!.monthValue,
                    dateEnd.value!!.dayOfMonth,
                    hour,
                    minute
                )
            },
            dateEnd.value!!.hour,
            dateEnd.value!!.minute,
            true
        )
    }


    private fun verifyCondition(): Boolean {
        var good = true
        if (findViewById<EditText>(R.id.eventManagementNameField).text.toString() == "") {
            good = false
            HelperFunctions.showToast(getString(R.string.empty_name_field), this)
        }
        if (findViewById<EditText>(R.id.eventManagementDescriptionField).text.toString() == "") {
            good = false
            HelperFunctions.showToast(getString(R.string.description_not_empty), this)
        }

        if (dateEnd.value!!.isBefore(dateStart.value!!)) {
            good = false
            HelperFunctions.showToast(getString(R.string.end_date_before_start_date), this)
        }

        if (findViewById<Switch>(R.id.swtLimitedEvent).isChecked) {
            if (findViewById<EditText>(R.id.etNbPart).text.toString()
                    .toInt() < MIN_PART_NB
            ) {
                good = false
                HelperFunctions.showToast(
                    "The number of participant must be >= $MIN_PART_NB",
                    this
                )
            }
        }

        return good
    }

}
