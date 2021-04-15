package com.github.sdpteam15.polyevents.database.objects

import com.github.sdpteam15.polyevents.database.*
import com.github.sdpteam15.polyevents.model.UserEntity
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.Test

class DatabaseListenerTest {
    lateinit var user: UserEntity
    lateinit var mockedDatabase: FirebaseFirestore
    lateinit var database: DatabaseInterface

    @Test
    fun variablesAreCorrectlySet(){
        val user = UserEntity(
            uid = googleId,
            username = usernameEntity,
            birthDate = birthDate,
            name = name,
            email = email
        )
        FirestoreDatabaseProvider.firstConnectionUser = user
        MatcherAssert.assertThat(
            FirestoreDatabaseProvider.firstConnectionUser,
            CoreMatchers.`is`(user)
        )

        val lastQuerySuccessListener= OnSuccessListener<QuerySnapshot> { }
        val lastSetSuccessListener= OnSuccessListener<Void> { }
        val lastFailureListener= OnFailureListener {  }
        val lastGetSuccessListener= OnSuccessListener<DocumentSnapshot> { }
        val lastAddSuccessListener= OnSuccessListener<DocumentReference> { }

        FirestoreDatabaseProvider.lastQuerySuccessListener= lastQuerySuccessListener
        FirestoreDatabaseProvider.lastSetSuccessListener= lastSetSuccessListener
        FirestoreDatabaseProvider.lastFailureListener= lastFailureListener
        FirestoreDatabaseProvider.lastGetSuccessListener= lastGetSuccessListener
        FirestoreDatabaseProvider.lastAddSuccessListener= lastAddSuccessListener

        MatcherAssert.assertThat(
            FirestoreDatabaseProvider.lastQuerySuccessListener,
            CoreMatchers.`is`(lastQuerySuccessListener)
        )
        MatcherAssert.assertThat(
            FirestoreDatabaseProvider.lastSetSuccessListener,
            CoreMatchers.`is`(lastSetSuccessListener)
        )
        MatcherAssert.assertThat(
            FirestoreDatabaseProvider.lastFailureListener,
            CoreMatchers.`is`(lastFailureListener)
        )
        MatcherAssert.assertThat(
            FirestoreDatabaseProvider.lastGetSuccessListener,
            CoreMatchers.`is`(lastGetSuccessListener)
        )
        MatcherAssert.assertThat(
            FirestoreDatabaseProvider.lastAddSuccessListener,
            CoreMatchers.`is`(lastAddSuccessListener)
        )

    }
}