package org.catrobat.catroid.content.actions

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.ViewGroup
import android.widget.*
import com.badlogic.gdx.scenes.scene2d.Action
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.catrobat.catroid.CatroidApplication
import org.catrobat.catroid.content.Scope
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.formulaeditor.UserVariable
import org.catrobat.catroid.stage.StageActivity
import org.json.JSONArray
import java.io.File
import kotlin.math.max
import kotlin.math.min

class ChatResponseAction : Action() {
    private var scope: Scope? = null
    var questionFormula: Formula? = null
    var answerVariable: UserVariable? = null

    companion object {
        private var currentContainer: LinearLayout? = null
    }

    private var isFirstMessage = true

    override fun act(delta: Float): Boolean {
        if (!ReadChatListFromDeviceAction.isFresh) {
            showToast("⚠️ Please select a training file before training.")
            return true
        }
        displayChatUI()
        ReadChatListFromDeviceAction.isFresh = false
        return true
    }

    private fun displayChatUI() {
        val activity = StageActivity.activeStageActivity.get() ?: return

        val trainfilenameaiml = ReadChatListFromDeviceAction.jsontransfer
        if (trainfilenameaiml.isNullOrBlank()) {
            activity.runOnUiThread {
                Toast.makeText(
                    activity,
                    "❗ Please select a training data file before starting the chat.",
                    Toast.LENGTH_LONG
                ).show()
            }
            return
        }

        val filename = "/data/user/0/org.catrobat.catroid/files/catroidchatbot/$trainfilenameaiml.json"

        activity.runOnUiThread {
            currentContainer?.let { (it.parent as? ViewGroup)?.removeView(it) }

            val container = LinearLayout(activity).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(24, 24, 24, 24)
                background = GradientDrawable().apply {
                    shape = GradientDrawable.RECTANGLE
                    setColor(Color.parseColor("#FAFAFA"))
                    cornerRadius = 20f
                    setStroke(2, Color.parseColor("#BDBDBD"))
                }
            }

            val scrollView = ScrollView(activity).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f
                ).apply { bottomMargin = 16 }
            }

            val chatLayout = LinearLayout(activity).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(8, 8, 8, 8)
            }

            scrollView.addView(chatLayout)

            val questionText = try {
                questionFormula?.interpretString(scope)?.takeIf { it.isNotBlank() }
            } catch (e: Exception) { null }

            val inputField = EditText(activity).apply {
                hint = if (isFirstMessage && !questionText.isNullOrEmpty()) questionText else "Write your message"
                textSize = 16f
                setTextColor(Color.BLACK)
                setHintTextColor(Color.GRAY)
                background = GradientDrawable().apply {
                    cornerRadius = 50f
                    setStroke(2, Color.parseColor("#DDDDDD"))
                    setColor(Color.WHITE)
                }
                setPadding(40, 20, 40, 20)
            }

            val sendButton = Button(activity).apply {
                text = "➤"
                textSize = 20f
                setTextColor(Color.WHITE)
                background = GradientDrawable().apply {
                    shape = GradientDrawable.OVAL
                    setColor(Color.parseColor("#4CAF50"))
                }
                setPadding(20, 20, 20, 20)
                setOnClickListener {
                    var userText = inputField.text.toString()
                    if (isFirstMessage && userText.isBlank() && !questionText.isNullOrEmpty()) {
                        userText = questionText
                    }

                    if (userText.isNotBlank()) {
                        val userMessage = TextView(activity).apply {
                            text = userText
                            textSize = 16f
                            setTextColor(Color.DKGRAY)
                            background = GradientDrawable().apply {
                                cornerRadius = 30f
                                setColor(Color.parseColor("#C8E6C9"))
                            }
                            setPadding(24, 16, 24, 16)
                            layoutParams = LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                            ).apply {
                                gravity = Gravity.START
                                topMargin = 12
                            }
                        }

                        val botReply = getBotReply(activity, filename, userText)

                        val botMessage = TextView(activity).apply {
                            text = botReply
                            textSize = 16f
                            setTextColor(Color.BLACK)
                            background = GradientDrawable().apply {
                                cornerRadius = 30f
                                setColor(Color.parseColor("#D1C4E9"))
                            }
                            setPadding(24, 16, 24, 16)
                            layoutParams = LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                            ).apply {
                                gravity = Gravity.END
                                topMargin = 12
                            }
                        }

                        chatLayout.addView(userMessage)
                        chatLayout.addView(botMessage)

                        answerVariable?.value = botReply
                        inputField.text.clear()
                        inputField.hint = "Write your message"
                        scrollView.post { scrollView.fullScroll(ScrollView.FOCUS_DOWN) }
                        isFirstMessage = false
                    }
                }
            }

            val inputRow = LinearLayout(activity).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
                setPadding(0, 8, 0, 0)
                addView(inputField, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply {
                    rightMargin = 12
                })
                addView(sendButton, LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ))
            }

            container.addView(scrollView)
            container.addView(inputRow)

            val layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            ).apply { setMargins(16, 16, 16, 16) }

            activity.addContentView(container, layoutParams)
            currentContainer = container
        }
    }

    private fun detectIntent(input: String): String {
        val lower = input.lowercase()
        return when {
            lower.contains("how many") || lower.contains("number of") -> "count"
            lower.contains("what is") || lower.contains("define") -> "definition"
            lower.contains("where is") || lower.contains("location") -> "location"
            lower.contains("who is") -> "person"
            lower.contains("when") || lower.contains("founded") -> "time"
            else -> "unknown"
        }
    }

    private val knownEntities = listOf("nsu", "diu", "buet", "cse", "ict", "mct", "ewu", "students")

    private fun extractEntities(input: String): List<String> {
        return input.lowercase()
            .split(Regex("\\W+"))
            .filter { it in knownEntities }
    }

    private fun levenshtein(s1: String, s2: String): Int {
        val dp = Array(s1.length + 1) { IntArray(s2.length + 1) }
        for (i in 0..s1.length) dp[i][0] = i
        for (j in 0..s2.length) dp[0][j] = j
        for (i in 1..s1.length) {
            for (j in 1..s2.length) {
                val cost = if (s1[i - 1] == s2[j - 1]) 0 else 1
                dp[i][j] = min(
                    min(dp[i - 1][j] + 1, dp[i][j - 1] + 1),
                    dp[i - 1][j - 1] + cost
                )
            }
        }
        return dp[s1.length][s2.length]
    }

    private fun getBotReply(context: Context, filename: String, userInput: String): String {
        val dataset = readChatbotData(context, filename)
        val intent = detectIntent(userInput)
        val entities = extractEntities(userInput)

        var bestResponse: String? = null
        var lowestDistance = Int.MAX_VALUE

        // 1. Strict match (intent + entity)
        for (i in 0 until dataset.length()) {
            val item = dataset.optJSONObject(i)
            val itemIntent = item.optString("intent")
            val itemEntities = item.optJSONArray("entities")?.let { 0.until(it.length()).map { j -> it.getString(j) } } ?: listOf()

            if (itemIntent == intent && entities.any { it in itemEntities }) {
                return item.optString("response")
            }
        }

        // 2. Relaxed match (intent only)
        for (i in 0 until dataset.length()) {
            val item = dataset.optJSONObject(i)
            val itemIntent = item.optString("intent")
            if (itemIntent == intent) {
                return item.optString("response")
            }
        }

        // 3. Fuzzy fallback
        for (i in 0 until dataset.length()) {
            val item = dataset.optJSONObject(i)
            val itemInput = item.optString("input")
            val distance = levenshtein(userInput.lowercase(), itemInput.lowercase())
            val similarity = 1.0 - (distance.toDouble() / max(userInput.length, itemInput.length))

            if (distance < lowestDistance && similarity >= 0.5) {
                lowestDistance = distance
                bestResponse = item.optString("response")
            }
        }

        return bestResponse ?: "Sorry, I’m not sure how to answer that. Try rephrasing your question."
    }

    private fun readChatbotData(context: Context, filename: String): JSONArray {
        return try {
            val file = File(filename)
            if (!file.exists()) JSONArray() else JSONArray(file.readText())
        } catch (e: Exception) {
            JSONArray()
        }
    }
    private fun showToast(message: String) {
        val context = CatroidApplication.getAppContext()
        CoroutineScope(Dispatchers.Main).launch {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }
    }
    fun setScope(scope: Scope) { this.scope = scope }
    fun setFormula(formula: Formula) { this.questionFormula = formula }
    override fun reset() { scope = null; questionFormula = null }
}