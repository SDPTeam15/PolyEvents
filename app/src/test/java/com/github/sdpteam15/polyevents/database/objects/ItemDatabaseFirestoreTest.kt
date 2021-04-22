package com.github.sdpteam15.polyevents.database.objects

import com.github.sdpteam15.polyevents.database.DatabaseConstant
import com.github.sdpteam15.polyevents.database.DatabaseConstant.CollectionConstant.ITEM_COLLECTION
import com.github.sdpteam15.polyevents.database.DatabaseConstant.CollectionConstant.ITEM_TYPE_COLLECTION
import com.github.sdpteam15.polyevents.database.DatabaseInterface
import com.github.sdpteam15.polyevents.database.FirestoreDatabaseProvider
import com.github.sdpteam15.polyevents.database.FirestoreDatabaseProviderTest
import com.github.sdpteam15.polyevents.database.observe.ObservableList
import com.github.sdpteam15.polyevents.login.GoogleUserLogin
import com.github.sdpteam15.polyevents.login.UserLogin
import com.github.sdpteam15.polyevents.login.UserLoginInterface
import com.github.sdpteam15.polyevents.model.Item
import com.github.sdpteam15.polyevents.model.UserEntity
import com.github.sdpteam15.polyevents.model.UserProfile
import com.github.sdpteam15.polyevents.util.ItemEntityAdapter
import com.github.sdpteam15.polyevents.util.ItemTypeAdapter
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito
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

    @Test
    fun variableCorrectlySet() {
        val mockedUserLogin =
            mock(UserLoginInterface::class.java) as UserLoginInterface<AuthResult>
        UserLogin.currentUserLogin = mockedUserLogin
        FirestoreDatabaseProvider.currentUser = user
        Mockito.`when`(mockedUserLogin.isConnected()).thenReturn(true)
        FirestoreDatabaseProvider.currentProfile = UserProfile()
        assert(ItemDatabaseFirestore.currentUser == FirestoreDatabaseProvider.currentUser)
        assert(ItemDatabaseFirestore.currentProfile == FirestoreDatabaseProvider.currentProfile)
        assert(ItemDatabaseFirestore.firestore == mockedDatabase)
    }

    @After
    fun teardown() {
        FirestoreDatabaseProvider.firestore = null
        ItemDatabaseFirestore.firestore = null
        UserLogin.currentUserLogin = GoogleUserLogin
    }


    @Test
    fun addItemInDatabaseWorks() {
        val mockedCollectionReference = mock(CollectionReference::class.java)
        val taskReferenceMock = mock(Task::class.java) as Task<DocumentReference>

        val testItem = Item("xxxbananaxxx", "banana", "OTHER")
        val testQuantity = 3

        When(mockedDatabase.collection(ITEM_COLLECTION.value)).thenReturn(mockedCollectionReference)
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

        var itemNameAdded: String? = ""
        var itemTypeAdded: String? = null
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

        val testItem = Item("xxxbananaxxx", "banana", "OTHER")
        val testQuantity = 3

        When(mockedDatabase.collection(ITEM_COLLECTION.value)).thenReturn(mockedCollectionReference)
        When(mockedCollectionReference.document(testItem.itemId!!)).thenReturn(documentReference)
        When(
            documentReference.set(
                ItemEntityAdapter.toItemDocument(
                    testItem,
                    testQuantity
                )
            )
        ).thenReturn(taskMock)

        var itemNameUpdated: String? = ""
        var itemTypeUpdated: String? = null
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

        val testItem = Item("xxxbananaxxx", "banana", "OTHER")
        val testQuantity = 3

        When(mockedDatabase.collection(ITEM_COLLECTION.value)).thenReturn(mockedCollectionReference)
        When(mockedCollectionReference.document(testItem.itemId!!)).thenReturn(documentReference)
        When(documentReference.delete()).thenReturn(taskMock)

        var itemNameAdded: String? = ""
        var itemTypeAdded: String? = null
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

    @Test
    fun getItemListInDatabaseWorks() {
        val mockedCollectionReference = mock(CollectionReference::class.java)
        val taskReferenceMock = mock(Task::class.java) as Task<QuerySnapshot>
        val mockedQuerySnapshot = mock(QuerySnapshot::class.java) as QuerySnapshot

        val testItems = ObservableList<Pair<Item, Int>>()

        val itemToBeAdded = mutableListOf<Pair<Item, Int>>()
        itemToBeAdded.add(Pair(Item("item1", "230V Plug", "PLUG"), 20))
        itemToBeAdded.add(Pair(Item("item2", "Cord rewinder (50m)", "PLUG"), 10))
        itemToBeAdded.add(Pair(Item("item3", "Microphone", "MICROPHONE"), 1))
        itemToBeAdded.add(Pair(Item("item4", "Cooking plate", "OTHER"), 5))
        itemToBeAdded.add(Pair(Item("item5", "Cord rewinder (100m)", "PLUG"), 1))
        itemToBeAdded.add(Pair(Item("item6", "Cord rewinder (10m)", "PLUG"), 30))
        itemToBeAdded.add(Pair(Item("item7", "Fridge(large)", "OTHER"), 2))


        When(mockedDatabase.collection(ITEM_COLLECTION.value)).thenReturn(mockedCollectionReference)
        When(mockedCollectionReference.get()).thenReturn(
            taskReferenceMock
        )

        When(taskReferenceMock.addOnSuccessListener(any())).thenAnswer {
            FirestoreDatabaseProvider.lastQuerySuccessListener!!.onSuccess(mockedQuerySnapshot)
            //set method in hard to see if the success listener is successfully called
            testItems.addAll(itemToBeAdded)
            taskReferenceMock
        }
        When(taskReferenceMock.addOnFailureListener(any())).thenAnswer {
            taskReferenceMock
        }

        val result = FirestoreDatabaseProvider.itemDatabase!!.getItemsList(testItems)
        assert(result.value!!)
        for (itemType in itemToBeAdded) {
            assert(itemType in testItems)
        }
    }

    @Test
    fun getAvailableItemsWorks() {
        val mockedCollectionReference = mock(CollectionReference::class.java)
        val mockedCollectionReferenceFiltered = mock(CollectionReference::class.java)
        val taskReferenceMock = mock(Task::class.java) as Task<QuerySnapshot>
        val mockedQuerySnapshot = mock(QuerySnapshot::class.java) as QuerySnapshot

        val testItems = ObservableList<Pair<Item, Int>>()

        val itemToBeAdded = mutableListOf<Pair<Item, Int>>()
        itemToBeAdded.add(Pair(Item("item1", "230V Plug", "PLUG"), 20))
        itemToBeAdded.add(Pair(Item("item2", "Cord rewinder (50m)", "PLUG"), 10))
        itemToBeAdded.add(Pair(Item("item3", "Microphone", "MICROPHONE"), 1))
        itemToBeAdded.add(Pair(Item("item4", "Cooking plate", "OTHER"), 5))
        itemToBeAdded.add(Pair(Item("item5", "Cord rewinder (100m)", "PLUG"), 1))
        itemToBeAdded.add(Pair(Item("item6", "Cord rewinder (10m)", "PLUG"), 30))
        itemToBeAdded.add(Pair(Item("item7", "Fridge(large)", "OTHER"), 2))


        When(mockedDatabase.collection(ITEM_COLLECTION.value)).thenReturn(mockedCollectionReference)
        When(
            mockedCollectionReference.whereGreaterThan(
                DatabaseConstant.ItemConstants.ITEM_COUNT.value,
                0
            )
        ).thenReturn(mockedCollectionReferenceFiltered)
        When(mockedCollectionReferenceFiltered.get()).thenReturn(
            taskReferenceMock
        )

        When(taskReferenceMock.addOnSuccessListener(any())).thenAnswer {
            FirestoreDatabaseProvider.lastQuerySuccessListener!!.onSuccess(mockedQuerySnapshot)
            //set method in hard to see if the success listener is successfully called
            testItems.addAll(itemToBeAdded)
            taskReferenceMock
        }
        When(taskReferenceMock.addOnFailureListener(any())).thenAnswer {
            taskReferenceMock
        }

        val result = FirestoreDatabaseProvider.itemDatabase!!.getAvailableItems(testItems)
        assert(result.value!!)
        for (itemType in itemToBeAdded) {
            assert(itemType in testItems)
        }
    }


    @Test
    fun addItemTypeInDatabaseWorks() {
        val mockedCollectionReference = mock(CollectionReference::class.java)
        val taskReferenceMock = mock(Task::class.java) as Task<DocumentReference>
        val docReferenceMock = mock(DocumentReference::class.java) as DocumentReference
        val testItemType = "TEST_ITEM_TYPE"

        When(mockedDatabase.collection(ITEM_TYPE_COLLECTION.value)).thenReturn(
            mockedCollectionReference
        )
        When(
            mockedCollectionReference.add(
                ItemTypeAdapter.toDocument(testItemType)
            )
        ).thenReturn(
            taskReferenceMock
        )
        When(docReferenceMock.id).thenReturn("test")

        var itemTypeAdded: String? = null
        When(taskReferenceMock.addOnSuccessListener(any())).thenAnswer {
            FirestoreDatabaseProvider.lastAddSuccessListener!!.onSuccess(docReferenceMock)
            //set method in hard to see if the success listener is successfully called
            itemTypeAdded = testItemType
            taskReferenceMock
        }
        When(taskReferenceMock.addOnFailureListener(any())).thenAnswer {
            taskReferenceMock
        }

        val result = FirestoreDatabaseProvider.itemDatabase!!.createItemType(testItemType)
        assert(result.value!!)
        assert(itemTypeAdded == testItemType)
    }

    @Test
    fun getItemTypesInDatabaseWorks() {
        val mockedCollectionReference = mock(CollectionReference::class.java)
        val taskReferenceMock = mock(Task::class.java) as Task<QuerySnapshot>
        val mockedQuerySnapshot = mock(QuerySnapshot::class.java) as QuerySnapshot

        val itemTypesToBeAdded = mutableListOf("Plug", "Microphone", "Wire", "Fridge")
        val testItemType = ObservableList<String>()

        When(mockedDatabase.collection(ITEM_TYPE_COLLECTION.value)).thenReturn(
            mockedCollectionReference
        )
        When(mockedCollectionReference.get()).thenReturn(
            taskReferenceMock
        )

        When(taskReferenceMock.addOnSuccessListener(any())).thenAnswer {
            FirestoreDatabaseProvider.lastQuerySuccessListener!!.onSuccess(mockedQuerySnapshot)
            //set method in hard to see if the success listener is successfully called
            testItemType.addAll(itemTypesToBeAdded)
            taskReferenceMock
        }
        When(taskReferenceMock.addOnFailureListener(any())).thenAnswer {
            taskReferenceMock
        }

        val result = FirestoreDatabaseProvider.itemDatabase!!.getItemTypes(testItemType)
        assert(result.value!!)
        for (itemType in itemTypesToBeAdded) {
            assert(itemType in testItemType)
        }
    }
}