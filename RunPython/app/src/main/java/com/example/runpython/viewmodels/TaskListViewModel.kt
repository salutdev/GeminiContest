package com.example.runpython.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.runpython.App
import com.example.runpython.models.ListTask
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TaskListViewModel : ViewModel() {

    val taskDao = App.taskDatabase.getTaskDao()

    private val _taskList = MutableStateFlow<List<ListTask>>(emptyList())
    val taskList: StateFlow<List<ListTask>> = _taskList

//    init {
//        populateTaskList()
//    }

    fun populateTaskList() {

        viewModelScope.launch(Dispatchers.IO) {
            try {
                _taskList.value = taskDao.getAllTasksForList()
            } catch (e: Exception) {
                Log.e("", e.message.toString())
                throw e
            }
        }
    }
}