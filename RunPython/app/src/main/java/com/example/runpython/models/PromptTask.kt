package com.example.runpython.models

import androidx.room.Entity

@Entity
data class PromptTask (
    val taskTitle: String,
    val taskStatement: String,
)
