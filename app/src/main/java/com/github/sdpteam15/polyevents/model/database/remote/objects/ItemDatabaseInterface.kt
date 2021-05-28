package com.github.sdpteam15.polyevents.model.database.remote.objects

import com.github.sdpteam15.polyevents.model.database.remote.Database
import com.github.sdpteam15.polyevents.model.database.remote.Matcher
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
     * @param total total amount of items
     * @param userAccess the user profile to use its permission
     * @return An observer that will be set to true if the communication with the DB is over and no error
     */
    fun createItem(
        item: Item,
        total: Int,
        userAccess: UserProfile? = currentProfile
    ): Observable<String>

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
     * @param total total amount of items
     * @param remaining remaining amount of items
     * @param userAccess the user profile to use its permission
     * @return An observer that will be set to true if the communication with the DB is over and no error
     */
    fun updateItem(
        item: Item,
        total: Int,
        remaining: Int,
        userAccess: UserProfile? = currentProfile
    ): Observable<Boolean>

    /**
     * Get list of items
     * @param itemList the list of items that will be set when the DB returns the information
     * @param matcher to add a filter to our request
     * @param userAccess profile for database access
     * @return An observer that will be set to true if the communication with the DB is over and no error
     */
    fun getItemsList(
        itemList: ObservableList<Triple<Item, Int, Int>>,
        matcher: Matcher? = null,
        userAccess: UserProfile? = currentProfile
    ): Observable<Boolean>

    /**
     * Get list of available items
     * @param itemList the list of items that will be set when the DB returns the information
     * @param userAccess profile for database access
     * @return An observer that will be set to true if the communication with the DB is over and no error
     */
    fun getAvailableItems(
        itemList: ObservableList<Triple<Item, Int, Int>>,
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