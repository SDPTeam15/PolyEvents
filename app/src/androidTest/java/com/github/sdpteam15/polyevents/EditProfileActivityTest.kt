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
import com.github.sdpteam15.polyevents.model.UserEntity
import com.github.sdpteam15.polyevents.model.UserProfile
import com.github.sdpteam15.polyevents.model.UserRole
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
    lateinit var mokedUserEntity: UserEntity
    lateinit var mokedUserProfile: UserProfile

    private val id: ViewInteraction get() = Espresso.onView(withId(R.id.EditProfileActivity_ID))
    private val idLayout: ViewInteraction get() = Espresso.onView(withId(R.id.EditProfileActivity_IDLayout))
    private val rank: ViewInteraction get() = Espresso.onView(withId(R.id.EditProfileActivity_Rank))
    private val rankLayout: ViewInteraction get() = Espresso.onView(withId(R.id.EditProfileActivity_RankLayout))
    private val name: ViewInteraction get() = Espresso.onView(withId(R.id.EditProfileActivity_Name))
    private val cancel: ViewInteraction get() = Espresso.onView(withId(R.id.EditProfileActivity_Cancel))
    private val save: ViewInteraction get() = Espresso.onView(withId(R.id.EditProfileActivity_Save))

    fun setup(rank: UserRole, pid: String) {
        val intent =
            Intent(ApplicationProvider.getApplicationContext(), EditProfileActivity::class.java)
        intent.putExtra(CALLER_RANK, rank.toString())
        intent.putExtra(EDIT_PROFILE_ID, pid)

        mokedDatabaseInterface = mock(DatabaseInterface::class.java)
        mokedUserEntity = UserEntity(
            uid = "UID",
            username = "UName",
            name = "UName"
        )
        mokedUserProfile = UserProfile(
            pid = "PID",
            profileName = "PName",
            userRole = rank
        )

        Database.currentDatabase = mokedDatabaseInterface


        When(mokedDatabaseInterface.currentUser).thenReturn(mokedUserEntity)
        When(mokedDatabaseInterface.currentProfile).thenReturn(mokedUserProfile)

        When(mokedDatabaseInterface.getProfileById(EditProfileActivity.updater, pid)).thenAnswer {
            EditProfileActivity.updater.postValue(mokedUserProfile)
            Observable(true)
        }
        When(mokedDatabaseInterface.updateProfile(mokedUserProfile)).thenAnswer {
            Observable(true)
        }

        scenario = ActivityScenario.launch(intent)
    }

    @After
    fun end() {
        Database.currentDatabase = FirestoreDatabaseProvider
    }

    fun playoff() {
        scenario.close()
    }

    @Test
    fun adminCanEdit() {
        setup(UserRole.ADMIN, "PID")

        rank.perform(replaceText(UserRole.ADMIN.toString()))
        name.perform(click())
        activity.rankOnFocusChangeListener(false)
        rank.check(matches(ViewMatchers.withText("Admin")))

        activity.nameOnFocusChangeListener(true)
        name.perform(replaceText("notnull"))
        activity.nameOnFocusChangeListener(false)
        name.check(matches(ViewMatchers.withText("notnull")))

        Espresso.closeSoftKeyboard()

        cancel.perform(click())
    }

    @Test
    fun adminCanUpdate() {
        setup(UserRole.ADMIN, "PID")

        rank.perform(replaceText(UserRole.ADMIN.toString()))
        name.perform(click())
        activity.rankOnFocusChangeListener(false)
        rank.check(matches(ViewMatchers.withText("Admin")))

        activity.nameOnFocusChangeListener(true)
        name.perform(replaceText("notnull"))
        activity.nameOnFocusChangeListener(false)
        name.check(matches(ViewMatchers.withText("notnull")))

        Espresso.closeSoftKeyboard()

        save.perform(click())
    }

    @Test
    fun adminCanNotUpdate() {
        setup(UserRole.ADMIN, "PID")

        When(mokedDatabaseInterface.updateProfile(mokedUserProfile)).thenAnswer {
            Observable(false)
        }

        rank.perform(replaceText(UserRole.ADMIN.toString()))
        name.perform(click())
        activity.rankOnFocusChangeListener(false)
        rank.check(matches(ViewMatchers.withText("Admin")))

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
        setup(UserRole.PARTICIPANT, "TestPID")
        idLayout.check(matches(not(isDisplayed())))
        rankLayout.check(matches(not(isDisplayed())))
        playoff()
    }

    @Test
    fun staffCanNotEdit() {
        setup(UserRole.STAFF, "TestPID")
        idLayout.check(matches(not(isDisplayed())))
        rankLayout.check(matches(not(isDisplayed())))
        playoff()
    }

    @Test
    fun activityProviderCanNotEdit() {
        setup(UserRole.ORGANIZER, "TestPID")
        idLayout.check(matches(not(isDisplayed())))
        rankLayout.check(matches(not(isDisplayed())))
        playoff()
    }
}