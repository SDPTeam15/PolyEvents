package com.github.sdpteam15.polyevents

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.github.sdpteam15.polyevents.fragments.LoginFragment
import com.github.sdpteam15.polyevents.fragments.ProfileFragment
import com.github.sdpteam15.polyevents.user.User
import com.github.sdpteam15.polyevents.user.UserInterface
import com.google.firebase.auth.FirebaseAuth
import org.hamcrest.Matchers
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.junit.MockitoJUnitRunner

import com.github.sdpteam15.polyevents.database.DatabaseUserInterface

private const val diplayNameTest = "Test displayName"
private const val emailTest = "Test email"
private const val uidTest = "Test uid"

@RunWith(MockitoJUnitRunner::class)
class ProfileFragmentTest {
    @Rule
    @JvmField
    var testRule = ActivityScenarioRule<MainActivity>(MainActivity::class.java)

    lateinit var user: UserInterface
    lateinit var mockedDatabaseUser: DatabaseUserInterface

    @Before
    fun setup() {
        mockedDatabaseUser = mock(DatabaseUserInterface::class.java)
        `when`(mockedDatabaseUser.email).thenReturn(emailTest)
        `when`(mockedDatabaseUser.displayName).thenReturn(diplayNameTest)
        `when`(mockedDatabaseUser.uid).thenReturn(uidTest)
        user = User.invoke(mockedDatabaseUser)
        testRule = ActivityScenarioRule<MainActivity>(MainActivity::class.java)
    }

    @Test
    fun signOutButtonRedirectToLoginFragment() {
        FirebaseAuth.getInstance().signOut()
        val profileFragment = MainActivity.fragments[R.id.id_fragment_profile] as ProfileFragment
        profileFragment.currentUser = user
        onView(withId(R.id.ic_login)).perform(click())
        onView(withId(R.id.id_fragment_profile)).check(matches(isDisplayed()))

        onView(withId(R.id.btnLogout)).perform(click())
        onView(withId(R.id.id_fragment_login)).check(matches(isDisplayed()))
    }

    @Test
    fun backToSignInIfCurrentUserNull() {
        FirebaseAuth.getInstance().signOut()
        val profileFragment = MainActivity.fragments[R.id.id_fragment_profile] as ProfileFragment
        profileFragment.currentUser = null

        onView(withId(R.id.ic_login)).perform(click())
        onView(withId(R.id.id_fragment_login)).check(matches(isDisplayed()))

        val loginFragment = MainActivity.fragments[R.id.ic_login] as LoginFragment
        loginFragment.currentUser = user

        onView(withId(R.id.ic_home)).perform(click())
        onView(withId(R.id.ic_login)).perform(click())

        onView(withId(R.id.id_fragment_profile)).check(matches(isDisplayed()))
    }

    @Test
    fun signInButtonRedirectToProfileFragmentAfterSuccess() {
        FirebaseAuth.getInstance().signOut()
        val loginFragment = MainActivity.fragments[R.id.ic_login] as LoginFragment
        loginFragment.currentUser = null

        onView(withId(R.id.ic_login)).perform(click())
        loginFragment.currentUser = user

        val profileFragment = MainActivity.fragments[R.id.id_fragment_profile] as ProfileFragment
        profileFragment.currentUser = user

        onView(withId(R.id.btnLogin)).perform(click())
        onView(withId(R.id.id_fragment_profile)).check(matches(isDisplayed()))

        onView(withId(R.id.profileName)).check(matches(withText(Matchers.equalTo(diplayNameTest))))
        onView(withId(R.id.ProfileEmail)).check(matches(withText(Matchers.equalTo(emailTest))))
        onView(withId(R.id.profileUID)).check(matches(withText(Matchers.equalTo(uidTest))))
    }


}