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
        /**
         * All constant needed for the current activity
         */
        const val MIN_PART_NB = 1
        const val EMPTY_PART_NB = "0"
        const val TYPE_START = "start"
        const val TYPE_END = "END"
        val dateStart = Observable<LocalDateTime>()
        val dateEnd = Observable<LocalDateTime>()
        private const val defaultPartNb = "10"

        /**
         * Create a LocalDateTime from the parameters and post its value into the correct observable
         * @param type which date field to update (TYPE_START or TYPE_END)
         * @param year the year of the date
         * @param month the month of the date
         * @param day the day of the date
         * @param hour the hour of the date
         * @param minute the minute of the date
         */
        fun postValueDate(type: String, year: Int, month: Int, day: Int, hour: Int, minute: Int) {
            if (type == TYPE_START) {
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

        // Get intent value (if not NEW_EVENT_ID we know that we are in edition mode)
        val id = intent.getStringExtra(EventManagementListActivity.EVENT_ID_INTENT)!!
        isCreation = id == EventManagementListActivity.NEW_EVENT_ID
        curId = id

        setupDateListener()
        // Default date
        dateStart.postValue(LocalDateTime.now().withSecond(0).withNano(0))
        dateEnd.postValue(LocalDateTime.now().withSecond(0).withNano(0))

        val adapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, zoneName)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        findViewById<Spinner>(R.id.spinner_zone).adapter = adapter

        // Add all the zones retrieve from the database to the spinner
        zoneObserver.observeAdd(this) {
            val name = it.value.zoneName!!
            zoneName.add(name)
            mapIndexToId[zoneName.indexOf(name)] = it.value.zoneId!!
            adapter.notifyDataSetChanged()
        }

        // Get all zones from the database or redirect if there is a problem
        currentDatabase.zoneDatabase!!.getAllZones(null, null, zoneObserver).observe(this) {
            if (!it.value) {
                HelperFunctions.showToast(getString(R.string.failed_get_zones), this)
                startActivity(Intent(this, EventManagementListActivity::class.java))
            }
        }

        // Call all the setup method
        setDateListener()
        setTimeListener()
        setButtonListener()
        setupSwitchListener()
        setupViewInActivity(false)
        manageButtonSetup()
    }

    /**
     * Setup observers that will observe the date start and date end live data. This will update the text field accordingly every time a change occurs.
     */
    private fun setupDateListener() {
        dateStart.observe(this) {
            updateTextDate(TYPE_START)
        }
        dateEnd.observe(this) {
            updateTextDate(TYPE_END)
        }
    }

    /**
     * Setup the lister on the switch limitedEvent
     */
    private fun setupSwitchListener() {
        val nbpart = findViewById<EditText>(R.id.etNbPart)
        findViewById<Switch>(R.id.swtLimitedEvent).setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                nbpart.visibility = View.VISIBLE
                nbpart.setText(defaultPartNb)
            } else {
                nbpart.visibility = View.INVISIBLE
                nbpart.setText(EMPTY_PART_NB)
            }
        }
    }

    /**
     * Add the listeners to the dialog buttons
     */
    private fun setButtonListener() {
        findViewById<Button>(R.id.btnStartDate).setOnClickListener {
            dialogStartDate.show()
        }
        findViewById<Button>(R.id.btnEndDate).setOnClickListener {
            dialogEndDate.show()
        }
        findViewById<Button>(R.id.btnStartTime).setOnClickListener {
            dialogStartTime.show()
        }
        findViewById<Button>(R.id.btnEndTime).setOnClickListener {
            dialogEndTime.show()
        }
    }

    /**
     * Setup all the views in activity, i.e. set default values in field and which field is displayed
     * @param onCallback if the call to this method result from a callback or not (information from database or not)
     */
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
            nbpart.setText(EMPTY_PART_NB)
        } else {
            val event = observableEvent.value!!
            btnManage.text = getString(R.string.update_event_btn_text)
            nameET.setText(event.eventName)
            descET.setText(event.description)

            nbpart.setText(EMPTY_PART_NB)
            nbpart.visibility = View.INVISIBLE

            if (event.isLimitedEvent()) {
                nbpart.setText(event.getMaxNumberOfSlots()!!.toString())
                nbpart.visibility = View.VISIBLE
            }
            switch.isChecked = event.isLimitedEvent()
            dateStart.postValue(event.startTime!!)
            dateEnd.postValue(event.endTime!!)
            updateTextDate(TYPE_END)
            updateTextDate(TYPE_START)
        }
    }

    /**
     * Set the correct listerner on the manage button depending if we are in creation or edit mode
     * If in edit mode, also get the event information
     */
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

    /**
     * Update the text field of the date given in parameter (date should have been modified before calling the function)
     * @param type which date field to update (TYPE_START or TYPE_END)
     */
    private fun updateTextDate(type: String) {
        if (TYPE_START == type) {
            findViewById<TextView>(R.id.et_start_date).text =
                dateStart.value!!.toString().replace("T"," ")
        } else {
            findViewById<TextView>(R.id.et_end_date).text = dateEnd.value!!.toString().replace("T"," ")
        }
    }

    /**
     * Redirect to the event list activity with a success message if the query to the database is successful otherwise display an error message and stay on actvitiy
     * @param msgSuccess the message to display in case of successs
     * @param msgError the message to display in case of error
     * @param success if the query is successful
     */
    private fun redirectOrDisplayError(msgSuccess: String, msgError: String, success: Boolean) {
        if (success) {
            HelperFunctions.showToast(msgSuccess, this)
            startActivity(Intent(this, EventManagementListActivity::class.java))
        } else {
            HelperFunctions.showToast(msgError, this)
        }
    }

    /**
     * Create an event from written information by the user in the UI activity
     * @return An event created from all the information
     */
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

    /**
     * Set all the listener methods in the date picker dialogs
     */
    private fun setDateListener() {
        dialogEndDate = DatePickerDialog(
            this, { _: DatePicker, year: Int, month: Int, day: Int ->
                postValueDate(
                    TYPE_END,
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
                    TYPE_START,
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

    /**
     * Set all the listeners methods in the time picker dialogs
     */
    private fun setTimeListener() {
        dialogStartTime = TimePickerDialog(
            this, { _: TimePicker, hour: Int, minute: Int ->
                postValueDate(
                    TYPE_START,
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
                    TYPE_END,
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

    /**
     * Verify that all the condition are satisfied. If it is not the case, display the corresponding error message
     * @return if all the conditions are satisfied or not
     */
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
