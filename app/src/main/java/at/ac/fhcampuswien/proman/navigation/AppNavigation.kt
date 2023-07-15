package at.ac.fhcampuswien.proman.navigation


import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionContext

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navigation
import at.ac.fhcampuswien.proman.constants.MyConstant
import at.ac.fhcampuswien.proman.screens.*
import at.ac.fhcampuswien.proman.viewmodels.DetailViewModel
import at.ac.fhcampuswien.proman.viewmodels.LoginViewModel
import at.ac.fhcampuswien.proman.viewmodels.ProjectViewModel
import at.ac.fhcampuswien.proman.viewmodels.TaskViewModel

@Composable
fun ScreensNavigation(
    navController: NavHostController = rememberNavController(),
    loginViewModel: LoginViewModel,
    detailViewModel: DetailViewModel,
    taskViewModel: TaskViewModel,
    projectViewModel: ProjectViewModel,
    mContext: Context
) {

    NavHost(
        navController = navController,
        startDestination = NestedRoute.Login.name//Screen.SplashScreen.route

    ) {

        authGraph(navController, loginViewModel)
        homeGraph(
            mContext = mContext,
            navController = navController,
            detailViewModel,
            taskViewModel,
            projectViewModel
        )
    }
}


fun NavGraphBuilder.authGraph(
    navController: NavHostController,
    loginViewModel: LoginViewModel,
) {

    navigation(
        startDestination = Screen.SplashScreen.route,
        route = NestedRoute.Login.name
    ) {
        composable(route = Screen.SplashScreen.route) {
            SplashScreen(navController = navController)
        }
        composable(route = Screen.StartScreen.route) {
            StartScreen(navController = navController)
        }
        composable(route = Screen.LoginScreen.route) {
            LoginScreen(
                onNavToHomePage = {
                    navController.navigate(NestedRoute.Main.name) {
                        launchSingleTop = true
                        popUpTo(route = Screen.LoginScreen.route) {
                            inclusive = true
                        }
                    }
                },
                loginViewModel = loginViewModel

            ) {
                navController.navigate(Screen.RegisterScreen.route) {
                    launchSingleTop = true
                    popUpTo(Screen.LoginScreen.route) {
                        inclusive = true
                    }
                }
            }
        }


        composable(route = Screen.RegisterScreen.route) {
            RegisterScreen(
                onNavToHomePage = {
                    navController.navigate(NestedRoute.Main.name) {
                        popUpTo(Screen.RegisterScreen.route) {
                            inclusive = true
                        }
                    }
                },
                loginViewModel = loginViewModel
            ) {
                navController.navigate(Screen.LoginScreen.route)
            }

        }

    }
}


fun NavGraphBuilder.homeGraph(
    mContext: Context,
    navController: NavHostController,
    detailViewModel: DetailViewModel,
    taskViewModel: TaskViewModel,
    projectViewModel: ProjectViewModel
) {
    navigation(
        startDestination = Screen.ProjectScreen.route,
        route = NestedRoute.Main.name
    ) {
        val proUiState = projectViewModel.proUiState


        composable(Screen.ProjectScreen.route) {
            ProjectScreen(
                context = mContext,
                navController = navController,
                projectViewModel = projectViewModel,
                onProjectClick = { projectID ->
                    navController.navigate(
                        Screen.TaskScreen.route + "?projectId=${proUiState.projectName}"
                    ) {
                        launchSingleTop = true
                    }
                }) {
                navController.navigate(Screen.StartScreen.route) {
                    launchSingleTop = true
                    popUpTo(0) {
                        inclusive = true
                    }
                }
            }
        }



        composable(
            route = Screen.TaskScreen.route + "?projectId={projectId}",
            arguments = listOf(navArgument("projectId") {
                type = NavType.StringType
                defaultValue = ""
            })
        ) { backStackEntry ->

            TaskScreen(
                projectViewModel = projectViewModel,
                taskViewModel = taskViewModel,
                onTaskClick = { taskId ->
                    navController.navigate(
                        Screen.DetailScreen.route + "?id=$taskId"
                    ) {
                        launchSingleTop = true
                    }
                },
                navToDetailPage = {
                    navController.navigate(Screen.DetailScreen.route)
                },
                projectId = backStackEntry.arguments?.getString("projectId") as String,
            )
        }

        composable(
            route = Screen.DetailScreen.route + "?id={id}",
            arguments = listOf(navArgument("id") {
                type = NavType.StringType
                defaultValue = ""
            })
        ) { entry ->
            MyConstant.taskId = entry.arguments?.getString("id") as String
            DetailScreen(
                context = mContext,
                detailViewModel = detailViewModel,
                taskId = entry.arguments?.getString("id") as String,
            ) {
                navController.navigateUp()
            }


        }
    }

}





