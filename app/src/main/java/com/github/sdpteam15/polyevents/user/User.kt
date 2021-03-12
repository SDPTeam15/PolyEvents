package com.github.sdpteam15.polyevents.user

import com.github.sdpteam15.polyevents.database.Database.Companion.currentDatabase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

/**
 * Application user
 * @param firebaseUser associates FirebaseUser
 */
class User private constructor(override val firebaseUser: FirebaseUser) : UserInterface {
    companion object {
        private val instances: MutableMap<String, User> = HashMap()

        /**
         * Application user
         * @param firebaseUser associates FirebaseUser
         * @return the application user associates to the FirebaseUser
         */
        fun invoke(firebaseUser: FirebaseUser): User {
            if (!instances.containsKey(firebaseUser.uid))
                instances[firebaseUser.uid] = User(firebaseUser)
            return instances[firebaseUser.uid] as User
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
        val CurrentUser: UserInterface?
            get() {
                try {
                    val currentUser: FirebaseUser =
                        FirebaseAuth.getInstance().currentUser ?: return null
                    if (lastCurrentUserUid != null && currentUser.uid != lastCurrentUserUid)
                        invoke(lastCurrentUserUid!!)?.removeCache()
                    lastCurrentUserUid = currentUser.uid
                    return invoke(currentUser)
                } catch (e: ExceptionInInitializerError) {
                    return null
                } catch (e: NoClassDefFoundError) {
                    return null
                }
            }
    }

    private var mutableProfileList: MutableList<ProfileInterface>? = null
        get() {
            field = field ?: currentDatabase.getListProfile(uid, this).toMutableList()
            if (field!!.size == 0) {
                val profile = Profile(name, this)
                field!!.add(profile)
                currentDatabase.addProfile(profile, uid, this)
            }
            return field
        }

    override val profileList: List<ProfileInterface>
        get() = mutableProfileList as List<ProfileInterface>
    override val name: String
        get() = firebaseUser.displayName ?: ""
    override val uid: String
        get() = firebaseUser.uid
    override val email: String
        get() = firebaseUser.email ?: ""

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
        val profile = Profile(name, this)
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