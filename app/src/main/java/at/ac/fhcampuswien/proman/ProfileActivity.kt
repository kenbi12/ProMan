package at.ac.fhcampuswien.proman

import android.Manifest
import android.R.attr.previewImage
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import at.ac.fhcampuswien.proman.repository.User_COLLECTION_REF
import at.ac.fhcampuswien.proman.ui.theme.ProManTheme
import at.ac.fhcampuswien.proman.ui.theme.Purple500
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage


class ProfileActivity : ComponentActivity() {

    private val auth = FirebaseAuth.getInstance()
    private val uid = auth.currentUser?.uid.toString()
    private val ref = Firebase.firestore.collection(User_COLLECTION_REF)

    private val storage = FirebaseStorage.getInstance().reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProManTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    ProfileScreen()
                }
            }
        }
    }

    // Check if the permission is already granted
    private fun isReadStoragePermissionGranted(): Boolean {
        val permission = if (Build.VERSION.SDK_INT < 33) Manifest.permission.READ_EXTERNAL_STORAGE
        else Manifest.permission.READ_MEDIA_IMAGES

        val result = ContextCompat.checkSelfPermission(this, permission)
        return result == PackageManager.PERMISSION_GRANTED
    }

    private fun requestReadStoragePermission() {
        val permission = if (Build.VERSION.SDK_INT < 33) Manifest.permission.READ_EXTERNAL_STORAGE
        else Manifest.permission.READ_MEDIA_IMAGES

        ActivityCompat.requestPermissions(this, arrayOf(permission), 99)
    }


    @Composable
    fun ProfileScreen() {

        val uri = remember {
            mutableStateOf("")
        }
        val name = remember {
            mutableStateOf("")
        }

        val launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent(),
            onResult = {
                if (it != null) {
                    uri.value = it.toString()
                }
            }
        )

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
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
                    text = "Profile",
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

            Spacer(modifier = Modifier.size(40.dp))
            AsyncImage(model = uri.value,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(100.dp)
                    .background(
                        shape = RoundedCornerShape(50.dp), color = Color.White
                    )
                    .clickable {
                        if (isReadStoragePermissionGranted()) {
                            launcher.launch("image/*");
                        } else {
                            requestReadStoragePermission()
                        }
                    })

            Spacer(modifier = Modifier.size(10.dp))
            OutlinedTextField(modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
                value = name.value,
                onValueChange = {
                    name.value = it
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                    )
                },
                label = {
                    Text(text = "Name", color = Color.DarkGray)
                })

            Button(
                onClick = {
                    if (name.value.isNotEmpty() && uri.value.isNotEmpty()) {

                        storage.child(uid).putFile(uri.value.toUri())
                            .addOnSuccessListener { taskSnapshot ->
                                val downloadUrlTask = taskSnapshot.storage.downloadUrl
                                downloadUrlTask.addOnSuccessListener { downloadUri ->
                                    val downloadUrl = downloadUri.toString()
                                    // Use the download URL as needed
                                    //  Log.d("url",downloadUrl)
                                    val updateData = hashMapOf<String, Any>(
                                        "url" to downloadUrl,
                                        "name" to name.value,
                                    )
                                    ref.document(uid).update(updateData).addOnSuccessListener {
                                        Toast.makeText(
                                            this@ProfileActivity,
                                            "Upload Success",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        finish()
                                    }
                                }.addOnFailureListener { exception ->
                                    // Handle any errors while getting the download URL
                                    Toast.makeText(
                                        this@ProfileActivity,
                                        "Upload Failed",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }.addOnFailureListener { exception ->
                                // Handle any errors during the upload
                                Toast.makeText(
                                    this@ProfileActivity,
                                    "Upload Failed",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                    } else if (name.value.isNotEmpty()) {

                        val updateData = hashMapOf<String, Any>(
                            "name" to name.value
                        )
                        ref.document(uid).update(updateData).addOnSuccessListener {
                            Toast.makeText(
                                this@ProfileActivity,
                                "Upload Success",
                                Toast.LENGTH_SHORT
                            ).show()
                        }.addOnFailureListener {
                            Toast.makeText(
                                this@ProfileActivity,
                                "Upload Failed",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    } else if (uri.value.isNotEmpty()) {
                        storage.child(uid).putFile(uri.value.toUri())
                            .addOnSuccessListener { taskSnapshot ->
                                val downloadUrlTask = taskSnapshot.storage.downloadUrl
                                downloadUrlTask.addOnSuccessListener { downloadUri ->
                                    val downloadUrl = downloadUri.toString()
                                    // Use the download URL as needed
                                    val updateData = hashMapOf<String, Any>(
                                        "url" to downloadUrl
                                    )
                                    ref.document(uid).update(updateData).addOnSuccessListener {
                                        Toast.makeText(
                                            this@ProfileActivity,
                                            "Upload Success",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        finish()
                                    }.addOnFailureListener {
                                        Log.d("url exp", it.message.toString())
                                        Toast.makeText(
                                            this@ProfileActivity,
                                            "Upload Failed",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }.addOnFailureListener { exception ->
                                    // Handle any errors while getting the download URL
                                    Toast.makeText(
                                        this@ProfileActivity,
                                        "Upload Failed",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }.addOnFailureListener { exception ->
                                // Handle any errors during the upload
                                Toast.makeText(
                                    this@ProfileActivity,
                                    "Upload Failed",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                    }
                },
                colors = ButtonDefaults.buttonColors(backgroundColor = Purple500),
            ) {

                Text(
                    text = "Update", style = TextStyle(
                        color = Color.White,
                        fontSize = 14.sp,
                    )
                )
            }


        }

    }
}
