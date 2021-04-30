package com.github.sdpteam15.polyevents.view.fragments

import android.content.Intent
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.github.sdpteam15.polyevents.*
import com.github.sdpteam15.polyevents.view.adapter.EventItemAdapter
import com.github.sdpteam15.polyevents.view.activity.EditProfileActivity.Companion.EDIT_PROFILE_ID
import com.github.sdpteam15.polyevents.model.database.remote.Database.currentDatabase
import com.github.sdpteam15.polyevents.model.database.remote.DatabaseInterface
import com.github.sdpteam15.polyevents.model.database.remote.NUMBER_UPCOMING_EVENTS
import com.github.sdpteam15.polyevents.model.database.remote.objects.EventDatabaseInterface
import com.github.sdpteam15.polyevents.model.database.remote.objects.UserDatabaseInterface
import com.github.sdpteam15.polyevents.model.observable.Observable
import com.github.sdpteam15.polyevents.model.observable.ObservableList
import com.github.sdpteam15.polyevents.model.database.remote.login.GoogleUserLogin
import com.github.sdpteam15.polyevents.model.database.remote.login.UserLogin
import com.github.sdpteam15.polyevents.model.database.remote.login.UserLoginInterface
import com.github.sdpteam15.polyevents.model.entity.UserEntity
import com.github.sdpteam15.polyevents.model.entity.UserProfile
import com.github.sdpteam15.polyevents.model.entity.UserRole
import com.github.sdpteam15.polyevents.view.activity.MainActivity
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import org.hamcrest.Matchers
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.anyOrNull
import org.mockito.Mockito.`when` as When


private const val displayNameTest = "Test displayName"
private const val emailTest = "Test email"
private const val uidTest = "Test uid"

private const val displayNameTest2 = "Test displayName2"
private const val emailTest2 = "Test email2"
private const val uidTest2 = "Test uid2"

private const val pidTest = "Test pid"

@RunWith(MockitoJUnitRunner::class)
class ProfileLoginFragmentTests {
    @Rule
    @JvmField
    var testRule = ActivityScenarioRule(MainActivity::class.java)

    lateinit var user: UserEntity
    lateinit var userObservable: Observable<UserEntity>
    lateinit var user2: UserEntity
    lateinit var profile: UserProfile
    lateinit var mockedDatabaseUser: UserEntity
    lateinit var mockedDatabaseUser2: UserEntity

    lateinit var mockedUserDatabase: UserDatabaseInterface
    lateinit var mockedEventDatabase: EventDatabaseInterface
    lateinit var mockedDatabase: DatabaseInterface
    lateinit var endingRequest: Observable<Boolean>

    @Before
    fun setup() {
        user = UserEntity(uid = uidTest, email = emailTest, name = displayNameTest)
        user2 = UserEntity(uid = uidTest2, email = emailTest2, name = displayNameTest2)
        profile = UserProfile()

        testRule = ActivityScenarioRule<MainActivity>(MainActivity::class.java)
        endingRequest = Observable()
        mockedDatabaseUser = UserEntity(uid = uidTest, email = emailTest, name = displayNameTest)

        //Create Mock database
        mockedUserDatabase =  mock(UserDatabaseInterface::class.java)

        mockedDatabase = HelperTestFunction.defaultMockDatabase()
        mockedEventDatabase = mock(EventDatabaseInterface::class.java)

        When(mockedDatabase.eventDatabase).thenReturn(mockedEventDatabase)
        When(mockedDatabase.userDatabase).thenReturn(mockedUserDatabase)

        When(mockedDatabase.currentUser).thenReturn(null)
        val homeFragment = MainActivity.fragments[R.id.ic_home] as HomeFragment
        When(
            mockedEventDatabase.getEvents(
                null,
                NUMBER_UPCOMING_EVENTS.toLong(),
                homeFragment.events
            )
        ).thenAnswer {
            Observable(true)
        }
        When(
            mockedEventDatabase.getEvents(
                null,
                NUMBER_UPCOMING_EVENTS.toLong(),
                homeFragment.events
            )
        ).thenAnswer {
            Observable(true)
        }
        userObservable = Observable()
        When(mockedDatabase.currentUserObservable)
            .thenAnswer { userObservable }
        currentDatabase = mockedDatabase
    }


    @Test
    fun signInCalledTheCorrectMethod() {
        val loginFragment = MainActivity.fragments[R.id.ic_login] as LoginFragment
        loginFragment.currentUser = null
        val mockFirebaseAuth = mock(FirebaseAuth::class.java)
        GoogleUserLogin.firebaseAuth = mockFirebaseAuth
        When(mockFirebaseAuth.currentUser).thenReturn(null)

        onView(withId(R.id.ic_login)).perform(click())
        onView(withId(R.id.id_fragment_login)).check(matches(isDisplayed()))

        onView(withId(R.id.btnLogin)).perform(click())

        assert(GoogleUserLogin.gso != null)
        assert(GoogleUserLogin.signIn != null)
        GoogleUserLogin.firebaseAuth = null
    }

    /**
     * Helper method that bypass the check if the user is in database and directly return true
     */
    private fun loginDirectly(loginFragment: LoginFragment, id: Int) {
        //Mock the inDatabase method so that it returns true directly
        val endingRequest2 = Observable<Boolean>()
        When(
            mockedUserDatabase.inDatabase(loginFragment.inDbObservable, uidTest)
        ).thenAnswer { _ ->
            loginFragment.inDbObservable.postValue(true)
            endingRequest2
        }
        //click on the given button to go further in the appliction
        onView(withId(id)).perform(click())
        //Notify that the in Database
        endingRequest2.postValue(true)
    }

    @Test
    fun clickOnSignInLaunchTheCorrectIntent() {
        val loginFragment = MainActivity.fragments[R.id.ic_login] as LoginFragment
        loginFragment.currentUser = null

        val mockedUserLogin = mock(UserLoginInterface::class.java) as UserLoginInterface<AuthResult>
        UserLogin.currentUserLogin = mockedUserLogin
        When(mockedUserLogin.isConnected()).thenReturn(false)

        onView(withId(R.id.ic_login)).perform(click())
        onView(withId(R.id.id_fragment_login)).check(matches(isDisplayed()))

        var set = false
        When(mockedUserLogin.signIn(anyOrNull(), anyOrNull(), anyOrNull())).thenAnswer {
            set = true
            Unit
        }
        onView(withId(R.id.btnLogin)).perform(click())
        assert(set)
        UserLogin.currentUserLogin = GoogleUserLogin
    }

    @Test
    fun receveivedInfoTriggerTheLogin() {
        val loginFragment = MainActivity.fragments[R.id.ic_login] as LoginFragment
        loginFragment.currentUser = null

        val mockedUserLogin = mock(UserLoginInterface::class.java) as UserLoginInterface<AuthResult>
        UserLogin.currentUserLogin = mockedUserLogin
        When(mockedUserLogin.isConnected()).thenReturn(false)
        val mockedTask = mock(Task::class.java) as Task<AuthResult>


        onView(withId(R.id.ic_login)).perform(click())
        onView(withId(R.id.id_fragment_login)).check(matches(isDisplayed()))

        var set = false
        When(
            mockedUserLogin.getResultFromIntent(
                anyOrNull(),
                anyOrNull(),
                anyOrNull()
            )
        ).thenAnswer {
            set = true
            mockedTask
        }
        loginFragment.onActivityResult(loginFragment.SIGN_IN_RC, 10, Intent())
        assert(set)
        UserLogin.currentUserLogin = GoogleUserLogin
    }

    @Test
    fun signOutButtonRedirectToLoginFragment() {
        UserLogin.currentUserLogin.signOut()
        val loginFragment = MainActivity.fragments[R.id.ic_login] as LoginFragment
        loginFragment.currentUser = user
        val profileFragment = MainActivity.fragments[R.id.id_fragment_profile] as ProfileFragment

        profileFragment.currentUser = user
        onView(withId(R.id.ic_login)).perform(click())
        onView(withId(R.id.id_fragment_profile)).check(matches(isDisplayed()))

        onView(withId(R.id.btnLogout))
            .perform(scrollTo())
        onView(withId(R.id.btnLogout)).perform(click())
        onView(withId(R.id.id_fragment_login)).check(matches(isDisplayed()))
    }

    @Test
    fun loginFragmentIfNotLoggedInOtherwiseProfileFragment() {
        UserLogin.currentUserLogin.signOut()
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
        UserLogin.currentUserLogin.signOut()

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
            mockedUserDatabase.getUserInformation(
                profileFragment.userInfoLiveData,
                uidTest
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
        var endingRequestUpdate = Observable<Boolean>()
        var updated = false
        When(
            mockedUserDatabase.updateUserInformation(anyOrNull(), anyOrNull())
        ).thenAnswer { _ ->
            updated = true
            endingRequestUpdate
        }

        //Mock the getInformations
        When(
            mockedUserDatabase.getUserInformation(
                profileFragment.userInfoLiveData,
                uidTest
            )
        ).thenAnswer { _ ->
            profileFragment.userInfoLiveData.postValue(user2)
            endingRequest
        }
        onView(withId(R.id.btnUpdateInfos))
            .perform(scrollTo())
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
            mockedUserDatabase.getUserInformation(
                profileFragment.userInfoLiveData,
                uidTest
            )
        ).thenAnswer { _ ->
            profileFragment.userInfoLiveData.postValue(user)
            endingRequest
        }

        //Nothing happens
        endingRequestUpdate = Observable()
        onView(withId(R.id.btnUpdateInfos)).perform(click())
        endingRequestUpdate.postValue(false)

        //check that the values are still the same
        onView(withId(R.id.profileName)).check(matches(withText(Matchers.equalTo(displayNameTest2))))
        onView(withId(R.id.profileEmail)).check(matches(withText(Matchers.equalTo(emailTest2))))
    }

    private fun initDBTests() {
        //Make sure we are not connected to Firebase
        UserLogin.currentUserLogin.signOut()
        //remove current user so that we stay on login fragment
        val loginFragment = MainActivity.fragments[R.id.ic_login] as LoginFragment
        loginFragment.currentUser = null
        onView(withId(R.id.ic_login)).perform(click())
        //make sure we are on login fragment
        onView(withId(R.id.id_fragment_login)).check(matches(isDisplayed()))

        //set the user variable to avoid redirection from Profile Fragment
        val profileFragment = MainActivity.fragments[R.id.id_fragment_profile] as ProfileFragment
        profileFragment.currentUser = user
        loginFragment.currentUser = user
    }

    @Test
    fun ifNotInDbAddIt() {
        val loginFragment = MainActivity.fragments[R.id.ic_login] as LoginFragment
        val profileFragment = MainActivity.fragments[R.id.id_fragment_profile] as ProfileFragment

        initDBTests()
        //Mock the in database method, to return false
        val endingRequestInDatabase = Observable<Boolean>()
        When(
            mockedUserDatabase.inDatabase(
                loginFragment.inDbObservable,
                uidTest
            )
        ).thenAnswer { _ ->
            loginFragment.inDbObservable.postValue(false)
            endingRequestInDatabase
        }

        val endingRequestFirstConnection = Observable<Boolean>()
        var accountCreated = false
        //Mock the firstConnexion method so that it sets the boolean to true if called
        When(
            mockedUserDatabase.firstConnexion(
                loginFragment.currentUser!!
            )
        ).thenAnswer { _ ->
            accountCreated = true
            endingRequestFirstConnection
        }

        //Mock the getInformation method to be able to launch the Profile Fragment
        When(
            mockedUserDatabase.getUserInformation(
                profileFragment.userInfoLiveData,
                uidTest
            )
        ).thenAnswer { _ ->
            profileFragment.userInfoLiveData.postValue(user)
            endingRequest
        }

        //Click on login
        onView(withId(R.id.btnLogin)).perform(click())
        //Notify that the inDatabase request was successfully performed
        endingRequestInDatabase.postValue(true)
        //Notify that the firstConnection request was successfully performed
        endingRequestFirstConnection.postValue(true)
        //Notify that the getUserAInformation request was successfully performed
        endingRequest.postValue(true)

        //Wait enough time for the boolean to be set correctly
        var i = 0
        while (!accountCreated && i++ < 5) {
            Thread.sleep(1000)
        }
        assert(accountCreated)
        assert(i < 5)
    }

    @Test
    fun ifInDbDoNotAddIt() {

        val loginFragment = MainActivity.fragments[R.id.ic_login] as LoginFragment
        val profileFragment = MainActivity.fragments[R.id.id_fragment_profile] as ProfileFragment
        initDBTests()

        val endingRequestFirstConnection = Observable<Boolean>()
        var accountNotCreated = true

        //Mock the firstConnexion method so that it sets the boolean to false if called
        When(
            mockedUserDatabase.firstConnexion(
                loginFragment.currentUser!!
            )
        ).thenAnswer { _ ->
            accountNotCreated = false
            endingRequestFirstConnection
        }

        //Mock the getInformation method to be able to launch the Profile Fragment
        When(
            mockedUserDatabase.getUserInformation(
                profileFragment.userInfoLiveData,
                uidTest
            )
        ).thenAnswer { _ ->
            profileFragment.userInfoLiveData.postValue(user)
            endingRequest
        }

        loginDirectly(loginFragment, R.id.btnLogin)
        //Notify that the firstConnection request was successfully performed
        endingRequestFirstConnection.postValue(true)
        //Notify that the getUserAInformation request was successfully performed
        endingRequest.postValue(true)

        //Wait enough time
        Thread.sleep(3000)
        assert(accountNotCreated)
    }

    @Test
    fun ifIssueWithCommunicationDoesNothing() {
        val loginFragment = MainActivity.fragments[R.id.ic_login] as LoginFragment
        initDBTests()

        val endingRequestFirstConnection = Observable<Boolean>()

        //Mock the firstConnexion method so that it sets the boolean to false if called
        When(
            mockedUserDatabase.firstConnexion(
                loginFragment.currentUser!!
            )
        ).thenAnswer {
            endingRequestFirstConnection
        }

        When(
            mockedUserDatabase.inDatabase(
                loginFragment.inDbObservable,
                uidTest
            )
        ).thenAnswer { _ ->
            loginFragment.inDbObservable.postValue(false)
            endingRequest
        }
        onView(withId(R.id.btnLogin)).perform(click())
        //Notify that the firstConnection request was successfully performed
        endingRequestFirstConnection.postValue(false)
        //Notify that the getUserAInformation request was successfully performed
        endingRequest.postValue(false)
        Thread.sleep(2000)
        onView(withId(R.id.id_fragment_login)).check(matches(isDisplayed()))

    }

    @Test
    fun profilesAreDisplayed() {
        val loginFragment = MainActivity.fragments[R.id.ic_login] as LoginFragment
        loginFragment.currentUser = user

        val profileFragment = MainActivity.fragments[R.id.id_fragment_profile] as ProfileFragment
        profileFragment.currentUser = user
        loginDirectly(loginFragment, R.id.ic_login)

        //Mock the profile
        When(
            mockedUserDatabase.getUserProfilesList(
                anyOrNull(),
                anyOrNull(),
                anyOrNull()
            )
        ).thenAnswer {
            val profiles = it!!.arguments[0] as ObservableList<UserProfile>
            profiles.clear(currentDatabase)
            profiles.add(
                UserProfile(
                    pid = pidTest,
                    profileName = displayNameTest,
                    userRole = UserRole.PARTICIPANT,
                    users = mutableListOf(user.uid)
                ), currentDatabase
            )
            profiles.add(
                UserProfile(
                    pid = pidTest,
                    profileName = displayNameTest,
                    userRole = UserRole.ADMIN,
                    users = mutableListOf(user.uid)
                ), currentDatabase
            )
            Observable(true)
        }

        user.profiles.add(pidTest)
        userObservable.postValue(user)

        onView(withId(R.id.id_recycler_profile_list)).check(RecyclerViewItemCountAssertion(2))
    }


    @Test
    fun addButtonPopupAddsItemToList() {

        val loginFragment = MainActivity.fragments[R.id.ic_login] as LoginFragment
        loginFragment.currentUser = user

        val profileFragment = MainActivity.fragments[R.id.id_fragment_profile] as ProfileFragment
        profileFragment.currentUser = user
        loginDirectly(loginFragment, R.id.ic_login)

        //Mock the profile
        When(
            mockedUserDatabase.addUserProfileAndAddToUser(
                anyOrNull(),
                anyOrNull(),
                anyOrNull()
            )
        ).thenAnswer {
            Observable(true)
        }
        When(
            mockedUserDatabase.getUserProfilesList(
                anyOrNull(),
                anyOrNull(),
                anyOrNull()
            )
        ).thenAnswer {
            val profiles = it!!.arguments[0] as ObservableList<UserProfile>
            profiles.clear(currentDatabase)
            Observable(true)
        }

        user.profiles.add(pidTest)
        userObservable.postValue(user)

        onView(withId(R.id.id_add_profile_button)).perform(click())
        onView(withId(R.id.id_edittext_profile_name)).perform(ViewActions.typeText(displayNameTest))
        Espresso.closeSoftKeyboard()
        onView(withId(R.id.id_confirm_add_item_button)).perform(click())
        Thread.sleep(1000)
        onView(withId(R.id.id_recycler_profile_list))
            .check(RecyclerViewItemCountAssertion(1))
    }


    @Test
    fun nullUserRedirectToLoginFragment(){
        val loginFragment = MainActivity.fragments[R.id.ic_login] as LoginFragment
        loginFragment.currentUser = mockedDatabaseUser
        When(mockedDatabase.currentUser).thenReturn(null)

        val profileFragment = MainActivity.fragments[R.id.id_fragment_profile] as ProfileFragment
        profileFragment.currentUser = null
        onView(withId(R.id.ic_login)).perform(click())
        onView(withId(R.id.id_fragment_login)).check(matches(isDisplayed()))
    }

    @Test
    fun removeButtonRemovesProfilesFromList() {

        val loginFragment = MainActivity.fragments[R.id.ic_login] as LoginFragment
        loginFragment.currentUser = user

        val profileFragment =
            MainActivity.fragments[R.id.id_fragment_profile] as ProfileFragment
        profileFragment.currentUser = user
        loginDirectly(loginFragment, R.id.ic_login)

        //Mock the profile
        When(
            mockedUserDatabase.getUserProfilesList(
                anyOrNull(),
                anyOrNull(),
                anyOrNull()
            )
        ).thenAnswer {
            val profiles = it!!.arguments[0] as ObservableList<UserProfile>
            profiles.clear(currentDatabase)
            profiles.add(
                UserProfile(
                    pid = pidTest,
                    profileName = displayNameTest,
                    userRole = UserRole.PARTICIPANT,
                    users = mutableListOf(user.uid)
                ), currentDatabase
            )
            Observable(true)
        }
        When(
            mockedUserDatabase.removeProfileFromUser(
                anyOrNull(),
                anyOrNull(),
                anyOrNull()
            )
        ).thenReturn(Observable(true))

        user.profiles.add(pidTest)
        userObservable.postValue(user)

        onView(withId(R.id.id_recycler_profile_list)).perform(
            RecyclerViewActions.actionOnItemAtPosition<EventItemAdapter.ItemViewHolder>(
                0, TestHelper.clickChildViewWithId(R.id.id_profile_remove_item)
            )
        )
        Thread.sleep(1000)
        onView(withId(R.id.id_recycler_profile_list)).check(RecyclerViewItemCountAssertion(0))
    }

    @Test
    fun editButtonStartProfileEdition() {
        val loginFragment = MainActivity.fragments[R.id.ic_login] as LoginFragment
        loginFragment.currentUser = user

        val profileFragment =
            MainActivity.fragments[R.id.id_fragment_profile] as ProfileFragment
        profileFragment.currentUser = user
        loginDirectly(loginFragment, R.id.ic_login)

        //Mock the profile
        When(
            mockedUserDatabase.getUserProfilesList(
                anyOrNull(),
                anyOrNull(),
                anyOrNull()
            )
        ).thenAnswer {
            val profiles = it!!.arguments[0] as ObservableList<UserProfile>
            profiles.clear(currentDatabase)
            profiles.add(
                UserProfile(
                    pid = pidTest,
                    profileName = displayNameTest,
                    userRole = UserRole.PARTICIPANT,
                    users = mutableListOf(user.uid)
                ), currentDatabase
            )
            Observable(true)
        }

        user.profiles.add(pidTest)
        userObservable.postValue(user)
        Intents.init()
        onView(withId(R.id.id_recycler_profile_list)).perform(
            RecyclerViewActions.actionOnItemAtPosition<EventItemAdapter.ItemViewHolder>(
                0, TestHelper.clickChildViewWithId(R.id.id_profile_edit_item)
            )
        )
        Thread.sleep(1000)
        Intents.intended(
            IntentMatchers.hasExtra(
                EDIT_PROFILE_ID,
                pidTest
            )
        )
        Intents.release()
    }
}