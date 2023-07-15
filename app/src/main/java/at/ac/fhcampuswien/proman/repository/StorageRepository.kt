package at.ac.fhcampuswien.proman.repository

import android.util.Log
import at.ac.fhcampuswien.proman.constants.MyConstant
import at.ac.fhcampuswien.proman.models.Project
import at.ac.fhcampuswien.proman.models.Tasks
import at.ac.fhcampuswien.proman.models.User
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

const val PROJECTS_COLLECTION_REF = "Projects"
const val TASKS_COLLECTION_REF = "Tasks"
const val User_COLLECTION_REF = "Users"
const val OTHER_TASK_REF = "OTasks"

class StorageRepository {

    fun user() = Firebase.auth.currentUser
    fun hasUser(): Boolean = Firebase.auth.currentUser != null

    fun getUserId(): String = Firebase.auth.currentUser?.uid.orEmpty()


    private val projectsCollectionRef: CollectionReference =
        Firebase.firestore.collection(PROJECTS_COLLECTION_REF)

    private val taskCollectionRef: CollectionReference =
        Firebase.firestore.collection(TASKS_COLLECTION_REF)

    private val userCollectionRef: CollectionReference =
        Firebase.firestore.collection(User_COLLECTION_REF)

    private val tasksRef: CollectionReference = Firebase.firestore.collection(TASKS_COLLECTION_REF)
    private val taskDocumentRef = tasksRef.document().id


    private val userRef: CollectionReference = Firebase.firestore.collection(User_COLLECTION_REF)


    fun getUserTasks(
        userId: String,
    ): Flow<Resources<List<Tasks>>> = callbackFlow {
        var snapshotStateListener: ListenerRegistration? = null

        try {
            snapshotStateListener = tasksRef
                .whereEqualTo("projectId", MyConstant.projectId)
                .addSnapshotListener { snapshot, e ->
                    val response = if (snapshot != null) {
                        val notes = snapshot.toObjects(Tasks::class.java)
                        notes.sortBy { it.timestamp }
                        Resources.Success(data = notes)

                    } else {
                        Resources.Error(throwable = e?.cause)
                    }

                    trySend(response)

                }


        } catch (e: Exception) {
            trySend(Resources.Error(e.cause))
            e.printStackTrace()
        }

        awaitClose {
            snapshotStateListener?.remove()
        }


    }


    fun getTask(
        noteId: String,
        onError: (Throwable?) -> Unit,
        onSuccess: (Tasks?) -> Unit
    ) {
        tasksRef
            .document(noteId)
            .get()
            .addOnSuccessListener {
                onSuccess.invoke(it?.toObject(Tasks::class.java))
            }
            .addOnFailureListener { result ->
                onError.invoke(result.cause)
            }


    }

    fun addTask(
        userId: String,
        projectId: String,
        title: String,
        description: String,
        timestamp: Timestamp,
        color: Int = 0,
        onComplete: (Boolean) -> Unit,
    ) {
        val documentId = taskCollectionRef.document().id

        userCollectionRef.document(userId).get().addOnSuccessListener {
            val name = it.get("name").toString()

            val task = Tasks(
                userId,
                projectId,
                title,
                description,
                timestamp,
                colorIndex = color,
                documentId = documentId,
                createdBy = name
            )
            //val values: Tasks = task

//        val updateData = hashMapOf<String, Any>(
//            "colorIndex" to color,
//            "description" to description,
//            "title" to title,
//            "timestamp" to timestamp,
//            "userId" to userId,
//            "projectId" to projectId
//        )

            taskCollectionRef
                .document(documentId)
                .set(task)
                .addOnCompleteListener { result ->
                    onComplete.invoke(result.isSuccessful)
                }

        }

    }

    fun deleteTask(noteId: String, onComplete: (Boolean) -> Unit) {
        taskCollectionRef.document(noteId)
            .delete()
            .addOnCompleteListener {
                onComplete.invoke(it.isSuccessful)
            }
    }

    fun updateTask(
        title: String,
        note: String,
        color: Int,
        noteId: String,
        onResult: (Boolean) -> Unit
    ) {
        val updateData = hashMapOf<String, Any>(
            "colorIndex" to color,
            "description" to note,
            "title" to title,
        )

        tasksRef.document(noteId)
            .update(updateData)
            .addOnCompleteListener {
                onResult(it.isSuccessful)
            }


    }


    fun getUserProjects(
        userId: String,
    ): Flow<Resources<List<Project>>> = callbackFlow {
        var snapshotStateListener: ListenerRegistration? = null

        try {
            snapshotStateListener = projectsCollectionRef
                .orderBy("timestamp")
                .whereEqualTo("userId", userId)
                .addSnapshotListener { snapshot, e ->
                    val response = if (snapshot != null) {
                        val projects = snapshot.toObjects(Project::class.java)
                        Resources.Success(data = projects)
                    } else {
                        Resources.Error(throwable = e?.cause)
                    }
                    trySend(response)

                }


        } catch (e: Exception) {
            trySend(Resources.Error(e.cause))
            e.printStackTrace()
        }

        awaitClose {
            snapshotStateListener?.remove()
        }
    }

    fun getProject(
        projectId: String,
        onError: (Throwable?) -> Unit,
        onSuccess: (Project?) -> Unit
    ) {
        projectsCollectionRef
            .document(projectId)
            .get()
            .addOnSuccessListener {
                onSuccess.invoke(it?.toObject(Project::class.java))
            }
            .addOnFailureListener { result ->
                onError.invoke(result.cause)
            }


    }

    fun updateProject(
        title: String,
        note: String,
        color: Int,
        projectId: String,
        onResult: (Boolean) -> Unit
    ) {
        val updateData = hashMapOf<String, Any>(
            "colorIndex" to color,
            "description" to note,
            "title" to title,
        )

        projectsCollectionRef.document(projectId)
            .update("tasks", updateData)
            .addOnCompleteListener {
                onResult(it.isSuccessful)
            }


    }

    fun addProject(
        userId: String,
        projectName: String,
        timestamp: Timestamp,
        tasks: ArrayList<Tasks>,
        onComplete: (Boolean) -> Unit,
    ) {

        val newProjectId = projectsCollectionRef.document().id

        userCollectionRef.document(userId).get().addOnSuccessListener {
            val name = it.get("name").toString()

            val project = Project(
                userId,
                projectName,
                timestamp,
                documentID = newProjectId,
                tasks = ArrayList(tasks),
                createdBy = name
                //listOf<Tasks>(Tasks("","","","", Timestamp.now(),0,"" )) // listOf<Tasks>()//addTask(),
            )
            projectsCollectionRef
                .document(newProjectId)
                .set(project)
                .addOnCompleteListener { result ->
                    onComplete.invoke(result.isSuccessful)
                }

        }
    }


    fun deleteProject(userId: String, onComplete: (Boolean) -> Unit) {
        projectsCollectionRef.document(userId)
            .delete()
            .addOnCompleteListener {
                onComplete.invoke(it.isSuccessful)
            }
    }

    fun signOut() = Firebase.auth.signOut()
    fun registerUser(
        id: String,
        email: String,
        name: String,
        selected: Boolean,
        onComplete: (Boolean) -> Unit,
    ) {
        val user = User(
            id,
            email,
            name,
            selected
        )
        userRef
            .document(id)
            .set(user)
            .addOnCompleteListener { result ->
                onComplete.invoke(result.isSuccessful)
            }


    }

}


sealed class Resources<T>(
    val data: T? = null,
    val throwable: Throwable? = null,
) {
    class Loading<T> : Resources<T>()
    class Success<T>(data: T?) : Resources<T>(data = data)
    class Error<T>(throwable: Throwable?) : Resources<T>(throwable = throwable)

}