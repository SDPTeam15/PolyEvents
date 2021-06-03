package com.github.sdpteam15.polyevents.view.activity.admin

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.github.sdpteam15.polyevents.R
import com.github.sdpteam15.polyevents.helper.HelperFunctions
import com.github.sdpteam15.polyevents.helper.HelperFunctions.localDatetimeToString
import com.github.sdpteam15.polyevents.helper.HelperFunctions.showProgressDialog
import com.github.sdpteam15.polyevents.model.database.remote.Database.currentDatabase
import com.github.sdpteam15.polyevents.model.entity.Event
import com.github.sdpteam15.polyevents.model.entity.UserEntity
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
    private val organiserName = ArrayList<String>()
    private val mapIndexToOrganiserId: MutableMap<Int, String> = mutableMapOf()
    private val zoneObserver = ObservableList<Zone>()
    private val organiserObserver = ObservableList<UserEntity>()
    private lateinit var dialogStartDate: DatePickerDialog
    private lateinit var dialogEndDate: DatePickerDialog
    private lateinit var dialogStartTime: TimePickerDialog
    private lateinit var dialogEndTime: TimePickerDialog
    private var isCreation: Boolean = true
    private var curId = ""
    private val observableEvent = Observable<Event>()

    // True if the current user is not an admin
    private var isActivityProvider = false

    // True if the current user is not an admin and he is currently editing an event edit request.
    private var isModificationActivityProvider = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_management)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        // Get intent value (if not NEW_EVENT_ID we know that we are in edition mode)
        val id = intent.getStringExtra(EventManagementListActivity.EVENT_ID_INTENT)!!
        // See if there are intents related to managers so that we can display everything accordingly.
        isActivityProvider = intent.hasExtra(EventManagementListActivity.INTENT_MANAGER)
        isModificationActivityProvider =
            intent.hasExtra(EventManagementListActivity.INTENT_MANAGER_EDIT)

        isCreation = id == EventManagementListActivity.NEW_EVENT_ID
        curId = id

        setupSpinnerAdapter()
        setupDateListener()

        // Default date
        dateStart.postValue(LocalDateTime.now().withSecond(0).withNano(0))
        dateEnd.postValue(LocalDateTime.now().withSecond(0).withNano(0))

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
     * Get all the resources from the database and setup all the adapter for the spinner in the activity
     */
    private fun setupSpinnerAdapter() {
        val adapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, zoneName)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        findViewById<Spinner>(R.id.id_spinner_zone).adapter = adapter

        // Add all the zones retrieve from the database to the spinner
        zoneObserver.observeAdd(this) {
            val name = it.value.zoneName!!
            zoneName.add(name)
            mapIndexToId[zoneName.indexOf(name)] = it.value.zoneId!!
            adapter.notifyDataSetChanged()
        }

        // Get all zones from the database or redirect if there is a problem
        currentDatabase.zoneDatabase.getAllZones(zoneObserver).observe(this) {
            if (!it.value) {
                HelperFunctions.showToast(getString(R.string.failed_get_zones), this)
                finish()
            }
        }

        // We only allow to choose the user if the current user is an admin
        // if the current user is an activity provider which will propose a event edit request, we will simply put its user id.
        if (!isActivityProvider) {
            val adapter2 =
                ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, organiserName)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            findViewById<Spinner>(R.id.spinner_organiser).adapter = adapter2

            // Get all users from the database or redirect if there is a problem
            currentDatabase.userDatabase.getListAllUsers(organiserObserver).observe(this) {
                if (!it.value) {
                    HelperFunctions.showToast(getString(R.string.failed_get_zones), this)
                    finish()
                }
            }

            // Add all the zones retrieve from the database to the spinner
            organiserObserver.observeAdd(this) {
                val name = it.value.name!!
                organiserName.add(name)
                mapIndexToOrganiserId[organiserName.indexOf(name)] = it.value.uid
                adapter2.notifyDataSetChanged()
            }
        }
    }

    /**
     * Setup the lister on the switch limitedEvent
     */
    private fun setupSwitchListener() {
        val nbpart = findViewById<EditText>(R.id.it_et_nb_part)
        findViewById<Switch>(R.id.id_swt_limited_event).setOnCheckedChangeListener { _, isChecked ->
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
        findViewById<Button>(R.id.id_start_date_button).setOnClickListener {
            dialogStartDate.show()
        }
        findViewById<Button>(R.id.id_btn_end_date).setOnClickListener {
            dialogEndDate.show()
        }
        findViewById<Button>(R.id.id_start_time_button).setOnClickListener {
            dialogStartTime.show()
        }
        findViewById<Button>(R.id.id_btn_end_time).setOnClickListener {
            dialogEndTime.show()
        }
    }

    /**
     * Setup all the views in activity, i.e. set default values in field and which field is displayed
     * @param onCallback if the call to this method result from a callback or not (information from database or not)
     */
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private fun setupViewInActivity(onCallback: Boolean) {
        val btnManage = findViewById<Button>(R.id.id_manage_event_button)
        val switch = findViewById<Switch>(R.id.id_swt_limited_event)
        val descET = findViewById<EditText>(R.id.id_description_event_edittext)
        val nameET = findViewById<EditText>(R.id.id_event_management_name_et)
        val nbpart = findViewById<EditText>(R.id.it_et_nb_part)
        val tagsEt = findViewById<EditText>(R.id.event_management_tags_edit)
        val spinnerOrg = findViewById<Spinner>(R.id.spinner_organiser)
        val spinnerZone = findViewById<Spinner>(R.id.id_spinner_zone)

        if (isActivityProvider) {
            spinnerOrg.visibility = View.INVISIBLE
            findViewById<TextView>(R.id.id_tv_spinner_organiser).visibility = View.INVISIBLE
        } else {
            findViewById<TextView>(R.id.id_tv_spinner_organiser).visibility = View.VISIBLE
            spinnerOrg.visibility = View.VISIBLE
        }

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
            var tagTxt = ""
            if (event.tags.size != 0) {
                val txt = event.tags.map {
                    "$it, "
                }.reduce { v, v2 ->
                    v + v2
                }
                tagTxt = txt.substring(0, txt.length - 2)
            }
            tagsEt.setText(tagTxt)
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

            // Select the correct organiser
            organiserObserver.observeOnce(this) {
                var idx = 0
                for (u in it.value.withIndex()) {
                    if (u.value.uid == event.organizer) {
                        idx = u.index
                        break
                    }
                }
                spinnerOrg.setSelection(idx)
            }

            // Select the correct zone
            zoneObserver.observeOnce(this) {
                var idx = 0
                for (u in it.value.withIndex()) {
                    if (u.value.zoneId == event.zoneId) {
                        idx = u.index
                        break
                    }
                }
                spinnerZone.setSelection(idx)
            }
        }
    }

    /**
     * Set the correct listerner on the manage button depending if we are in creation or edit mode
     * If in edit mode, also get the event information
     */
    private fun manageButtonSetup() {
        val btnManage = findViewById<Button>(R.id.id_manage_event_button)
        if (isCreation) {
            btnManage.setOnClickListener {
                handleCreateClick()
            }
        } else {
            btnManage.setOnClickListener {
                handleUpdateClick()
            }

            val infoGotten = Observable<Boolean>()

            // Get the correct information depending on if we edit an event edit request
            if (isModificationActivityProvider) {
                currentDatabase.eventDatabase.getEventEditFromId(curId, observableEvent)
                    .observe(this) {
                        if (it.value) {
                            setupViewInActivity(true)
                        } else {
                            HelperFunctions.showToast(
                                getString(R.string.failed_get_event_information),
                                this
                            )
                            finish()
                        }
                    }.then.updateOnce(this, infoGotten)
            } else {
                // Or if we edit an event
                currentDatabase.eventDatabase.getEventFromId(curId, observableEvent)
                    .observe(this) {
                        if (it.value) {
                            setupViewInActivity(true)
                        } else {
                            HelperFunctions.showToast(
                                getString(R.string.failed_get_event_information),
                                this
                            )
                            finish()
                        }
                    }.then.updateOnce(this, infoGotten)
            }
            // Show a waiting screen until all the information from the database are retrieved
            showProgressDialog(this, listOf(infoGotten), supportFragmentManager)
        }
    }

    /**
     * Handle the click on the create button
     */
    private fun handleCreateClick() {
        if (verifyCondition()) {
            val createEnded = Observable<Boolean>()
            if (isActivityProvider) {
                currentDatabase.eventDatabase.createEventEdit(getInformation()).observe(this) {
                    redirectOrDisplayError(
                        getString(R.string.event_edit_request_successfully_sent),
                        getString(R.string.event_edit_request_error),
                        it.value
                    )
                }.then.updateOnce(this, createEnded)
            } else {
                currentDatabase.eventDatabase.createEvent(getInformation()).observe(this) {
                    redirectOrDisplayError(
                        getString(R.string.event_creation_success),
                        getString(R.string.event_creation_failed),
                        it.value
                    )
                }.then.updateOnce(this, createEnded)
            }
            // Show a waiting screen until the creation is done for the database
            showProgressDialog(this, listOf(createEnded), supportFragmentManager)
        }
    }

    /**
     * Handle the click on the update button
     */
    private fun handleUpdateClick() {
        if (verifyCondition()) {
            val updateEnded = Observable<Boolean>()
            if (isActivityProvider) {
                if (isModificationActivityProvider) {
                    currentDatabase.eventDatabase.updateEventEdit(getInformation())
                        .observe(this) {
                            redirectOrDisplayError(
                                getString(R.string.event_edit_request_successfully_sent),
                                getString(R.string.event_edit_request_error),
                                it.value
                            )
                        }.then.updateOnce(this, updateEnded)
                } else {
                    currentDatabase.eventDatabase.createEventEdit(getInformation())
                        .observe(this) {
                            redirectOrDisplayError(
                                getString(R.string.event_edit_request_successfully_sent),
                                getString(R.string.event_edit_request_error),
                                it.value
                            )
                        }.then.updateOnce(this, updateEnded)
                }
            } else {
                currentDatabase.eventDatabase.updateEvent(getInformation()).observe(this) {
                    redirectOrDisplayError(
                        getString(R.string.event_update_success),
                        getString(R.string.failed_to_update_event_info),
                        it.value
                    )
                }.then.updateOnce(this, updateEnded)
            }
            // Show a waiting screen until the update is done for the database
            showProgressDialog(this, listOf(updateEnded), supportFragmentManager)
        }
    }

    /**
     * Update the text field of the date given in parameter (date should have been modified before calling the function)
     * @param type which date field to update (TYPE_START or TYPE_END)
     */
    private fun updateTextDate(type: String) {
        if (TYPE_START == type) {
            findViewById<TextView>(R.id.et_start_date).text =
                localDatetimeToString(dateStart.value!!)
        } else {
            findViewById<TextView>(R.id.et_end_date).text = localDatetimeToString(dateEnd.value!!)
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
            finish()
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

        val name = findViewById<EditText>(R.id.id_event_management_name_et).text.toString()
        val desc = findViewById<EditText>(R.id.id_description_event_edittext).text.toString()
        val selectedZone = findViewById<Spinner>(R.id.id_spinner_zone).selectedItemPosition
        val selectedOrganiser = findViewById<Spinner>(R.id.spinner_organiser).selectedItemPosition
        val limitedEvent = findViewById<Switch>(R.id.id_swt_limited_event).isChecked
        val nbParticipant = findViewById<EditText>(R.id.it_et_nb_part).text.toString().toInt()
        val tags =
            if (findViewById<EditText>(R.id.event_management_tags_edit).text.toString() != "") {
                findViewById<EditText>(R.id.event_management_tags_edit)
                    .text
                    .toString()
                    .trim()
                    .split(",")
                    .map {
                        it.trim()
                    }
                    .toSet()
                    .toMutableList()
            } else {
                mutableListOf()
            }

        val zoneNa = zoneName[selectedZone]
        val zoneId = mapIndexToId[selectedZone]
        var status: Event.EventStatus? = null

        // Add the event organiser id and the status if the current user is not an admin -> event edit request
        val organiserId: String
        if (!isActivityProvider) {
            organiserId = mapIndexToOrganiserId[selectedOrganiser]!!
        } else {
            organiserId = currentDatabase.currentUser!!.uid
            status = Event.EventStatus.PENDING
        }

        return Event(
            eventId = eventId,
            zoneId = zoneId,
            zoneName = zoneNa,
            startTime = dateStart.value,
            endTime = dateEnd.value,
            eventName = name,
            description = desc,
            limitedEvent = limitedEvent,
            maxNumberOfSlots = nbParticipant,
            organizer = organiserId,
            status = status,
            tags = tags
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
     * @return true if all the conditions are satisfied or false otherwise
     */
    private fun verifyCondition(): Boolean {
        var good = true
        if (findViewById<EditText>(R.id.id_event_management_name_et).text.toString() == "") {
            good = false
            HelperFunctions.showToast(getString(R.string.empty_name_field), this)
        }
        if (findViewById<EditText>(R.id.id_description_event_edittext).text.toString() == "") {
            good = false
            HelperFunctions.showToast(getString(R.string.description_not_empty), this)
        }

        if (dateEnd.value!!.isBefore(dateStart.value!!)) {
            good = false
            HelperFunctions.showToast(getString(R.string.end_date_before_start_date), this)
        }

        if (findViewById<Switch>(R.id.id_swt_limited_event).isChecked) {
            if (findViewById<EditText>(R.id.it_et_nb_part).text.toString()
                    .toInt() < MIN_PART_NB
            ) {
                good = false
                HelperFunctions.showToast(
                    getString(R.string.number_of_event_greater_than_0, MIN_PART_NB),
                    this
                )
            }
        }

        return good
    }
}
