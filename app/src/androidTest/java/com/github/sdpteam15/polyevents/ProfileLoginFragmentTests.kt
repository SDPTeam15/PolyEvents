package com.github.sdpteam15.polyevents

import androidx.test.espresso.Espresso.*
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.*
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.github.sdpteam15.polyevents.database.FirebaseUserInterface

import com.github.sdpteam15.polyevents.fragments.LoginFragment
import com.github.sdpteam15.polyevents.fragments.ProfileFragment
import com.github.sdpteam15.polyevents.user.User
import com.github.sdpteam15.polyevents.user.UserInterface
import com.google.firebase.auth.FirebaseAuth
import org.hamcrest.Matchers
import org.junit.Before
import org.mockito.Mockito.*
import org.mockito.Mockito.`when`
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

private const val diplayNameTest = "Test displayName"
private const val emailTest = "Test email"
private const val uidTest = "Test uid"

@RunWith(MockitoJUnitRunner::class)
class ProfileFragmentTest {
    @Rule
    @JvmField
    var testRule = ActivityScenarioRule<MainActivity>(MainActivity::class.java)

    lateinit var user:UserInterface
    lateinit var mockedFirebaseUser:FirebaseUserInterface


    @Before
    fun setup(){
        mockedFirebaseUser = mock(FirebaseUserInterface::class.java)
        user = User.invoke(mockedFirebaseUser)
        `when`(mockedFirebaseUser.email).thenReturn(emailTest)
        `when`(mockedFirebaseUser.displayName).thenReturn(diplayNameTest)
        `when`(mockedFirebaseUser.uid).thenReturn(uidTest)
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
    fun backToSignInIfCurrentUserNull(){
        FirebaseAuth.getInstance().signOut()
        val profileFragment = MainActivity.fragments[R.id.id_fragment_profile] as ProfileFragment
        profileFragment.currentUser = null

        onView(withId(R.id.ic_login)).perform(click())
        onView(withId(R.id.id_fragment_login)).check(matches(isDisplayed()))

        val loginFragment = MainActivity.fragments[R.id.ic_login] as LoginFragment
        loginFragment.currentUser = user
        onView(withId(R.id.ic_login)).perform(click())
        onView(withId(R.id.ic_home)).perform(click())
        onView(withId(R.id.ic_login)).perform(click())


        onView(withId(R.id.id_fragment_profile)).check(matches(isDisplayed()))
    }

    @Test
    fun signInButtonRedirectToProfileFragmentAfterSuccess(){
        FirebaseAuth.getInstance().signOut()
        val loginFragment = MainActivity.fragments[R.id.ic_login] as LoginFragment
        loginFragment.currentUser = null

        onView(withId(R.id.ic_login)).perform(click())
        loginFragment.currentUser = user

        val profileFragment = MainActivity.fragments[R.id.id_fragment_profile] as ProfileFragment
        profileFragment.currentUser = user

        onView(withId(R.id.btnLogin)).perform(click())
        onView(withId(R.id.id_fragment_profile)).check(matches(isDisplayed()))

        onView(withId(R.id.displayName)).check(matches(withText(Matchers.equalTo(diplayNameTest))))
        onView(withId(R.id.displayEmail)).check(matches(withText(Matchers.equalTo(emailTest))))
        onView(withId(R.id.displayUID)).check(matches(withText(Matchers.equalTo(uidTest))))
    }


}