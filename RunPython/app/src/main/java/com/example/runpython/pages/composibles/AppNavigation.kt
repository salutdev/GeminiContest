package com.example.runpython.pages.composibles

import androidx.compose.material3.DrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.runpython.shared.Routes
import com.example.runpython.pages.DisplayTask
import com.example.runpython.pages.RunCodePage
import com.example.runpython.pages.StartPage
import com.example.runpython.pages.TaskGenerationPage
import com.example.runpython.pages.DisplayTaskList
import com.example.runpython.pages.EditTask

@Composable
fun AppNavigation(navController: NavHostController, drawerState: DrawerState, selectedPageState: MutableState<String>) {

    NavHost(navController = navController, startDestination = Routes.startPage) {

        composable(Routes.startPage) {
            StartPage(navController, selectedPageState)
        }

        composable(Routes.taskGenerationPage) {
            TaskGenerationPage(navController, drawerState)
        }

        composable(Routes.taskListPage) {
            DisplayTaskList(navController, drawerState)
        }

        composable(Routes.codePage + "/{taskId}") {
            val taskId = it.arguments?.getString("taskId")?.toInt() ?: 0
            RunCodePage(navController, taskId, drawerState)
        }

        composable("${Routes.taskPage}/{taskId}") {
            val taskId = it.arguments?.getString("taskId")?.toInt() ?: 0
            DisplayTask(navController, taskId, selectedPageState)
        }

        composable("${Routes.taskEditPage}/{taskId}") {
            val taskId = it.arguments?.getString("taskId")?.toInt() ?: 0
            EditTask(navController, taskId, selectedPageState)
        }
    }
}