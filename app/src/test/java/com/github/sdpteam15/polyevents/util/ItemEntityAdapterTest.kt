package com.github.sdpteam15.polyevents.util

import com.github.sdpteam15.polyevents.database.DatabaseConstant
import com.github.sdpteam15.polyevents.model.Item
import com.github.sdpteam15.polyevents.model.ItemType
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class ItemEntityAdapterTest {
    val itemId = "micro1"
    val itemType = ItemType.MICROPHONE

    lateinit var itemEntity: Item
    lateinit var document: HashMap<String, Any?>

    @Before
    fun setupItem() {
        itemEntity = Item(itemId, itemType)
        document = ItemEntityAdapter.toItemDocument(itemEntity)
    }

    @Test
    fun conversionOfDocumentToItemEntityPreservesData() {
        assertEquals(document[DatabaseConstant.ITEM_TYPE], itemEntity.itemType.itemtype)
        assertEquals(document[DatabaseConstant.ITEM_DOCUMENT_ID], itemEntity.itemId)
    }

    @Test
    fun conversionOfItemEntityToDocumentPreservesData() {
        val itemDocumentData: HashMap<String, Any?> = hashMapOf(
            DatabaseConstant.ITEM_DOCUMENT_ID to itemId,
            DatabaseConstant.ITEM_TYPE to itemType
        )
        val obtainedEvent = ItemEntityAdapter.toItemEntity(itemDocumentData)

        assertEquals(obtainedEvent, itemEntity)
    }
}