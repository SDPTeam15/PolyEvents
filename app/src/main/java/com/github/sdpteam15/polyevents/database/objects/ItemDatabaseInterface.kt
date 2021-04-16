package com.github.sdpteam15.polyevents.database.objects

import com.github.sdpteam15.polyevents.database.Database
import com.github.sdpteam15.polyevents.database.observe.Observable
import com.github.sdpteam15.polyevents.database.observe.ObservableList
import com.github.sdpteam15.polyevents.model.Item
import com.github.sdpteam15.polyevents.model.UserEntity
import com.github.sdpteam15.polyevents.model.UserProfile

interface ItemDatabaseInterface {
    val currentUser: UserEntity?
        get() = Database.currentDatabase.currentUser
    val currentProfile: UserProfile?
        get() = Database.currentDatabase.currentProfile

    /**
     * create a new Item
     * @param item item we want to add in the database
     * @param userAccess the user profile to use its permission
     * @return An observer that will be set to the new Item ID if the communication with the DB is over and no error
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

}