package com.example.runpython

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.runpython.pages.composibles.AppNavigation
import com.example.runpython.shared.Routes
import com.example.runpython.ui.theme.RunPythonTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val context = LocalContext.current
            val view = LocalView.current
            val window = (context as Activity).window

            window.statusBarColor = MaterialTheme.colorScheme.primary.toArgb()
            window.navigationBarColor = MaterialTheme.colorScheme.surfaceDim.toArgb()

            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = false // Use this for light or dark status bar text/icons
            }

            RunPythonTheme {
                DisplayContent()
            }
        }
    }

    @Composable
    fun DisplayContent() {

        val drawerState = rememberDrawerState(DrawerValue.Closed)
        val navController = rememberNavController()
        var selectedPageState = rememberSaveable {
            mutableStateOf("")
        }

        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                DrawerContent(navController, drawerState, selectedPageState)
            }
        ) {
            AppNavigation(navController, drawerState, selectedPageState)
        }
    }

    @Composable
    fun DrawerContent(navController: NavController, drawerState: DrawerState, selectedPageState: MutableState<String>) {

        val scope = rememberCoroutineScope()

        BackHandler(enabled = drawerState.isOpen) {
            scope.launch {
                drawerState.close()
            }
        }

        ModalDrawerSheet {
            Text(
                text = "Menu",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(16.dp)
            )
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))
            DisplayNavigationDrawerItem(
                navController,
                Routes.taskGenerationPage,
                "Challenge Generation",
                Routes.taskGenerationPage,
                selectedPageState,
                scope,
                drawerState,
                Icons.Default.Build)

            DisplayNavigationDrawerItem(
                navController,
                Routes.taskListPage,
                "Challenge List",
                Routes.taskListPage,
                selectedPageState,
                scope,
                drawerState,
                Icons.AutoMirrored.Filled.List)

            DisplayNavigationDrawerItem(
                navController,
                "${Routes.codePage}/0",
                "Code Editor",
                Routes.codePage,
                selectedPageState,
                scope,
                drawerState,
                Icons.Default.PlayArrow)
        }
    }

    @Composable
    private fun DisplayNavigationDrawerItem(
        navController: NavController,
        url: String,
        title: String,
        pageName: String,
        selectedPageState: MutableState<String>,
        scope: CoroutineScope,
        drawerState: DrawerState,
        icon: ImageVector
    ) {
        NavigationDrawerItem(label = { Text(title) },
            selected = selectedPageState.value == pageName,
            onClick = {
                selectedPageState.value = pageName
                scope.launch {
                    drawerState.close()
                }

                navController.navigate(url)
            },
            icon = {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = Color.Black
                )
            },
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
        )
    }
}
