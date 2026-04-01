package org.catrobat.catroid.content.actions

import android.graphics.Color
import android.graphics.Typeface
import android.view.Gravity
import android.view.ViewGroup
import android.widget.*
import com.badlogic.gdx.scenes.scene2d.Action
import org.catrobat.catroid.R
import org.catrobat.catroid.content.Scope
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.stage.StageActivity

class ChatIfLogicAction : Action() {
    var scope: Scope? = null
    var ifAction: Action? = null
    var elseAction: Action? = null
    var ifCondition: Formula? = null
    private var isInitialized = false
    private var isActionCompleted = false
    private var userInput: String? = null
    private var chatLayout: LinearLayout? = null

    companion object {
        private var existingContainer: FrameLayout? = null

        fun cleanupExistingViews(activity: StageActivity) {
            existingContainer?.let { container ->
                activity.runOnUiThread {
                    (container.parent as? ViewGroup)?.removeView(container)
                }
            }
            existingContainer = null
        }
    }

    private fun setupChatUI() {
        val activity = StageActivity.activeStageActivity.get() ?: return

        activity.runOnUiThread {
            cleanupExistingViews(activity)

            val container = FrameLayout(activity).also { existingContainer = it }

            chatLayout = LinearLayout(activity).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(16, 16, 16, 16)
            }

            val inputLayout = LinearLayout(activity).apply {
                orientation = LinearLayout.HORIZONTAL
                setPadding(20, 16, 20, 16)
                layoutParams = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    gravity = Gravity.BOTTOM
                }
            }

            val editText = EditText(activity).apply {
                hint = "Enter text"
                textSize = 15f
                setPadding(20, 16, 20, 16)
                setTextColor(Color.BLACK)
                setBackgroundResource(android.R.drawable.edit_text)
            }

            val sendButton = Button(activity).apply {
                text = "Send"
                setTextColor(Color.WHITE)
                setBackgroundColor(Color.BLUE)
                setOnClickListener {
                    val input = editText.text.toString()
                    if (input.isNotEmpty()) {
                        handleUserInput(input)
                        editText.text.clear()
                    }
                }
            }

            inputLayout.addView(editText, LinearLayout.LayoutParams(0,
                                                                    LinearLayout.LayoutParams.WRAP_CONTENT, 0.8f))
            inputLayout.addView(sendButton, LinearLayout.LayoutParams(0,
                                                                      LinearLayout.LayoutParams.WRAP_CONTENT, 0.2f))

            container.addView(chatLayout)
            container.addView(inputLayout)

            activity.addContentView(container, ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            ))
        }
    }

    private fun handleUserInput(input: String) {
        userInput = input
        if (checkCondition()) {
            ifAction?.act(0f)
        } else {
            elseAction?.act(0f)
        }
        displayMessage(input)
        isActionCompleted = true
    }

    private fun checkCondition(): Boolean {
        val conditionValue = ifCondition?.interpretString(scope) ?: ""
        return userInput == conditionValue
    }

    private fun displayMessage(message: String) {
        val activity = StageActivity.activeStageActivity.get() ?: return

        activity.runOnUiThread {
            chatLayout?.removeAllViews()

            TextView(activity).apply {
                text = "You entered: $message"
                textSize = 15f
                typeface = Typeface.SANS_SERIF
                setPadding(20, 16, 20, 16)
                setTextColor(Color.BLACK)
                setBackgroundResource(R.drawable.user_bubble)

                chatLayout?.addView(this, LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    gravity = Gravity.START
                    setMargins(16, 100, 16, 16)
                })
            }
        }
    }

    override fun act(delta: Float): Boolean {
        if (!isInitialized) {
            setupChatUI()
            isInitialized = true
        }
        return isActionCompleted
    }

    override fun reset() {
        scope = null
        chatLayout = null
        StageActivity.activeStageActivity.get()?.let { cleanupExistingViews(it) }
        isInitialized = false
        isActionCompleted = false
    }
}