package com.github.sdpteam15.polyevents.database

import com.github.sdpteam15.polyevents.database.observe.Observable
import com.github.sdpteam15.polyevents.database.observe.ObservableList
import com.github.sdpteam15.polyevents.model.Event
import com.github.sdpteam15.polyevents.model.Item
import com.github.sdpteam15.polyevents.model.UserEntity
import com.github.sdpteam15.polyevents.model.UserProfile
import com.google.android.gms.maps.model.LatLng
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import kotlin.test.assertNotNull
import org.hamcrest.CoreMatchers.`is` as Is

class FakeDatabaseTests {
    lateinit var mokedUserInterface: UserEntity
    lateinit var mokedUserProfile: UserProfile
    lateinit var mokedEvent: Event
    lateinit var mockedEventList: ObservableList<Event>
    lateinit var mockedItemList: ObservableList<Pair<Item,Int>>
    val uid = "TestUID"

    @Before
    fun setup() {
        mokedUserInterface = UserEntity(uid = uid)
        mokedUserProfile = UserProfile()
        mokedEvent = Event("xxxEventxxx")
        mockedEventList = ObservableList()
        mockedItemList = ObservableList()
    }

    @Test
    fun toRemoveTest() {
        assertNotNull(FakeDatabase.CURRENT_USER)
        assertNotNull(FakeDatabase.getProfilesList("", mokedUserInterface))
        assertNotNull(FakeDatabase.addProfile(mokedUserProfile, "", mokedUserInterface))
        assertNotNull(FakeDatabase.removeProfile(mokedUserProfile, "", mokedUserInterface))
        assertNotNull(FakeDatabase.updateProfile(mokedUserProfile, mokedUserInterface))
        FakeDatabase.getListEvent(null, 1, mockedEventList, mokedUserProfile).observe {
            if (it.value) assert(mockedEventList.size <= 1)
        }
        FakeDatabase.getListEvent(null, 100, mockedEventList, mokedUserProfile).observe {
            if (it.value) assert(mockedEventList.size <= 100)
        }
        FakeDatabase.getAvailableItems(mockedItemList).observe {
            if (it.value) assert(mockedItemList)
        }
        assert(FakeDatabase.updateEvent(mokedEvent, mokedUserProfile))
    }

    @Test
    fun getAvailableItemsTest(){

    }

    @Test
    fun updateUserInformationTest() {
        val hashMap = hashMapOf<String, String>()

        var IsUpdated = false
        FakeDatabase.updateUserInformation(hashMap, uid, mokedUserInterface)
            .observe { IsUpdated = it.value }
        assert(IsUpdated)
    }

    @Test
    fun firstConnexionTest() {
        var IsUpdated = false
        FakeDatabase.firstConnexion(mokedUserInterface, mokedUserInterface)
            .observe { IsUpdated = it!! }
        assert(IsUpdated)
    }

    @Test
    fun inDatabaseTest() {
        val isInDb = Observable<Boolean>()

        var IsUpdated = false
        var isInDbIsUpdated = false
        isInDb.observe { isInDbIsUpdated = it!! }

        FakeDatabase.inDatabase(isInDb, uid, mokedUserInterface).observe { IsUpdated = it!! }
        assert(IsUpdated)
        assert(isInDbIsUpdated)
    }

    @Test
    fun getUserInformationTest() {
        val user = Observable<UserEntity>()

        var IsUpdated = false
        var userIsUpdated = false
        user.observe { userIsUpdated = true }

        FakeDatabase.getUserInformation(user, uid, mokedUserInterface).observe { IsUpdated = it!! }
        assert(IsUpdated)
        assert(userIsUpdated)
    }

    @Test
    fun setUserLocationTest() {
        var isUpdated = false
        val lat = 46.548823
        val lng = 7.017012
        val pointToAdd = LatLng(lat, lng)
        FakeDatabase.setUserLocation(pointToAdd, mokedUserInterface).observe {
            isUpdated = it!!
        }

        assertThat(isUpdated, Is(true))
    }

    @Test
    fun getUsersLocationsTest() {
        val locations = Observable<List<LatLng>>()

        var isUpdated = false
        var locationsAreUpdated = false
        locations.observe { locationsAreUpdated = true }

        FakeDatabase.getUsersLocations(locations, mokedUserInterface).observe {
            isUpdated = it!!
        }

        assertThat(isUpdated, Is(true))
        assertThat(locationsAreUpdated, Is(true))
    }
}