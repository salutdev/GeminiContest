package com.example.runpython.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.runpython.models.Task

@Database(entities = [Task::class], version = 1)
abstract class TaskDatabase : RoomDatabase() {
    companion object {
        const val NAME = "TaskDb"
    }

    abstract fun getTaskDao(): TaskDao
}