package at.ac.fhcampuswien.proman.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import at.ac.fhcampuswien.proman.models.Tasks
import at.ac.fhcampuswien.proman.repository.Resources
import at.ac.fhcampuswien.proman.repository.StorageRepository
import kotlinx.coroutines.launch

class TaskViewModel (private val repository: StorageRepository = StorageRepository(),
) : ViewModel() {
    var taskUiState by mutableStateOf(TaskUiState())

    val user = repository.user()
    val hasUser: Boolean
        get() = repository.hasUser()
    private val userId: String
    get() = repository.getUserId()

    fun loadTasks(){
        if (hasUser){
            if (userId.isNotBlank()){
                getUserTasks(userId)
            }
        }else{
            taskUiState = taskUiState.copy(tasksList = Resources.Error(
                throwable = Throwable(message = "User is not Logged in")
            ))
        }
    }

    private fun getUserTasks(userId:String) = viewModelScope.launch {
        repository.getUserTasks(userId).collect {
            taskUiState = taskUiState.copy(tasksList = it)
        }
    }


    fun deleteTask(taskId:String) = repository.deleteTask(taskId){
        taskUiState = taskUiState.copy(taskDeletedStatus = it)
    }

}

data class TaskUiState(
    val tasksList: Resources<List<Tasks>> = Resources.Loading(),
    val taskDeletedStatus: Boolean = false,
)