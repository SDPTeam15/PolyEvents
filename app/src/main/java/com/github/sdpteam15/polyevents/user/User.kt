package com.github.sdpteam15.polyevents.user

import com.github.sdpteam15.polyevents.database.Database.currentDatabase
import com.github.sdpteam15.polyevents.database.DatabaseUserInterface

/**
 * Application user
 * @param databaseUser associates FirebaseUser
 */
class User private constructor(override val databaseUser: DatabaseUserInterface) : UserInterface {
    companion object {
        private val instances: MutableMap<String, User> = HashMap()

        /**
         * Application user
         * @param firebaseUser associates FirebaseUser
         * @return the application user associates to the FirebaseUser
         */
        fun invoke(databaseUser: DatabaseUserInterface): User {
            if (!instances.containsKey(databaseUser.uid))
                instances[databaseUser.uid] = User(databaseUser)
            return instances[databaseUser.uid] as User
        }

        /**
         * Application user
         * @param uid associates FirebaseUser.uid
         * @return the application user associates to the uid
         */
        fun invoke(uid: String): User? = instances[uid]

        private var lastCurrentUserUid: String? = null

        /**
         * Current user of the application
         */
        var currentUser: UserInterface? = null
            get() {
                if(field != null)
                    return field
                val currentUser: DatabaseUserInterface = currentDatabase.currentUser ?: return null
                if(lastCurrentUserUid != null && lastCurrentUserUid != currentUser.uid)
                    invoke(lastCurrentUserUid!!)?.removeCache()
                lastCurrentUserUid = currentUser.uid
                return invoke(currentUser)
            }
    }

    private var mutableProfileList: MutableList<ProfileInterface>? = null
        get() {
            field = field ?: currentDatabase.getListProfile(uid, this).toMutableList()
            if (field!!.size == 0) {
                val profile = Profile("$uid:0", name, Rank.RegisteredVisitor, this)
                field!!.add(profile)
                currentDatabase.addProfile(profile, uid, this)
            }
            return field
        }

    override val profileList: List<ProfileInterface>
        get() = mutableProfileList as List<ProfileInterface>
    override val name: String
        get() = databaseUser.displayName ?: ""
    override val uid: String
        get() = databaseUser.uid
    override val email: String
        get() = databaseUser.email ?: ""
    override val rank: Rank
        get() = databaseUser.rank


    /**
     * Current application user profile id
     */
    var currentProfileId: Int = 0
        set(value) {
            field =
                if (value >= 0 && value < this.profileList.size) value else throw IndexOutOfBoundsException(
                    "value must be between 0 and ${this.profileList.size}"
                )
        }

    override var currentProfile: ProfileInterface
        get() = profileList[currentProfileId]
        set(value) {
            for (v in this.profileList.withIndex()) {
                if (v.value == value) {
                    currentProfileId = v.index
                    break
                }
            }
        }

    override fun removeCache() {
        mutableProfileList = null
    }

    override fun newProfile(name: String) {
        val pid = mutableProfileList!!.size
        val profile = Profile("$uid:$pid", name, Rank.RegisteredVisitor, this)
        mutableProfileList!!.add(profile)
        currentDatabase.addProfile(profile, uid, this)
    }

    override fun removeProfile(profile: ProfileInterface): Boolean {
        if (!profileList.contains(profile))
            return false
        if (currentProfile == profile)
            currentProfileId = 0
        mutableProfileList?.remove(profile)
        currentDatabase.removeProfile(profile, uid, this)
        if (profileList.isEmpty())
            newProfile(this.name)
        return true
    }
}