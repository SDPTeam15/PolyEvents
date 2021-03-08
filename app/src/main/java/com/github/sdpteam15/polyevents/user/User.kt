package com.github.sdpteam15.polyevents.user

import com.github.sdpteam15.polyevents.database.DatabaseInterface
import com.github.sdpteam15.polyevents.database.DatabaseObject
import com.github.sdpteam15.polyevents.database.FirebaseUserInterface

/**
 * Application user
 * @param FirebaseUser associates FirebaseUser
 */
class User private constructor(override val FirebaseUser: FirebaseUserInterface) : UserInterface {
    companion object {
        private val instances: MutableMap<String, User> = HashMap()

        /**
         * Application user
         * @param firebaseUser associates FirebaseUser
         * @return the application user associates to the FirebaseUser
         */
        fun invoke(firebaseUser: FirebaseUserInterface?): User {
            if (firebaseUser == null)
                throw IllegalArgumentException("FirebaseUser must be not null")
            if (!instances.containsKey(firebaseUser.uid))
                instances[firebaseUser.uid] = User(firebaseUser)
            return instances[firebaseUser.uid] as User
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
    var database: DatabaseInterface = DatabaseObject.Singleton

    private fun FromDatabase(): MutableList<ProfileInterface> {
        if (profileList == null)
            profileList = database.getListProfile(UID, this).toMutableList()
        if ((profileList as MutableList<ProfileInterface>).size == 0) {
            val profile = Profile(Name)
            (profileList as MutableList<ProfileInterface>).add(profile)
            database.addProfile(profile, UID, this)
        }
        return profileList as MutableList<ProfileInterface>
    }

    override val ProfileList: List<ProfileInterface>
        get() = FromDatabase() as List<ProfileInterface>

    override val Name: String
        get() = FirebaseUser.displayName
    override val UID: String
        get() = FirebaseUser.uid
    override val Email: String
        get() = FirebaseUser.email

    /**
     * Current application user profile id
     */
    var CurrentProfileId: Int
        get() = currentProfileId
        set(value) {
            if (value < 0 || value >= ProfileList.size)
                throw IndexOutOfBoundsException("value must be between 0 and ${ProfileList.size}")
            else currentProfileId = value
        }

    override var CurrentProfile: ProfileInterface
        get() = ProfileList[currentProfileId]
        set(value) {
            var i: Int = 0
            for (v in ProfileList) {
                if (v == value) {
                    CurrentProfileId = i
                    break
                }
                ++i
            }
        }

    override fun removeCache() {
        profileList = null
    }

    override fun newProfile(name: String) {
        val profileList = FromDatabase()
        val profile = Profile(name)
        profileList.add(profile)
        database.addProfile(profile, UID, this)
    }

    override fun removeProfile(profile: ProfileInterface): Boolean {
        val profileList = FromDatabase()
        if (!(profileList as MutableList<ProfileInterface>).contains(profile))
            return false
        if (CurrentProfile == profile)
            currentProfileId = 0;
        profileList.remove(profile)
        database.removeProfile(profile, UID, this)
        if (profileList.size == 0)
            newProfile(this.Name)
        return true;
    }
}