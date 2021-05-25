package com.github.sdpteam15.polyevents.model.database.remote.adapter

/**
 * A interface for converting between user entities in our code and
 * documents in the database. Not unlike the DTO (Data
 * transfer object) concept.
 */
interface AdapterInterface<T> : AdapterToDocumentInterface<T>, AdapterFromDocumentInterface<T>