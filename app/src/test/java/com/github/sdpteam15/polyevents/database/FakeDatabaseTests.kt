package com.github.sdpteam15.polyevents.database
import com.github.sdpteam15.polyevents.database.observe.Observable
import com.github.sdpteam15.polyevents.database.observe.ObservableList
import com.github.sdpteam15.polyevents.model.Event
import com.github.sdpteam15.polyevents.model.Item
import com.github.sdpteam15.polyevents.model.UserEntity
import com.github.sdpteam15.polyevents.model.UserProfile
import com.github.sdpteam15.polyevents.model.Zone
import com.google.android.gms.maps.model.LatLng
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import kotlin.test.assertNotNull
import org.hamcrest.CoreMatchers.`is` as Is

class FakeDatabaseTests {
    lateinit var mockedUserInterface: UserEntity
    lateinit var mockedUserProfile: UserProfile
    lateinit var mockedEvent: Event
    lateinit var mockedEventList: ObservableList<Event>
    lateinit var mockedItemList: ObservableList<Pair<Item,Int>>
    val uid = "TestUID"

    @Before
    fun setup() {
        mockedUserInterface = UserEntity(uid = uid)
        mockedUserProfile = UserProfile()
        mockedEvent = Event("xxxEventxxx")
        mockedEventList = ObservableList()
        mockedItemList = ObservableList()
    }

    @Test
    fun toRemoveTest() {
        assertNotNull(FakeDatabase.CURRENT_USER)
        assertNotNull(FakeDatabase.getProfilesList("", mockedUserInterface))
        assertNotNull(FakeDatabase.addProfile(mockedUserProfile, "", mockedUserInterface))
        assertNotNull(FakeDatabase.removeProfile(mockedUserProfile, "", mockedUserInterface))
        assertNotNull(FakeDatabase.updateProfile(mockedUserProfile, mockedUserInterface))
        assert(FakeDatabase.getZoneInformation("",Observable(),mockedUserInterface).value!!)
        assert(FakeDatabase.updateZoneInformation("", Zone(),mockedUserInterface).value!!)
        assert(FakeDatabase.createZone(Zone(),mockedUserInterface).value!!)
    }

    @Test
    fun updateUserInformationTest() {
        val hashMap = hashMapOf<String, String>()

        var IsUpdated = false
        FakeDatabase.updateUserInformation(hashMap, uid, mockedUserInterface)
            .observe { IsUpdated = it.value }
        assert(IsUpdated)
    }

    @Test
    fun firstConnexionTest() {
        var IsUpdated = false
        FakeDatabase.firstConnexion(mockedUserInterface, mockedUserInterface)
            .observe { IsUpdated = it.value }
        assert(IsUpdated)
    }

    @Test
    fun inDatabaseTest() {
        val isInDb = Observable<Boolean>()

        var IsUpdated = false
        var isInDbIsUpdated = false
        isInDb.observe { isInDbIsUpdated = it.value }

        FakeDatabase.inDatabase(isInDb, uid, mockedUserInterface).observe { IsUpdated = it.value }
        assert(IsUpdated)
        assert(isInDbIsUpdated)
    }

    @Test
    fun getUserInformationTest() {
        val user = Observable<UserEntity>()

        var IsUpdated = false
        var userIsUpdated = false
        user.observe { userIsUpdated = true }

        FakeDatabase.getUserInformation(user, uid, mockedUserInterface).observe { IsUpdated = it.value }
        assert(IsUpdated)
        assert(userIsUpdated)
    }

    @Test
    fun setUserLocationTest() {
        var isUpdated = false
        val lat = 46.548823
        val lng = 7.017012
        val pointToAdd = LatLng(lat, lng)
        FakeDatabase.setUserLocation(pointToAdd, mockedUserInterface).observe {
            isUpdated = it.value
        }

        assertThat(isUpdated, Is(true))
    }

    @Test
    fun getUsersLocationsTest() {
        val locations = Observable<List<LatLng>>()

        var isUpdated = false
        var locationsAreUpdated = false
        locations.observe { locationsAreUpdated = true }

        FakeDatabase.getUsersLocations(locations, mockedUserInterface).observe {
            isUpdated = it.value
        }

        assertThat(isUpdated, Is(true))
        assertThat(locationsAreUpdated, Is(true))
    }
}