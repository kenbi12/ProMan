package at.ac.fhcampuswien.proman.screens

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Divider
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import at.ac.fhcampuswien.proman.OtherTaskActivity
import at.ac.fhcampuswien.proman.ProfileActivity
import at.ac.fhcampuswien.proman.constants.MyConstant
import at.ac.fhcampuswien.proman.widgets.HomeTopAppBar
import at.ac.fhcampuswien.proman.models.Project
import at.ac.fhcampuswien.proman.models.Tasks
import at.ac.fhcampuswien.proman.navigation.Screen
import at.ac.fhcampuswien.proman.repository.Resources
import at.ac.fhcampuswien.proman.viewmodels.ProjectViewModel
import coil.compose.AsyncImage


import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale


@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun ProjectScreen(
    navController: NavHostController,
    projectViewModel: ProjectViewModel,
    //projectId : String,
    context: Context,
    onProjectClick: (id: String) -> Unit,
    navToLoginPage: () -> Unit
) {

    val uid = FirebaseAuth.getInstance().currentUser?.uid.toString()
    val projectsUiState = projectViewModel.projectsUiState
    val proUiState = projectViewModel.proUiState
    LaunchedEffect(key1 = Unit) {
        projectViewModel.loadProjects()
    }

    val uri = remember {
        mutableStateOf("")
    }

    val name = remember {
        mutableStateOf("")
    }

    FirebaseFirestore.getInstance().collection("Users")
        .document(uid).get().addOnSuccessListener {
            uri.value = it.get("url").toString()
            name.value = it.get("name").toString()
            Log.d("cvv", name.value + uri.value)
        }

    val state = rememberScaffoldState()
    val scope = rememberCoroutineScope()


    var openDialog by remember {
        mutableStateOf(false)
    }
    var selectedProject: Project? by remember {
        mutableStateOf(null)
    }


    Scaffold(
        scaffoldState = state,
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .background(color = Color.Black)
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(modifier = Modifier.size(20.dp))
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = null,
                        modifier = Modifier
                            .size(28.dp)
                            .clickable {
                                scope.launch {
                                    state.drawerState.open()
                                }
                            },
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.size(20.dp))
                    Text(
                        text = "Projects",
                        style = TextStyle(
                            fontSize = 24.sp,
                            color = Color.White
                        ),
                        fontWeight = FontWeight.Black,
                    )
                    Spacer(modifier = Modifier.width(100.dp))
                    Text(
                        text = "Other Tasks",
                        style = TextStyle(
                            fontSize = 24.sp,
                            color = Color.White
                        ),
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.clickable {
                            context.startActivity(Intent(context, OtherTaskActivity::class.java))
                        }
                    )
                }
            }
        },
        drawerContent = {
            Box(modifier = Modifier.width(140.dp)) {

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    AsyncImage(
                        model = uri.value,
                        contentDescription = null,
                        modifier = Modifier
                            .size(100.dp)
                            .background(
                                shape = RoundedCornerShape(50.dp),
                                color = Color.White
                            )
                    )
                    Spacer(modifier = Modifier.size(15.dp))
                    Text(
                        text = name.value,
                        style = MaterialTheme.typography.h6,
                        fontWeight = FontWeight.Black,
                        color = Color.DarkGray
                    )

                    Spacer(modifier = Modifier.size(15.dp))
                    Divider(color = Color.Blue, thickness = 1.dp)
                    Spacer(modifier = Modifier.size(15.dp))
                    Row(
                        modifier = Modifier.clickable {
                            context.startActivity(Intent(context, ProfileActivity::class.java))
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Profile",
                            modifier = Modifier.padding(4.dp)
                        )
                        Text(
                            text = "Edit Profile", modifier = Modifier
                                .width(100.dp)
                                .padding(4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.size(15.dp))
                    Row(
                        modifier = Modifier.clickable {
                            projectViewModel.signOut()
                            navToLoginPage.invoke()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "Log Out",
                            modifier = Modifier.padding(4.dp)
                        )
                        Text(
                            text = "Log Out", modifier = Modifier
                                .width(100.dp)
                                .padding(4.dp)
                        )
                    }
                }

            }
        }) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "New Project",
                color = Color.DarkGray,
                style = MaterialTheme.typography.h5
            )


            val textFieldColors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color.DarkGray,
                unfocusedBorderColor = Color.LightGray
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                OutlinedTextField(
                    colors = textFieldColors,
                    value = proUiState.projectName,
                    onValueChange = { projectViewModel.onProjectNameChange(it) },
                    label = { Text(text = "Project", color = Color.DarkGray) },
                )
                IconButton(
                    modifier = Modifier.padding(16.dp),
                    onClick = { projectViewModel.addProject() }

                ) {


                    Icon(imageVector = Icons.Default.Add, contentDescription = "add")
                }

            }

            Divider()

            when (projectsUiState.projectsList) {
                is Resources.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .fillMaxSize()
                            .wrapContentSize(align = Alignment.Center)
                    )
                }

                is Resources.Success -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(1),
                        contentPadding = PaddingValues(16.dp),

                        ) {
                        items(projectsUiState.projectsList.data ?: emptyList()) { project ->

                            ProjectCard(
                                project = project,
                                onLongClick = {
                                    openDialog = true
                                    selectedProject = project
                                },
                            ) {
                                MyConstant.projectId = project.documentID
                                onProjectClick.invoke(project.documentID)
                            }

                        }
                    }
                    AnimatedVisibility(visible = openDialog) {
                        AlertDialog(
                            onDismissRequest = {
                                openDialog = false
                            },
                            title = { Text(text = "Delete The Task?") },
                            confirmButton = {
                                Button(
                                    onClick = {
                                        selectedProject?.documentID?.let {
                                            projectViewModel?.deleteProject(it)
                                        }
                                        openDialog = false
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        backgroundColor = Color.Red
                                    ),
                                ) {
                                    Text(text = "Delete")
                                }
                            },
                            dismissButton = {
                                Button(onClick = { openDialog = false }) {
                                    Text(text = "Cancel")
                                }
                            }
                        )


                    }
                }

                else -> {
                    Text(
                        text = projectsUiState.projectsList.throwable?.localizedMessage
                            ?: "Unknown Error",
                        color = Color.Red
                    )
                }
            }

        }


        /*    { padding ->
            Column(modifier = Modifier.padding(padding)) {
                when (projectUiState.projectList) {
                    is Resources.Loading -> {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .fillMaxSize()
                                .wrapContentSize(align = Alignment.Center)
                        )
                    }
                    is Resources.Success -> {

                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            contentPadding = PaddingValues(16.dp),

                            ) {
                            items(
                                projectUiState.projectList.data ?: emptyList()
                            ) { project ->Card(elevation = 14.dp, modifier = Modifier
                                .fillMaxSize()
                                .padding(2.dp, 2.dp)){
                                ProjectCard(
                                    project = project,
                                    projectViewModel = projectViewModel
                                ) {
                                    onClick.invoke(project.projectId)
                                }
                            }


                            }


                        }




                    }
                    else -> {
                        Text(
                            text = projectUiState.projectList.throwable?.localizedMessage ?: "Unknown Error",
                            color = Color.Red
                        )
                    }


                }


            }

        }*/


        LaunchedEffect(key1 = projectViewModel.hasUser) {
            if (!projectViewModel.hasUser) {
                navToLoginPage.invoke()
            }
        }

    }

}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ProjectCard(
    project: Project,
    onLongClick: () -> Unit,
    onClick: () -> Unit
) {


    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(6.dp)
            .combinedClickable(
                onLongClick = { onLongClick.invoke() },
                onClick = { onClick.invoke() }
            ),
        shape = RoundedCornerShape(corner = CornerSize(16.dp)),
        elevation = 6.dp,
    ) {

        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = project.projectName,
                    style = MaterialTheme.typography.body2
                )
            }

            Spacer(modifier = Modifier.size(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text ="Created By: "+ project.createdBy,
                    style = MaterialTheme.typography.body2
                )
            }

            Spacer(modifier = Modifier.size(4.dp))
            CompositionLocalProvider(
                LocalContentAlpha provides ContentAlpha.disabled
            ) {
                Text(
                    text = formatDate(project.timestamp),
                    style = MaterialTheme.typography.body1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .padding(4.dp)
                        .align(Alignment.End),
                    maxLines = 4
                )

            }


        }
    }
}

private fun formatDate(timestamp: Timestamp): String {
    val sdf = SimpleDateFormat("MM-dd-yy HH:mm", Locale.getDefault())
    return sdf.format(timestamp.toDate())
}