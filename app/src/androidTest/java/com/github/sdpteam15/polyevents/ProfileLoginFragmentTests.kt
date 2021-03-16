package com.github.sdpteam15.polyevents

import androidx.lifecycle.MutableLiveData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.github.sdpteam15.polyevents.database.Database.currentDatabase
import com.github.sdpteam15.polyevents.database.DatabaseInterface
import com.github.sdpteam15.polyevents.database.DatabaseUserInterface
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
import org.mockito.Mockito.mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.Mockito.`when` as When


private const val displayNameTest = "Test displayName"
private const val emailTest = "Test email"
private const val uidTest = "Test uid"

private const val displayNameTest2 = "Test displayName2"
private const val emailTest2 = "Test email2"
private const val uidTest2 = "Test uid2"

@RunWith(MockitoJUnitRunner::class)
class ProfileFragmentTest {
    @Rule
    @JvmField
    var testRule = ActivityScenarioRule<MainActivity>(MainActivity::class.java)

    lateinit var user: UserInterface
    lateinit var user2: UserInterface
    lateinit var mockedDatabaseUser: DatabaseUserInterface
    lateinit var mockedDatabaseUser2: DatabaseUserInterface
    lateinit var mockedDatabase: DatabaseInterface
    lateinit var endingRequest: MutableLiveData<Boolean>

    @Before
    fun setup() {
        mockedDatabaseUser = mock(DatabaseUserInterface::class.java)
        When(mockedDatabaseUser.email).thenReturn(emailTest)
        When(mockedDatabaseUser.displayName).thenReturn(displayNameTest)
        When(mockedDatabaseUser.uid).thenReturn(uidTest)
        user = User.invoke(mockedDatabaseUser)

        mockedDatabaseUser2 = mock(DatabaseUserInterface::class.java)
        When(mockedDatabaseUser2.email).thenReturn(emailTest2)
        When(mockedDatabaseUser2.displayName).thenReturn(displayNameTest2)
        When(mockedDatabaseUser2.uid).thenReturn(uidTest2)
        user2 = User.invoke(mockedDatabaseUser2)

        testRule = ActivityScenarioRule<MainActivity>(MainActivity::class.java)
        endingRequest = MutableLiveData()

        mockedDatabase = mock(DatabaseInterface::class.java)
        When(mockedDatabase.currentUser).thenReturn(null)
        currentDatabase = mockedDatabase
    }

    /**
     * Helper method that bypass the check if the user is in database and directly return true
     */
    private fun loginDirectly(loginFragment: LoginFragment, id: Int) {
        //Mock the sign in method
        val endingRequest2 = MutableLiveData<Boolean>()
        When(
            mockedDatabase.inDatabase(loginFragment.inDbMutableLiveData, uidTest, user)
        ).thenAnswer { _ ->
            loginFragment.inDbMutableLiveData.postValue(true)
            endingRequest2
        }
        onView(withId(id)).perform(click())
        endingRequest2.postValue(true)
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
    fun loginFragmentIfNotLoggedInOtherwiseProfileFragment() {
        FirebaseAuth.getInstance().signOut()
        val profileFragment = MainActivity.fragments[R.id.id_fragment_profile] as ProfileFragment
        profileFragment.currentUser = user

        val loginFragment = MainActivity.fragments[R.id.ic_login] as LoginFragment
        loginFragment.currentUser = null

        onView(withId(R.id.ic_login)).perform(click())
        onView(withId(R.id.id_fragment_login)).check(matches(isDisplayed()))

        loginFragment.currentUser = user
        onView(withId(R.id.ic_home)).perform(click())

        //login and check that the profile fragment is displayed
        loginDirectly(loginFragment, R.id.ic_login)
        onView(withId(R.id.id_fragment_profile)).check(matches(isDisplayed()))
    }


    @Test
    fun signInButtonRedirectToProfileFragmentAfterSuccess() {
        //Log out from Firebase if connected
        FirebaseAuth.getInstance().signOut()

        val loginFragment = MainActivity.fragments[R.id.ic_login] as LoginFragment
        loginFragment.currentUser = null

        //Go on login page
        onView(withId(R.id.ic_login)).perform(click())

        //set the user in login to something not null
        loginFragment.currentUser = user

        //set the user in profil fragment
        val profileFragment = MainActivity.fragments[R.id.id_fragment_profile] as ProfileFragment
        profileFragment.currentUser = user

        //Mock the get user information method
        When(
            mockedDatabase.getUserInformation(
                profileFragment.userInfoLiveData,
                uidTest,
                user
            )
        ).thenAnswer { _ ->
            profileFragment.userInfoLiveData.postValue(user)
            endingRequest
        }

        loginDirectly(loginFragment, R.id.btnLogin)

        //answer to getUserInformation
        endingRequest.postValue(true)
        onView(withId(R.id.id_fragment_profile)).check(matches(isDisplayed()))

        onView(withId(R.id.profileName)).check(matches(withText(Matchers.equalTo(displayNameTest))))
        onView(withId(R.id.profileEmail)).check(matches(withText(Matchers.equalTo(emailTest))))
    }


    @Test
    fun updateAreCorrectlyRefreshedAndDisplayed() {
        val loginFragment = MainActivity.fragments[R.id.ic_login] as LoginFragment
        loginFragment.currentUser = user

        val profileFragment = MainActivity.fragments[R.id.id_fragment_profile] as ProfileFragment
        profileFragment.currentUser = user
        loginDirectly(loginFragment, R.id.ic_login)

        //Mock the update UserInformation method
        var endingRequestUpdate = MutableLiveData<Boolean>()
        var updated = false
        When(
            mockedDatabase.updateUserInformation(profileFragment.hashMapNewInfos, uidTest, user)
        ).thenAnswer { _ ->
            updated = true
            endingRequestUpdate
        }

        //Mock the getInformations
        When(
            mockedDatabase.getUserInformation(
                profileFragment.userInfoLiveData,
                uidTest,
                user
            )
        ).thenAnswer { _ ->
            profileFragment.userInfoLiveData.postValue(user2)
            endingRequest
        }

        //Click on the update button
        onView(withId(R.id.btnUpdateInfos)).perform(click())
        endingRequestUpdate.postValue(true)
        endingRequest.postValue(true)

        //Check if the update method has been called
        assert(updated)

        //Check if the values are correctly displayed
        onView(withId(R.id.profileName)).check(matches(withText(Matchers.equalTo(displayNameTest2))))
        onView(withId(R.id.profileEmail)).check(matches(withText(Matchers.equalTo(emailTest2))))

        //Mock the getUserInformation method to post a user with other values than previously
        //So that we can see if the getUserInformation() has been called (it shouldn't)
        When(
            mockedDatabase.getUserInformation(
                profileFragment.userInfoLiveData,
                uidTest,
                user
            )
        ).thenAnswer { _ ->
            profileFragment.userInfoLiveData.postValue(user)
            endingRequest
        }

        //Nothing happens
        endingRequestUpdate = MutableLiveData()
        onView(withId(R.id.btnUpdateInfos)).perform(click())
        endingRequestUpdate.postValue(false)

        //check that the values are still the same
        onView(withId(R.id.profileName)).check(matches(withText(Matchers.equalTo(displayNameTest2))))
        onView(withId(R.id.profileEmail)).check(matches(withText(Matchers.equalTo(emailTest2))))
    }

    @Test
    fun ifNotInDbAddIt() {
        FirebaseAuth.getInstance().signOut()
        val loginFragment = MainActivity.fragments[R.id.ic_login] as LoginFragment
        loginFragment.currentUser = null
        onView(withId(R.id.ic_login)).perform(click())
        onView(withId(R.id.id_fragment_login)).check(matches(isDisplayed()))

        val profileFragment = MainActivity.fragments[R.id.id_fragment_profile] as ProfileFragment
        profileFragment.currentUser = user
        loginFragment.currentUser = user

        val endingRequestInDatabase = MutableLiveData<Boolean>()
        When(
            mockedDatabase.inDatabase(
                loginFragment.inDbMutableLiveData,
                uidTest,
                loginFragment.currentUser!!
            )
        ).thenAnswer { _ ->
            loginFragment.inDbMutableLiveData.postValue(false)
            endingRequestInDatabase
        }

        val endingRequestFirstConnection = MutableLiveData<Boolean>()
        var accountCreated = false

        When(
            mockedDatabase.firstConnexion(loginFragment.currentUser!!, loginFragment.currentUser!!)
        ).thenAnswer { _ ->
            accountCreated = true
            endingRequestFirstConnection
        }

        //Mock the getInformation method to be able to launch the Profile Fragment
        When(
            mockedDatabase.getUserInformation(
                profileFragment.userInfoLiveData,
                uidTest,
                user
            )
        ).thenAnswer { _ ->
            profileFragment.userInfoLiveData.postValue(user)
            endingRequest
        }

        onView(withId(R.id.btnLogin)).perform(click())
        endingRequestInDatabase.postValue(true)
        endingRequestFirstConnection.postValue(true)
        endingRequest.postValue(true)
        Thread.sleep(3000)
        assert(accountCreated)
    }

    @Test
    fun ifInDbDoNotAddIt() {
        FirebaseAuth.getInstance().signOut()
        val loginFragment = MainActivity.fragments[R.id.ic_login] as LoginFragment
        loginFragment.currentUser = null
        onView(withId(R.id.ic_login)).perform(click())
        onView(withId(R.id.id_fragment_login)).check(matches(isDisplayed()))

        val profileFragment = MainActivity.fragments[R.id.id_fragment_profile] as ProfileFragment
        profileFragment.currentUser = user
        loginFragment.currentUser = user

        val endingRequestFirstConnection = MutableLiveData<Boolean>()
        var accountNotCreated = true

        When(
            mockedDatabase.firstConnexion(loginFragment.currentUser!!, loginFragment.currentUser!!)
        ).thenAnswer { _ ->
            accountNotCreated = false
            endingRequestFirstConnection
        }

        //Mock the getInformation method to be able to launch the Profile Fragment
        When(
            mockedDatabase.getUserInformation(
                profileFragment.userInfoLiveData,
                uidTest,
                user
            )
        ).thenAnswer { _ ->
            profileFragment.userInfoLiveData.postValue(user)
            endingRequest
        }

        loginDirectly(loginFragment,R.id.btnLogin)
        endingRequestFirstConnection.postValue(true)
        endingRequest.postValue(true)
        Thread.sleep(3000)
        assert(accountNotCreated)
    }
}

