package com.github.sdpteam15.polyevents.view.activity.admin

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.github.sdpteam15.polyevents.R
import com.github.sdpteam15.polyevents.helper.HelperFunctions
import com.github.sdpteam15.polyevents.helper.HelperFunctions.showProgressDialog
import com.github.sdpteam15.polyevents.model.database.remote.Database.currentDatabase
import com.github.sdpteam15.polyevents.model.entity.UserEntity
import com.github.sdpteam15.polyevents.model.observable.Observable
import com.github.sdpteam15.polyevents.model.observable.ObservableList
import com.github.sdpteam15.polyevents.view.adapter.UserListAdapter

/**
 * Activity that displays the list of all users
 */
class UserManagementListActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private val users = ObservableList<UserEntity>()

    companion object {
        const val EXTRA_USER_ID = "USER_ID"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_management_list)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        recyclerView = findViewById(R.id.recycler_view_user_activity)
        recyclerView.setHasFixedSize(false)

        // Open the UserManagementActivity when we click on one element of the recycler view
        val openUser = { user: UserEntity ->
            val intent = Intent(this, UserManagementActivity::class.java).apply {
                putExtra(EXTRA_USER_ID, user.uid)
            }
            startActivity(intent)
        }
        recyclerView.adapter = UserListAdapter(users, openUser)

        getListUsers()
    }

    /**
     * Set observers and get users from database
     */
    private fun getListUsers() {
        val infoGotten = Observable<Boolean>()
        currentDatabase.userDatabase.getListAllUsers(users).observe {
            if (it.value) {
                recyclerView.adapter!!.notifyDataSetChanged()
            } else {
                HelperFunctions.showToast(getString(R.string.failed_to_get_list_users), this)
            }
        }.then.updateOnce(this, infoGotten)
        // Add a progress dialog to wait for the transaction with the database to be over
        showProgressDialog(this, listOf(infoGotten), supportFragmentManager)

        users.observe(this) {
            recyclerView.adapter!!.notifyDataSetChanged()
        }
    }
}