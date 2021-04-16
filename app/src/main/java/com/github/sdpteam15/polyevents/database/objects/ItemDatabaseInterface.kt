package com.github.sdpteam15.polyevents.database.objects

import com.github.sdpteam15.polyevents.database.observe.Observable
import com.github.sdpteam15.polyevents.database.observe.ObservableList
import com.github.sdpteam15.polyevents.model.Item
import com.github.sdpteam15.polyevents.model.UserEntity
import com.github.sdpteam15.polyevents.model.UserProfile

interface ItemDatabaseInterface {
    val currentUser: UserEntity?
    val currentProfile: UserProfile?

    /**
     * create a new Item
     * @param item item we want to add in the database
     * @param profile profile for database access
     * @return An observer that will be set to the new Item ID if the communication with the DB is over and no error
     */
    fun createItem(
        item: Item,
        count: Int,
        profile: UserProfile? = currentProfile
    ): Observable<Boolean>

    /**
     * @param itemId id of the item we want to remove from the database
     * @param profile profile for database access
     * @return An observer that will be set to true if the communication with the DB is over and no error
     */
    fun removeItem(
        itemId: String,
        profile: UserProfile? = currentProfile
    ): Observable<Boolean>

    /**
     * @param item item we want to update in the database
     * @param profile profile for database access
     * @return An observer that will be set to true if the communication with the DB is over and no error
     */
    fun updateItem(
        item: Item,
        count: Int,
        profile: UserProfile? = currentProfile
    ): Observable<Boolean>

    /**
     * Get list of items
     * @param itemList: the list of items that will be set when the DB returns the information
     * @param profile: profile for database access
     * @return An observer that will be set to true if the communication with the DB is over and no error
     */
    fun getItemsList(
        itemList: ObservableList<Pair<Item, Int>>,
        profile: UserProfile? = currentProfile
    ): Observable<Boolean>

    /**
     * Get list of available items
     * @param itemList: the list of items that will be set when the DB returns the information
     * @param profile: profile for database access
     * @return An observer that will be set to true if the communication with the DB is over and no error
     */
    fun getAvailableItems(
        itemList: ObservableList<Pair<Item, Int>>,
        profile: UserProfile? = currentProfile
    ): Observable<Boolean>

}