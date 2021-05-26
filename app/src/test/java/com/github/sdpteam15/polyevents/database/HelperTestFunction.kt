package com.github.sdpteam15.polyevents.database

import com.github.sdpteam15.polyevents.model.database.remote.DatabaseConstant
import com.github.sdpteam15.polyevents.model.database.remote.DatabaseInterface
import com.github.sdpteam15.polyevents.model.database.remote.matcher.Matcher
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

    class AddEntityAndGetIdArgs(
        val element: Any,
        val collection: DatabaseConstant.CollectionConstant,
        val adapter: AdapterToDocumentInterface<out Any> //'out E' is '? extends E' in java
    )
    private val nextAddEntityAndGetIdQueue: Queue<(AddEntityAndGetIdArgs) -> String> = LinkedList()
    fun nextAddEntityAndGetId(function: (AddEntityAndGetIdArgs) -> String) =
        nextAddEntityAndGetIdQueue.add(function)
    private val lastAddEntityAndGetIdQueue: Queue<AddEntityAndGetIdArgs> = LinkedList()
    fun lastAddEntityAndGetId() = lastAddEntityAndGetIdQueue.poll()

    class AddEntityArgs(
        val element: Any,
        val collection: DatabaseConstant.CollectionConstant,
        val adapter: AdapterToDocumentInterface<out Any> //'out E' is '? extends E' in java
    )
    private val nextAddEntityQueue: Queue<(AddEntityArgs) -> Boolean> = LinkedList()
    fun nextAddEntity(function: (AddEntityArgs) -> Boolean) = nextAddEntityQueue.add(function)
    private val lastAddEntityQueue: Queue<AddEntityArgs> = LinkedList()
    fun lastAddEntity() = lastAddEntityQueue.poll()

    class AddListEntityArgs(
        val elements: List<Any>,
        val collection: DatabaseConstant.CollectionConstant,
        val adapter: AdapterToDocumentInterface<in Any>
    )
    private val nextAddListEntityQueue: Queue<(AddListEntityArgs) -> Pair<Boolean, List<String>?>> = LinkedList()
    fun nextAddListEntity(function: (AddListEntityArgs) -> Pair<Boolean, List<String>?>) =
        nextAddListEntityQueue.add(function)
    private val lastAddListEntityQueue: Queue<AddListEntityArgs> = LinkedList()
    fun lastAddListEntity() = lastAddListEntityQueue.poll()

    class SetEntityArgs(
        val element: Any?,
        val id: String,
        val collection: DatabaseConstant.CollectionConstant,
        val adapter: AdapterToDocumentInterface<out Any>? //'out E' is '? extends E' in java
    )
    private val nextSetEntityQueue: Queue<(SetEntityArgs) -> Boolean> = LinkedList()
    fun nextSetEntity(function: (SetEntityArgs) -> Boolean) = nextSetEntityQueue.add(function)
    private val lastSetEntityQueue: Queue<SetEntityArgs> = LinkedList()
    fun lastSetEntity() = lastSetEntityQueue.poll()

    class SetListEntityArgs(
        val elements: List<Pair<String, Any?>>,
        val collection: DatabaseConstant.CollectionConstant,
        val adapter: AdapterToDocumentInterface<in Any>
    )
    private val nextSetListEntityQueue: Queue<(SetListEntityArgs) -> Boolean> = LinkedList()
    fun nextSetListEntity(function: (SetListEntityArgs) -> Boolean) =
        nextSetListEntityQueue.add(function)
    private val lastSetListEntityQueue: Queue<SetListEntityArgs> = LinkedList()
    fun lastSetListEntity() = lastSetListEntityQueue.poll()

    class DeleteEntityArgs(
        val id: String,
        val collection: DatabaseConstant.CollectionConstant
    )
    private val nextDeleteEntityQueue: Queue<(DeleteEntityArgs) -> Boolean> = LinkedList()
    fun nextDeleteEntity(function: (DeleteEntityArgs) -> Boolean) =
        nextDeleteEntityQueue.add(function)
    private val lastDeleteEntityQueue: Queue<DeleteEntityArgs> = LinkedList()
    fun lastDeleteEntity() = lastDeleteEntityQueue.poll()

    class DeleteListEntityArgs(
        val ids: List<String>,
        val collection: DatabaseConstant.CollectionConstant
    )
    private val nextDeleteListEntityQueue: Queue<(DeleteListEntityArgs) -> Boolean> = LinkedList()
    fun nextDeleteListEntity(function: (DeleteListEntityArgs) -> Boolean) =
        nextDeleteListEntityQueue.add(function)
    private val lastDeleteListEntityQueue: Queue<DeleteListEntityArgs> = LinkedList()
    fun lastDeleteListEntity() = lastDeleteListEntityQueue.poll()

    class GetEntityArgs(
        val element: Observable<in Any>,
        val id: String,
        val collection: DatabaseConstant.CollectionConstant,
        val adapter: AdapterFromDocumentInterface<in Any>? //'in E' is '? super E' in java
    )
    private val nextGetEntityQueue: Queue<(GetEntityArgs) -> Boolean> = LinkedList()
    fun nextGetEntity(function: (GetEntityArgs) -> Boolean) = nextGetEntityQueue.add(function)
    private val lastGetEntityQueue: Queue<GetEntityArgs> = LinkedList()
    fun lastGetEntity() = lastGetEntityQueue.poll()

    class GetListEntityArgs(
        val element: ObservableList<in Any>,
        val ids: MutableList<String>?,
        val matcher: Matcher?,
        val collection: DatabaseConstant.CollectionConstant,
        val adapter: AdapterFromDocumentInterface<in Any>? //'in E' is '? super E' in java
    )
    private val nextGetListEntityQueue: Queue<(GetListEntityArgs) -> Boolean> = LinkedList()
    fun nextGetListEntity(function: (GetListEntityArgs) -> Boolean) =
        nextGetListEntityQueue.add(function)
    private val lastGetListEntityQueue: Queue<GetListEntityArgs> = LinkedList()
    fun lastGetListEntity() = lastGetListEntityQueue.poll()

    fun clearQueue() {
        (nextAddEntityAndGetIdQueue as LinkedList).clear()
        (lastAddEntityAndGetIdQueue as LinkedList).clear()
        (nextAddEntityQueue as LinkedList).clear()
        (lastAddEntityQueue as LinkedList).clear()
        (nextAddListEntityQueue as LinkedList).clear()
        (lastAddListEntityQueue as LinkedList).clear()
        (nextSetEntityQueue as LinkedList).clear()
        (lastSetEntityQueue as LinkedList).clear()
        (nextSetListEntityQueue as LinkedList).clear()
        (lastSetListEntityQueue as LinkedList).clear()
        (nextDeleteEntityQueue as LinkedList).clear()
        (lastDeleteEntityQueue as LinkedList).clear()
        (nextDeleteListEntityQueue as LinkedList).clear()
        (lastDeleteListEntityQueue as LinkedList).clear()
        (nextGetEntityQueue as LinkedList).clear()
        (lastGetEntityQueue as LinkedList).clear()
        (nextGetListEntityQueue as LinkedList).clear()
        (lastGetListEntityQueue as LinkedList).clear()
    }

    fun mockDatabaseInterface(): DatabaseInterface {
        val mokeDatabaseInterface = Mockito.mock(DatabaseInterface::class.java)
        When(
            mokeDatabaseInterface.addEntityAndGetId(
                anyOrNull(),
                anyOrNull(),
                anyOrNull()
            )
        ).thenAnswer {
            val iterator = it!!.arguments.iterator()
            lastAddEntityAndGetIdQueue.add(
                AddEntityAndGetIdArgs(
                    iterator.next() as Any,
                    iterator.next() as DatabaseConstant.CollectionConstant,
                    iterator.next() as AdapterToDocumentInterface<Any>,
                )
            )
            Observable(
                (nextAddEntityAndGetIdQueue.poll() ?: { "" })(lastAddEntityAndGetIdQueue.peek()!!)
            )
        }
        When(
            mokeDatabaseInterface.addEntity(
                anyOrNull(),
                anyOrNull(),
                anyOrNull()
            )
        ).thenAnswer {
            val iterator = it!!.arguments.iterator()
            lastAddEntityQueue.add(
                AddEntityArgs(
                    iterator.next() as Any,
                    iterator.next() as DatabaseConstant.CollectionConstant,
                    iterator.next() as AdapterToDocumentInterface<Any>,
                )
            )
            Observable((nextAddEntityQueue.poll() ?: { true })(lastAddEntityQueue.peek()!!))
        }
        When(
            mokeDatabaseInterface.addListEntity<Any>(
                anyOrNull(),
                anyOrNull(),
                anyOrNull()
            )
        ).thenAnswer {
            val iterator = it!!.arguments.iterator()
            lastAddListEntityQueue.add(
                AddListEntityArgs(
                    iterator.next() as List<Any>,
                    iterator.next() as DatabaseConstant.CollectionConstant,
                    iterator.next() as AdapterToDocumentInterface<in Any>
                )
            )
            Observable((nextAddListEntityQueue.poll() ?: { Pair(true, listOf<String>()) })(lastAddListEntityQueue.peek()!!))
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
            lastSetEntityQueue.add(
                SetEntityArgs(
                    iterator.next() as Any,
                    iterator.next() as String,
                    iterator.next() as DatabaseConstant.CollectionConstant,
                    iterator.next() as AdapterToDocumentInterface<Any>?,
                )
            )
            Observable((nextSetEntityQueue.poll() ?: { true })(lastSetEntityQueue.peek()!!))
        }
        When(
            mokeDatabaseInterface.setListEntity<Any>(
                anyOrNull(),
                anyOrNull(),
                anyOrNull()
            )
        ).thenAnswer {
            val iterator = it!!.arguments.iterator()
            lastSetListEntityQueue.add(
                SetListEntityArgs(
                    iterator.next() as List<Pair<String, Any?>>,
                    iterator.next() as DatabaseConstant.CollectionConstant,
                    iterator.next() as AdapterToDocumentInterface<in Any>
                )
            )
            Observable((nextSetListEntityQueue.poll() ?: { true })(lastSetListEntityQueue.peek()!!))
        }
        When(
            mokeDatabaseInterface.deleteEntity(
                anyOrNull(),
                anyOrNull()
            )
        ).thenAnswer {
            val iterator = it!!.arguments.iterator()
            lastDeleteEntityQueue.add(
                DeleteEntityArgs(
                    iterator.next() as String,
                    iterator.next() as DatabaseConstant.CollectionConstant,
                )
            )
            Observable((nextDeleteEntityQueue.poll() ?: { true })(lastDeleteEntityQueue.peek()!!))
        }
        When(
            mokeDatabaseInterface.deleteListEntity(
                anyOrNull(),
                anyOrNull()
            )
        ).thenAnswer {
            val iterator = it!!.arguments.iterator()
            lastDeleteListEntityQueue.add(
                DeleteListEntityArgs(
                    iterator.next() as List<String>,
                    iterator.next() as DatabaseConstant.CollectionConstant,
                )
            )
            Observable((nextDeleteListEntityQueue.poll() ?: { true })(lastDeleteListEntityQueue.peek()!!))
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
            lastGetEntityQueue.add(
                GetEntityArgs(
                    iterator.next() as Observable<Any>,
                    iterator.next() as String,
                    iterator.next() as DatabaseConstant.CollectionConstant,
                    iterator.next() as AdapterFromDocumentInterface<Any>?,
                )
            )
            Observable((nextGetEntityQueue.poll() ?: { true })(lastGetEntityQueue.peek()!!))
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
            lastGetListEntityQueue.add(
                GetListEntityArgs(
                    iterator.next() as ObservableList<Any>,
                    iterator.next() as MutableList<String>?,
                    iterator.next() as Matcher?,
                    iterator.next() as DatabaseConstant.CollectionConstant,
                    iterator.next() as AdapterFromDocumentInterface<Any>?,
                )
            )
            Observable((nextGetListEntityQueue.poll() ?: { true })(lastGetListEntityQueue.peek()!!))
        }
        return mokeDatabaseInterface
    }
}