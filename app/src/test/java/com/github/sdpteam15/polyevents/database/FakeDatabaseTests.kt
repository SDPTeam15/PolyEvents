package com.github.sdpteam15.polyevents.database

import com.github.sdpteam15.polyevents.database.observe.Observable
import com.github.sdpteam15.polyevents.event.Event
import com.github.sdpteam15.polyevents.user.ProfileInterface
import com.github.sdpteam15.polyevents.user.UserInterface
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import java.util.HashMap
import kotlin.test.assertNotNull

class FakeDatabaseTests {
    lateinit var mokedUserInterface : UserInterface
    lateinit var mokedProfileInterface : ProfileInterface
    lateinit var mokedEvent : Event
    val uid = "TestUID"

    @Before
    fun setup(){
        mokedUserInterface = mock(UserInterface::class.java)
        mokedProfileInterface = mock(ProfileInterface::class.java)
        mokedEvent = mock(Event::class.java)
    }

    @Test
    fun toRemoveTest() {
        assertNotNull(FakeDatabase.currentUser)
        assertNotNull(FakeDatabase.getListProfile("", mokedUserInterface))
        assertNotNull(FakeDatabase.addProfile(mokedProfileInterface, "", mokedUserInterface))
        assertNotNull(FakeDatabase.removeProfile(mokedProfileInterface, "", mokedUserInterface))
        assertNotNull(FakeDatabase.updateProfile(mokedProfileInterface, mokedUserInterface))
        assert(FakeDatabase.getListEvent("", 1, mokedProfileInterface).size <= 1)
        assert(FakeDatabase.getListEvent("", 100, mokedProfileInterface).size <= 100)
        assert(FakeDatabase.getUpcomingEvents(1, mokedProfileInterface).size <= 1)
        assert(FakeDatabase.getUpcomingEvents(100, mokedProfileInterface).size <= 100)
        assert(FakeDatabase.updateEvent(mokedEvent, mokedProfileInterface))
    }

    @Test
    fun updateUserInformationTest(){
        val hashMap = hashMapOf<String, String>()

        var IsUpdated = false
        FakeDatabase.updateUserInformation(hashMap, uid, mokedUserInterface).observe { IsUpdated = it!! }
        assert(IsUpdated)
    }

    @Test
    fun firstConnexionTest(){
        var IsUpdated = false
        FakeDatabase.firstConnexion(mokedUserInterface, mokedUserInterface).observe { IsUpdated = it!! }
        assert(IsUpdated)
    }

    @Test
    fun inDatabaseTest(){
        val isInDb = Observable<Boolean>()

        var IsUpdated = false
        var isInDbIsUpdated = false
        isInDb.observe {  isInDbIsUpdated = it!! }

        FakeDatabase.inDatabase(isInDb, uid, mokedUserInterface).observe { IsUpdated = it!! }
        assert(IsUpdated)
        assert(isInDbIsUpdated)
    }

    @Test
    fun getUserInformationTest(){
        val user = Observable<UserInterface>()

        var IsUpdated = false
        var userIsUpdated = false
        user.observe { userIsUpdated = true }

        FakeDatabase.getUserInformation(user, uid, mokedUserInterface).observe { IsUpdated = it!! }
        assert(IsUpdated)
        assert(userIsUpdated)
    }
}