package at.ac.fhcampuswien.proman.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import at.ac.fhcampuswien.proman.R
import at.ac.fhcampuswien.proman.navigation.Screen
import at.ac.fhcampuswien.proman.ui.theme.LoginBackground

@Composable
fun StartScreen(navController: NavHostController){



    Surface(
        color = Color.White,
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(LoginBackground), // Set the background color here

        ) {



    Column(modifier = Modifier.fillMaxSize(),

        //verticalArrangement = Arrangement.Center,
       // horizontalAlignment = Alignment.CenterHorizontally

    ) {
        Image(painter = painterResource(id = R.drawable.login_image),
            contentDescription = "login_Image",
            modifier = Modifier.size(400.dp)

        )
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "ProMan Team.",
                modifier = Modifier.size(120.dp),
                color = Color.White,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
            )
            Divider()
            Text(text = "Start working on your Project now!", color = Color.LightGray)

        }


        Spacer(modifier = Modifier.padding(35.dp))



        Row(modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.Bottom,


        ) {


        Button(
            onClick = { navController.navigate(Screen.LoginScreen.route) },
            modifier = Modifier.height(45.dp),
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.White),
            shape = RoundedCornerShape(5.dp),
            elevation = ButtonDefaults.elevation(
                defaultElevation = 10.dp,
                pressedElevation = 15.dp,
                disabledElevation = 0.dp
            ),


            ) {
            Text(text = "Sign in ")
        }
        Button(
            onClick = { navController.navigate(Screen.RegisterScreen.route) },
            modifier = Modifier.height(45.dp),
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.DarkGray),
            shape = RoundedCornerShape(5.dp),
            elevation = ButtonDefaults.elevation(
                defaultElevation = 10.dp,
                pressedElevation = 15.dp,
                disabledElevation = 0.dp
            )

        ) {
            Text(text = "Register", color = Color.White)
                    }
                }
            }
        }
    }
}