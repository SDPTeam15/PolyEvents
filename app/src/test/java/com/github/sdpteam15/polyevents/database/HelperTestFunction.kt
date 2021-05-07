package com.github.sdpteam15.polyevents.database

import com.github.sdpteam15.polyevents.model.database.remote.DatabaseConstant
import com.github.sdpteam15.polyevents.model.database.remote.DatabaseInterface
import com.github.sdpteam15.polyevents.model.database.remote.Matcher
import com.github.sdpteam15.polyevents.model.database.remote.adapter.AdapterFromDocumentInterface
import com.github.sdpteam15.polyevents.model.database.remote.adapter.AdapterToDocumentInterface
import com.github.sdpteam15.polyevents.model.observable.Observable
import com.github.sdpteam15.polyevents.model.observable.ObservableList
import org.mockito.Mockito
import org.mockito.kotlin.anyOrNull
import java.util.*
import org.mockito.Mockito.`when` as When

@Suppress("UNCHECKED_CAST")
object HelperTestFunction {

    val nextBooleanFun: Queue<() -> Boolean> = LinkedList()
    val nextStringFun: Queue<() -> String> = LinkedList()
    val nextPairStringListFun: Queue<() -> Pair<Boolean, List<String>?>> = LinkedList()

    fun nextBoolean(boolean: Boolean) = nextBooleanFun.add { boolean }
    fun nextString(string: String) = nextStringFun.add { string }
    fun nextPairStringList(pair: Pair<Boolean, List<String>?>) = nextPairStringListFun.add { pair }

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

    class addListEntityArgs(
        val elements: List<Any>,
        val collection: DatabaseConstant.CollectionConstant,
        val adapter: AdapterToDocumentInterface<in Any>
    )

    val addListEntityQueue: Queue<addListEntityArgs> = LinkedList()

    class setEntityArgs(
        val element: Any?,
        val id: String,
        val collection: DatabaseConstant.CollectionConstant,
        val adapter: AdapterToDocumentInterface<out Any>? //'out E' is '? extends E' in java
    )

    val setEntityQueue: Queue<setEntityArgs> = LinkedList()

    class setListEntityArgs(
        val elements: List<Pair<String, Any?>>,
        val collection: DatabaseConstant.CollectionConstant,
        val adapter: AdapterToDocumentInterface<in Any>
    )

    val setListEntityQueue: Queue<setListEntityArgs> = LinkedList()

    class deleteEntityArgs(
        val id: String,
        val collection: DatabaseConstant.CollectionConstant
    )


    val deleteEntityQueue: Queue<deleteEntityArgs> = LinkedList()

    class deleteListEntityArgs(
        val ids: List<String>,
        val collection: DatabaseConstant.CollectionConstant
    )

    val deleteListEntityQueue: Queue<deleteListEntityArgs> = LinkedList()

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
        (nextBooleanFun as LinkedList).clear()
        (nextStringFun as LinkedList).clear()
        (addEntityAndGetIdQueue as LinkedList).clear()
        (addEntityQueue as LinkedList).clear()
        (setEntityQueue as LinkedList).clear()
        (deleteEntityQueue as LinkedList).clear()
        (getEntityQueue as LinkedList).clear()
        (getListEntityQueue as LinkedList).clear()
    }

    fun mockFor(): DatabaseInterface {
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
            Observable((nextStringFun.poll() ?: { "" })())
        }
        When(
            mokeDatabaseInterface.addEntity(
                anyOrNull(),
                anyOrNull(),
                anyOrNull()
            )
        ).thenAnswer {
            val iterator = it!!.arguments.iterator()
            addEntityQueue.add(
                addEntityArgs(
                    iterator.next() as Any,
                    iterator.next() as DatabaseConstant.CollectionConstant,
                    iterator.next() as AdapterToDocumentInterface<Any>,
                )
            )
            Observable((nextBooleanFun.poll() ?: { true })())
        }
        When(
            mokeDatabaseInterface.addListEntity<Any>(
                anyOrNull(),
                anyOrNull(),
                anyOrNull()
            )
        ).thenAnswer {
            val iterator = it!!.arguments.iterator()
            addListEntityQueue.add(
                addListEntityArgs(
                    iterator.next() as List<Any>,
                    iterator.next() as DatabaseConstant.CollectionConstant,
                    iterator.next() as AdapterToDocumentInterface<in Any>
                )
            )
            Observable((nextPairStringListFun.poll() ?: { Pair(true, listOf()) })())
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
            Observable((nextBooleanFun.poll() ?: { true })())
        }
        When(
            mokeDatabaseInterface.setListEntity<Any>(
                anyOrNull(),
                anyOrNull(),
                anyOrNull()
            )
        ).thenAnswer {
            val iterator = it!!.arguments.iterator()
            setListEntityQueue.add(
                setListEntityArgs(
                    iterator.next() as List<Pair<String, Any?>>,
                    iterator.next() as DatabaseConstant.CollectionConstant,
                    iterator.next() as AdapterToDocumentInterface<in Any>
                )
            )
            Observable((nextBooleanFun.poll() ?: { true })())
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
            Observable((nextBooleanFun.poll() ?: { true })())
        }
        When(
            mokeDatabaseInterface.deleteListEntity(
                anyOrNull(),
                anyOrNull()
            )
        ).thenAnswer {
            val iterator = it!!.arguments.iterator()
            deleteListEntityQueue.add(
                deleteListEntityArgs(
                    iterator.next() as List<String>,
                    iterator.next() as DatabaseConstant.CollectionConstant,
                )
            )
            Observable((nextBooleanFun.poll() ?: { true })())
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
            Observable((nextBooleanFun.poll() ?: { true })())
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
            Observable((nextBooleanFun.poll() ?: { true })())
        }
        return mokeDatabaseInterface
    }
}