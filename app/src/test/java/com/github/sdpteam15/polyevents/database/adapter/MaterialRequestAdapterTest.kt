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
    val eventid = "e1"
    val staffid = "u2"
    lateinit var materialRequest: MaterialRequest

    @Before
    fun setupEvent() {
        materialRequest = MaterialRequest(
            matId,
            items,
            time,
            userid,
            eventid,
            MaterialRequest.Status.PENDING,
            "",
            staffid
        )
    }

    @Test
    fun conversionOfMatReqToDocumentPreservesData() {
        val document = MaterialRequestAdapter.toDocument(materialRequest)
        assertEquals(document[MATERIAL_REQUEST_LIST.value], materialRequest.items)
        assertEquals(document[MATERIAL_REQUEST_ADMIN_MESSAGE.value], materialRequest.adminMessage)
        assertEquals(document[MATERIAL_REQUEST_USER_ID.value], materialRequest.userId)
        assertEquals(document[MATERIAL_REQUEST_STAFF_IN_CHARGE.value], materialRequest.staffInChargeId )
        assertEquals(document[MATERIAL_REQUEST_STATUS.value], materialRequest.status.ordinal)
    }

    @Test
    fun conversionOfDocumentToEventPreservesData() {


        val matReqDocumentData: HashMap<String, Any?> = hashMapOf(
            MATERIAL_REQUEST_ID.value to materialRequest.requestId,
            MATERIAL_REQUEST_LIST.value to materialRequest.items,
            MATERIAL_REQUEST_USER_ID.value to materialRequest.userId,
            MATERIAL_REQUEST_STATUS.value to materialRequest.status.ordinal.toLong(),
            MATERIAL_REQUEST_ADMIN_MESSAGE.value to materialRequest.adminMessage,
            MATERIAL_REQUEST_STAFF_IN_CHARGE.value to materialRequest.staffInChargeId,
            MATERIAL_REQUEST_EVENT_ID.value to materialRequest.eventId
        )

        val matReqWithoutDate = materialRequest.copy(time = null)
        val obtainedEvent =
            MaterialRequestAdapter.fromDocument(matReqDocumentData, materialRequest.requestId!!)

        assertEquals(matReqWithoutDate, obtainedEvent)
    }

}