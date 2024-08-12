package com.example.runpython.pages.composibles

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.runpython.models.Task
import com.example.runpython.models.TestCase

@Composable
fun TaskDetails(task: Task?, testCases: List<TestCase>?, displayTaskId: Boolean, displaySolution: Boolean, modifier: Modifier = Modifier) {

    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    if(task != null) {
        LazyColumn(modifier = modifier.padding(10.dp)) {
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {

                    val title = if (displayTaskId) "${task.id}. ${task.taskTitle}" else task.taskTitle

                    Text(
                        text = title,
                        modifier = Modifier.weight(1f).padding(end = 10.dp),
                        fontSize = 25.sp
                    )

                FormattedLevel(task.level)
            }

            Spacer(modifier = Modifier.height(8.dp))

                Row {
                    Text("Topics: ", modifier = Modifier.padding(start = 20.dp))
                    Text(task.topics)
                }

                Row {
                    Text("Time complexity: ", modifier = Modifier.padding(start = 20.dp))
                    Text(task.timeComplexity)
                }

                Row {
                    Text("Space complexity: ", modifier = Modifier.padding(start = 20.dp))
                    Text(task.spaceComplexity)
                }

                Spacer(modifier = Modifier.height(15.dp))

                Text(task.taskStatement)

                Spacer(modifier = Modifier.height(20.dp))

                Text("Test cases", fontSize = 18.sp, fontWeight = FontWeight.Bold)

                DisplayTestCases(testCases, clipboardManager, context)

                if (displaySolution && !task.solution.isNullOrEmpty()) {
                    Spacer(modifier = Modifier.height(20.dp))
                    Text("My solution", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(15.dp))
                    Text(task.solution)
                }
            }
        }
    }
}

@Composable
private fun DisplayTestCases(
    testCases: List<TestCase>?,
    clipboardManager: ClipboardManager,
    context: Context
) {
    testCases?.forEach { testCase ->
        Spacer(modifier = Modifier.height(10.dp))

        Text("TestCase ${testCase?.id ?: ""}", fontSize = 16.sp)

        Row(
            modifier = Modifier.padding(start = 10.dp),
            verticalAlignment = Alignment.Top
        ) {
            Text("Input: ")
            BasicText(
                text = AnnotatedString(testCase?.input ?: ""),
                style = TextStyle(
                    color = Color.Black,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier
                    .padding(top = 3.dp)
                    .clickable {
                        // Copy text to clipboard
                        clipboardManager.setText(AnnotatedString(testCase?.input ?: ""))
                        Toast
                            .makeText(
                                context,
                                "Text copied to clipboard",
                                Toast.LENGTH_SHORT
                            )
                            .show()
                    }
            )
        }

        Row(
            modifier = Modifier.padding(start = 10.dp),
            verticalAlignment = Alignment.Top
        ) {
            Text("Output: ")
            BasicText(
                text = AnnotatedString(testCase?.output ?: ""),
                style = TextStyle(
                    color = Color.Black,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier
                    .padding(top = 3.dp)
                    .clickable {
                        // Copy text to clipboard
                        clipboardManager.setText(AnnotatedString(testCase?.output ?: ""))
                        Toast
                            .makeText(
                                context,
                                "Text copied to clipboard",
                                Toast.LENGTH_SHORT
                            )
                            .show()
                    }
            )
        }
    }
}