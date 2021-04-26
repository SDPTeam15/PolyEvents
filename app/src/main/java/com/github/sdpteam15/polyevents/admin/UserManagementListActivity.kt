package com.github.sdpteam15.polyevents.admin

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.github.sdpteam15.polyevents.R
import com.github.sdpteam15.polyevents.adapter.UserListAdapter
import com.github.sdpteam15.polyevents.database.Database.currentDatabase
import com.github.sdpteam15.polyevents.database.observe.ObservableList
import com.github.sdpteam15.polyevents.helper.HelperFunctions
import com.github.sdpteam15.polyevents.model.UserEntity

class UserManagementListActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private val users = ObservableList<UserEntity>()
    companion object{
        const val EXTRA_USER_ID = "USER_ID"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_management_list)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        recyclerView = findViewById(R.id.recycler_view_user)
        recyclerView.setHasFixedSize(false)

        val openUser = {
            user: UserEntity ->
            val intent = Intent(this, UserManagementActivity::class.java).apply {
                putExtra(EXTRA_USER_ID, user.uid)
            }
            startActivity(intent)
        }
        recyclerView.adapter = UserListAdapter(users, openUser)

        getListUsers()
    }

    private fun getListUsers(){
        currentDatabase.userDatabase!!.getListAllUsers(users).observe {
            if(it.value){
                recyclerView.adapter!!.notifyDataSetChanged()
            }else {
                HelperFunctions.showToast(getString(R.string.failed_to_get_list_users), this)
            }
        }

        users.observe(this){
            recyclerView.adapter!!.notifyDataSetChanged()
        }
    }
}