package com.github.sdpteam15.polyevents.model.database.remote.adapter

import com.github.sdpteam15.polyevents.model.database.remote.DatabaseConstant
import com.github.sdpteam15.polyevents.model.database.remote.DatabaseConstant.EventEditConstant.EVENT_EDIT_STATUS
import com.github.sdpteam15.polyevents.model.entity.Event

object EventEditAdapter : AdapterInterface<Event> {
    override fun toDocument(element: Event): HashMap<String, Any?> {
        val map = EventAdapter.toDocument(element)
        map[EVENT_EDIT_STATUS.value] = element.status!!.ordinal
        map[DatabaseConstant.EventEditConstant.EVENT_EDIT_ADMIN_MESSAGE.value] = element.adminMessage
        map[DatabaseConstant.EventConstant.EVENT_DOCUMENT_ID.value] = element.eventId
        return map
    }

    override fun fromDocument(document: MutableMap<String, Any?>, id: String): Event {
        val event = EventAdapter.fromDocument(document, id)
        event.eventId = document[DatabaseConstant.EventConstant.EVENT_DOCUMENT_ID.value] as String?

        event.status = Event.EventStatus.fromOrdinal(
            ((document[EVENT_EDIT_STATUS.value] ?: 0) as Long).toInt()
        )!!

        event.adminMessage = (document[DatabaseConstant.EventEditConstant.EVENT_EDIT_ADMIN_MESSAGE.value] as String?)
        return event
    }
}