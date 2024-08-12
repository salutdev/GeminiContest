package com.example.runpython.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.runpython.models.ListTask
import com.example.runpython.models.PromptTask
import com.example.runpython.models.Task

@Dao
interface TaskDao {

    @Query("select * from Task")
    fun getAllTasks(): LiveData<List<Task>>

    @Query("select id, taskTitle, level from Task")
    fun getAllTasksForList():  List<ListTask>

    @Query("select taskTitle, taskStatement  from Task")
    fun getAllTasksForPrompt(): List<PromptTask>

    @Query("select * from Task where id = :id")
    fun getTaskById(id: Int): Task

    @Insert
    fun addTask(task: Task)

    @Query("update Task set solution = :solution  where id = :taskId")
    fun updateSolution(taskId: Int, solution: String)

    @Update
    fun updateTask(task: Task)

    @Query("delete from Task where id = :id")
    fun deleteTask(id: Int)
}