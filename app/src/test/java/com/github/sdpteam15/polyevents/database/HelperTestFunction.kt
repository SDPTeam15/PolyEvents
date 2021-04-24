package com.github.sdpteam15.polyevents.database

import com.github.sdpteam15.polyevents.database.observe.Observable
import com.github.sdpteam15.polyevents.database.observe.ObservableList
import com.github.sdpteam15.polyevents.util.AdapterFromDocumentInterface
import com.github.sdpteam15.polyevents.util.AdapterToDocumentInterface
import org.mockito.Mockito
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
        val adapter: AdapterFromDocumentInterface<in Any>? //'in E' is '? super E' in java
    )

    val getEntityQueue: Queue<getEntityArgs> = LinkedList()

    class getListEntityArgs(
        val element: ObservableList<out Any>,
        val ids: MutableList<String>?,
        val matcher: Matcher?,
        val collection: DatabaseConstant.CollectionConstant,
        val adapter: AdapterFromDocumentInterface<in Any>? //'in E' is '? super E' in java
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

    fun mockFor() : DatabaseInterface {
        val mokeDatabaseInterface = Mockito.mock(DatabaseInterface::class.java)
        When(
            mokeDatabaseInterface.addEntityAndGetId(
                anyOrNull(),
                anyOrNull(),
                anyOrNull()
            )
        ).thenAnswer {
            val iterator = it!!.arguments.iterator()
            addEntityAndGetIdQueue.add(
                addEntityAndGetIdArgs(
                    iterator.next() as Any,
                    iterator.next() as DatabaseConstant.CollectionConstant,
                    iterator.next() as AdapterToDocumentInterface<Any>,
                )
            )
            Observable(nextString.poll() ?: "")
        }
        When(
            mokeDatabaseInterface.addEntity(
                anyOrNull(),
                anyOrNull(),
                anyOrNull()
            )
        ).thenAnswer {
            addEntityQueue.add(
                addEntityArgs(
                    it!!.arguments[0] as Any,
                    it!!.arguments[1] as DatabaseConstant.CollectionConstant,
                    it!!.arguments[2] as AdapterToDocumentInterface<Any>,
                )
            )
            Observable(nextBoolean.poll() ?: true)
        }
        When(
            mokeDatabaseInterface.setEntity(
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull()
            )
        ).thenAnswer {
            val iterator = it!!.arguments.iterator()
            setEntityQueue.add(
                setEntityArgs(
                    iterator.next() as Any,
                    iterator.next() as String,
                    iterator.next() as DatabaseConstant.CollectionConstant,
                    iterator.next() as AdapterToDocumentInterface<Any>?,
                )
            )
            Observable(nextBoolean.poll() ?: true)
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
            Observable(nextBoolean.poll() ?: true)
        }
        When(
            mokeDatabaseInterface.getEntity<Any>(
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull()
            )
        ).thenAnswer {
            val iterator = it!!.arguments.iterator()
            getEntityQueue.add(
                getEntityArgs(
                    iterator.next() as Observable<Any>,
                    iterator.next() as String,
                    iterator.next() as DatabaseConstant.CollectionConstant,
                    iterator.next() as AdapterFromDocumentInterface<Any>?,
                )
            )
            Observable(nextBoolean.poll() ?: true)
        }
        When(
            mokeDatabaseInterface.getListEntity<Any>(
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
                    iterator.next() as ObservableList<Any>,
                    iterator.next() as MutableList<String>?,
                    iterator.next() as Matcher?,
                    iterator.next() as DatabaseConstant.CollectionConstant,
                    iterator.next() as AdapterFromDocumentInterface<Any>?,
                )
            )
            Observable(nextBoolean.poll() ?: true)
        }
        return mokeDatabaseInterface
    }
}