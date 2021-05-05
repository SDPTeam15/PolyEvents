package com.github.sdpteam15.polyevents.database

import com.github.sdpteam15.polyevents.model.database.remote.*
import com.github.sdpteam15.polyevents.model.observable.Observable
import com.github.sdpteam15.polyevents.model.observable.ObservableList
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.anyOrNull
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import org.mockito.Mockito.`when` as When

const val TEST_STRING = "test_string"
const val TEST_ID = "test_id"
const val TEST_ID1 = "test_id1"
const val TEST_ID2 = "test_id2"

class FirestoreDatabaseProviderTest {
    lateinit var mokeFirestore: FirebaseFirestore

    @Before
    fun setup() {
        mokeFirestore = mock(FirebaseFirestore::class.java)

        FirestoreDatabaseProvider.firestore = mokeFirestore
    }

    @Test
    fun addEntityAndGetId() {
        var hashMap: HashMap<String, Any?>? = null
        var lastAddSuccessListener: OnSuccessListener<DocumentReference>? = null
        var lastFailureListener: OnFailureListener? = null

        When(mokeFirestore.collection(anyOrNull())).thenAnswer {
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

        val mokeDocumentReference = mock(DocumentReference::class.java)
        When(mokeDocumentReference.id).thenAnswer {
            TEST_ID
        }

        lastAddSuccessListener!!.onSuccess(mokeDocumentReference)
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

        When(mokeFirestore.collection(anyOrNull())).thenAnswer {
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

        val mokeDocumentReference = mock(DocumentReference::class.java)
        When(mokeDocumentReference.id).thenAnswer {
            TEST_ID
        }

        lastAddSuccessListener!!.onSuccess(mokeDocumentReference)
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
    fun setEntity() {
        var hashMap: HashMap<String, Any?>? = null
        var lastAddSuccessListener: OnSuccessListener<Void>? = null
        var lastFailureListener: OnFailureListener? = null

        When(mokeFirestore.collection(anyOrNull())).thenAnswer {
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
        }

        lastFailureListener!!.onFailure(Exception())
        end.observeOnce {
            assert(!it.value)
        }
    }

    @Test
    fun deleteEntity() {
        var lastAddSuccessListener: OnSuccessListener<Void>? = null
        var lastFailureListener: OnFailureListener? = null

        When(mokeFirestore.collection(anyOrNull())).thenAnswer {
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
        }

        lastFailureListener!!.onFailure(Exception())
        end.observeOnce {
            assert(!it.value)
        }
    }

    @Test
    fun getEntity() {
        var lastAddSuccessListener: OnSuccessListener<DocumentSnapshot>? = null
        var lastFailureListener: OnFailureListener? = null

        When(mokeFirestore.collection(anyOrNull())).thenAnswer {
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
        }

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
        }
        result.observeOnce {
            assertEquals(TEST_ID, it.value.id)
            assertEquals(TEST_STRING, it.value.string)
        }

        lastFailureListener!!.onFailure(Exception())
        end.observeOnce {
            assert(!it.value)
        }
    }

    @Test
    fun getListEntity() {
        val lastAddSuccessListenerDocumentSnapshot =
            mutableMapOf<String, OnSuccessListener<DocumentSnapshot>>()
        var lastAddSuccessListenerQuerySnapshot: OnSuccessListener<QuerySnapshot>? = null

        var lastFailureListener: OnFailureListener? = null

        When(mokeFirestore.collection(anyOrNull())).thenAnswer {
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
                        it!!.arguments[0] as OnSuccessListener<QuerySnapshot>
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

        lastAddSuccessListenerDocumentSnapshot[TEST_ID]!!.onSuccess(mockDocumentSnapshot)
        end.observeOnce {
            assert(!it.value)
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
        }

        lastFailureListener!!.onFailure(Exception())
        end.observeOnce {
            assert(!it.value)
            assertEquals(TEST_ID, result[0].id)
            assertEquals(TEST_STRING, result[0].string)
            assertEquals(TEST_ID1, result[1].id)
            assertEquals(TEST_STRING, result[1].string)
            assertEquals(TEST_ID2, result[2].id)
            assertEquals(TEST_STRING, result[2].string)
        }

        end = FirestoreDatabaseProvider.getListEntity(
            result,
            null,
            null,
            DatabaseConstant.CollectionConstant.TEST_COLLECTION
        )

        assertNotNull(lastAddSuccessListenerQuerySnapshot)
        assertNotNull(lastFailureListener)

        val mockQuerySnapshot = mock(QuerySnapshot::class.java)

        lastFailureListener!!.onFailure(Exception())
        end.observeOnce {
            assert(!it.value)
        }

        When(mockQuerySnapshot.iterator()).thenAnswer {
            val mock = mock(QueryDocumentSnapshot::class.java)
            When(mock.data).thenAnswer {
                mapOf<String, Any?>(
                    TEST_STR to TEST_STRING
                )
            }
            When(mock.id).thenAnswer {
                TEST_ID
            }
            listOf<QueryDocumentSnapshot>(mock).iterator()
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
        }

        When(mockQuerySnapshot.iterator()).thenAnswer {
            val mock = mock(QueryDocumentSnapshot::class.java)
            When(mock.data).thenAnswer {
                mapOf<String, Any?>(
                    TEST_STR to TEST_STRING
                )
            }
            When(mock.id).thenAnswer {
                TEST_ID
            }
            listOf<QueryDocumentSnapshot>(mock).iterator()
        }

        lastAddSuccessListenerQuerySnapshot!!.onSuccess(mockQuerySnapshot)

        assert(!result.isEmpty())
    }
}