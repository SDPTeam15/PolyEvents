package com.github.sdpteam15.polyevents.helper

import android.content.Context
import android.provider.Settings.Global.getString
import androidx.lifecycle.LifecycleOwner
import com.github.sdpteam15.polyevents.R
import com.github.sdpteam15.polyevents.model.database.remote.Database
import com.github.sdpteam15.polyevents.model.database.remote.Database.currentDatabase
import com.github.sdpteam15.polyevents.model.database.remote.DatabaseConstant
import com.github.sdpteam15.polyevents.model.entity.*
import com.github.sdpteam15.polyevents.model.entity.MaterialRequest.Status.*
import com.github.sdpteam15.polyevents.model.observable.Observable
import com.github.sdpteam15.polyevents.model.observable.ObservableList
import com.github.sdpteam15.polyevents.model.observable.ObservableMap

/**
 * The goal of this objects is to create function to nested relation from the database
 */
object DatabaseHelper {

    /**
     * Delete an event and all its material request and event edits from the database
     * @param e the event we want to delete
     */
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

        currentDatabase.materialRequestDatabase.getMaterialRequestList(materialRequests) {
            it.whereEqualTo(
                DatabaseConstant.MaterialRequestConstant.MATERIAL_REQUEST_EVENT_ID.value,
                e.eventId!!
            )
        }.observeOnce {
            if (it.value) {
                deleteEventEdit(e).observeOnce { currentDatabase.eventDatabase.removeEvent(e.eventId!!) }

            }
        }
    }

    /**
     * Delete a zone and all its associated event from the database
     * @param zone the zone we want to delete
     */
    fun deleteZone(zone: Zone) {
        val events = ObservableList<Event>()
        events.observeAdd { deleteEvent(it.value) }
        currentDatabase.routeDatabase.removeEdgeConnectedToZone(zone)
        currentDatabase.eventDatabase.getEvents({
            it.whereEqualTo(
                DatabaseConstant.EventConstant.EVENT_ZONE_ID.value,
                zone.zoneId!!
            )
        }, null, events)
        currentDatabase.zoneDatabase.updateZoneInformation(zone.zoneId!!,zone.copy(status = Zone.Status.DELETED))
    }

    /**
     * Cancel all event edits of the given event in the database
     * @param e The event from which we want to cancel all event edits
     */
    private fun deleteEventEdit(e: Event): Observable<Boolean> {
        val eventEdit = ObservableList<Event>()
        eventEdit.observeAdd {
            currentDatabase.eventDatabase.updateEventEdit(it.value.copy(status = Event.EventStatus.CANCELED))
        }
        return currentDatabase.eventDatabase.getEventEdits({
            it.whereEqualTo(
                DatabaseConstant.EventConstant.EVENT_DOCUMENT_ID.value,
                e.eventId!!
            )
        }, eventEdit)
    }

    /**
     * Cancel a material request and give back items
     * @param materialRequest The material requet we want to cancel
     */
    fun cancelMaterialRequest(materialRequest: MaterialRequest) {
        val items = ObservableList<Triple<Item, Int, Int>>()
        items.observeAdd {
            val item =
                it.value.copy(third = it.value.third + materialRequest.items[it.value.first.itemId]!!)
            currentDatabase.itemDatabase.updateItem(item.first, item.second, item.third)
        }
        currentDatabase.itemDatabase.getItemsList(
            items,
            null,
            materialRequest.items.map { it.key }).observeOnce {
            if (it.value) {
                updateMaterialRequestStatus(materialRequest, CANCELED)
            }
        }
    }

    /**
     * Update the material to the given status
     * @param materialRequest the material request we want to update
     * @param status the new status of the material request
     */
    private fun updateMaterialRequestStatus(
        materialRequest: MaterialRequest,
        status: MaterialRequest.Status
    ) {
        currentDatabase.materialRequestDatabase.updateMaterialRequest(
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

    /**
     * Gets the username of the given userId from the database and adds it to the userNames map
     * @param userId user Id to retrieve
     * @param users the map from userId to user name
     * @param lifecycleOwner the lifecycleOwner for the observable
     * @param context the current context
     */
    fun addToUsersFromDB(userId: String, users : ObservableMap<String, String>, lifecycleOwner: LifecycleOwner, context: Context) {
        val tempUsers = Observable<UserEntity>()
        currentDatabase.userDatabase.getUserInformation(tempUsers, userId)
            .observeOnce(lifecycleOwner) { ans ->
                if (ans.value) {
                    users[userId] = tempUsers.value?.name ?: "UNKNOWN"
                } else {
                    HelperFunctions.showToast(
                        context.getString(R.string.failed_to_get_username_from_database),
                        context
                    )
                }
            }
    }
}