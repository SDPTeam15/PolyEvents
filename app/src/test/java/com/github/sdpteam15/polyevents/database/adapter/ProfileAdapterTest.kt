package com.github.sdpteam15.polyevents.database.adapter

import com.github.sdpteam15.polyevents.model.database.remote.DatabaseConstant
import com.github.sdpteam15.polyevents.model.database.remote.adapter.ProfileAdapter
import com.github.sdpteam15.polyevents.model.entity.UserProfile
import com.github.sdpteam15.polyevents.model.entity.UserRole
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class ProfileAdapterTest {
    val profileId = "profileId"
    val profileName = "profileName"
    val users = mutableListOf<String>()

    val profileRank = UserRole.ADMIN
    lateinit var profile: UserProfile

    @Before
    fun setupProfile() {
        profile = UserProfile(
                profileId,profileName, profileRank, users
        )
    }

    @Test
    fun conversionOfProfileoDocumentPreservesData() {
        val document = ProfileAdapter.toDocument(profile)

        Assert.assertEquals(document[DatabaseConstant.ProfileConstants.PROFILE_ID.value], profileId)
        Assert.assertEquals(document[DatabaseConstant.ProfileConstants.PROFILE_NAME.value], profileName)
        Assert.assertEquals(document[DatabaseConstant.ProfileConstants.PROFILE_RANK.value], profileRank.userRole)
        Assert.assertEquals(document[DatabaseConstant.ProfileConstants.PROFILE_USERS.value], users)
    }

    @Test
    fun conversionOfDocumentToEventPreservesData() {
        val profil: HashMap<String, Any?> = hashMapOf(
                DatabaseConstant.ProfileConstants.PROFILE_ID.value to profile.pid,
                DatabaseConstant.ProfileConstants.PROFILE_NAME.value to profile.profileName,
                DatabaseConstant.ProfileConstants.PROFILE_RANK.value to profile.userRole.userRole,
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
                DatabaseConstant.ProfileConstants.PROFILE_RANK.value to null,
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