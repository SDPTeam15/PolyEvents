package com.github.sdpteam15.polyevents.util

import com.github.sdpteam15.polyevents.database.DatabaseConstant.ItemConstants.*
import com.github.sdpteam15.polyevents.model.Item
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class ItemEntityAdapterTest {
    val itemId = "micro1"
    val itemName = "Microphone Elgato"
    val itemType = "MICROPHONE"
    val itemQuantity = 5

    lateinit var itemEntity: Item
    lateinit var document: HashMap<String, Any?>

    @Before
    fun setupItem() {
        itemEntity = Item(
            itemId = itemId,
            itemName = itemName,
            itemType = itemType
        )
        document = ItemEntityAdapter.toItemDocument(itemEntity, itemQuantity)
    }

    @Test
    fun conversionOfDocumentToItemEntityPreservesData() {

        val itemDocumentData: HashMap<String, Any?> = hashMapOf(
            ITEM_NAME.value to itemName,
            ITEM_DOCUMENT_ID.value to itemId,
            ITEM_TYPE.value to itemType,
            ITEM_COUNT.value to itemQuantity.toLong()
        )
        val obtainedItem = ItemEntityAdapter.toItemEntity(itemDocumentData, itemId)

        assertEquals(obtainedItem.first, itemEntity)
        assertEquals(obtainedItem.second, itemQuantity)
    }

    @Test
    fun conversionOfItemEntityToDocumentPreservesData() {
        assertEquals(document[ITEM_TYPE.value] as String, itemEntity.itemType)
        assertEquals(document[ITEM_DOCUMENT_ID.value], itemEntity.itemId)
        assertEquals(document[ITEM_COUNT.value], itemQuantity)
        assertEquals(document[ITEM_NAME.value], itemName)
    }

    @Test
    fun conversionOfItemEntityToDocumentPreservesDataDoesntAddAMissingId() {
        val itemEntity2 = Item(
            itemId = null,
            itemName = itemName,
            itemType = itemType
        )
        document = ItemEntityAdapter.toItemDocument(itemEntity2, itemQuantity)

        assertEquals(document[ITEM_TYPE.value] as String, itemEntity.itemType)
        assertEquals(document[ITEM_COUNT.value], itemQuantity)
        assertEquals(document[ITEM_NAME.value], itemName)
        assert(!document.containsKey(ITEM_DOCUMENT_ID.value))
    }
}