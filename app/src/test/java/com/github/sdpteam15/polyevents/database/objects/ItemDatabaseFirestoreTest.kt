package com.github.sdpteam15.polyevents.database.objects

import com.github.sdpteam15.polyevents.database.HelperTestFunction
import com.github.sdpteam15.polyevents.model.database.remote.Database
import com.github.sdpteam15.polyevents.model.database.remote.DatabaseConstant
import com.github.sdpteam15.polyevents.model.database.remote.DatabaseInterface
import com.github.sdpteam15.polyevents.model.database.remote.FirestoreDatabaseProvider
import com.github.sdpteam15.polyevents.model.database.remote.adapter.ItemEntityAdapter
import com.github.sdpteam15.polyevents.model.database.remote.adapter.ItemTypeAdapter
import com.github.sdpteam15.polyevents.model.database.remote.objects.ItemDatabase
import com.github.sdpteam15.polyevents.model.database.remote.objects.ItemDatabaseInterface
import com.github.sdpteam15.polyevents.model.entity.Item
import com.github.sdpteam15.polyevents.model.entity.UserEntity
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
private const val itemTotal = 4
private const val itemRemaining = 3


@Suppress("UNCHECKED_CAST", "TYPE_INFERENCE_ONLY_INPUT_TYPES_WARNING")
class ItemDatabaseFirestoreTest {
    lateinit var user: UserEntity
    lateinit var database: DatabaseInterface
    lateinit var mockedItemDatabase: ItemDatabaseInterface
    lateinit var item: Item
    lateinit var createdItemTriple: Triple<Item, Int, Int>
    lateinit var itemTriple: Triple<Item, Int, Int>

    @Before
    fun setup() {
        user = UserEntity(
            uid = uidTest,
            name = displayNameTest,
            email = emailTest
        )


        item = Item(itemId = itemId, itemName = itemName, itemType = itemType)
        createdItemTriple = Triple(item, itemTotal, itemTotal)
        itemTriple = Triple(item, itemTotal, itemRemaining)

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

        Database.currentDatabase = mockedDatabase

        Database.currentDatabase = FirestoreDatabaseProvider
    }

    @Test
    fun updateItem() {

        HelperTestFunction.nextSetEntity { true }
        mockedItemDatabase.updateItem(item,itemTotal, itemRemaining)
            .observeOnce { assert(it.value) }.then.postValue(true)

        val set = HelperTestFunction.lastSetEntity()!!

        assertEquals(itemTriple, set.element)
        assertEquals(itemId, set.id)
        assertEquals(DatabaseConstant.CollectionConstant.ITEM_COLLECTION, set.collection)
        assertEquals(ItemEntityAdapter, set.adapter)
    }

    @Test
    fun addItem() {

        HelperTestFunction.nextAddEntityAndGetId { itemId }
        mockedItemDatabase.createItem(item,itemTotal)
            .observeOnce { assert(it.value == itemId) }.then.postValue("")

        val set = HelperTestFunction.lastAddEntityAndGetId()!!

        assertEquals(createdItemTriple, set.element)
        assertEquals(DatabaseConstant.CollectionConstant.ITEM_COLLECTION, set.collection)
        assertEquals(ItemEntityAdapter, set.adapter)
    }

    @Test
    fun getItemList() {
        val items = ObservableList<Triple<Item,Int, Int>>()

        HelperTestFunction.nextGetListEntity { true }
        mockedItemDatabase.getItemsList(items)
            .observeOnce { assert(it.value) }.then.postValue(false)

        val getList = HelperTestFunction.lastGetListEntity()!!

        assertEquals(items, getList.element)
        assertEquals(DatabaseConstant.CollectionConstant.ITEM_COLLECTION, getList.collection)
        assertEquals(ItemEntityAdapter, getList.adapter)
    }

    @Test
    fun getAvailableItems() {
        val items = ObservableList<Triple<Item,Int, Int>>()

        HelperTestFunction.nextGetListEntity { true }
        mockedItemDatabase.getAvailableItems(items)
            .observeOnce { assert(it.value) }.then.postValue(false)

        val getList = HelperTestFunction.lastGetListEntity()!!

        assertEquals(items, getList.element)
        assertEquals(DatabaseConstant.CollectionConstant.ITEM_COLLECTION, getList.collection)
        assertEquals(ItemEntityAdapter, getList.adapter)
    }

    @Test
    fun removeItem() {

        HelperTestFunction.nextSetEntity { true }
        mockedItemDatabase.removeItem(itemId)
            .observeOnce { assert(it.value) }.then.postValue(false)


        val del = HelperTestFunction.lastDeleteEntity()!!

        assertEquals(itemId, del.id)
        assertEquals(DatabaseConstant.CollectionConstant.ITEM_COLLECTION, del.collection)
    }

    @Test
    fun getItemTypeList() {
        val items = ObservableList<String>()

        HelperTestFunction.nextGetListEntity { true }
        mockedItemDatabase.getItemTypes(items)
            .observeOnce { assert(it.value) }.then.postValue(false)

        val getList = HelperTestFunction.lastGetListEntity()!!

        assertEquals(items, getList.element)
        assertEquals(DatabaseConstant.CollectionConstant.ITEM_TYPE_COLLECTION, getList.collection)
        assertEquals(ItemTypeAdapter, getList.adapter)
    }


    @Test
    fun createItemType(){
        val itemType = "itemTypeTest"
        HelperTestFunction.nextAddEntity { true }
        mockedItemDatabase.createItemType(itemType)
            .observeOnce { assert(it.value) }.then.postValue(false)

        val add = HelperTestFunction.lastAddEntity()!!

        assertEquals(itemType, add.element)
        assertEquals(DatabaseConstant.CollectionConstant.ITEM_TYPE_COLLECTION, add.collection)
        assertEquals(ItemTypeAdapter, add.adapter)
    }
}