package at.ac.fhcampuswien.proman.screens

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import at.ac.fhcampuswien.proman.repository.Resources
import at.ac.fhcampuswien.proman.ui.theme.*
import at.ac.fhcampuswien.proman.viewmodels.TaskUiState
import at.ac.fhcampuswien.proman.viewmodels.TaskViewModel
import at.ac.fhcampuswien.proman.models.Tasks
import at.ac.fhcampuswien.proman.viewmodels.ProjectViewModel
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun TaskScreen(
    taskViewModel: TaskViewModel?,
    projectViewModel: ProjectViewModel,
    projectId: String,
    onTaskClick: (id: String) -> Unit,
    navToDetailPage: (String) -> Unit
) {
    val taskUiState = taskViewModel?.taskUiState ?: TaskUiState()

    var openDialog by remember {
        mutableStateOf(false)
    }
    var selectedTask: Tasks? by remember {
        mutableStateOf(null)
    }

    //val scope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState()


    LaunchedEffect(key1 = Unit) {

        //if (isProjectIdNotBlank) { taskViewModel?.loadTasks() } else { detailViewModel?.resetState() }
        taskViewModel?.loadTasks()
    }


    Scaffold(
        scaffoldState = scaffoldState,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navToDetailPage.invoke(projectId) },
                backgroundColor = Color.DarkGray
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = Color.LightGray


                )
            }
        },
        topBar = {

            TopAppBar(backgroundColor = LoginBackground,
                navigationIcon = {},
                actions = {},
                title = {
                    Text(text = "Tasks", color = Color.Gray)
                }
            )


        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            when (taskUiState.tasksList) {
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
                        items(taskUiState.tasksList.data ?: emptyList()) { task ->
                            Card(
                                elevation = 14.dp, modifier = Modifier
                                    .fillMaxSize()
                                    .padding(3.dp, 3.dp)
                                    .background(
                                        shape = RoundedCornerShape(10.dp),
                                        color = Color.White
                                    )
                            ) {
                                TaskItem(
                                    tasks = task,
                                    onLongClick = {
                                        openDialog = true
                                        selectedTask = task
                                    },
                                ) {
                                    onTaskClick.invoke(task.documentId)
                                }
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
                                        selectedTask?.documentId?.let {
                                            taskViewModel?.deleteTask(it)
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
                        text = taskUiState
                            .tasksList.throwable?.localizedMessage ?: "Unknown Error",
                        color = Color.Red
                    )
                }
            }
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TaskItem(
    tasks: Tasks,
    onLongClick: () -> Unit,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .combinedClickable(
                onLongClick = { onLongClick.invoke() },
                onClick = { onClick.invoke() }
            )
            .fillMaxWidth(),
        //  backgroundColor = Utils.colors[tasks.colorIndex]
    ) {

        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(15.dp)
                    .background(
                        color = Utils.colors[tasks.colorIndex]
                    )
            )
            Spacer(modifier = Modifier.size(4.dp))
            Text(
                text = tasks.title,
                style = MaterialTheme.typography.h6,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Clip,
                modifier = Modifier.padding(4.dp)
            )
            Spacer(modifier = Modifier.size(4.dp))
            CompositionLocalProvider(
                LocalContentAlpha provides ContentAlpha.disabled
            ) {
                Text(
                    text = tasks.description,
                    style = MaterialTheme.typography.body1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(4.dp),
                    maxLines = 4
                )

            }

            Spacer(modifier = Modifier.size(4.dp))
            CompositionLocalProvider(
                LocalContentAlpha provides ContentAlpha.disabled
            ) {
                Text(
                    text = "Created by: " + tasks.createdBy,
                    style = MaterialTheme.typography.body1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .padding(4.dp),
                    maxLines = 4
                )

            }

            Spacer(modifier = Modifier.size(4.dp))
            CompositionLocalProvider(
                LocalContentAlpha provides ContentAlpha.disabled
            ) {
                Text(
                    text = formatDate(tasks.timestamp),
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

