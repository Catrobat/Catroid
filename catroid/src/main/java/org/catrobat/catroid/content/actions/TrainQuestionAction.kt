package org.catrobat.catroid.content.actions

import android.util.Log
import android.widget.Toast
import com.badlogic.gdx.scenes.scene2d.Action
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.catrobat.catroid.CatroidApplication
import org.catrobat.catroid.content.Scope
import org.catrobat.catroid.formulaeditor.Formula
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.IOException

class TrainQuestionAction : Action() {
    lateinit var askQuestion: Formula
    lateinit var responseQuestion: Formula
    var question: String? = null
    var response: String? = null
    lateinit var scope: Scope

    override fun act(delta: Float): Boolean {
        question = askQuestion.interpretString(scope)
        response = responseQuestion.interpretString(scope)

        if (!ReadChatListFromDeviceAction.isFresh) {
            showToast("⚠️ Please select a training file before training.")
            return true
        }

        val trainfilenameaiml = ReadChatListFromDeviceAction.jsontransfer
        if (trainfilenameaiml.isNullOrBlank()) {
            showToast("❌ Training filename is missing.")
            return true
        }

        val context = CatroidApplication.getAppContext()
        val file = File(context.filesDir, "catroidchatbot/$trainfilenameaiml.json")
        file.parentFile?.mkdirs() // Ensure directory exists
        val filename = file.absolutePath

        addTrainingPair(filename, question ?: "", response ?: "")
        showToast("✅ Trained: \"$question\" → \"$response\"")

        ReadChatListFromDeviceAction.isFresh = false
        return true
    }

    private fun addTrainingPair(filename: String, userInput: String, botReply: String) {
        val dataset = readChatbotDataFilePath(filename)

        val pair = JSONObject().apply {
            put("input", userInput.lowercase())
            put("response", botReply)
        }
        dataset.put(pair)

        Log.d("TrainQuestionAction", "Adding pair: $userInput → $botReply")
        Log.d("TrainQuestionAction", "Dataset before write:\n${dataset.toString(2)}")

        writeChatbotDataFilePath(filename, dataset)
    }

    private fun writeChatbotDataFilePath(filename: String, trainingData: JSONArray) {
        try {
            val file = File(filename)
            file.parentFile?.mkdirs()
            file.writeText(trainingData.toString())
            Log.d("TrainQuestionAction", "✅ Data written to file: $filename")
        } catch (e: IOException) {
            Log.e("TrainQuestionAction", "❌ Failed to write file", e)
            showToast("❌ Write failed: ${e.localizedMessage}")
        }
    }

    private fun readChatbotDataFilePath(filename: String): JSONArray {
        return try {
            val file = File(filename)
            if (file.exists()) {
                val json = file.readText()
                JSONArray(json)
            } else {
                JSONArray()
            }
        } catch (e: Exception) {
            Log.e("TrainQuestionAction", "❌ Failed to read file", e)
            JSONArray()
        }
    }

    fun setFormulaQuestion(formula: Formula) {
        this.askQuestion = formula
    }

    fun setFormulaResponse(formula: Formula) {
        this.responseQuestion = formula
    }

    private fun showToast(message: String) {
        val context = CatroidApplication.getAppContext()
        CoroutineScope(Dispatchers.Main).launch {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }
    }
}
