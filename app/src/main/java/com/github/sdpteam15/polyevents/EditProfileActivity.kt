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
import com.github.sdpteam15.polyevents.user.ProfileInterface
import com.github.sdpteam15.polyevents.user.Rank
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

const val CALLER_RANK = "com.github.sdpteam15.polyevents.user.CALLER_RANK"
const val EDIT_PROFILE_ID = "com.github.sdpteam15.polyevents.user.EDIT_PROFILE_ID"

class EditProfileActivity : AppCompatActivity() {
    companion object {
        val updater = Observable<ProfileInterface>()
        val map = HashMap<String, String>()
    }

    private val id: TextInputEditText get() = findViewById(R.id.EditProfileActivity_ID)
    private val idLayout: LinearLayout get() = findViewById(R.id.EditProfileActivity_IDLayout)
    private val rank: AutoCompleteTextView get() = findViewById(R.id.EditProfileActivity_Rank)
    private val rankLayout: LinearLayout get() = findViewById(R.id.EditProfileActivity_RankLayout)
    private val name: TextInputEditText get() = findViewById(R.id.EditProfileActivity_Name)
    private val save: Button get() = findViewById(R.id.EditProfileActivity_Save)
    private val cancel: Button get() = findViewById(R.id.EditProfileActivity_Cancel)
    private val callerRank: Rank get() = Rank.valueOf(intent.getStringExtra(CALLER_RANK)!!)
    private val pid: String get() = intent.getStringExtra(EDIT_PROFILE_ID)!!
    private lateinit var profile: ProfileInterface

    private var lastRank: Rank = Rank.Visitor
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

        if (callerRank != Rank.Admin) {
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
            id.setText(it.id)
            lastRank = it.rank
            rank.setText(it.rank.toString())
            lastName = it.name
            name.setText(it.name)
        }
        currentDatabase.getProfileById(updater, pid)

        cancel.setOnClickListener {
            onBackPressed()
        }

        save.setOnClickListener {
            val newName = name.text.toString()
            val newRank = Rank.valueOf(rank.text.toString())

            if(profile.rank != newRank)
                map[DatabaseConstant.PROFILE_RANK] = newRank.toString()
            if(profile.name != newName)
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
                lastRank = Rank.valueOf(rank.text.toString())
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