package com.github.sdpteam15.polyevents

import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.sdpteam15.polyevents.HelperTestFunction.getCurrentActivity
import com.github.sdpteam15.polyevents.database.Database
import com.github.sdpteam15.polyevents.database.DatabaseInterface
import com.github.sdpteam15.polyevents.database.FirestoreDatabaseProvider
import com.github.sdpteam15.polyevents.database.observe.Observable
import com.github.sdpteam15.polyevents.user.ProfileInterface
import com.github.sdpteam15.polyevents.user.Rank
import com.github.sdpteam15.polyevents.user.User
import com.github.sdpteam15.polyevents.user.UserInterface
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when` as When

@RunWith(AndroidJUnit4::class)
class EditProfileActivityTest {

    lateinit var scenario: ActivityScenario<EditProfileActivity>
    val activity: EditProfileActivity get() = getCurrentActivity()

    lateinit var mokedDatabaseInterface: DatabaseInterface
    lateinit var mokedUserInterface: UserInterface
    lateinit var mokedProfileInterface: ProfileInterface

    private val id: ViewInteraction get() = Espresso.onView(withId(R.id.EditProfileActivity_ID))
    private val idLayout: ViewInteraction get() = Espresso.onView(withId(R.id.EditProfileActivity_IDLayout))
    private val rank: ViewInteraction get() = Espresso.onView(withId(R.id.EditProfileActivity_Rank))
    private val rankLayout: ViewInteraction get() = Espresso.onView(withId(R.id.EditProfileActivity_RankLayout))
    private val name: ViewInteraction get() = Espresso.onView(withId(R.id.EditProfileActivity_Name))
    private val cancel: ViewInteraction get() = Espresso.onView(withId(R.id.EditProfileActivity_Cancel))
    private val save: ViewInteraction get() = Espresso.onView(withId(R.id.EditProfileActivity_Save))

    fun setup(rank: Rank, pid: String) {
        val intent =
            Intent(ApplicationProvider.getApplicationContext(), EditProfileActivity::class.java)
        intent.putExtra(CALLER_RANK, rank.toString())
        intent.putExtra(EDIT_PROFILE_ID, pid)

        mokedDatabaseInterface = mock(DatabaseInterface::class.java)
        mokedUserInterface = mock(UserInterface::class.java)
        mokedProfileInterface = mock(ProfileInterface::class.java)

        Database.currentDatabase = mokedDatabaseInterface
        User.currentUser = mokedUserInterface

        When(mokedUserInterface.uid).thenReturn("UID")
        When(mokedUserInterface.name).thenReturn("UName")
        When(mokedUserInterface.email).thenReturn(null)
        When(mokedUserInterface.rank).thenReturn(rank)
        When(mokedUserInterface.currentProfile).thenReturn(mokedProfileInterface)

        When(mokedDatabaseInterface.getListProfile(pid)).thenReturn(listOf(mokedProfileInterface))
        When(mokedDatabaseInterface.getProfileById(EditProfileActivity.updater, pid)).thenAnswer {
            EditProfileActivity.updater.postValue(mokedProfileInterface)
            Observable(true)
        }
        When(mokedDatabaseInterface.updateProfile(EditProfileActivity.map, pid)).thenAnswer {
            Observable(true)
        }

        When(mokedProfileInterface.id).thenReturn(pid)
        When(mokedProfileInterface.name).thenReturn("PName")
        When(mokedProfileInterface.rank).thenReturn(rank)

        scenario = ActivityScenario.launch(intent)
    }

    @After
    fun end() {
        User.currentUser = null
        Database.currentDatabase = FirestoreDatabaseProvider
    }

    fun playoff() {
        scenario.close()
    }

    @Test
    fun adminCanEdit() {
        setup(Rank.Admin, "PID")

        rank.perform(replaceText(Rank.Admin.toString()))
        name.perform(click())
        activity.rankOnFocusChangeListener(false)
        rank.check(matches(ViewMatchers.withText(Rank.Admin.toString())))

        activity.nameOnFocusChangeListener(true)
        name.perform(replaceText("notnull"))
        activity.nameOnFocusChangeListener(false)
        name.check(matches(ViewMatchers.withText("notnull")))

        Espresso.closeSoftKeyboard()

        cancel.perform(click())
    }

    @Test
    fun adminCanUpdate() {
        setup(Rank.Admin, "PID")

        rank.perform(replaceText(Rank.Admin.toString()))
        name.perform(click())
        activity.rankOnFocusChangeListener(false)
        rank.check(matches(ViewMatchers.withText(Rank.Admin.toString())))

        activity.nameOnFocusChangeListener(true)
        name.perform(replaceText("notnull"))
        activity.nameOnFocusChangeListener(false)
        name.check(matches(ViewMatchers.withText("notnull")))

        Espresso.closeSoftKeyboard()

        save.perform(click())
    }

    @Test
    fun adminCanNotUpdate() {
        setup(Rank.Admin, "PID")

        When(mokedDatabaseInterface.updateProfile(EditProfileActivity.map, "PID")).thenAnswer {
            Observable(false)
        }

        rank.perform(replaceText(Rank.Admin.toString()))
        name.perform(click())
        activity.rankOnFocusChangeListener(false)
        rank.check(matches(ViewMatchers.withText(Rank.Admin.toString())))

        activity.nameOnFocusChangeListener(true)
        name.perform(replaceText("notnull"))
        activity.nameOnFocusChangeListener(false)
        name.check(matches(ViewMatchers.withText("notnull")))

        Espresso.closeSoftKeyboard()

        save.perform(click())

        playoff()
    }

    @Test
    fun visitorCanNotEdit() {
        setup(Rank.Visitor, "TestPID")
        idLayout.check(matches(not(isDisplayed())))
        rankLayout.check(matches(not(isDisplayed())))
        playoff()
    }

    @Test
    fun staffCanNotEdit() {
        setup(Rank.Staff, "TestPID")
        idLayout.check(matches(not(isDisplayed())))
        rankLayout.check(matches(not(isDisplayed())))
        playoff()
    }

    @Test
    fun activityProviderCanNotEdit() {
        setup(Rank.ActivityProvider, "TestPID")
        idLayout.check(matches(not(isDisplayed())))
        rankLayout.check(matches(not(isDisplayed())))
        playoff()
    }

    @Test
    fun registeredVisitorProviderCanNotEdit() {
        setup(Rank.RegisteredVisitor, "TestPID")
        idLayout.check(matches(not(isDisplayed())))
        rankLayout.check(matches(not(isDisplayed())))
        playoff()
    }
}