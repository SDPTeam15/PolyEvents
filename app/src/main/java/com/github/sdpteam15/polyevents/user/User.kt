package com.github.sdpteam15.polyevents.user

import com.github.sdpteam15.polyevents.database.DatabaseObject
import com.google.firebase.auth.FirebaseUser

/**
 * Application user
 * @param FirebaseUser associates FirebaseUser
 */
class User private constructor(override val FirebaseUser: FirebaseUser) : UserInterface {
    companion object {
        private val instances: MutableMap<String, User> = HashMap()

        /**
         * Application user
         * @param FirebaseUser associates FirebaseUser
         * @return the application user associates to the FirebaseUser
         */
        fun invoke(FirebaseUser: FirebaseUser?): User {
            if (FirebaseUser == null)
                throw IllegalArgumentException("FirebaseUser must be not null")
            if (!instances.containsKey(FirebaseUser.uid))
                instances[FirebaseUser.uid] = User(FirebaseUser)
            return instances[FirebaseUser.uid] as User
        }

        /**
         * Application user
         * @param uid associates FirebaseUser.uid
         * @return the application user associates to the uid
         */
        fun invoke(uid: String?): User? {
            if (uid == null)
                return null
            return instances[uid]
        }
    }

    private var profileList: MutableList<ProfileInterface>? = null;
    private var currentProfileId: Int = 0

    override val ProfileList: List<ProfileInterface>
        get() {
            if (profileList == null)
                profileList = DatabaseObject.Singleton.getListProfile(UID, this).toMutableList()
            if (profileList?.size == 0) {
                profileList?.add(Profile(this.Name))
            }
            return profileList as List<ProfileInterface>
        }

    override val Name: String
        get() = FirebaseUser.displayName
    override val UID: String
        get() = FirebaseUser.uid

    /**
     * Current application user profile id
     */
    var CurrentProfileId: Int
        get() = currentProfileId
        set(value) {
            if (value < 0 || value > ProfileList.size)
                throw IndexOutOfBoundsException("value must be between 0 and ${ProfileList.size}")
            else currentProfileId = value
        }

    override val CurrentProfile: ProfileInterface
        get() = ProfileList[currentProfileId]

    override fun removeCache() {
        profileList = null
    }

    override fun newProfile(name: String) {
        if(profileList == null)
            profileList = DatabaseObject.Singleton.getListProfile(UID, this).toMutableList()
        val profile = Profile(name)
        profileList?.add(profile)
        DatabaseObject.Singleton.addProfile(profile, UID, this)
    }

    override fun removeProfile(profile: ProfileInterface) {
        if(profileList == null)
            profileList = DatabaseObject.Singleton.getListProfile(UID, this).toMutableList()
        profileList?.remove(profile)
        DatabaseObject.Singleton.removeProfile(profile, UID, this)
    }
}