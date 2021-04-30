package com.github.sdpteam15.polyevents.model.database.remote.objects

import com.github.sdpteam15.polyevents.model.database.remote.Database
import com.github.sdpteam15.polyevents.model.entity.Item
import com.github.sdpteam15.polyevents.model.entity.UserEntity
import com.github.sdpteam15.polyevents.model.entity.UserProfile
import com.github.sdpteam15.polyevents.model.observable.Observable
import com.github.sdpteam15.polyevents.model.observable.ObservableList

interface ItemDatabaseInterface {
    val currentUser: UserEntity?
        get() = Database.currentDatabase.currentUser
    val currentProfile: UserProfile?
        get() = Database.currentDatabase.currentProfile

    /**
     * create a new Item
     * @param item item we want to add in the database
     * @param userAccess the user profile to use its permission
     * @return An observer that will be set to true if the communication with the DB is over and no error
     */
    fun createItem(
        item: Item,
        count: Int,
        userAccess: UserProfile? = currentProfile
    ): Observable<Boolean>

    /**
     * @param itemId id of the item we want to remove from the database
     * @param userAccess the user profile to use its permission
     * @return An observer that will be set to true if the communication with the DB is over and no error
     */
    fun removeItem(
        itemId: String,
        userAccess: UserProfile? = currentProfile
    ): Observable<Boolean>

    /**
     * @param item item we want to update in the database
     * @param userAccess the user profile to use its permission
     * @return An observer that will be set to true if the communication with the DB is over and no error
     */
    fun updateItem(
        item: Item,
        count: Int,
        userAccess: UserProfile? = currentProfile
    ): Observable<Boolean>

    /**
     * Get list of items
     * @param itemList the list of items that will be set when the DB returns the information
     * @param userAccess profile for database access
     * @return An observer that will be set to true if the communication with the DB is over and no error
     */
    fun getItemsList(
        itemList: ObservableList<Pair<Item, Int>>,
        userAccess: UserProfile? = currentProfile
    ): Observable<Boolean>

    /**
     * Get list of available items
     * @param itemList the list of items that will be set when the DB returns the information
     * @param userAccess profile for database access
     * @return An observer that will be set to true if the communication with the DB is over and no error
     */
    fun getAvailableItems(
        itemList: ObservableList<Pair<Item, Int>>,
        userAccess: UserProfile? = currentProfile
    ): Observable<Boolean>

    /**
     * create a new Item Type
     * @param itemType item type we want to add in the database
     * @param userAccess the user profile to use its permission
     * @return An observer that will be set to true if the communication with the DB is over and no error
     */
    fun createItemType(
        itemType: String,
        userAccess: UserProfile? = currentProfile
    ): Observable<Boolean>

    /**
     * Get list of existing items types
     * @param itemTypeList the list of item types that will be set when the DB returns the information
     * @param userAccess profile for database access
     * @return An observer that will be set to true if the communication with the DB is over and no error
     */
    fun getItemTypes(
        itemTypeList: ObservableList<String>,
        userAccess: UserProfile? = currentProfile
    ): Observable<Boolean>
}