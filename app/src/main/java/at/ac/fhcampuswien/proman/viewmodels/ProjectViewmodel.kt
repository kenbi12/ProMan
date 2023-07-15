package at.ac.fhcampuswien.proman.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import at.ac.fhcampuswien.proman.models.Project
import at.ac.fhcampuswien.proman.models.Tasks
import at.ac.fhcampuswien.proman.repository.Resources
import at.ac.fhcampuswien.proman.repository.StorageRepository
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch

class ProjectViewModel (private val repository: StorageRepository = StorageRepository(), ) : ViewModel(){

    var projectsUiState by mutableStateOf(ProjectsUiState())


    val hasUser: Boolean
        get() = repository.hasUser()
    private val userId: String
        get() = repository.getUserId()

    fun loadProjects(){
        if (hasUser){
            if (userId.isNotBlank()){
                getUserProjects(userId)
            }
        }else{
            projectsUiState = projectsUiState.copy(projectsList = Resources.Error(
                throwable = Throwable(message = "User is not Logged in")
            ))
        }
    }

    private fun getUserProjects(userId:String) = viewModelScope.launch {
        repository.getUserProjects(userId).collect {
            projectsUiState = projectsUiState.copy(projectsList = it)
        }
    }

    fun deleteProject(userId:String) = repository.deleteProject(userId){
        projectsUiState = projectsUiState.copy(projectsDeletedStatus = it)
    }


    var proUiState by mutableStateOf(ProUiState())
        private set



    private val user: FirebaseUser?
        get() = repository.user()



    fun onProjectNameChange(projectName: String) {
        proUiState = proUiState.copy(projectName = projectName)
    }
    fun ontasksTitleChange(tasks : Tasks){
        proUiState = proUiState.copy(tasks.title)
    }
    fun ontasksDescChange(tasks : Tasks){
        proUiState = proUiState.copy(tasks.description)
    }
    fun ontasksColorChange(tasks : Tasks){
        proUiState = proUiState.copy(tasks.colorIndex.toString())
    }


    fun addProject(){
        if (hasUser){
            repository.addProject(
                userId = user!!.uid,
                projectName = proUiState.projectName,
                timestamp = Timestamp.now(),
                tasks = ArrayList(proUiState.tasks)
            ){
                proUiState = proUiState.copy(projectAddedStatus = it)
            }
        }


    }

    fun setEditFields(project:  Project){
        proUiState = proUiState.copy(
            projectName = project.projectName,

            )

    }

    fun getProject(projectId:String){
        repository.getProject(
            projectId = projectId,
            onError = {},
        ){
            proUiState = proUiState.copy(selectedProject = it)
            proUiState.selectedProject?.let { it1 -> setEditFields(it1) }
        }
    }

   // fun updateProject( projectId: String){}

    fun resetProjectAddedStatus(){
        proUiState = proUiState.copy(
            projectAddedStatus = false,
           // updateProjectStatus = false,
        )
    }

    fun resetState(){
        proUiState = ProUiState()
    }


    fun signOut() = repository.signOut()




}



data class ProjectsUiState(
    val projectsList: Resources<List<Project>> = Resources.Loading(),
    val projectsDeletedStatus: Boolean = false,
)

data class ProUiState(
    val projectName: String = "",
    val project: String = "",
    val tasks : List<Tasks> = listOf(),
    val projectAddedStatus: Boolean = false,
   // val updateProjectStatus: Boolean = false,
    val selectedProject: Project? = null,
)










