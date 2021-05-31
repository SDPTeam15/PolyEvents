package com.github.sdpteam15.polyevents.database

import com.github.sdpteam15.polyevents.model.database.remote.Database.currentDatabase
import com.github.sdpteam15.polyevents.model.database.remote.DatabaseConstant
import com.github.sdpteam15.polyevents.model.database.remote.FirestoreDatabaseProvider
import com.github.sdpteam15.polyevents.model.database.remote.StringWithID
import com.github.sdpteam15.polyevents.model.database.remote.TEST_STR
import com.github.sdpteam15.polyevents.model.database.remote.adapter.UserAdapter
import com.github.sdpteam15.polyevents.model.database.remote.login.GoogleUserLogin
import com.github.sdpteam15.polyevents.model.database.remote.login.UserLogin
import com.github.sdpteam15.polyevents.model.database.remote.login.UserLoginInterface
import com.github.sdpteam15.polyevents.model.database.remote.objects.*
import com.github.sdpteam15.polyevents.model.entity.UserEntity
import com.github.sdpteam15.polyevents.model.observable.Observable
import com.github.sdpteam15.polyevents.model.observable.ObservableList
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.firestore.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.anyOrNull
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import org.mockito.Mockito.`when` as When

const val TEST_STRING = "test_string"
const val TEST_ID = "test_id"
const val TEST_ID1 = "test_id1"
const val TEST_ID2 = "test_id2"

@Suppress("UNCHECKED_CAST", "TYPE_INFERENCE_ONLY_INPUT_TYPES_WARNING")
class FirestoreDatabaseProviderTest {
    lateinit var mockFirestore: FirebaseFirestore

    @Before
    fun setup() {
        mockFirestore = mock(FirebaseFirestore::class.java)
        FirestoreDatabaseProvider.firestore = mockFirestore
    }

    @Test
    fun canAddAndEditField() {
        assertNotNull(currentDatabase.routeDatabase)
        assertNotNull(currentDatabase.materialRequestDatabase)
        assertNotNull(currentDatabase.userDatabase)
        assertNotNull(currentDatabase.userSettingsDatabase)
        assertNotNull(currentDatabase.zoneDatabase)
        assertNotNull(currentDatabase.itemDatabase)
        assertNotNull(currentDatabase.heatmapDatabase)
        assertNotNull(currentDatabase.eventDatabase)

        val materialRequestDB = mock(MaterialRequestDatabaseInterface::class.java)
        val userDb = mock(UserDatabaseInterface::class.java)
        val routeDb = mock(RouteDatabaseInterface::class.java)
        val userSettingsDb = mock(UserSettingsDatabaseInterface::class.java)
        val eventDb = mock(EventDatabaseInterface::class.java)
        val heatmapDb = mock(HeatmapDatabaseInterface::class.java)
        val ItemDb = mock(ItemDatabaseInterface::class.java)
        val zoneDb = mock(ZoneDatabaseInterface::class.java)

        currentDatabase.eventDatabase = eventDb
        currentDatabase.heatmapDatabase = heatmapDb
        currentDatabase.itemDatabase = ItemDb
        currentDatabase.zoneDatabase = zoneDb
        currentDatabase.userSettingsDatabase = userSettingsDb
        currentDatabase.routeDatabase = routeDb
        currentDatabase.userDatabase = userDb
        currentDatabase.materialRequestDatabase = materialRequestDB
        assertEquals(currentDatabase.routeDatabase, routeDb)
        assertEquals(currentDatabase.materialRequestDatabase, materialRequestDB)
        assertEquals(currentDatabase.userDatabase, userDb)
        assertEquals(currentDatabase.userSettingsDatabase, userSettingsDb)
        assertEquals(currentDatabase.zoneDatabase, zoneDb)
        assertEquals(currentDatabase.itemDatabase, ItemDb)
        assertEquals(currentDatabase.heatmapDatabase, heatmapDb)
        assertEquals(currentDatabase.eventDatabase, eventDb)

    }

    @Test
    fun addEntityAndGetId() {
        var hashMap: HashMap<String, Any?>? = null
        var lastAddSuccessListener: OnSuccessListener<DocumentReference>? = null
        var lastFailureListener: OnFailureListener? = null

        When(mockFirestore.collection(anyOrNull())).thenAnswer {
            val mock = mock(CollectionReference::class.java)
            When(mock.add(anyOrNull())).thenAnswer {
                hashMap = it!!.arguments[0] as HashMap<String, Any?>
                val mock2 = mock(Task::class.java) as Task<DocumentReference>
                When(mock2.addOnSuccessListener(anyOrNull())).thenAnswer {
                    lastAddSuccessListener =
                        it!!.arguments[0] as OnSuccessListener<DocumentReference>
                    mock2
                }
                When(mock2.addOnFailureListener(anyOrNull())).thenAnswer {
                    lastFailureListener = it!!.arguments[0] as OnFailureListener
                    mock2
                }
                mock2
            }
            mock
        }

        val end = FirestoreDatabaseProvider.addEntityAndGetId(
            StringWithID(TEST_ID, TEST_STRING),
            DatabaseConstant.CollectionConstant.TEST_COLLECTION
        )

        assertNotNull(hashMap)
        assertNotNull(lastAddSuccessListener)
        assertNotNull(lastFailureListener)

        assertEquals(TEST_STRING, hashMap!![TEST_STR] as String)

        val mockDocumentReference = mock(DocumentReference::class.java)
        When(mockDocumentReference.id).thenAnswer {
            TEST_ID
        }

        lastAddSuccessListener!!.onSuccess(mockDocumentReference)
        end.observeOnce {
            assertEquals(TEST_ID, it.value)
        }

        lastFailureListener!!.onFailure(Exception())
        end.observeOnce {
            assertEquals("", it.value)
        }
    }

    @Test
    fun addEntity() {
        var hashMap: HashMap<String, Any?>? = null
        var lastAddSuccessListener: OnSuccessListener<DocumentReference>? = null
        var lastFailureListener: OnFailureListener? = null

        When(mockFirestore.collection(anyOrNull())).thenAnswer {
            val mock = mock(CollectionReference::class.java)
            When(mock.add(anyOrNull())).thenAnswer {
                hashMap = it!!.arguments[0] as HashMap<String, Any?>
                val mock2 = mock(Task::class.java) as Task<DocumentReference>
                When(mock2.addOnSuccessListener(anyOrNull())).thenAnswer {
                    lastAddSuccessListener =
                        it!!.arguments[0] as OnSuccessListener<DocumentReference>
                    mock2
                }
                When(mock2.addOnFailureListener(anyOrNull())).thenAnswer {
                    lastFailureListener = it!!.arguments[0] as OnFailureListener
                    mock2
                }
                mock2
            }
            mock
        }

        var end = FirestoreDatabaseProvider.addEntity(
            StringWithID(TEST_ID, TEST_STRING),
            DatabaseConstant.CollectionConstant.TEST_COLLECTION
        )

        assertNotNull(hashMap)
        assertNotNull(lastAddSuccessListener)
        assertNotNull(lastFailureListener)

        assertEquals(TEST_STRING, hashMap!![TEST_STR] as String)

        val mockDocumentReference = mock(DocumentReference::class.java)
        When(mockDocumentReference.id).thenAnswer {
            TEST_ID
        }

        lastAddSuccessListener!!.onSuccess(mockDocumentReference)
        end.observeOnce {
            assert(it.value)
        }

        end = FirestoreDatabaseProvider.addEntity(
            StringWithID(TEST_ID, TEST_STRING),
            DatabaseConstant.CollectionConstant.TEST_COLLECTION
        )

        assertNotNull(hashMap)
        assertNotNull(lastAddSuccessListener)
        assertNotNull(lastFailureListener)

        lastFailureListener!!.onFailure(Exception())
        end.observeOnce {
            assert(!it.value)
        }
    }

    @Test
    fun addListEntity() {
        val hashMap = mutableListOf<HashMap<String, Any?>>()
        val lastAddSuccessListener = mutableListOf<OnSuccessListener<DocumentReference>>()
        val lastFailureListener = mutableListOf<OnFailureListener>()

        When(mockFirestore.collection(anyOrNull())).thenAnswer {
            val mock = mock(CollectionReference::class.java)
            When(mock.add(anyOrNull())).thenAnswer {
                hashMap.add(it!!.arguments[0] as HashMap<String, Any?>)
                val mock2 = mock(Task::class.java) as Task<DocumentReference>
                When(mock2.addOnSuccessListener(anyOrNull())).thenAnswer {
                    lastAddSuccessListener.add(it!!.arguments[0] as OnSuccessListener<DocumentReference>)
                    mock2
                }
                When(mock2.addOnFailureListener(anyOrNull())).thenAnswer {
                    lastFailureListener.add(it!!.arguments[0] as OnFailureListener)
                    mock2
                }
                mock2
            }
            mock
        }

        FirestoreDatabaseProvider.addListEntity(
            listOf(),
            DatabaseConstant.CollectionConstant.TEST_COLLECTION
        ).observeOnce {
            assert(it.value.first)
            assert(it.value.second.isEmpty())
        }.then.postValue(Pair(false, listOf()))

        val end = FirestoreDatabaseProvider.addListEntity(
            listOf(
                StringWithID(TEST_ID1, TEST_STRING),
                StringWithID(TEST_ID2, TEST_STRING),
            ),
            DatabaseConstant.CollectionConstant.TEST_COLLECTION
        )

        assertEquals(2, hashMap.size)
        assertEquals(2, lastAddSuccessListener.size)
        assertEquals(2, lastFailureListener.size)

        lastFailureListener[0].onFailure(Exception())
        lastFailureListener[1].onFailure(Exception())
        end.observeOnce {
            assert(!it.value.first)
        }.then.postValue(Pair(true, listOf()))
        lastFailureListener[1].onFailure(Exception())
        end.observeOnce {
            assert(!it.value.first)
        }.then.postValue(Pair(true, listOf()))

        val mockDocumentReference1 = mock(DocumentReference::class.java)
        When(mockDocumentReference1.id).thenAnswer {
            TEST_ID1
        }
        val mockDocumentReference2 = mock(DocumentReference::class.java)
        When(mockDocumentReference2.id).thenAnswer {
            TEST_ID2
        }

        lastAddSuccessListener[0].onSuccess(mockDocumentReference1)
        lastAddSuccessListener[1].onSuccess(mockDocumentReference2)
        end.observeOnce {
            assert(it.value.first)
            assertEquals(2, it.value.second.size)
            assertEquals(TEST_ID1, it.value.second[0])
            assertEquals(TEST_ID2, it.value.second[1])
        }.then.postValue(Pair(false, listOf()))
    }

    @Test
    fun setEntity() {
        var hashMap: HashMap<String, Any?>? = null
        var lastAddSuccessListener: OnSuccessListener<Void>? = null
        var lastFailureListener: OnFailureListener? = null

        When(mockFirestore.collection(anyOrNull())).thenAnswer {
            val mock = mock(CollectionReference::class.java)
            When(mock.document(anyOrNull())).thenAnswer {
                val mock2 = mock(DocumentReference::class.java)
                When(mock2.set(anyOrNull())).thenAnswer {
                    hashMap = it!!.arguments[0] as HashMap<String, Any?>
                    val mock3 = mock(Task::class.java) as Task<DocumentReference>
                    When(mock3.addOnSuccessListener(anyOrNull())).thenAnswer {
                        lastAddSuccessListener =
                            it!!.arguments[0] as OnSuccessListener<Void>
                        mock3
                    }
                    When(mock3.addOnFailureListener(anyOrNull())).thenAnswer {
                        lastFailureListener = it!!.arguments[0] as OnFailureListener
                        mock3
                    }
                    mock3
                }
                mock2
            }
            mock
        }

        val end = FirestoreDatabaseProvider.setEntity(
            StringWithID(TEST_ID, TEST_STRING),
            TEST_ID,
            DatabaseConstant.CollectionConstant.TEST_COLLECTION
        )

        assertNotNull(hashMap)
        assertNotNull(lastAddSuccessListener)
        assertNotNull(lastFailureListener)

        assertEquals(TEST_STRING, hashMap!![TEST_STR] as String)

        lastAddSuccessListener!!.onSuccess(null)
        end.observeOnce {
            assert(it.value)
        }.then.postValue(false)

        lastFailureListener!!.onFailure(Exception())
        end.observeOnce {
            assert(!it.value)
        }.then.postValue(true)
    }

    @Test
    fun setListEntity() {
        val hashMap = mutableListOf<HashMap<String, Any?>>()
        val lastAddSuccessListener = mutableListOf<OnSuccessListener<Void>>()
        val lastFailureListener = mutableListOf<OnFailureListener>()

        When(mockFirestore.collection(anyOrNull())).thenAnswer {
            val mock = mock(CollectionReference::class.java)
            When(mock.document(anyOrNull())).thenAnswer {
                val mock2 = mock(DocumentReference::class.java)
                When(mock2.set(anyOrNull())).thenAnswer {
                    hashMap.add(it!!.arguments[0] as HashMap<String, Any?>)
                    val mock3 = mock(Task::class.java) as Task<DocumentReference>
                    When(mock3.addOnSuccessListener(anyOrNull())).thenAnswer { it2 ->
                        lastAddSuccessListener.add(
                            it2!!.arguments[0] as OnSuccessListener<Void>
                        )
                        mock3
                    }
                    When(mock3.addOnFailureListener(anyOrNull())).thenAnswer {
                        lastFailureListener.add(it!!.arguments[0] as OnFailureListener)
                        mock3
                    }
                    mock3
                }
                mock2
            }
            mock
        }

        val end = FirestoreDatabaseProvider.setListEntity(
            listOf(
                Pair(TEST_ID1, StringWithID(TEST_ID1, TEST_STRING)),
                Pair(TEST_ID2, StringWithID(TEST_ID2, TEST_STRING)),
            ),
            DatabaseConstant.CollectionConstant.TEST_COLLECTION
        )

        FirestoreDatabaseProvider.setListEntity(
            listOf<Pair<String, StringWithID?>>(),
            DatabaseConstant.CollectionConstant.TEST_COLLECTION
        ).observeOnce {
            assert(it.value.first)
        }.then.postValue(Pair(false, listOf()))

        assertEquals(2, hashMap.size)
        assertEquals(2, lastAddSuccessListener.size)
        assertEquals(2, lastFailureListener.size)


        lastFailureListener[0].onFailure(Exception())
        end.observeOnce {
            assert(!it.value.first)
        }.then.postValue(Pair(false, listOf()))
        lastFailureListener[1].onFailure(Exception())
        end.observeOnce {
            assert(!it.value.first)
        }.then.postValue(Pair(false, listOf()))

        end.postValue(Pair(false, listOf()))
        lastAddSuccessListener[0].onSuccess(null)
        end.observeOnce {
            assert(!it.value.first)
        }
        lastAddSuccessListener[1].onSuccess(null)
        end.observeOnce {
            assert(it.value.first)
        }.then.postValue(Pair(false, listOf()))
    }

    @Test
    fun deleteEntity() {
        var lastAddSuccessListener: OnSuccessListener<Void>? = null
        var lastFailureListener: OnFailureListener? = null

        When(mockFirestore.collection(anyOrNull())).thenAnswer {
            val mock = mock(CollectionReference::class.java)
            When(mock.document(anyOrNull())).thenAnswer {
                val mock2 = mock(DocumentReference::class.java)
                When(mock2.delete()).thenAnswer {
                    val mock3 = mock(Task::class.java) as Task<DocumentReference>
                    When(mock3.addOnSuccessListener(anyOrNull())).thenAnswer {
                        lastAddSuccessListener =
                            it!!.arguments[0] as OnSuccessListener<Void>
                        mock3
                    }
                    When(mock3.addOnFailureListener(anyOrNull())).thenAnswer {
                        lastFailureListener = it!!.arguments[0] as OnFailureListener
                        mock3
                    }
                    mock3
                }
                mock2
            }
            mock
        }

        val end = FirestoreDatabaseProvider.deleteEntity(
            TEST_ID,
            DatabaseConstant.CollectionConstant.TEST_COLLECTION
        )

        assertNotNull(lastAddSuccessListener)
        assertNotNull(lastFailureListener)

        lastAddSuccessListener!!.onSuccess(null)
        end.observeOnce {
            assert(it.value)
        }.then.postValue(false)

        lastFailureListener!!.onFailure(Exception())
        end.observeOnce {
            assert(!it.value)
        }.then.postValue(true)
    }

    @Test
    fun deleteListEntity() {
        val lastAddSuccessListener = mutableListOf<OnSuccessListener<Void>>()
        val lastFailureListener = mutableListOf<OnFailureListener>()

        When(mockFirestore.collection(anyOrNull())).thenAnswer {
            val mock = mock(CollectionReference::class.java)
            When(mock.document(anyOrNull())).thenAnswer {
                val mock2 = mock(DocumentReference::class.java)
                When(mock2.delete()).thenAnswer {
                    val mock3 = mock(Task::class.java) as Task<DocumentReference>
                    When(mock3.addOnSuccessListener(anyOrNull())).thenAnswer {
                        lastAddSuccessListener.add(it!!.arguments[0] as OnSuccessListener<Void>)
                        mock3
                    }
                    When(mock3.addOnFailureListener(anyOrNull())).thenAnswer {
                        lastFailureListener.add(it!!.arguments[0] as OnFailureListener)
                        mock3
                    }
                    mock3
                }
                mock2
            }
            mock
        }

        val end = FirestoreDatabaseProvider.deleteListEntity(
            listOf(TEST_ID1, TEST_ID2),
            DatabaseConstant.CollectionConstant.TEST_COLLECTION
        )

        assertEquals(2, lastAddSuccessListener.size)
        assertEquals(2, lastFailureListener.size)


        lastFailureListener[0].onFailure(Exception())
        end.observeOnce {
            assert(!it.value.first)
        }.then.postValue(Pair(false, listOf()))
        lastFailureListener[1].onFailure(Exception())
        end.observeOnce {
            assert(!it.value.first)
        }.then.postValue(Pair(false, listOf()))

        end.postValue(Pair(false, listOf()))
        lastAddSuccessListener[0].onSuccess(null)
        end.observeOnce {
            assert(!it.value.first)
        }
        lastAddSuccessListener[1].onSuccess(null)
        end.observeOnce {
            assert(it.value.first)
        }.then.postValue(Pair(false, listOf()))
    }

    @Test
    fun getEntity() {
        var lastAddSuccessListener: OnSuccessListener<DocumentSnapshot>? = null
        var lastFailureListener: OnFailureListener? = null

        When(mockFirestore.collection(anyOrNull())).thenAnswer {
            val mock = mock(CollectionReference::class.java)
            When(mock.document(anyOrNull())).thenAnswer {
                val mock2 = mock(DocumentReference::class.java)
                When(mock2.get()).thenAnswer {
                    val mock3 = mock(Task::class.java) as Task<DocumentSnapshot>
                    When(mock3.addOnSuccessListener(anyOrNull())).thenAnswer {
                        lastAddSuccessListener =
                            it!!.arguments[0] as OnSuccessListener<DocumentSnapshot>
                        mock3
                    }
                    When(mock3.addOnFailureListener(anyOrNull())).thenAnswer {
                        lastFailureListener = it!!.arguments[0] as OnFailureListener
                        mock3
                    }
                    mock3
                }
                mock2
            }
            mock
        }

        val result = Observable<StringWithID>()

        val end = FirestoreDatabaseProvider.getEntity(
            result,
            TEST_ID,
            DatabaseConstant.CollectionConstant.TEST_COLLECTION
        )

        assertNotNull(lastAddSuccessListener)
        assertNotNull(lastFailureListener)

        val mockDocumentSnapshot = mock(DocumentSnapshot::class.java)

        When(mockDocumentSnapshot.data).thenAnswer {
            null
        }

        lastAddSuccessListener!!.onSuccess(mockDocumentSnapshot)
        end.observeOnce {
            assert(!it.value)
        }.then.postValue(true)

        When(mockDocumentSnapshot.data).thenAnswer {
            mapOf<String, Any?>(
                TEST_STR to TEST_STRING
            )
        }
        When(mockDocumentSnapshot.id).thenAnswer {
            TEST_ID
        }

        lastAddSuccessListener!!.onSuccess(mockDocumentSnapshot)
        end.observeOnce {
            assert(it.value)
        }.then.postValue(false)
        result.observeOnce {
            assertEquals(TEST_ID, it.value.id)
            assertEquals(TEST_STRING, it.value.string)
        }

        lastFailureListener!!.onFailure(Exception())
        end.observeOnce {
            assert(!it.value)
        }.then.postValue(true)
    }

    @Test
    fun getListEntity() {
        val lastAddSuccessListenerDocumentSnapshot =
            mutableMapOf<String, OnSuccessListener<DocumentSnapshot>>()
        var lastAddSuccessListenerQuerySnapshot: OnSuccessListener<com.github.sdpteam15.polyevents.model.database.remote.matcher.QuerySnapshot>? =
            null

        var lastFailureListener: OnFailureListener? = null

        When(mockFirestore.collection(anyOrNull())).thenAnswer {
            val mock = mock(CollectionReference::class.java)
            When(mock.document(anyOrNull())).thenAnswer {
                val id = it!!.arguments[0] as String
                val mock3 = mock(DocumentReference::class.java)
                When(mock3.get()).thenAnswer {
                    val mock2 = mock(Task::class.java) as Task<DocumentSnapshot>
                    When(mock2.addOnSuccessListener(anyOrNull())).thenAnswer {
                        lastAddSuccessListenerDocumentSnapshot[id] =
                            it!!.arguments[0] as OnSuccessListener<DocumentSnapshot>
                        mock2
                    }
                    When(mock2.addOnFailureListener(anyOrNull())).thenAnswer {
                        lastFailureListener = it!!.arguments[0] as OnFailureListener
                        mock2
                    }
                    mock2
                }
                mock3
            }
            When(mock.get()).thenAnswer {
                val mock2 = mock(Task::class.java) as Task<QuerySnapshot>
                When(mock2.addOnSuccessListener(anyOrNull())).thenAnswer {
                    lastAddSuccessListenerQuerySnapshot =
                        it!!.arguments[0] as OnSuccessListener<com.github.sdpteam15.polyevents.model.database.remote.matcher.QuerySnapshot>
                    mock2
                }
                When(mock2.addOnFailureListener(anyOrNull())).thenAnswer {
                    lastFailureListener = it!!.arguments[0] as OnFailureListener
                    mock2
                }
                When(
                    mock2.onSuccessTask<com.github.sdpteam15.polyevents.model.database.remote.matcher.QuerySnapshot>(
                        anyOrNull()
                    )
                ).thenAnswer {
                    mock2
                }
                mock2
            }
            mock
        }

        val result = ObservableList<StringWithID>()

        var end = FirestoreDatabaseProvider.getListEntity(
            result,
            listOf(TEST_ID, TEST_ID1, TEST_ID2),
            null,
            DatabaseConstant.CollectionConstant.TEST_COLLECTION
        )

        assertNotNull(lastAddSuccessListenerDocumentSnapshot[TEST_ID])
        assertNotNull(lastAddSuccessListenerDocumentSnapshot[TEST_ID1])
        assertNotNull(lastAddSuccessListenerDocumentSnapshot[TEST_ID2])
        assertNotNull(lastFailureListener)

        val mockDocumentSnapshot = mock(DocumentSnapshot::class.java)

        When(mockDocumentSnapshot.data).thenAnswer {
            null
        }

        When(mockDocumentSnapshot.data).thenAnswer {
            mapOf<String, Any?>(
                TEST_STR to TEST_STRING
            )
        }
        When(mockDocumentSnapshot.id).thenAnswer {
            TEST_ID
        }
        lastAddSuccessListenerDocumentSnapshot[TEST_ID]!!.onSuccess(mockDocumentSnapshot)

        When(mockDocumentSnapshot.id).thenAnswer {
            TEST_ID1
        }
        lastAddSuccessListenerDocumentSnapshot[TEST_ID1]!!.onSuccess(mockDocumentSnapshot)

        When(mockDocumentSnapshot.id).thenAnswer {
            TEST_ID2
        }
        lastAddSuccessListenerDocumentSnapshot[TEST_ID2]!!.onSuccess(mockDocumentSnapshot)

        end.observeOnce {
            assert(it.value)
        }.then.postValue(false)

        When(mockDocumentSnapshot.id).thenAnswer {
            TEST_ID
        }
        When(mockDocumentSnapshot.data).thenAnswer {
            null
        }
        lastAddSuccessListenerDocumentSnapshot[TEST_ID]!!.onSuccess(mockDocumentSnapshot)

        end.observeOnce {
            assert(!it.value)
        }.then.postValue(true)

        lastFailureListener!!.onFailure(Exception())
        end.observeOnce {
            assert(!it.value)
            assertEquals(1, result.size)
            assertEquals(TEST_STRING, result[0].string)
        }

        end = FirestoreDatabaseProvider.getListEntity(
            result,
            null,
            null,
            DatabaseConstant.CollectionConstant.TEST_COLLECTION
        )

        assertNotNull(lastAddSuccessListenerQuerySnapshot)
        assertNotNull(lastFailureListener)

        val mockQuerySnapshot =
            mock(com.github.sdpteam15.polyevents.model.database.remote.matcher.QuerySnapshot::class.java)

        lastFailureListener!!.onFailure(Exception())
        end.observeOnce {
            assert(!it.value)
        }.then.postValue(true)

        When(mockQuerySnapshot.iterator()).thenAnswer {
            listOf(
                com.github.sdpteam15.polyevents.model.database.remote.matcher.QueryDocumentSnapshot(
                    mapOf<String, Any?>(
                        TEST_STR to TEST_STRING
                    ),
                    TEST_ID
                )
            ).iterator()
        }

        lastAddSuccessListenerQuerySnapshot!!.onSuccess(mockQuerySnapshot)

        assert(!result.isEmpty())

        end = FirestoreDatabaseProvider.getListEntity(
            result,
            null,
            { collection -> collection },
            DatabaseConstant.CollectionConstant.TEST_COLLECTION
        )

        assertNotNull(lastAddSuccessListenerQuerySnapshot)
        assertNotNull(lastFailureListener)

        lastFailureListener!!.onFailure(Exception())
        end.observeOnce {
            assert(!it.value)
        }.then.postValue(true)

        lastAddSuccessListenerQuerySnapshot!!.onSuccess(mockQuerySnapshot)

        assert(!result.isEmpty())
    }

    @Test
    fun getCurrentUser() {
        val user = UserEntity("UserId", "UserUsername", "userName")
        val mockUserLogin = mock(UserLoginInterface::class.java) as UserLoginInterface<AuthResult>
        UserLogin.currentUserLogin = mockUserLogin
        When(mockUserLogin.isConnected()).thenReturn(false)
        assertNull(FirestoreDatabaseProvider.currentUser)

        When(mockUserLogin.isConnected()).thenReturn(true)
        currentDatabase.currentUser = user
        assertEquals(user, currentDatabase.currentUser)

        currentDatabase.currentUser = null

        val mockCollectionReference = mock(CollectionReference::class.java)
        val mockDocumentReference = mock(DocumentReference::class.java)
        val mockTask = mock(Task::class.java) as Task<DocumentSnapshot>
        var lastSuccessListener: OnSuccessListener<DocumentSnapshot>? = null
        var lastFailureListener: OnFailureListener? = null
        val mockDocumentSnapshot = mock(DocumentSnapshot::class.java)

        When(mockFirestore.collection(anyOrNull())).thenReturn(mockCollectionReference)
        When(mockCollectionReference.document(anyOrNull())).thenReturn(mockDocumentReference)
        When(mockDocumentReference.get()).thenReturn(mockTask)
        When(mockTask.addOnSuccessListener(anyOrNull())).thenAnswer {

            lastSuccessListener = it.arguments[0] as OnSuccessListener<DocumentSnapshot>
            mockTask
        }
        When(mockTask.addOnFailureListener(anyOrNull())).thenAnswer {
            lastFailureListener = it.arguments[0] as OnFailureListener
            mockTask
        }


        When(mockUserLogin.isConnected()).thenReturn(true)
        When(mockUserLogin.getCurrentUser()).thenReturn(user)
        assertEquals(FirestoreDatabaseProvider.currentUser, user)


        val userInfo = UserAdapter.toDocument(user)

        When(mockDocumentSnapshot.data).thenReturn(userInfo)
        When(mockDocumentSnapshot.id).thenReturn(user.uid)

        lastSuccessListener!!.onSuccess(mockDocumentSnapshot)

        assertEquals(FirestoreDatabaseProvider.currentUser, user)
        When(mockDocumentSnapshot.data).thenReturn(null)
        lastSuccessListener!!.onSuccess(mockDocumentSnapshot)
        assertEquals(FirestoreDatabaseProvider.currentUser, user)
        lastFailureListener!!.onFailure(java.lang.Exception())
        assertEquals(FirestoreDatabaseProvider.currentUser, user)


        UserLogin.currentUserLogin = GoogleUserLogin
    }
}