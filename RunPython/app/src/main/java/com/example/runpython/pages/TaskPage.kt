package com.example.runpython.pages

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.runpython.pages.composibles.TaskDetails
import com.example.runpython.shared.Routes
import com.example.runpython.shared.enums.SaveStage
import com.example.runpython.viewmodels.TaskViewModel
import kotlinx.coroutines.flow.filter

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun DisplayTask(navController: NavController, taskId: Int, selectedPageState: MutableState<String>) {

    val taskViewModel: TaskViewModel = viewModel()
    val taskState = taskViewModel.task.collectAsState(initial = null)
    val testCasesState = taskViewModel.testCases.collectAsState(initial = null)
    var showDialog by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }

    fun confirmDelete() {
        taskViewModel.deleteTask(taskId)
        showDialog = false
    }

    fun cancelDelete() {
        showDialog = false
    }

    LaunchedEffect(taskId) {
        taskViewModel.fetchTask(taskId)
    }

    LaunchedEffect(taskViewModel.taskDeletionStage) {
        taskViewModel.taskDeletionStage
            .filter { newTaskDeletionStage -> newTaskDeletionStage != SaveStage.INITIAL }
            .collect { newTaskDeletionStage ->
                if (newTaskDeletionStage == SaveStage.SAVED) {
                    navController.navigateUp()
                }
                else if (newTaskDeletionStage == SaveStage.ERROR) {
                    snackbarHostState.showSnackbar("Error deleting challenge")
                }

                taskViewModel.resetTaskDeletiontState()
            }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding(),
        topBar = {
            TopAppBar(
                title = {
                    Text("Challenge $taskId")
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Go back",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            selectedPageState.value = Routes.codePage
                            navController.navigate("${Routes.codePage}/$taskId")
                        }
                    ) {
                        Icon(imageVector = Icons.Default.PlayArrow, "Solve challenge")
                    }
                    IconButton(onClick = {
                        navController.navigate("${Routes.taskEditPage}/$taskId")
                    }) {
                        Icon(imageVector = Icons.Default.Edit, "Edit challenge")
                    }
                    IconButton(onClick = {
                        showDialog = true
                    }) {
                        Icon(imageVector = Icons.Default.Delete, "Delete challenge")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        TaskDetails(taskState.value, testCasesState.value, true, true, Modifier.padding(innerPadding))

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { cancelDelete() },
                title = { Text("Confirm Delete") },
                text = { Text("Are you sure you want to delete this challenge") },
                confirmButton = {
                    TextButton(onClick = { confirmDelete() }) {
                        Text("Delete")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { cancelDelete() }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}