package com.github.sdpteam15.polyevents

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.github.sdpteam15.polyevents.database.Database.currentDatabase
import com.github.sdpteam15.polyevents.database.DatabaseConstant
import com.github.sdpteam15.polyevents.database.observe.Observable
import com.github.sdpteam15.polyevents.helper.HelperFunctions
import com.github.sdpteam15.polyevents.model.UserProfile
import com.github.sdpteam15.polyevents.model.UserRole
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

const val CALLER_RANK = "com.github.sdpteam15.polyevents.user.CALLER_RANK"
const val EDIT_PROFILE_ID = "com.github.sdpteam15.polyevents.user.EDIT_PROFILE_ID"

class EditProfileActivity : AppCompatActivity() {
    companion object {
        val updater = Observable<UserProfile>()
        val map = HashMap<String, String>()
    }

    private val id: TextInputEditText get() = findViewById(R.id.EditProfileActivity_ID)
    private val idLayout: LinearLayout get() = findViewById(R.id.EditProfileActivity_IDLayout)
    private val rank: AutoCompleteTextView get() = findViewById(R.id.EditProfileActivity_Rank)
    private val rankLayout: LinearLayout get() = findViewById(R.id.EditProfileActivity_RankLayout)
    private val name: TextInputEditText get() = findViewById(R.id.EditProfileActivity_Name)
    private val save: Button get() = findViewById(R.id.EditProfileActivity_Save)
    private val cancel: Button get() = findViewById(R.id.EditProfileActivity_Cancel)
    private val callerRank: UserRole get() = UserRole.valueOf(intent.getStringExtra(CALLER_RANK)!!)
    private val pid: String get() = intent.getStringExtra(EDIT_PROFILE_ID)!!
    private lateinit var profile: UserProfile

    private var lastRank: UserRole = UserRole.PARTICIPANT
    private var lastName: String = "default"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            resources.getStringArray(R.array.EditProfileActivity_Ranks)
        )

        rank.setAdapter(adapter)

        if (callerRank != UserRole.ADMIN) {
            idLayout.visibility = View.GONE
            rankLayout.visibility = View.GONE
        }

        rank.setOnFocusChangeListener { view, b ->
            rankOnFocusChangeListener(b)
        }
        name.setOnFocusChangeListener { view, b ->
            nameOnFocusChangeListener(b)
        }

        updater.observe(this) {
            profile = it!!
            id.setText(it.userUid)
            lastRank = it.userRole
            rank.setText(it.userRole.toString())
            lastName = it.profileName ?: ""
            name.setText(it.profileName ?: "")
        }
        currentDatabase.getProfileById(updater, pid)

        cancel.setOnClickListener {
            onBackPressed()
        }

        save.setOnClickListener {
            val newName = name.text.toString()
            val newRank = UserRole.valueOf(rank.text.toString())

            if(profile.userRole != newRank)
                map[DatabaseConstant.PROFILE_RANK] = newRank.toString()
            if(profile.profileName != newName)
                map[DatabaseConstant.PROFILE_NAME] = newName

            currentDatabase.updateProfile(map, pid).observe {
                if(it == true)
                    onBackPressed()
                else
                    HelperFunctions.showToast(getString(R.string.EditProfileActivity_DatabaseError),this)
            }
        }
    }

    fun rankOnFocusChangeListener(b :Boolean) {
        if (b) {
            rank.setText("")
        } else {
            try {
                lastRank = UserRole.valueOf(rank.text.toString())
            } catch (e: Exception) {
                rank.setText(lastRank.toString())
            }
        }
    }

    fun nameOnFocusChangeListener(b :Boolean) {
        if (!b) {
            if(name.text.toString().isNotEmpty())
                lastName = name.text.toString()
            else
                name.setText(lastName)
        }
    }
}