package at.ac.fhcampuswien.proman.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import at.ac.fhcampuswien.proman.constants.MyConstant
import at.ac.fhcampuswien.proman.models.Project
import at.ac.fhcampuswien.proman.models.Tasks
import at.ac.fhcampuswien.proman.repository.StorageRepository
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.Timestamp

class DetailViewModel(
    private val repository: StorageRepository = StorageRepository()
) : ViewModel() {
    var detailUiState by mutableStateOf(DetailUiState())
        private set


    private val hasUser: Boolean
        get() = repository.hasUser()

    private val user: FirebaseUser?
        get() = repository.user()

    fun onColorChange(colorIndex: Int) {
        detailUiState = detailUiState.copy(colorIndex = colorIndex)
    }

    fun onTitleChange(title: String) {
        detailUiState = detailUiState.copy(title = title)
    }


    fun onTaskChange(task: String) {
        detailUiState = detailUiState.copy(task = task)
    }

    fun addTask() {
        if (hasUser) {
            repository.addTask(
                userId = user!!.uid,
                title = detailUiState.title,
                description = detailUiState.task,
                color = detailUiState.colorIndex,
                timestamp = Timestamp.now(),
                projectId = MyConstant.projectId,
            ) {
                detailUiState = detailUiState.copy(taskAddedStatus = it)
            }
        }


    }

    private fun setEditFields(task: Tasks) {
        detailUiState = detailUiState.copy(
            colorIndex = task.colorIndex,
            title = task.title,
            task = task.description

        )

    }

    fun getTask(noteId: String) {
        repository.getTask(
            noteId = noteId,
            onError = {},
        ) {
            detailUiState = detailUiState.copy(selectedTask = it)
            detailUiState.selectedTask?.let { it1 -> setEditFields(it1) }
        }
    }

    fun updateTask(
        taskId: String
    ) {
        repository.updateTask(
            title = detailUiState.title,
            note = detailUiState.task,
            noteId = taskId,
            color = detailUiState.colorIndex
        ) {
            detailUiState = detailUiState.copy(updateTaskStatus = it)
        }
    }

    fun resetTaskAddedStatus() {
        detailUiState = detailUiState.copy(
            taskAddedStatus = false,
            updateTaskStatus = false,
        )
    }

    fun resetState() {
        detailUiState = DetailUiState()
    }


}

data class DetailUiState(
    val projectId: String = "",
    val colorIndex: Int = 0,
    val title: String = "",
    val task: String = "",
    val taskAddedStatus: Boolean = false,
    val updateTaskStatus: Boolean = false,
    val selectedTask: Tasks? = null,
)
