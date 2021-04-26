package com.github.sdpteam15.polyevents.util

import com.github.sdpteam15.polyevents.EventActivity.Companion.event
import com.github.sdpteam15.polyevents.database.Database
import com.github.sdpteam15.polyevents.database.DatabaseConstant
import com.github.sdpteam15.polyevents.model.*
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class ProfileAdapterTest {
    val profileId = "profileId"
    val profileName = "profileName"
    val users = mutableListOf<String>()

    val profileRank = UserRole.ADMIN
    lateinit var profile:UserProfile

    @Before
    fun setupProfile() {
        profile = UserProfile(
                profileId,profileName, profileRank, users
        )
    }

    @Test
    fun conversionOfEventToDocumentPreservesData() {
        val document = ProfileAdapter.toDocument(profile)

        Assert.assertEquals(document[DatabaseConstant.ProfileConstants.PROFILE_ID.value], profileId)
        Assert.assertEquals(document[DatabaseConstant.ProfileConstants.PROFILE_NAME.value], profileName)
        Assert.assertEquals(document[DatabaseConstant.ProfileConstants.PROFILE_RANK.value], profileRank)
        Assert.assertEquals(document[DatabaseConstant.ProfileConstants.PROFILE_USERS.value], users)
    }

    @Test
    fun conversionOfDocumentToEventPreservesData() {
        val profil: HashMap<String, Any?> = hashMapOf(
                DatabaseConstant.ProfileConstants.PROFILE_ID.value to profile.pid,
                DatabaseConstant.ProfileConstants.PROFILE_NAME.value to profile.profileName,
                DatabaseConstant.ProfileConstants.PROFILE_RANK.value to profile.userRole.toString(),
                DatabaseConstant.ProfileConstants.PROFILE_USERS.value to profile.users
        )

        val obtainedProfile =
                ProfileAdapter.fromDocument(profil, profileId)

        assert(obtainedProfile.pid == profile.pid)
        assert(obtainedProfile.profileName == profile.profileName)
        assert(obtainedProfile.userRole == profile.userRole)
        assert(obtainedProfile.users == profile.users)
    }

    @Test
    fun conversonWithoutRolePutParticipant(){
        val profil: HashMap<String, Any?> = hashMapOf(
                DatabaseConstant.ProfileConstants.PROFILE_ID.value to profile.pid,
                DatabaseConstant.ProfileConstants.PROFILE_NAME.value to profile.profileName,
                DatabaseConstant.ProfileConstants.PROFILE_RANK.value to "",
                DatabaseConstant.ProfileConstants.PROFILE_USERS.value to profile.users
        )

        val obtainedProfile =
                ProfileAdapter.fromDocument(profil, profileId)

        assert(obtainedProfile.pid == profile.pid)
        assert(obtainedProfile.profileName == profile.profileName)
        assert(obtainedProfile.userRole == UserRole.PARTICIPANT)
        assert(obtainedProfile.users == profile.users)
    }
}