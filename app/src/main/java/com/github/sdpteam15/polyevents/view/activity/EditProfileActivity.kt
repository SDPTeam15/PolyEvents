package com.github.sdpteam15.polyevents.view.activity

import android.os.Bundle
import android.view.View
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.github.sdpteam15.polyevents.R
import com.github.sdpteam15.polyevents.helper.HelperFunctions
import com.github.sdpteam15.polyevents.model.database.remote.Database.currentDatabase
import com.github.sdpteam15.polyevents.model.entity.UserProfile
import com.github.sdpteam15.polyevents.model.entity.UserRole
import com.github.sdpteam15.polyevents.model.observable.Observable
import com.github.sdpteam15.polyevents.model.callback.UserModifiedInterface
import com.github.sdpteam15.polyevents.view.adapter.ProfileRankListAdapter
import com.google.android.material.textfield.TextInputEditText

class EditProfileActivity : AppCompatActivity() {
    companion object {
        val updater = Observable<UserProfile>()
        var end = Observable<Boolean>()
        const val CALLER_RANK = "com.github.sdpteam15.polyevents.user.CALLER_RANK"
        const val EDIT_PROFILE_ID = "com.github.sdpteam15.polyevents.user.EDIT_PROFILE_ID"
        var callback: UserModifiedInterface? = null
    }

    private val id: TextInputEditText get() = findViewById(R.id.EditProfileActivity_ID)
    private val idLayout: LinearLayout get() = findViewById(R.id.EditProfileActivity_IDLayout)
    private val rank: AutoCompleteTextView get() = findViewById(R.id.EditProfileActivity_Rank)
    private val rankLayout: LinearLayout get() = findViewById(R.id.EditProfileActivity_RankLayout)
    private val name: TextInputEditText get() = findViewById(R.id.EditProfileActivity_Name)
    private val save: Button get() = findViewById(R.id.EditProfileActivity_Save)
    private val cancel: Button get() = findViewById(R.id.EditProfileActivity_Cancel)
    private val callerRank: UserRole? get() = UserRole.fromString(intent.getStringExtra(CALLER_RANK)!!)
    private val pid: String get() = intent.getStringExtra(EDIT_PROFILE_ID)!!
    private lateinit var profile: UserProfile

    private var lastRank: UserRole = UserRole.PARTICIPANT
    private var lastName: String = "default"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        end.postValue(false, this)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val adapter = ProfileRankListAdapter(
            this,
            android.R.layout.simple_list_item_1,
            resources.getStringArray(R.array.Ranks)
        )

        rank.setAdapter(adapter)
        if (callerRank != UserRole.ADMIN) {
            idLayout.visibility = View.GONE
            rankLayout.visibility = View.GONE
        }

        rank.setOnFocusChangeListener { _, b ->
            rankOnFocusChangeListener(b)
        }

        name.setOnFocusChangeListener { _, b ->
            nameOnFocusChangeListener(b)
        }

        updater.observe(this) {
            profile = it.value
            id.setText(profile.pid)
            lastRank = profile.userRole
            rank.setText(rankToString(profile.userRole))
            lastName = profile.profileName ?: ""
            name.setText(profile.profileName ?: "")
        }
        currentDatabase.userDatabase.getProfileById(updater, pid)

        cancel.setOnClickListener {
            onBackPressed()
        }

        save.setOnClickListener {
            val newName = name.text.toString()
            if (newName.isNotEmpty()) {
                val newRank = stringToRank(rank.text.toString())

                profile.userRole = newRank
                profile.profileName = newName

                currentDatabase.userDatabase.updateProfile(profile).observeOnce(this) {
                    if (it.value) {
                        end.postValue(true, this)
                        callback?.profileHasChanged()
                        callback = null
                        onBackPressed()
                    } else
                        HelperFunctions.showToast(
                            getString(R.string.EditProfileActivity_DatabaseError),
                            this
                        )
                }
            } else {
                HelperFunctions.showToast(
                    getString(R.string.EditProfileActivity_NameShouldNotBeEmpty),
                    this
                )
            }
        }
    }

    fun rankOnFocusChangeListener(b: Boolean) {
        if (!b) {
            lastRank = stringToRank(rank.text.toString())
            rank.setText(rankToString(lastRank))
        }
    }

    fun nameOnFocusChangeListener(b: Boolean) {
        if (!b) {
            if (name.text.toString().isNotEmpty())
                lastName = name.text.toString()
            else
                name.setText(lastName)
        }
    }

    fun rankToString(rank: UserRole): String =
        when (rank) {
            UserRole.ADMIN -> resources.getStringArray(R.array.Ranks)[0]
            UserRole.ORGANIZER -> resources.getStringArray(R.array.Ranks)[1]
            UserRole.STAFF -> resources.getStringArray(R.array.Ranks)[2]
            UserRole.PARTICIPANT -> resources.getStringArray(R.array.Ranks)[3]
        }


    private fun stringToRank(rank: String): UserRole {
        when (rank) {
            resources.getStringArray(R.array.Ranks)[0] -> return UserRole.ADMIN
            resources.getStringArray(R.array.Ranks)[1] -> return UserRole.ORGANIZER
            resources.getStringArray(R.array.Ranks)[2] -> return UserRole.STAFF
            resources.getStringArray(R.array.Ranks)[3] -> return UserRole.PARTICIPANT
        }
        return lastRank
    }
}