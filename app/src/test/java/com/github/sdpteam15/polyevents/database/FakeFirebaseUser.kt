package com.github.sdpteam15.polyevents.database

/**
 * Fake FirebaseUser
 */
class FakeFirebaseUser (
    override val displayName : String = "Test Name",
    override val uid: String = "Test UID",
    override val email: String = "Test email"
) : FirebaseUserInterface {}