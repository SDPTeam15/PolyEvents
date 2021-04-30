package com.github.sdpteam15.polyevents.database.adapter


import com.github.sdpteam15.polyevents.model.database.remote.DatabaseConstant.MaterialRequestConstant.*
import com.github.sdpteam15.polyevents.model.database.remote.adapter.MaterialRequestAdapter
import com.github.sdpteam15.polyevents.model.entity.MaterialRequest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime

class MaterialRequestAdapterTest {
    val matId = "m1"
    val items = mutableMapOf(Pair("i1", 1), Pair("i2", 3), Pair("i3", 6))

    val time = LocalDateTime.now()
    val userid = "u1"

    lateinit var materialRequest: MaterialRequest

    @Before
    fun setupEvent() {
        materialRequest = MaterialRequest(
            matId,
            items,
            time,
            userid
        )
    }

    @Test
    fun conversionOfMatReqToDocumentPreservesData() {
        val document = MaterialRequestAdapter.toDocument(materialRequest)
        assertEquals(document[MATERIAL_REQUEST_LIST.value], materialRequest.items)

        assertEquals(document[MATERIAL_REQUEST_USER_ID.value], materialRequest.userId)

    }

    @Test
    fun conversionOfDocumentToEventPreservesData() {


        val matReqDocumentData: HashMap<String, Any?> = hashMapOf(
            MATERIAL_REQUEST_ID.value to materialRequest.requestId,
            MATERIAL_REQUEST_LIST.value to materialRequest.items,
            MATERIAL_REQUEST_USER_ID.value to materialRequest.userId,
        )

        val matReqWithoutDate = materialRequest.copy(time = null)
        val obtainedEvent =
            MaterialRequestAdapter.fromDocument(matReqDocumentData, materialRequest.requestId!!)

        assertEquals(matReqWithoutDate, obtainedEvent)
    }

}