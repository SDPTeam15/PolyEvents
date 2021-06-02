package com.github.sdpteam15.polyevents.database.adapter

import com.github.sdpteam15.polyevents.model.database.remote.DatabaseConstant.ItemConstants.*
import com.github.sdpteam15.polyevents.model.database.remote.adapter.ItemEntityAdapter
import com.github.sdpteam15.polyevents.model.entity.Item
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class ItemEntityAdapterTest {
    val itemId = "micro1"
    val itemName = "Microphone Elgato"
    val itemType = "MICROPHONE"
    val itemQuantity = 5
    val itemRemainingQuantity = 4

    lateinit var itemEntity: Item
    lateinit var document: HashMap<String, Any?>

    @Before
    fun setupItem() {
        itemEntity = Item(
            itemId = itemId,
            itemName = itemName,
            itemType = itemType
        )
        document = ItemEntityAdapter.toItemDocument(itemEntity, itemQuantity, itemRemainingQuantity)
    }

    @Test
    fun conversionOfDocumentToItemEntityPreservesData() {

        val itemDocumentData: HashMap<String, Any?> = hashMapOf(
            ITEM_NAME.value to itemName,
            ITEM_TYPE.value to itemType,
            ITEM_TOTAL.value to itemQuantity.toLong(),
            ITEM_REMAINING.value to itemRemainingQuantity.toLong()
        )
        val obtainedItem = ItemEntityAdapter.fromDocument(itemDocumentData, itemId)

        assertEquals(obtainedItem.first, itemEntity)
        assertEquals(obtainedItem.second, itemQuantity)
        assertEquals(obtainedItem.third, itemRemainingQuantity)
    }

    @Test
    fun conversionOfItemEntityToDocumentPreservesData() {
        assertEquals(document[ITEM_TYPE.value] as String, itemEntity.itemType)
        assertEquals(document[ITEM_TOTAL.value], itemQuantity)
        assertEquals(document[ITEM_NAME.value], itemName)
        assertEquals(document[ITEM_REMAINING.value], itemRemainingQuantity)
    }

    @Test
    fun conversionOfItemEntityToDocumentPreservesDataDoesntAddAMissingId() {
        val itemEntity2 = Item(
            itemId = null,
            itemName = itemName,
            itemType = itemType
        )
        document =
            ItemEntityAdapter.toItemDocument(itemEntity2, itemQuantity, itemRemainingQuantity)

        assertEquals(document[ITEM_TYPE.value] as String, itemEntity.itemType)
        assertEquals(document[ITEM_TOTAL.value], itemQuantity)
        assertEquals(document[ITEM_NAME.value], itemName)
        assertEquals(document[ITEM_REMAINING.value], itemRemainingQuantity)
        assert(!document.containsKey(ITEM_DOCUMENT_ID.value))
    }
}