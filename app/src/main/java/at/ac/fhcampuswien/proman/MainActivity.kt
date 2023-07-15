package at.ac.fhcampuswien.proman

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import at.ac.fhcampuswien.proman.navigation.ScreensNavigation
import at.ac.fhcampuswien.proman.ui.theme.ProManTheme
import at.ac.fhcampuswien.proman.viewmodels.DetailViewModel
import at.ac.fhcampuswien.proman.viewmodels.LoginViewModel
import at.ac.fhcampuswien.proman.viewmodels.TaskViewModel
import at.ac.fhcampuswien.proman.viewmodels.ProjectViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val loginViewModel = viewModel(modelClass = LoginViewModel::class.java)
            val taskViewModel = viewModel(modelClass = TaskViewModel::class.java)
            val detailViewModel = viewModel(modelClass = DetailViewModel::class.java)
            val projectViewModel = viewModel(modelClass = ProjectViewModel::class.java)
            ProManTheme(darkTheme = false) {

                ScreensNavigation(
                    loginViewModel = loginViewModel,
                    detailViewModel = detailViewModel,
                    taskViewModel = taskViewModel,
                    projectViewModel = projectViewModel,
                    mContext = this
                )

            }
        }
    }
}
