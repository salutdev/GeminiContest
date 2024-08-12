package com.example.runpython

import android.app.Application
import androidx.room.Room
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import com.example.runpython.db.TaskDatabase

class App : Application() {

    companion object {
        lateinit var taskDatabase: TaskDatabase
    }

    override fun onCreate() {
        super.onCreate()
        if (!Python.isStarted()) {
            Python.start(AndroidPlatform(this))
        }

        taskDatabase = Room.databaseBuilder(
            applicationContext,
            TaskDatabase::class.java,
            TaskDatabase.NAME
        ).build()
    }
}