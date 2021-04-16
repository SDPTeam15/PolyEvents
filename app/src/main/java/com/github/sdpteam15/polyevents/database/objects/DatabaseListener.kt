package com.github.sdpteam15.polyevents.database.objects

import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot

object DatabaseListener {
    //Method used to get listener in the test set to mock and test the database
    var lastQuerySuccessListener: OnSuccessListener<QuerySnapshot>? = null
    var lastSetSuccessListener: OnSuccessListener<Void>? = null
    var lastFailureListener: OnFailureListener? = null
    var lastGetSuccessListener: OnSuccessListener<DocumentSnapshot>? = null
    var lastAddSuccessListener: OnSuccessListener<DocumentReference>?=null
}