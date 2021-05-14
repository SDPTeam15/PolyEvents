package com.github.sdpteam15.polyevents.database.objects

import com.github.sdpteam15.polyevents.database.HelperTestFunction
import com.github.sdpteam15.polyevents.model.database.remote.Database
import com.github.sdpteam15.polyevents.model.database.remote.DatabaseConstant
import com.github.sdpteam15.polyevents.model.database.remote.DatabaseInterface
import com.github.sdpteam15.polyevents.model.database.remote.FirestoreDatabaseProvider
import com.github.sdpteam15.polyevents.model.database.remote.adapter.ItemEntityAdapter
import com.github.sdpteam15.polyevents.model.database.remote.adapter.ItemTypeAdapter
import com.github.sdpteam15.polyevents.model.database.remote.adapter.ZoneAdapter
import com.github.sdpteam15.polyevents.model.database.remote.objects.ItemDatabase
import com.github.sdpteam15.polyevents.model.database.remote.objects.ItemDatabaseInterface
import com.github.sdpteam15.polyevents.model.database.remote.objects.ZoneDatabase
import com.github.sdpteam15.polyevents.model.database.remote.objects.ZoneDatabaseInterface
import com.github.sdpteam15.polyevents.model.entity.Item
import com.github.sdpteam15.polyevents.model.entity.UserEntity
import com.github.sdpteam15.polyevents.model.entity.UserProfile
import com.github.sdpteam15.polyevents.model.entity.Zone
import com.github.sdpteam15.polyevents.model.observable.Observable
import com.github.sdpteam15.polyevents.model.observable.ObservableList
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import kotlin.test.assertEquals

private const val displayNameTest = "Test displayName"
private const val emailTest = "Test email"
private const val uidTest = "Test uid"
private const val itemId = "itemId"
private const val itemName = "ZONENAME"
private const val itemType = "ZONEDESC"
private const val itemCount = 4

@Suppress("UNCHECKED_CAST")
class ItemDatabaseFirestoreTest {
    lateinit var user: UserEntity
    lateinit var database: DatabaseInterface
    lateinit var mockedItemDatabase: ItemDatabaseInterface
    lateinit var item: Item
    lateinit var itemPair: Pair<Item,Int>

    @Before
    fun setup() {
        user = UserEntity(
            uid = uidTest,
            name = displayNameTest,
            email = emailTest
        )


        item = Item(itemId = itemId,itemName = itemName, itemType = itemType)
        itemPair = Pair(item, itemCount)

        val mockDatabaseInterface = HelperTestFunction.mockDatabaseInterface()
        mockedItemDatabase = ItemDatabase(mockDatabaseInterface)
        HelperTestFunction.clearQueue()
    }

    @Test
    fun getCurrentUserReturnCorrectOne() {
        val mockedDatabase = Mockito.mock(DatabaseInterface::class.java)
        Mockito.`when`(mockedDatabase.currentUser).thenReturn(UserEntity(""))
        Database.currentDatabase = mockedDatabase
        assertEquals(mockedItemDatabase.currentUser, UserEntity(""))

        Mockito.`when`(mockedDatabase.currentProfile).thenReturn(UserProfile(""))
        Database.currentDatabase = mockedDatabase
        assertEquals(mockedItemDatabase.currentProfile, UserProfile(""))

        Database.currentDatabase = FirestoreDatabaseProvider
    }

    @Test
    fun updateItem() {
        val userAccess = UserProfile()

        HelperTestFunction.nextSetEntity { true }
        mockedItemDatabase.updateItem(item,itemCount, userAccess)
            .observeOnce { assert(it.value) }.then.postValue(true)

        val set = HelperTestFunction.lastSetEntity()!!

        assertEquals(itemPair, set.element)
        assertEquals(itemId, set.id)
        assertEquals(DatabaseConstant.CollectionConstant.ITEM_COLLECTION, set.collection)
        assertEquals(ItemEntityAdapter, set.adapter)
    }

    @Test
    fun addItem() {
        val userAccess = UserProfile()

        HelperTestFunction.nextAddEntity { true }
        mockedItemDatabase.createItem(item,itemCount, userAccess)
            .observeOnce { assert(it.value) }.then.postValue(true)

        val set = HelperTestFunction.lastAddEntity()!!

        assertEquals(itemPair, set.element)
        assertEquals(DatabaseConstant.CollectionConstant.ITEM_COLLECTION, set.collection)
        assertEquals(ItemEntityAdapter, set.adapter)
    }

    @Test
    fun getItemList() {
        val items = ObservableList<Pair<Item,Int>>()
        val userAccess = UserProfile("uid")

        HelperTestFunction.nextGetListEntity { true }
        mockedItemDatabase.getItemsList(items,  userAccess)
            .observeOnce { assert(it.value) }.then.postValue(false)

        val getList = HelperTestFunction.lastGetListEntity()!!

        assertEquals(items, getList.element)
        assertEquals(DatabaseConstant.CollectionConstant.ITEM_COLLECTION, getList.collection)
        assertEquals(ItemEntityAdapter, getList.adapter)
    }

    @Test
    fun removeItem() {
        val userAccess = UserProfile("uid")

        HelperTestFunction.nextSetEntity { true }
        mockedItemDatabase.removeItem(itemId, userAccess)
            .observeOnce { assert(it.value) }.then.postValue(false)


        val del = HelperTestFunction.lastDeleteEntity()!!

        assertEquals(itemId, del.id)
        assertEquals(DatabaseConstant.CollectionConstant.ITEM_COLLECTION, del.collection)
    }

    @Test
    fun getItemTypeList() {
        val items = ObservableList<String>()
        val userAccess = UserProfile("uid")

        HelperTestFunction.nextGetListEntity { true }
        mockedItemDatabase.getItemTypes(items,  userAccess)
            .observeOnce { assert(it.value) }.then.postValue(false)

        val getList = HelperTestFunction.lastGetListEntity()!!

        assertEquals(items, getList.element)
        assertEquals(DatabaseConstant.CollectionConstant.ITEM_TYPE_COLLECTION, getList.collection)
        assertEquals(ItemTypeAdapter, getList.adapter)
    }
}