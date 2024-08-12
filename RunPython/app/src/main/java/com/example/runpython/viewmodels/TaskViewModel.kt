package com.example.runpython.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.runpython.App
import com.example.runpython.models.Task
import com.example.runpython.models.TestCase
import com.example.runpython.models.deserializers.JsonStringFieldDeserializer
import com.example.runpython.shared.enums.SaveStage
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class TaskViewModel : ViewModel() {

    val taskDao = App.taskDatabase.getTaskDao()
    private var currentTaskId: Int? = null

    private val _task = MutableStateFlow<Task?>(Task())
    val task: StateFlow<Task?> = _task

    private val _testCases = MutableStateFlow<List<TestCase>?>(null)
    val testCases: StateFlow<List<TestCase>?> = _testCases

    private val _taskDeletionStage = MutableStateFlow(SaveStage.INITIAL)
    val taskDeletionStage: StateFlow<SaveStage> = _taskDeletionStage

    private val _taskEditStage = MutableStateFlow(SaveStage.INITIAL)
    val taskEditStage: StateFlow<SaveStage> = _taskEditStage

    fun updateSolution(solution: String) {
        _task.value = _task.value?.copy(solution = solution)
    }

    fun fetchTask(taskId: Int) {
        if (currentTaskId == taskId) {
            //return // Return if the same task is already being fetched
        }
        currentTaskId = taskId

        viewModelScope.launch(Dispatchers.IO) {
            val task = taskDao.getTaskById(taskId)
            _task.value = task

            if (task != null) {
                val gsonForDeserialization: Gson = GsonBuilder()
                    .registerTypeAdapter(String::class.java, JsonStringFieldDeserializer())
                    .create()

                val testCasesListType = object : TypeToken<List<TestCase>>() {}.type
                _testCases.value = gsonForDeserialization.fromJson(task.testCases, testCasesListType)
            }
        }
    }

    fun deleteTask(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                taskDao.deleteTask(id)
                _taskDeletionStage.value = SaveStage.SAVED
            }
            catch(e: Exception) {
                Log.e("Failed to delete challenge", e.message.toString())
                _taskDeletionStage.value = SaveStage.ERROR
            }
        }
    }

    fun resetTaskDeletiontState() {
        _taskDeletionStage.value = SaveStage.INITIAL
    }

    fun resetTaskEditState() {
        _taskEditStage.value = SaveStage.INITIAL
    }

    fun updateTask(task: Task) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                taskDao.updateTask(task)
                _taskEditStage.value = SaveStage.SAVED
            }
            catch(e: Exception) {
                Log.e("Failed to update challenge", e.message.toString())
                _taskEditStage.value = SaveStage.ERROR
            }
        }
    }

    fun updateTestCase(updatedTestCase: TestCase) {
        val updatedTestCases = _testCases.value?.map { if (it.id == updatedTestCase.id) updatedTestCase else it }
        _testCases.value = updatedTestCases
    }

    fun testCasesToJsonString(testCases: List<TestCase>?): String {
        var testCasesStr = ""
        testCases?.let {
            testCasesStr = Gson().toJson(testCases).replace("\\\"", "")
        }

        return testCasesStr
    }
}