package com.github.sdpteam15.polyevents.database.objects

import com.github.sdpteam15.polyevents.database.DatabaseConstant.ITEM_COLLECTION
import com.github.sdpteam15.polyevents.database.DatabaseInterface
import com.github.sdpteam15.polyevents.database.FirestoreDatabaseProvider
import com.github.sdpteam15.polyevents.model.Item
import com.github.sdpteam15.polyevents.model.ItemType
import com.github.sdpteam15.polyevents.model.UserEntity
import com.github.sdpteam15.polyevents.util.ItemEntityAdapter
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when` as When

private const val displayNameTest = "Test displayName"
private const val emailTest = "Test email"
private const val uidTest = "Test uid"
private val listProfile = ArrayList<String>()

class ItemDatabaseFirestoreTest {
    lateinit var user: UserEntity
    lateinit var mockedDatabase: FirebaseFirestore
    lateinit var database: DatabaseInterface

    @Before
    fun setup() {
        user = UserEntity(
            uid = uidTest,
            name = displayNameTest,
            email = emailTest,
            profiles = listProfile
        )

        //Mock the database and set it as the default database
        mockedDatabase = mock(FirebaseFirestore::class.java)
        FirestoreDatabaseProvider.firestore = mockedDatabase
        //FirestoreDatabaseProvider.userDatabase =  mockedDatabaseUser
        ItemDatabaseFirestore.firestore = mockedDatabase

        FirestoreDatabaseProvider.lastQuerySuccessListener = null
        FirestoreDatabaseProvider.lastSetSuccessListener = null
        FirestoreDatabaseProvider.lastFailureListener = null
        FirestoreDatabaseProvider.lastGetSuccessListener = null
        FirestoreDatabaseProvider.lastAddSuccessListener = null
    }

    @After
    fun teardown() {
        FirestoreDatabaseProvider.firestore = null
        ItemDatabaseFirestore.firestore = null
    }


    @Test
    fun addItemInDatabaseWorks() {
        val mockedCollectionReference = mock(CollectionReference::class.java)
        val taskReferenceMock = mock(Task::class.java) as Task<DocumentReference>

        val testItem = Item("xxxbananaxxx", "banana", ItemType.OTHER)
        val testQuantity = 3

        When(mockedDatabase.collection(ITEM_COLLECTION)).thenReturn(mockedCollectionReference)
        When(
            mockedCollectionReference.add(
                ItemEntityAdapter.toItemDocument(
                    testItem,
                    testQuantity
                )
            )
        ).thenReturn(
            taskReferenceMock
        )

        var itemNameAdded = ""
        var itemTypeAdded: ItemType? = null
        var itemCountAdded = 0
        var itemIdAdded = ""

        When(taskReferenceMock.addOnSuccessListener(any())).thenAnswer {
            FirestoreDatabaseProvider.lastAddSuccessListener!!.onSuccess(null)
            //set method in hard to see if the success listener is successfully called
            itemNameAdded = testItem.itemName
            itemTypeAdded = testItem.itemType
            itemCountAdded = testQuantity
            itemIdAdded = testItem.itemId!!
            taskReferenceMock
        }
        When(taskReferenceMock.addOnFailureListener(any())).thenAnswer {
            taskReferenceMock
        }

        val result = FirestoreDatabaseProvider.itemDatabase!!.createItem(testItem, testQuantity)
        assert(result.value!!)
        assert(itemNameAdded == testItem.itemName)
        assert(itemTypeAdded == testItem.itemType)
        assert(itemCountAdded == testQuantity)
        assert(itemIdAdded == testItem.itemId!!)
    }


    @Test
    fun updateItemInDatabaseWorks() {
        val mockedCollectionReference = mock(CollectionReference::class.java)
        val documentReference = mock(DocumentReference::class.java) as DocumentReference
        val taskMock = mock(Task::class.java) as Task<Void>

        val testItem = Item("xxxbananaxxx", "banana", ItemType.OTHER)
        val testQuantity = 3

        When(mockedDatabase.collection(ITEM_COLLECTION)).thenReturn(mockedCollectionReference)
        When(mockedCollectionReference.document(testItem.itemId!!)).thenReturn(documentReference)
        When(
            documentReference.set(
                ItemEntityAdapter.toItemDocument(
                    testItem,
                    testQuantity
                )
            )
        ).thenReturn(taskMock)

        var itemNameUpdated = ""
        var itemTypeUpdated: ItemType? = null
        var itemCountUpdated = 0
        var itemIdUpdated = ""

        When(taskMock.addOnSuccessListener(any())).thenAnswer {
            FirestoreDatabaseProvider.lastSetSuccessListener!!.onSuccess(null)
            //set method in hard to see if the success listener is successfully called
            itemNameUpdated = testItem.itemName
            itemTypeUpdated = testItem.itemType
            itemCountUpdated = testQuantity
            itemIdUpdated = testItem.itemId!!
            taskMock
        }
        When(taskMock.addOnFailureListener(any())).thenAnswer {
            taskMock
        }

        val result = FirestoreDatabaseProvider.itemDatabase!!.updateItem(testItem, testQuantity)
        assert(result.value!!)
        assert(itemNameUpdated == testItem.itemName)
        assert(itemTypeUpdated == testItem.itemType)
        assert(itemCountUpdated == testQuantity)
        assert(itemIdUpdated == testItem.itemId!!)
    }


    @Test
    fun removeItemFromDatabaseWorks() {
        val mockedCollectionReference = mock(CollectionReference::class.java)
        val documentReference = mock(DocumentReference::class.java) as DocumentReference
        val taskMock = mock(Task::class.java) as Task<Void>

        val testItem = Item("xxxbananaxxx", "banana", ItemType.OTHER)
        val testQuantity = 3

        When(mockedDatabase.collection(ITEM_COLLECTION)).thenReturn(mockedCollectionReference)
        When(mockedCollectionReference.document(testItem.itemId!!)).thenReturn(documentReference)
        When(documentReference.delete()).thenReturn(taskMock)

        var itemNameAdded = ""
        var itemTypeAdded: ItemType? = null
        var itemCountAdded = 0
        var itemIdAdded = ""

        When(taskMock.addOnSuccessListener(any())).thenAnswer {
            FirestoreDatabaseProvider.lastSetSuccessListener!!.onSuccess(null)
            //set method in hard to see if the success listener is successfully called
            itemNameAdded = testItem.itemName
            itemTypeAdded = testItem.itemType
            itemCountAdded = testQuantity
            itemIdAdded = testItem.itemId!!
            taskMock
        }
        When(taskMock.addOnFailureListener(any())).thenAnswer {
            taskMock
        }

        val result = FirestoreDatabaseProvider.itemDatabase!!.removeItem(testItem.itemId as String)
        assert(result.value!!)
        assert(itemNameAdded == testItem.itemName)
        assert(itemTypeAdded == testItem.itemType)
        assert(itemCountAdded == testQuantity)
        assert(itemIdAdded == testItem.itemId!!)
    }
}