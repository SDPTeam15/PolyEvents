package com.github.sdpteam15.polyevents.helper

import com.github.sdpteam15.polyevents.model.database.remote.Database.currentDatabase
import com.github.sdpteam15.polyevents.model.database.remote.DatabaseConstant
import com.github.sdpteam15.polyevents.model.entity.Event
import com.github.sdpteam15.polyevents.model.entity.Item
import com.github.sdpteam15.polyevents.model.entity.MaterialRequest
import com.github.sdpteam15.polyevents.model.entity.MaterialRequest.Status.*
import com.github.sdpteam15.polyevents.model.entity.Zone
import com.github.sdpteam15.polyevents.model.observable.Observable
import com.github.sdpteam15.polyevents.model.observable.ObservableList


object DatabaseHelper {

    fun deleteEvent(e: Event) {
        val materialRequests = ObservableList<MaterialRequest>()
        materialRequests.observeAdd {
            val request = it.value

            when (request.status) {
                PENDING, REFUSED -> updateMaterialRequestStatus(request, CANCELED)
                ACCEPTED ->
                    cancelMaterialRequest(request)
                DELIVERING -> updateMaterialRequestStatus(request, RETURNING)
                DELIVERED -> updateMaterialRequestStatus(request, RETURN_REQUESTED)
                CANCELED, RETURNING, RETURNED, RETURN_REQUESTED -> Unit
            }

        }

        currentDatabase.materialRequestDatabase!!.getMaterialRequestList(materialRequests) {
            it.whereEqualTo(
                DatabaseConstant.MaterialRequestConstant.MATERIAL_REQUEST_EVENT_ID.value,
                e.eventId!!
            )
        }.observeOnce {
            if (it.value) {
                deleteEventEdit(e).observeOnce { currentDatabase.eventDatabase!!.removeEvent(e.eventId!!) }
            }
        }
    }

    fun deleteZone(zone: Zone) {
        val events = ObservableList<Event>()
        events.observeAdd { deleteEvent(it.value) }
        //TODO REMOVE ROUTENODES ROUTEEDGES
        currentDatabase.eventDatabase!!.getEvents({
            it.whereEqualTo(
                DatabaseConstant.EventConstant.EVENT_ZONE_ID.value,
                zone.zoneId!!
            )
        }, null, events)
        currentDatabase.zoneDatabase!!.deleteZone(zone)
    }

    private fun deleteEventEdit(e: Event): Observable<Boolean> {
        val eventEdit = ObservableList<Event>()
        eventEdit.observeAdd {
            currentDatabase.eventDatabase!!.updateEventEdit(it.value.copy(status = Event.EventStatus.CANCELED))
        }
        return currentDatabase.eventDatabase!!.getEventEdits({
            it.whereEqualTo(
                DatabaseConstant.EventConstant.EVENT_DOCUMENT_ID.value,
                e.eventId!!
            )
        }, eventEdit)

    }

    fun cancelMaterialRequest(materialRequest: MaterialRequest) {
        val items = ObservableList<Triple<Item, Int, Int>>()
        items.observeAdd {
            val item =
                it.value.copy(third = it.value.third + materialRequest.items[it.value.first.itemId]!!)
            currentDatabase.itemDatabase!!.updateItem(item.first, item.second, item.third)
        }
        currentDatabase.itemDatabase!!.getItemsList(
            items,
            null,
            materialRequest.items.map { it.key }).observeOnce {
            if (it.value) {
                updateMaterialRequestStatus(materialRequest, CANCELED)
            }
        }

    }

    fun updateMaterialRequestStatus(
        materialRequest: MaterialRequest,
        status: MaterialRequest.Status
    ) {
        currentDatabase.materialRequestDatabase!!.updateMaterialRequest(
            materialRequest.requestId!!,
            materialRequest.copy(
                status = status, staffInChargeId = if (status == RETURN_REQUESTED) {
                    null
                } else {
                    materialRequest.staffInChargeId
                }
            )

        )
    }

}