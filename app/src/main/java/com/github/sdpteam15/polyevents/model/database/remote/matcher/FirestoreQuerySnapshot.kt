package com.github.sdpteam15.polyevents.model.database.remote.matcher


class FirestoreQuerySnapshot(private val querySnapshot: com.google.firebase.firestore.QuerySnapshot) :
    QuerySnapshot {
    override fun iterator() =
        object : Iterator<QueryDocumentSnapshot> {
            val it = querySnapshot.iterator()
            override fun hasNext() = it.hasNext()
            override fun next() : QueryDocumentSnapshot {
                val v = it.next()
                return QueryDocumentSnapshot(v.data, v.id)
            }
        }
}

