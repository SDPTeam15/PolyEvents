package com.github.sdpteam15.polyevents.admin

import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.github.sdpteam15.polyevents.MainActivity
import com.github.sdpteam15.polyevents.R
import com.github.sdpteam15.polyevents.RecyclerViewItemCountAssertion
import com.github.sdpteam15.polyevents.adapter.UserListAdapter
import com.github.sdpteam15.polyevents.database.Database
import com.github.sdpteam15.polyevents.database.DatabaseInterface
import com.github.sdpteam15.polyevents.database.FirestoreDatabaseProvider
import com.github.sdpteam15.polyevents.database.objects.UserDatabaseInterface
import com.github.sdpteam15.polyevents.database.observe.Observable
import com.github.sdpteam15.polyevents.database.observe.ObservableList
import com.github.sdpteam15.polyevents.model.UserEntity
import com.github.sdpteam15.polyevents.model.UserProfile
import com.github.sdpteam15.polyevents.model.UserRole
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.anyOrNull
import org.mockito.Mockito.`when` as When

val uidTest = "uidTest"
val nameTest = "nameTest"
val usernameTest = "usernameTest"
val emailTest = "emailTest"

class UserManagementListTest {
    lateinit var users: MutableList<UserEntity>

    private fun setupUser() {
        users = mutableListOf()
        users.add(
            UserEntity(
                uidTest + "1",
                email = emailTest + "1",
                name = nameTest + "1",
                username = usernameTest + "1"
            )
        )

        users.add(
            UserEntity(
                uidTest + "2",
                email = emailTest + "2",
                name = nameTest + "2",
                username = usernameTest + "2"
            )
        )

        users.add(
            UserEntity(
                uidTest + "3",
                email = emailTest + "3",
                name = nameTest + "3",
                username = usernameTest + "3"
            )
        )

        users.add(
            UserEntity(
                uidTest + "4",
                email = emailTest + "4",
                name = nameTest + "4",
                username = usernameTest + "4"
            )
        )
    }

    var mainActivity = ActivityScenarioRule(MainActivity::class.java)
    lateinit var scenario: ActivityScenario<MainActivity>
    lateinit var mockUserDB: UserDatabaseInterface
    val obs = Observable<Boolean>()
    val obs2 = Observable<Boolean>()

    @Before
    fun setup() {
        setupUser()
        val defProfile = UserProfile(pid = "PID", "PNAME", UserRole.ADMIN)
        val mockDatabase = mock(DatabaseInterface::class.java)
        mockUserDB = mock(UserDatabaseInterface::class.java)
        val userEntity = UserEntity("uid", "username", "name", email = "email")
        Database.currentDatabase = mockDatabase
        Database.currentDatabase.userDatabase = mockUserDB
        When(mockDatabase.userDatabase).thenReturn(mockUserDB)

        When(mockDatabase.currentProfile).thenReturn(defProfile)
        When(mockUserDB.currentProfile).thenReturn(defProfile)
        When(mockDatabase.currentUser).thenReturn(userEntity)
        When(mockUserDB.currentUser).thenReturn(userEntity)

        When(mockUserDB.getListAllUsers(anyOrNull(), anyOrNull())).thenAnswer {
            (it.arguments[0] as ObservableList<UserEntity>).addAll(users)
            obs
        }
        When(mockUserDB.getUserInformation(anyOrNull(), anyOrNull(), anyOrNull())).thenAnswer {
            obs2
        }
        val userObs = Observable<UserEntity>()
        When(mockDatabase.currentUserObservable).thenReturn(userObs)

        val intent = Intent(ApplicationProvider.getApplicationContext(), MainActivity::class.java)
        scenario = ActivityScenario.launch(intent)
        Espresso.onView(withId(R.id.ic_home)).perform(ViewActions.click())
        Espresso.onView(withId(R.id.id_fragment_admin_hub))
            .check(ViewAssertions.matches(isDisplayed()))
        Espresso.onView(withId(R.id.btnRedirectUserManagement)).perform(ViewActions.click())
        obs2.postValue(true)
    }

    @After
    fun after() {
        Database.currentDatabase = FirestoreDatabaseProvider
    }

    @Test
    fun correctNumberUsersDisplayed() {
        obs.postValue(true)
        Espresso.onView(ViewMatchers.withId(R.id.recycler_view_user))
            .check(RecyclerViewItemCountAssertion(users.size))
    }

    @Test
    fun getNothingStaysOnActivity() {
        obs.postValue(false)
        Espresso.onView(withId(R.id.recycler_view_user)).check(
            ViewAssertions.matches(
                isDisplayed()
            )
        )
    }

    @Test
    fun userManagementActivityOpensOnClick() {
        Intents.init()
        obs.postValue(true)
        Espresso.onView(withId(R.id.recycler_view_user)).perform(
            RecyclerViewActions.actionOnItemAtPosition<UserListAdapter.UserViewHolder>(
                0,
                ViewActions.click()
            )
        )
        Intents.intended(IntentMatchers.hasComponent(UserManagementActivity::class.java.name))
        Intents.release()
    }
}