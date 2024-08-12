package com.example.runpython.viewmodels

import android.app.Application
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.runpython.App
import com.example.runpython.models.PromptTask
import com.example.runpython.models.Task
import com.example.runpython.models.TestCase
import com.example.runpython.models.deserializers.JsonStringFieldDeserializer
import com.example.runpython.shared.Constants
import com.example.runpython.shared.enums.SaveStage
import com.google.ai.client.generativeai.GenerativeModel
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.IOException

class TaskGenerationViewModel(application: Application) : AndroidViewModel(application) {

    private val context = getApplication<Application>().applicationContext

    private val taskDao = App.taskDatabase.getTaskDao()

//    val promptTaskList by lazy {
//        mutableStateListOf<Task>()
//    }

    var promptTaskList: MutableList<PromptTask>? = null

    var task: Task? by mutableStateOf(null)

    private val _testCases = MutableStateFlow<List<TestCase>?>(null)
    val testCases: StateFlow<List<TestCase>?> = _testCases

    private var _saveStage = MutableStateFlow(SaveStage.INITIAL)
    val saveStage: StateFlow<SaveStage> = _saveStage

    var errorMessage by mutableStateOf("")

    var loading by mutableStateOf(false)
    var saved by mutableStateOf(true)
    var isInitScreen = true

    val generativeModel = GenerativeModel(
        //modelName = "gemini-1.5-flash",
        modelName = "gemini-pro",
        // Access your API key as a Build Configuration variable (see "Set up your API key" above)
        apiKey = Constants.apiKey,
//        generationConfig = generationConfig {
//            temperature = 0.1f
//            topP = 0.5f
//            topK = 10
//        }
    )

    fun generateTask() {
        //MainScope().launch {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                loading = true
                saved = false
                errorMessage = ""
                isInitScreen = false

                val prompt = getPrompt()
                val response = generativeModel.generateContent(prompt)
                val responseJson = response.text.toString()
                val modelResponse = cleanResponseJson(responseJson)

                task = Gson().fromJson(modelResponse, Task::class.java)

                if (task != null) {
                    //Define the type of the list
                    val gsonForDeserialization: Gson = GsonBuilder()
                        .registerTypeAdapter(String::class.java, JsonStringFieldDeserializer())
                        .create()

                    val testCasesListType = object : TypeToken<List<TestCase>>() {}.type
                    _testCases.value = gsonForDeserialization.fromJson(task!!.testCases, testCasesListType)
                }

                if (task?.taskTitle != null && task?.taskStatement != null) {
                    promptTaskList?.add(PromptTask(task!!.taskTitle, task!!.taskStatement))
                }
                Log.i("Response from Gemini", response.text.toString())
            }
            catch (e: JsonSyntaxException) {
                Log.e("Incorrect json", e.message.toString())
                errorMessage = "Model response was not in correct format. Please try again."
                saved = true
            }
            catch (e: Exception) {
                Log.e("Task generation", e.message.toString())
                errorMessage = "Error occurred during challenge generation. Please try again."
                saved = true
            }
            finally {
                loading = false
            }
        }
    }

    fun saveTask() {
        if (task != null) {
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    taskDao.addTask(task!!)
                    _saveStage.value = SaveStage.SAVED
                    saved = true
                }
                catch (e: Exception) {
                    Log.e("Error saving chanlenge", e.message.toString())
                    _saveStage.value = SaveStage.ERROR
                }
            }
        }
    }

    fun resetSaveState() {
        _saveStage.value = SaveStage.INITIAL
    }

    private fun cleanResponseJson(json: String): String =
        json.substring(
            startIndex = json.indexOf('{'),
            endIndex = json.lastIndexOf('}') + 1)

    private fun getPrompt(): String {
        var prompt = ""
        try {
            val assetManager = context.assets
            prompt = assetManager
                .open("prompt.txt")
                .bufferedReader(Charsets.UTF_8)
                .use { inputStream ->
                    inputStream.readText()
                }

            prompt += getPromptAppendix()
        }
        catch (e: IOException) {
            Log.e("Error getting prompt from assets", e.message.toString())
            //e.printStackTrace()
            throw e
        }

        return prompt
    }

    private fun getPromptAppendix(): String {
        var appendix = ""
        if (promptTaskList == null) {
            promptTaskList = taskDao.getAllTasksForPrompt().toMutableList()
        }

        val generatedTasks = Gson().toJson(promptTaskList)
        appendix = "\n Please generate new challenge and don't repeat the following ones. \n ${generatedTasks}"

        return appendix
    }
}