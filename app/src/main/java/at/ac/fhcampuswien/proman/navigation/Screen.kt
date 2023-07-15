package at.ac.fhcampuswien.proman.navigation

const val Project_ARGUMENT_KEY = "projectId"
const val Task_ARGUMENT_KEY = "taskId"

sealed class Screen(val route: String) {
    object SplashScreen : Screen("splash_screen")
    object StartScreen : Screen("Start_screen")
    object LoginScreen : Screen("Login_screen")
    object RegisterScreen : Screen("Register_screen")
    object ProjectScreen : Screen("Project_screen")



    object TaskScreen : Screen("task/{$Project_ARGUMENT_KEY}") {
        fun withId(projectId: String): String {
            return this.route.replace(oldValue = "{$Project_ARGUMENT_KEY}", newValue = projectId)
        }
    }

    object DetailScreen : Screen("detail/{$Task_ARGUMENT_KEY}") {
        fun withId(id: String): String {
            return this.route.replace(oldValue = "{$Task_ARGUMENT_KEY}", newValue = id)
        }
    }

}

enum class NestedRoute {
    Main,
    Login
}