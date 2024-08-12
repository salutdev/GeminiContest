package com.example.runpython.pages

import android.view.Gravity
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.runpython.models.Task
import com.example.runpython.pages.composibles.TaskDetails
import com.example.runpython.shared.Constants
import com.example.runpython.shared.Routes
import com.example.runpython.viewmodels.CodeViewModel
import com.example.runpython.viewmodels.TaskViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RunCodePage(navController: NavController, taskId: Int, drawerState: DrawerState) {

    val codeViewModel: CodeViewModel = viewModel()

    var result = remember {
        mutableStateOf("")
    }

    var isCardVisible by remember { mutableStateOf(false) }
    var fabCoordinates by remember { mutableStateOf<LayoutCoordinates?>(null) }

    val taskViewModel: TaskViewModel = viewModel()
    val taskState = taskViewModel.task.collectAsState(initial = null)

    var codeChanged by remember {
        mutableStateOf(false)
    }

    if (taskId != 0) {
        LaunchedEffect(taskId) {
            taskViewModel.fetchTask(taskId)
        }
    }

    val scope = rememberCoroutineScope()

    //val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding(),
        floatingActionButton = {
            if (taskId != 0) {
                FloatingActionButton(
                    onClick = { isCardVisible = !isCardVisible },
                    modifier = Modifier
                        .onGloballyPositioned { coordinates ->
                            fabCoordinates = coordinates
                        }
                ) {
                    Icon(Icons.Filled.Info, contentDescription = "Info")
                }
            }
        },
        topBar = {
            TopAppBar(
                title = {
                    Text("Python code editor")
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
                    if (taskId != 0) {
                        IconButton(
                            onClick = {
                                codeViewModel.updateSolutionInDb(
                                    taskId,
                                    taskState.value?.solution ?: ""
                                )
                                codeChanged = false

                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Saved")
                                }
                            },
                            enabled = codeChanged
                        ) {
                            Icon(imageVector = Icons.Default.Done, "Save code")
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    actionIconContentColor = Color.White
                ),
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier
            .padding(innerPadding)
            .padding(16.dp)
        ) {
            OutlinedTextField(
                value = taskState.value?.solution ?: "",
                onValueChange = { text -> taskViewModel.updateSolution(text)
                    codeChanged = true
                },
                enabled = !isCardVisible && drawerState.isClosed,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                singleLine = false
            )

            Text(result.value, modifier = Modifier)

            Row {
                Button(
                    onClick = {
                        result.value = codeViewModel.executeCode(taskState.value?.solution ?: "")
                    },
                ) {
                    Text(text = "Run code")
                }
            }
        }

        if (taskId != 0) {
            AnimatedInfoCard(taskState, innerPadding, isCardVisible)
        }
    }
}

@Composable
private fun AnimatedInfoCard(
    taskState: State<Task?>,
    innerPadding: PaddingValues,
    isCardVisible: Boolean
) {
    val taskInfoCardShift = 57

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(top = taskInfoCardShift.dp),
        contentAlignment = Alignment.BottomEnd
    ) {
        AnimatedVisibility(
            visible = isCardVisible,
            enter = expandIn(
                initialSize = { IntSize(0, 0) },
                animationSpec = tween(durationMillis = 300)
            ) + fadeIn(animationSpec = tween(durationMillis = 300)),
            exit = shrinkOut(
                targetSize = { IntSize(0, 0) },
                animationSpec = tween(durationMillis = 300)
            ) + fadeOut(animationSpec = tween(durationMillis = 300))
        ) {
            InfoCard(
                taskState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .background(Color.White, RoundedCornerShape(8.dp))
                    .offset(y = -taskInfoCardShift.dp)
            )
        }
    }
}

@Composable
fun InfoCard(taskState: State<Task?>, modifier: Modifier = Modifier) {

    val taskViewModel: TaskViewModel = viewModel()
    val testCasesState = taskViewModel.testCases.collectAsState(initial = null)

    Card(
        modifier = modifier.zIndex(10f),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp,
            pressedElevation = 8.dp
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        TaskDetails(taskState.value, testCasesState.value, true, false)
    }
}

//private fun getInitialCode(): String {
//    // 3. Declare some Python code that will be interpreted
//    // In our case, the fibonacci sequence
//    var code = "def fibonacci_of(n):\n"
//    code += "   if n in {0, 1}:  # Base case\n"
//    code += "       return n\n"
//    code += "   return fibonacci_of(n - 2) + fibonacci_of(n - 1)\n"
//    code += "\n"
//    code += "print([fibonacci_of(n) for n in range(10)])\n"
//    return code
//}
