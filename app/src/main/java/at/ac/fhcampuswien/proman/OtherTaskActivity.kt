package at.ac.fhcampuswien.proman

import android.os.Bundle
import android.provider.SyncStateContract.Constants
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.MutableLiveData
import at.ac.fhcampuswien.proman.models.Tasks
import at.ac.fhcampuswien.proman.repository.OTHER_TASK_REF
import at.ac.fhcampuswien.proman.repository.TASKS_COLLECTION_REF
import at.ac.fhcampuswien.proman.ui.theme.ProManTheme
import at.ac.fhcampuswien.proman.ui.theme.Utils
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Locale

class OtherTaskActivity : ComponentActivity() {
    private val taskList = ArrayList<Tasks>()
    private val liveData = MutableLiveData(true)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProManTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val list = remember {
                        mutableStateListOf<Tasks>()
                    }
                    otherTaskUsers()
                    liveData.observe(this) {
                        list.clear()
                        list.addAll(taskList)
                    }
                    MyTasks(list = list)
                }
            }
        }
    }

    private fun otherTaskUsers() {
        if (FirebaseAuth.getInstance().currentUser != null) {
            val cUser = FirebaseAuth.getInstance().currentUser!!.uid

            FirebaseFirestore.getInstance().collection(OTHER_TASK_REF)
                .whereEqualTo("userId", cUser).get().addOnSuccessListener { snapshot ->
                    if (snapshot.size() > 0) {
                        snapshot.forEach {
                            val taskId = it.get("taskId").toString()
                            Log.d("cvvv", "otherTaskUsers: taskId: $taskId")
                            getTasks(taskId)
                        }
                    }
                }
        }
    }

    private fun getTasks(taskId: String) {
        FirebaseFirestore.getInstance().collection(TASKS_COLLECTION_REF).document(taskId).get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    val mTasks = snapshot.toObject(Tasks::class.java)
                    taskList.add(mTasks!!)
                    Log.d("cvvv", "getTasks: $mTasks")
                    liveData.value = true
                }
            }
    }

    @Composable
    fun MyTasks(list: List<Tasks>) {
        Column() {

            ConstraintLayout(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
            ) {
                val (backBtn, titleRef) = createRefs()

                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = null,
                    modifier = Modifier
                        .size(24.dp)
                        .constrainAs(backBtn) {
                            top.linkTo(parent.top)
                            bottom.linkTo(parent.bottom)
                            start.linkTo(parent.start, 20.dp)
                        }
                        .clickable {
                            finish()
                        }
                )

                Text(
                    text = "Assigned Tasks",
                    style = androidx.compose.material.MaterialTheme.typography.h5,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Clip,
                    modifier = Modifier
                        .padding(4.dp)
                        .constrainAs(titleRef) {
                            top.linkTo(parent.top)
                            bottom.linkTo(parent.bottom)
                            end.linkTo(parent.end)
                            start.linkTo(parent.start)
                        }
                )

            }

            Spacer(modifier = Modifier.size(15.dp))
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),

                ) {
                items(list) { task ->
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
                            tasks = task
                        )
                    }


                }


            }
        }
    }

    @Composable
    fun TaskItem(
        tasks: Tasks,
    ) {
        Card(
            modifier = Modifier
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
                    style = androidx.compose.material.MaterialTheme.typography.h6,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Clip,
                    modifier = Modifier.padding(4.dp)
                )
                Spacer(modifier = Modifier.size(4.dp))
                CompositionLocalProvider(
                    LocalContentAlpha provides ContentAlpha.disabled
                ) {
                    androidx.compose.material.Text(
                        text = tasks.description,
                        style = androidx.compose.material.MaterialTheme.typography.body1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(4.dp),
                        maxLines = 4
                    )

                }

                Spacer(modifier = Modifier.size(4.dp))
                CompositionLocalProvider(
                    LocalContentAlpha provides ContentAlpha.disabled
                ) {
                    androidx.compose.material.Text(
                        text = "Created by: " + tasks.createdBy,
                        style = androidx.compose.material.MaterialTheme.typography.body1,
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
                    androidx.compose.material.Text(
                        text = formatDate(tasks.timestamp),
                        style = androidx.compose.material.MaterialTheme.typography.body1,
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

}

