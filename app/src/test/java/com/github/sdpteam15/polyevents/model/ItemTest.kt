package com.github.sdpteam15.polyevents.model

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class ItemTest {
    lateinit var testItem: Item

    val itemName = "micro1"
    val itemType = ItemType.MICROPHONE

    @Before
    fun setup() {
        testItem = Item(itemId = null, itemName = itemName, itemType = itemType)
    }

    @Test
    fun testItemCorrectlyConstructed() {
        assertEquals(testItem.itemName, itemName)
        assertEquals(testItem.itemType, itemType)
    }
}