package com.github.sdpteam15.polyevents.model.database.remote.adapter

import com.github.sdpteam15.polyevents.model.database.remote.DatabaseConstant.EventEditConstant.EVENT_EDIT_STATUS
import com.github.sdpteam15.polyevents.model.entity.Event

object EventEditAdapter : AdapterInterface<Event> {
    override fun toDocument(element: Event): HashMap<String, Any?> {
        val map = EventAdapter.toDocument(element)
        map[EVENT_EDIT_STATUS.value] = element.status!!.ordinal
        return map
    }

    override fun fromDocument(document: MutableMap<String, Any?>, id: String): Event {
        val event = fromDocument(document, id)
        event.status = Event.EventStatus.fromOrdinal(
            ((document[EVENT_EDIT_STATUS.value] ?: 0) as Long).toInt()
        )!!
        return event
    }
}