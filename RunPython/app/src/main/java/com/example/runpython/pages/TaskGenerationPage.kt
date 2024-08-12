package com.example.runpython.pages

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.runpython.pages.composibles.TaskDetails
import com.example.runpython.shared.enums.SaveStage
import com.example.runpython.viewmodels.TaskGenerationViewModel
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch

@Composable
fun TaskGenerationPage(navController: NavController, drawerState: DrawerState) {

    val taskGenerationViewModel: TaskGenerationViewModel = viewModel()
    val testCasesState = taskGenerationViewModel.testCases.collectAsState(initial = null)

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(taskGenerationViewModel.saveStage) {
        taskGenerationViewModel.saveStage
            .filter { newSaveStage -> newSaveStage != SaveStage.INITIAL }
            .collect { newSaveStage ->
                if (newSaveStage == SaveStage.SAVED) {
                    snackbarHostState.showSnackbar("Saved")
                }
                else if (newSaveStage == SaveStage.ERROR) {
                    snackbarHostState.showSnackbar("Error saving challenge")
                }

                taskGenerationViewModel.resetSaveState()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding(),
        topBar = {
            DisplayTopBar(taskGenerationViewModel, drawerState)
        },
        bottomBar = {
            DisplayBottomBar()
        }
    ) { innerPadding ->

        if (taskGenerationViewModel.loading) {
            DisplayProgressIndicator(innerPadding)
        } else {
            if (taskGenerationViewModel.isInitScreen) {
                DisplayInitialMessage(innerPadding)
            } else if (taskGenerationViewModel.errorMessage == "") {
                TaskDetails(
                    taskGenerationViewModel.task,
                    testCasesState.value,
                    false,
                    Modifier.padding(innerPadding)
                )
            } else {
                DisplayErrorMessage(innerPadding, taskGenerationViewModel)
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun DisplayTopBar(taskGenerationViewModel: TaskGenerationViewModel, drawerState: DrawerState) {

    val scope = rememberCoroutineScope()

    TopAppBar(
        title = {
            Text("Challenge generation")
        },
        navigationIcon = {
            IconButton(onClick = {
                scope.launch {
                    drawerState.open()
                }
            }) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Main menu",
                    tint = Color.White
                )
            }
        },
        actions = {
            IconButton(
                onClick = {
                    taskGenerationViewModel.generateTask()
                },
                enabled = !taskGenerationViewModel.loading
            ) {
                Icon(imageVector = Icons.Default.Build, "Generate challenge")
            }
            IconButton(
                onClick = {
                    taskGenerationViewModel.saveTask()
                },
                enabled = !taskGenerationViewModel.loading && !taskGenerationViewModel.saved
            ) {
                Icon(imageVector = Icons.Default.Done, "Save challenge")
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = Color.White,
            actionIconContentColor = Color.White
        ),
    )
}

@Composable
private fun DisplayBottomBar() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Challenges are generated by AI. At the moment this technology is not perfect. There may be errors in generated content. Please verify all fields. Test cases are especially error prone. You can save a challenge and edit fields.",
            modifier = Modifier.align(Alignment.Center),
            color = Color.Red
        )
    }
}

@Composable
private fun DisplayInitialMessage(innerPadding: PaddingValues) {
    Box(
        modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Click",
                modifier = Modifier.padding(20.dp),
                fontSize = 16.sp
            )

            Icon(imageVector = Icons.Default.Build, "Generate challenge")

            Text(
                text = "to generate challenge.",
                modifier = Modifier.padding(20.dp),
                fontSize = 16.sp
            )
        }
    }
}

@Composable
private fun DisplayErrorMessage(
    innerPadding: PaddingValues,
    taskGenerationViewModel: TaskGenerationViewModel
) {
    Box(
        modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = taskGenerationViewModel.errorMessage,
            modifier = Modifier.padding(20.dp),
            fontSize = 16.sp
        )
    }
}

@Composable
private fun DisplayProgressIndicator(innerPadding: PaddingValues) {
    Box(
        modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = MaterialTheme.colorScheme.secondary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
        )
    }
}
