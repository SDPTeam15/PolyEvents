package com.github.sdpteam15.polyevents.database.objects

import com.github.sdpteam15.polyevents.database.HelperTestFunction
import com.github.sdpteam15.polyevents.model.database.remote.Database
import com.github.sdpteam15.polyevents.model.database.remote.DatabaseConstant
import com.github.sdpteam15.polyevents.model.database.remote.DatabaseInterface
import com.github.sdpteam15.polyevents.model.database.remote.FirestoreDatabaseProvider
import com.github.sdpteam15.polyevents.model.database.remote.adapter.ItemEntityAdapter
import com.github.sdpteam15.polyevents.model.database.remote.adapter.ItemTypeAdapter
import com.github.sdpteam15.polyevents.model.database.remote.adapter.MaterialRequestAdapter
import com.github.sdpteam15.polyevents.model.database.remote.objects.ItemDatabase
import com.github.sdpteam15.polyevents.model.database.remote.objects.ItemDatabaseInterface
import com.github.sdpteam15.polyevents.model.database.remote.objects.MaterialRequestDatabase
import com.github.sdpteam15.polyevents.model.database.remote.objects.MaterialRequestDatabaseInterface
import com.github.sdpteam15.polyevents.model.entity.Item
import com.github.sdpteam15.polyevents.model.entity.MaterialRequest
import com.github.sdpteam15.polyevents.model.entity.UserEntity
import com.github.sdpteam15.polyevents.model.entity.UserProfile
import com.github.sdpteam15.polyevents.model.observable.Observable
import com.github.sdpteam15.polyevents.model.observable.ObservableList
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import java.time.LocalDateTime
import kotlin.test.assertEquals

private const val displayNameTest = "Test displayName"
private const val emailTest = "Test email"
private const val uidTest = "Test uid"
private const val requestId = "requestId"
private val items = mapOf("i1" to 1, "i2" to 1, "i3" to 2)
private val date = LocalDateTime.now()
private const val eventid = "eventId"
private val status = MaterialRequest.Status.PENDING

@Suppress("UNCHECKED_CAST")
class MaterialRequestDatabaseFirestoreTest {
    lateinit var user: UserEntity
    lateinit var database: DatabaseInterface
    lateinit var mockedMaterialRequestDatabase: MaterialRequestDatabaseInterface
    lateinit var materialRequest: MaterialRequest
    lateinit var createdMaterialRequest : MaterialRequest

    @Before
    fun setup() {
        user = UserEntity(
            uid = uidTest,
            name = displayNameTest,
            email = emailTest
        )

        materialRequest = MaterialRequest(
            requestId = requestId,
            items = items,
            time = date,
            userId = uidTest,
            eventId = eventid,
            status = status,
            adminMessage = null,
            staffInChargeId = null
        )


        val mockDatabaseInterface = HelperTestFunction.mockDatabaseInterface()
        mockedMaterialRequestDatabase = MaterialRequestDatabase(mockDatabaseInterface)
        HelperTestFunction.clearQueue()
    }

    @Test
    fun getCurrentUserReturnCorrectOne() {
        val mockedDatabase = Mockito.mock(DatabaseInterface::class.java)
        Mockito.`when`(mockedDatabase.currentUser).thenReturn(UserEntity(""))
        Database.currentDatabase = mockedDatabase
        assertEquals(mockedMaterialRequestDatabase.currentUser, UserEntity(""))

        Mockito.`when`(mockedDatabase.currentProfile).thenReturn(UserProfile(""))
        Database.currentDatabase = mockedDatabase
        assertEquals(mockedMaterialRequestDatabase.currentProfile, UserProfile(""))

        Database.currentDatabase = FirestoreDatabaseProvider
    }

    @Test
    fun updateMaterialRequest() {
        val userAccess = UserProfile()

        HelperTestFunction.nextSetEntity { true }
        mockedMaterialRequestDatabase.updateMaterialRequest(requestId,materialRequest, userAccess)
            .observeOnce { assert(it.value) }.then.postValue(false)

        val set = HelperTestFunction.lastSetEntity()!!

        assertEquals(materialRequest, set.element)
        assertEquals(requestId, set.id)
        assertEquals(DatabaseConstant.CollectionConstant.MATERIAL_REQUEST_COLLECTION, set.collection)
        assertEquals(MaterialRequestAdapter, set.adapter)
    }

    @Test
    fun createMaterialRequest() {
        val userAccess = UserProfile()

        HelperTestFunction.nextAddEntity { true }
        mockedMaterialRequestDatabase.createMaterialRequest(materialRequest,userAccess)
            .observeOnce { assert(it.value) }.then.postValue(false)

        val set = HelperTestFunction.lastAddEntity()!!

        assertEquals(materialRequest, set.element)
        assertEquals(DatabaseConstant.CollectionConstant.MATERIAL_REQUEST_COLLECTION, set.collection)
        assertEquals(MaterialRequestAdapter, set.adapter)
    }

    @Test
    fun getMaterialRequestList() {
        val materialRequests = ObservableList<MaterialRequest>()
        val userAccess = UserProfile("uid")

        HelperTestFunction.nextGetListEntity { true }
        mockedMaterialRequestDatabase.getMaterialRequestList(materialRequests, userAccess = userAccess)
            .observeOnce { assert(it.value) }.then.postValue(false)

        val getList = HelperTestFunction.lastGetListEntity()!!

        assertEquals(materialRequests, getList.element)
        assertEquals(DatabaseConstant.CollectionConstant.MATERIAL_REQUEST_COLLECTION, getList.collection)
        assertEquals(MaterialRequestAdapter, getList.adapter)
    }

    @Test
    fun getMaterialRequestListByUser() {
        val materialRequests = ObservableList<MaterialRequest>()
        val userAccess = UserProfile("uid")

        HelperTestFunction.nextGetListEntity { true }
        mockedMaterialRequestDatabase.getMaterialRequestListByUser(materialRequests, userAccess = userAccess, userId = uidTest)
            .observeOnce { assert(it.value) }.then.postValue(false)

        val getList = HelperTestFunction.lastGetListEntity()!!

        assertEquals(materialRequests, getList.element)
        assertEquals(DatabaseConstant.CollectionConstant.MATERIAL_REQUEST_COLLECTION, getList.collection)
        assertEquals(MaterialRequestAdapter, getList.adapter)
    }

    @Test
    fun deleteMaterialRequest() {
        val userAccess = UserProfile("uid")

        HelperTestFunction.nextSetEntity { true }
        mockedMaterialRequestDatabase.deleteMaterialRequest(requestId, userAccess)
            .observeOnce { assert(it.value) }.then.postValue(false)


        val del = HelperTestFunction.lastDeleteEntity()!!

        assertEquals(requestId, del.id)
        assertEquals(DatabaseConstant.CollectionConstant.MATERIAL_REQUEST_COLLECTION, del.collection)
    }

    @Test
    fun getMaterialReequestById() {
        val userAccess = UserProfile("uid")
        val observable = Observable<MaterialRequest>()
        HelperTestFunction.nextGetEntity { true }
        mockedMaterialRequestDatabase.getMaterialRequestById(observable, requestId, userAccess)
            .observeOnce { assert(it.value) }.then.postValue(false)


        val get = HelperTestFunction.lastGetEntity()!!

        assertEquals(requestId, get.id)
        assertEquals(DatabaseConstant.CollectionConstant.MATERIAL_REQUEST_COLLECTION, get.collection)
    }

}