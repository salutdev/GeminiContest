package com.example.runpython.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.runpython.models.deserializers.JsonStringFieldDeserializer
import com.example.runpython.models.deserializers.TopicsFieldDeserializer
import com.google.gson.annotations.JsonAdapter

@Entity
data class Task (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val taskTitle: String = "",
    val taskStatement: String = "",
    val level: String = "",
    val timeComplexity: String = "",
    val spaceComplexity: String = "",
    @JsonAdapter(TopicsFieldDeserializer::class) val topics: String = "",
    @JsonAdapter(JsonStringFieldDeserializer::class) val testCases: String = "",
    val solution: String?  = ""
)
