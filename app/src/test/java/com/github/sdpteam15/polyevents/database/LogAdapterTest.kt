package com.github.sdpteam15.polyevents.database

import com.github.sdpteam15.polyevents.helper.HelperFunctions.apply
import com.github.sdpteam15.polyevents.model.database.local.adapter.LogAdapter
import com.github.sdpteam15.polyevents.model.database.local.adapter.LogAdapterFromDocument
import com.github.sdpteam15.polyevents.model.database.local.adapter.LogAdapterToDocument
import com.github.sdpteam15.polyevents.model.database.remote.DatabaseConstant.CollectionConstant.TEST_COLLECTION
import com.github.sdpteam15.polyevents.model.database.remote.StringWithID
import com.github.sdpteam15.polyevents.model.database.remote.adapter.AdapterFromDocumentInterface
import com.github.sdpteam15.polyevents.model.database.remote.adapter.AdapterToDocumentInterface
import com.github.sdpteam15.polyevents.view.PolyEventsApplication
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@Suppress("UNCHECKED_CAST")
class LogAdapterTest {
    @Before
    fun setup() {
        PolyEventsApplication.inTest = true
    }

    @Test
    fun document() {
        val adapterToDocument =
            LogAdapterToDocument(TEST_COLLECTION.adapter as AdapterToDocumentInterface<StringWithID>)
        val adapterFromDocument =
            LogAdapterFromDocument(TEST_COLLECTION.adapter as AdapterFromDocumentInterface<StringWithID>)

        val value1 = StringWithID("id", "string")
        val value2: StringWithID? = null

        val document1 = adapterToDocument.toDocument(value1)
        assertEquals(true, document1.apply { it[LogAdapter.IS_VALID] })
        assertNotNull(document1.apply { it[LogAdapter.LAST_UPDATE] })


        val document2 = adapterToDocument.toDocument(value2)
        assertEquals(false, document2.apply { it[LogAdapter.IS_VALID] })
        assertNotNull(document2.apply { it[LogAdapter.LAST_UPDATE] })

        assertEquals(value1.toString(), adapterFromDocument.fromDocument(document1, "id")?.toString())
        assertNull(adapterFromDocument.fromDocument(document2, ""))
    }
}