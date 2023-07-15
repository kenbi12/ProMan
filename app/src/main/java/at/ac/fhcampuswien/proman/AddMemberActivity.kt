package at.ac.fhcampuswien.proman

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import at.ac.fhcampuswien.proman.constants.MyConstant
import at.ac.fhcampuswien.proman.repository.OTHER_TASK_REF
import at.ac.fhcampuswien.proman.repository.PROJECTS_COLLECTION_REF
import at.ac.fhcampuswien.proman.ui.theme.ProManTheme
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class AddMemberActivity : ComponentActivity() {
    private val otherTaskRef: CollectionReference =
        Firebase.firestore.collection(OTHER_TASK_REF)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProManTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AddMember()
                }
            }
        }
    }

    @Composable
    fun AddMember() {

        val email = remember {
            mutableStateOf("")
        }
        Column(modifier = Modifier.fillMaxSize()) {
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

                androidx.compose.material3.Text(
                    text = "Add Member",
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
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    value = email.value,
                    onValueChange = { email.value = it },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = null,
                        )
                    },
                    label = {
                        Text(text = "Email", color = Color.DarkGray)
                    }
                )
                Spacer(modifier = Modifier.size(16.dp))

                Button(
                    onClick = {
                        isUserAvailable(email.value)
                    },
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.DarkGray),
                ) {

                    Text(
                        text = "Add Member",
                        style = TextStyle(
                            color = Color.White
                        )
                    )
                }


            }
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    value = email.value,
                    onValueChange = { email.value = it },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = null,
                        )
                    },
                    label = {
                        Text(text = "Email", color = Color.DarkGray)
                    }
                )
                Spacer(modifier = Modifier.size(16.dp))

                Button(
                    onClick = {
                        isUserAvailable(email.value)
                    },
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.DarkGray),
                ) {

                    Text(
                        text = "Add Member",
                        style = TextStyle(
                            color = Color.White
                        )
                    )
                }


            }
        }


    }

    fun isUserAvailable(email: String) {
        FirebaseFirestore.getInstance().collection("Users").whereEqualTo("email", email).get()
            .addOnSuccessListener { snapShot ->
                if (snapShot.size() > 0) {
                    snapShot.forEach {
                        val id = it.id
                        val docId = otherTaskRef.document().id
                        val addMemberData = hashMapOf<String, Any>(
                            "projectId" to MyConstant.projectId,
                            "taskId" to MyConstant.taskId,
                            "userId" to id
                        )
                        otherTaskRef.document(docId).set(addMemberData).addOnSuccessListener {
                            Toast.makeText(this, "User Added", Toast.LENGTH_SHORT).show()
                        }.addOnFailureListener {
                            Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "User Not Available", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
