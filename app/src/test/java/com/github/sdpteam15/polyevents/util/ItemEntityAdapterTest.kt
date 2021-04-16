package com.github.sdpteam15.polyevents.util

import com.github.sdpteam15.polyevents.database.DatabaseConstant
import com.github.sdpteam15.polyevents.model.Item
import com.github.sdpteam15.polyevents.model.ItemType
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class ItemEntityAdapterTest {
    val itemId = "micro1"
    val itemName = "Microphone Elgato"
    val itemType = ItemType.MICROPHONE
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
            DatabaseConstant.ITEM_NAME to itemName,
            DatabaseConstant.ITEM_DOCUMENT_ID to itemId,
            DatabaseConstant.ITEM_TYPE to itemType.name,
            DatabaseConstant.ITEM_COUNT to itemQuantity.toLong()
        )
        val obtainedItem = ItemEntityAdapter.toItemEntity(itemDocumentData, itemId)

        assertEquals(obtainedItem.first, itemEntity)
        assertEquals(obtainedItem.second, itemQuantity)
    }

    @Test
    fun conversionOfItemEntityToDocumentPreservesData() {
        assertEquals(
            ItemType.valueOf(
                document[DatabaseConstant.ITEM_TYPE] as String
            ), itemEntity.itemType
        )
        assertEquals(document[DatabaseConstant.ITEM_DOCUMENT_ID], itemEntity.itemId)
        assertEquals(document[DatabaseConstant.ITEM_COUNT], itemQuantity)
        assertEquals(document[DatabaseConstant.ITEM_NAME], itemName)
    }

    @Test
    fun conversionOfItemEntityToDocumentPreservesDataDoesntAddAMissingId() {
        val itemEntity2 = Item(
            itemId = null,
            itemName = itemName,
            itemType = itemType
        )
        document = ItemEntityAdapter.toItemDocument(itemEntity2, itemQuantity)

        assertEquals(
            ItemType.valueOf(
                document[DatabaseConstant.ITEM_TYPE] as String
            ), itemEntity.itemType
        )
        assertEquals(document[DatabaseConstant.ITEM_COUNT], itemQuantity)
        assertEquals(document[DatabaseConstant.ITEM_NAME], itemName)
        assert(!document.containsKey(DatabaseConstant.ITEM_DOCUMENT_ID))
    }
}