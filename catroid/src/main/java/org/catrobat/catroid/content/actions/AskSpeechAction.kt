/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2022 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * An additional term exception under section 7 of the GNU Affero
 * General Public License, version 3, is available at
 * http://developer.catrobat.org/license_additional_term
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.content.actions

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import android.util.Log
import com.badlogic.gdx.scenes.scene2d.Action
import org.catrobat.catroid.content.Scope
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.formulaeditor.InterpretationException
import org.catrobat.catroid.formulaeditor.SensorHandler
import org.catrobat.catroid.formulaeditor.UserVariable
import org.catrobat.catroid.stage.StageActivity
import org.catrobat.catroid.stage.StageActivity.IntentListener

class AskSpeechAction : Action(), IntentListener {
    var scope: Scope? = null
    var questionFormula: Formula? = null
    var answerVariable: UserVariable? = null
    private var questionAsked = false
    private var answerReceived = false

    private fun askQuestion() {
        StageActivity.messageHandler?.obtainMessage(
            StageActivity.REGISTER_INTENT,
            arrayListOf(this)
        )?.sendToTarget()
        questionAsked = true
    }

    private fun createRecognitionIntent(question: String) =
        Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).also { intent ->
            intent.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            intent.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE,
                SensorHandler.getListeningLanguageSensor()
            )
            if (question.isNotBlank()) {
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, question)
            }
        }

    fun setAnswerText(answer: String) {
        answerVariable?.apply {
            value = answer
        }
        answerReceived = true
    }

    override fun act(delta: Float): Boolean {
        if (!questionAsked) {
            askQuestion()
        }
        return answerReceived
    }

    override fun restart() {
        questionAsked = false
        answerReceived = false
        super.restart()
    }

    override fun getTargetIntent(): Intent {
        val question = try {
            questionFormula?.interpretString(scope) ?: ""
        } catch (e: InterpretationException) {
            Log.e(TAG, "Formula interpretation in ask brick failed")
            ""
        }
        return createRecognitionIntent(question)
    }

    override fun onIntentResult(resultCode: Int, data: Intent) {
        when (resultCode) {
            Activity.RESULT_OK -> {
                val matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                Log.d(TAG, "Speech recognition results: $matches")
                setAnswerText(matches?.elementAtOrNull(0) ?: "")
            }
            Activity.RESULT_CANCELED, Activity.RESULT_FIRST_USER -> setAnswerText("")
            else -> Log.e(TAG, "unhandled speech recognizer resultCode $resultCode")
        }
    }

    companion object {
        private const val TAG = "AskSpeechAction"
    }
}
