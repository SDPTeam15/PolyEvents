package com.github.sdpteam15.polyevents.model

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class ItemTest {
    lateinit var testItem: Item

    val itemId = "micro1"
    val itemType = ItemType.MICROPHONE

    @Before
    fun setup() {
        testItem = Item(itemId = itemId, itemType = itemType)
    }

    @Test
    fun testItemCorrectlyConstructed() {
        assertEquals(testItem.itemId, itemId)
        assertEquals(testItem.itemType, itemType)
    }
}