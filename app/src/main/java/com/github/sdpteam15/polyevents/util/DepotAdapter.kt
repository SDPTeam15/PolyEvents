package com.github.sdpteam15.polyevents.util

import com.github.sdpteam15.polyevents.model.Depot
import com.github.sdpteam15.polyevents.model.Item
import com.google.firebase.firestore.DocumentSnapshot

/**
 * A class for converting between the Depot in our code and
 * documents in the Firebase database. Not unlike the conversion to
 * DTO (Data transfer object) concept. Special care is given here since
 * Depot is supposed to be a central depot hence a singleton.
 */
class DepotAdapter {
    companion object {
        fun depotToDocument(depot: Depot) =
                hashMapOf("inventory" to depot.inventory.mapKeys { it.key.itemName })

        fun depotDocumentToDepot(document: DocumentSnapshot) = {
            val currentInventory: MutableMap<Item, Int> =
                    (document.get("inventory") as MutableMap<String, Int>)
                            .mapKeys { Item.valueOf(it.key) }.toMutableMap()
            Depot.inventory += currentInventory
        }

    }
}