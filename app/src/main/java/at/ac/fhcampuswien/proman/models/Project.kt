package at.ac.fhcampuswien.proman.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

data class Project(
    val userId: String = "",
    val projectName: String = "",
    val timestamp: Timestamp = Timestamp.now(),
    val documentID: String = "",
    val tasks: ArrayList<Tasks> = ArrayList(),
    val createdBy: String = ""
)
