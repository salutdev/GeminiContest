package com.example.runpython.pages

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.runpython.models.Task
import com.example.runpython.models.TestCase
import com.example.runpython.shared.enums.SaveStage
import com.example.runpython.viewmodels.TaskViewModel
import kotlinx.coroutines.flow.filter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTask(navController: NavController, taskId: Int, selectedPageState: MutableState<String>) {

    val taskViewModel: TaskViewModel = viewModel()

    val testCasesState = taskViewModel.testCases.collectAsState(initial = null)
    val snackbarHostState = remember { SnackbarHostState() }

    var title by remember { mutableStateOf("") }
    var statement by remember { mutableStateOf("") }
    var level by remember { mutableStateOf("") }
    var topics by remember { mutableStateOf("") }
    var timeComplexity by remember { mutableStateOf("") }
    var spaceComplexity by remember { mutableStateOf("") }


    LaunchedEffect(taskId) {
        taskViewModel.fetchTask(taskId)

        taskViewModel.task.collect { task ->
            title = task?.taskTitle ?: ""
            statement = task?.taskStatement ?: ""
            level = task?.level ?: ""
            topics = task?.topics ?: ""
            timeComplexity = task?.timeComplexity ?: ""
            spaceComplexity = task?.spaceComplexity ?: ""
        }
    }

    LaunchedEffect(taskViewModel.taskEditStage) {
        taskViewModel.taskEditStage
            .filter { taskEditStage -> taskEditStage != SaveStage.INITIAL }
            .collect { newTaskEditStage ->
                if (newTaskEditStage == SaveStage.SAVED) {
                    snackbarHostState.showSnackbar("Updated")
                }
                else if (newTaskEditStage == SaveStage.ERROR) {
                    snackbarHostState.showSnackbar("Error updating challenge")
                }

                taskViewModel.resetTaskEditState()
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
                            val task = Task(
                                id = taskViewModel.task.value?.id ?: 0,
                                taskTitle = title,
                                topics = topics,
                                level = level,
                                timeComplexity = timeComplexity,
                                spaceComplexity = spaceComplexity,
                                taskStatement = statement,
                                testCases = taskViewModel.testCasesToJsonString(testCasesState.value)
                                )

                            taskViewModel.updateTask(task)
                        }
                    ) {
                        Icon(imageVector = Icons.Default.Done, "Update challenge")
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

        val testCases = testCasesState.value

        LazyColumn(modifier = Modifier
            .padding(innerPadding)
            .padding(10.dp)) {
            item {
                TextField(
                    value = title,
                    onValueChange = { newValue: String -> title = newValue },
                    enabled = true,
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(15.dp))

                TextField(
                    value = level,
                    onValueChange = { newValue: String -> level = newValue },
                    label = { Text("Level") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(15.dp))

                TextField(
                    value = topics,
                    onValueChange = { newValue: String -> topics = newValue },
                    label = { Text("Topics") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(15.dp))

                TextField(
                    value = timeComplexity,
                    onValueChange = { newValue: String -> timeComplexity = newValue },
                    label = { Text("Time complexity") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(15.dp))

                TextField(
                    value = spaceComplexity,
                    onValueChange = { newValue: String -> spaceComplexity = newValue },
                    label = { Text("Space complexity") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(15.dp))

                TextField(
                    value = statement,
                    onValueChange = { newValue: String -> statement = newValue },
                    label = { Text("Challenge statement") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text("Test cases", fontSize = 18.sp)

                Spacer(modifier = Modifier.height(15.dp))

                testCases?.forEach { testCase ->
                    ItemRow(testCase, onItemChange = { testCase -> taskViewModel.updateTestCase(testCase) })
                }
            }
        }
    }
}

@Composable
fun ItemRow(testCase: TestCase, onItemChange: (TestCase) -> Unit) {
    // Track changes to each item's fields
    var input by remember { mutableStateOf(testCase.input) }
    var output by remember { mutableStateOf(testCase.output) }

    Text(text = "TestCase ${testCase.id}")

    Spacer(modifier = Modifier.height(15.dp))

    TextField(
        value = input,
        onValueChange = { input = it },
        label = { Text("Input") },
        modifier = Modifier.fillMaxWidth()
    )

    Spacer(modifier = Modifier.height(15.dp))

    TextField(
        value = output,
        onValueChange = { output = it },
        label = { Text("Output") },
        modifier = Modifier.fillMaxWidth()
    )

    Spacer(modifier = Modifier.height(20.dp))

    // Pass updated item back to the ViewModel
    LaunchedEffect(input, output) {
        onItemChange(testCase.copy(input = input, output = output))
    }
}