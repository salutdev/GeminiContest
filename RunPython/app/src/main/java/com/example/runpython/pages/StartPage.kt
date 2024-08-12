package com.example.runpython.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.runpython.shared.Routes

@Composable
fun StartPage(navController: NavController, selectedPageState: MutableState<String>) {
    Box(modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center) {

        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally) {

            Button(onClick = {
                selectedPageState.value = Routes.taskGenerationPage
                navController.navigate(Routes.taskGenerationPage)
            }) {
                Text("Generate challenge with Gemini")
            }

            Spacer(modifier = Modifier.height(30.dp))

            Button(onClick = {
                selectedPageState.value = Routes.taskListPage
                navController.navigate(Routes.taskListPage)
            }) {
                Text("Challenge list")
            }

            Spacer(modifier = Modifier.height(30.dp))

            Button(onClick = {
                selectedPageState.value = Routes.codePage
                navController.navigate("${Routes.codePage}/0")
            }) {
                Text("Сode Editor")
            }
        }
    }
}