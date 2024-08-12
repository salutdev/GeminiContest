package com.example.runpython.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chaquo.python.PyException
import com.chaquo.python.Python
import com.example.runpython.App
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CodeViewModel : ViewModel() {

    val taskDao = App.taskDatabase.getTaskDao()

    fun executeCode(code: String): String {
        // 2. Obtain the python instance
        val py = Python.getInstance()

        // 4. Obtain the system's input stream (available from Chaquopy)
        val sys = py.getModule("sys")
        val io = py.getModule("io")

        // Obtain the interpreter.py module
        val console = py.getModule("interpreter")

        // 5. Redirect the system's output stream to the Python interpreter
        val textOutputStream = io.callAttr("StringIO")
        sys["stdout"] = textOutputStream

        // 6. Create a string variable that will contain the standard output of the Python interprete
        var interpreterOutput = ""

        // 7. Execute the Python code
        try {
            console.callAttrThrows("mainTextCode", code)

            interpreterOutput = textOutputStream.callAttr("getvalue").toString()
        } catch (e: PyException) {
            // If there's an error, you can obtain its output as well
            // e.g. if you mispell the code
            // Missing parentheses in call to 'print'
            // Did you mean print("text")?
            // <string>, line 1
            interpreterOutput = e.message.toString()
        } catch (throwable: Throwable) {
            throwable.printStackTrace()
        }

        // Outputs in the case of the fibonacci script:
        // "[0, 1, 1, 2, 3, 5, 8, 13, 21, 34]"
        //println(interpreterOutput)
        return interpreterOutput
    }

    fun updateSolutionInDb(taskId: Int, solution: String) {
        viewModelScope.launch(Dispatchers.IO) {
             taskDao.updateSolution(taskId, solution)
        }
    }
}