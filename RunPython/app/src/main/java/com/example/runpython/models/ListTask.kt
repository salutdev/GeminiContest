package com.example.runpython.models

import androidx.room.Entity

@Entity
data class ListTask(
    val id: Int,
    val taskTitle: String,
    val level: String,
)