package com.github.sdpteam15.polyevents.database

import com.github.sdpteam15.polyevents.database.observe.Observable
import com.github.sdpteam15.polyevents.database.observe.ObservableList
import com.github.sdpteam15.polyevents.util.AdapterToDocumentInterface
import org.mockito.kotlin.anyOrNull
import java.util.*
import org.mockito.Mockito.`when` as When

object HelperTestFunction {
    val nextBoolean: Queue<Boolean> = LinkedList()
    val nextString: Queue<String> = LinkedList()

    class addEntityAndGetIdArgs(
        val element: Any,
        val collection: DatabaseConstant.CollectionConstant,
        val adapter: AdapterToDocumentInterface<out Any> //'out E' is '? extends E' in java
    )

    val addEntityAndGetIdQueue: Queue<addEntityAndGetIdArgs> = LinkedList()

    class addEntityArgs(
        val element: Any,
        val collection: DatabaseConstant.CollectionConstant,
        val adapter: AdapterToDocumentInterface<out Any> //'out E' is '? extends E' in java
    )

    val addEntityQueue: Queue<addEntityArgs> = LinkedList()

    class setEntityArgs(
        val element: Any?,
        val id: String,
        val collection: DatabaseConstant.CollectionConstant,
        val adapter: AdapterToDocumentInterface<out Any>? //'out E' is '? extends E' in java
    )

    val setEntityQueue: Queue<setEntityArgs> = LinkedList()

    class deleteEntityArgs(
        val id: String,
        val collection: DatabaseConstant.CollectionConstant
    )

    val deleteEntityQueue: Queue<deleteEntityArgs> = LinkedList()

    class getEntityArgs(
        val element: Observable<out Any>,
        val id: String,
        val collection: DatabaseConstant.CollectionConstant,
        val adapter: AdapterToDocumentInterface<out Any>? //'out E' is '? extends E' in java
    )

    val getEntityQueue: Queue<getEntityArgs> = LinkedList()

    class getListEntityArgs(
        val element: ObservableList<out Any>,
        val ids: String?,
        val matcher: Matcher?,
        val collection: DatabaseConstant.CollectionConstant,
        val adapter: AdapterToDocumentInterface<out Any>? //'out E' is '? extends E' in java
    )

    val getListEntityQueue: Queue<getListEntityArgs> = LinkedList()

    fun clearQueue() {
        (nextBoolean as LinkedList).clear()
        (nextString as LinkedList).clear()
        (addEntityAndGetIdQueue as LinkedList).clear()
        (addEntityQueue as LinkedList).clear()
        (setEntityQueue as LinkedList).clear()
        (deleteEntityQueue as LinkedList).clear()
        (getEntityQueue as LinkedList).clear()
        (getListEntityQueue as LinkedList).clear()
    }

    inline fun <reified T : Any> mockFor(mokeDatabaseInterface: DatabaseInterface) {
        When(
            mokeDatabaseInterface.addEntityAndGetId<T>(
                anyOrNull(),
                anyOrNull(),
                anyOrNull()
            )
        ).thenAnswer {
            val iterator = it!!.arguments.iterator()
            addEntityAndGetIdQueue.add(
                addEntityAndGetIdArgs(
                    iterator.next() as T,
                    iterator.next() as DatabaseConstant.CollectionConstant,
                    iterator.next() as AdapterToDocumentInterface<T>,
                )
            )
            Observable(nextString.peek() ?: "")
        }
        When(
            mokeDatabaseInterface.addEntity<T>(
                anyOrNull(),
                anyOrNull(),
                anyOrNull()
            )
        ).thenAnswer {
            addEntityQueue.add(
                addEntityArgs(
                    it!!.arguments[0] as T,
                    it!!.arguments[1] as DatabaseConstant.CollectionConstant,
                    it!!.arguments[2] as AdapterToDocumentInterface<T>,
                )
            )
            Observable(nextBoolean.peek() ?: true)
        }
        When(
            mokeDatabaseInterface.setEntity<T>(
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull()
            )
        ).thenAnswer {
            val iterator = it!!.arguments.iterator()
            setEntityQueue.add(
                setEntityArgs(
                    iterator.next() as T,
                    iterator.next() as String,
                    iterator.next() as DatabaseConstant.CollectionConstant,
                    iterator.next() as AdapterToDocumentInterface<T>?,
                )
            )
            Observable(nextBoolean.peek() ?: true)
        }
        When(
            mokeDatabaseInterface.deleteEntity(
                anyOrNull(),
                anyOrNull()
            )
        ).thenAnswer {
            val iterator = it!!.arguments.iterator()
            deleteEntityQueue.add(
                deleteEntityArgs(
                    iterator.next() as String,
                    iterator.next() as DatabaseConstant.CollectionConstant,
                )
            )
            Observable(nextBoolean.peek() ?: true)
        }
        When(
            mokeDatabaseInterface.getEntity<T>(
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull()
            )
        ).thenAnswer {
            val iterator = it!!.arguments.iterator()
            getEntityQueue.add(
                getEntityArgs(
                    iterator.next() as Observable<T>,
                    iterator.next() as String,
                    iterator.next() as DatabaseConstant.CollectionConstant,
                    iterator.next() as AdapterToDocumentInterface<T>?,
                )
            )
            Observable(nextBoolean.peek() ?: true)
        }
        When(
            mokeDatabaseInterface.getListEntity<T>(
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull()
            )
        ).thenAnswer {
            val iterator = it!!.arguments.iterator()
            getListEntityQueue.add(
                getListEntityArgs(
                    iterator.next() as ObservableList<T>,
                    iterator.next() as String?,
                    iterator.next() as Matcher?,
                    iterator.next() as DatabaseConstant.CollectionConstant,
                    iterator.next() as AdapterToDocumentInterface<T>?,
                )
            )
            Observable(nextBoolean.peek() ?: true)
        }
    }
}