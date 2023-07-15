package at.ac.fhcampuswien.proman.models

import com.google.firebase.Timestamp


data class Tasks(
    val userId: String = "",
    val projectId: String = "",
    val title: String = "",
    val description: String = "",
    val timestamp: Timestamp = Timestamp.now(),
    val colorIndex: Int = 0,
    val documentId: String = "",
    val createdBy: String = ""
)