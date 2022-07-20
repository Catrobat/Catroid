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
package org.catrobat.catroid.stage

import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.RecognizerIntent.ACTION_RECOGNIZE_SPEECH
import android.speech.RecognizerIntent.EXTRA_LANGUAGE_MODEL
import android.speech.RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS
import android.speech.RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
import android.speech.SpeechRecognizer
import android.util.Log
import android.view.ContextThemeWrapper
import androidx.appcompat.app.AlertDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.catrobat.catroid.R
import org.catrobat.catroid.formulaeditor.SensorHandler
import java.util.Locale

class SpeechRecognitionHolder : SpeechRecognitionHolderInterface {

    private lateinit var speechRecognizer: SpeechRecognizer
    override var callback: OnSpeechRecognitionResultCallback? = null
    private lateinit var speechIntent: Intent
    private lateinit var listener: RecognitionListener

    companion object {
        private val TAG = SpeechRecognitionHolder::class.java.simpleName
        private const val QUICK_SEARCH_BOX_PKG = "com.google.android.googlequicksearchbox"
        private const val ONE_SECOND = 1000
        private const val FIVE_SECONDS = 5000
    }

    override fun forceSetLanguage() {
        speechIntent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE, SensorHandler.getListeningLanguageSensor()
        )
    }

    override fun initSpeechRecognition(
        stageActivity: StageActivity,
        stageResourceHolder: StageResourceHolder
    ) {

        speechIntent = Intent(ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(EXTRA_LANGUAGE_MODEL, LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, SensorHandler.getListeningLanguageSensor())
            putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, true)
            putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, stageActivity.packageName)
            putExtra(EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, ONE_SECOND)
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, FIVE_SECONDS)
        }

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(stageActivity)

        listener = object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle) {
                Log.d(TAG, "SpeechRecognizer: onReadyForSpeech$params")
            }

            override fun onBeginningOfSpeech() {
                Log.d(TAG, "SpeechRecognizer: onBeginningOfSpeech")
            }

            override fun onRmsChanged(rmsdB: Float) = Unit

            override fun onBufferReceived(buffer: ByteArray) = Unit

            override fun onEndOfSpeech() {
                Log.d(TAG, "SpeechRecognizer: onEndOfSpeech")
            }

            override fun onError(error: Int) {
                Log.e(TAG, "SpeechRecognizer onError: $error")
                when (error) {
                    SpeechRecognizer.ERROR_NO_MATCH -> {
                        speechRecognizer.cancel()
                        startListening()
                    }
                    SpeechRecognizer.ERROR_NETWORK -> {
                        // this error happens only in offline mode
                        // in case the chosen language is not downloaded or outdated
                        showDialog(stageActivity, stageResourceHolder)
                    }
                }
                Log.d(TAG, "SpeechRecognizer restarted!")
            }

            override fun onResults(results: Bundle) {
                // the possible recognition results, where the first element
                // is the most likely candidate.
                val candidates = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                Log.d(TAG, "SpeechRecognizer: onResults:$candidates")

                var spokenWords = ""
                if (candidates != null && candidates.isNotEmpty()) {
                    spokenWords = candidates.first()
                }

                callback?.onResult(spokenWords)
                SensorHandler.startSensorListener(stageActivity)
            }

            override fun onPartialResults(partialResults: Bundle) = Unit

            override fun onEvent(eventType: Int, params: Bundle) = Unit
        }
    }

    override fun startListening() {
        //  needed only for some smart phones like:
        //  Note 8 with ANDROID 9, Xiaomi MI A2 Android 10
        SensorHandler.stopSensorListeners()
        GlobalScope.launch(Dispatchers.Main.immediate) {
            speechRecognizer.setRecognitionListener(listener)
            speechRecognizer.startListening(speechIntent)
        }
    }

    override fun destroy() {
        GlobalScope.launch(Dispatchers.Main.immediate) {
            speechRecognizer.cancel()
            speechRecognizer.destroy()
        }
    }

    private fun showDialog(stageActivity: StageActivity, stageResourceHolder: StageResourceHolder) {
        val builder = AlertDialog
            .Builder(ContextThemeWrapper(stageActivity, R.style.Theme_AppCompat_Dialog))
        builder
            .setTitle(R.string.speech_recognition_offline_mode_error_dialog_title)
            .setMessage(
                stageActivity.getString(
                    R.string.speech_recognition_offline_mode_missing_data_error_dialog_msg,
                    Locale.getDefault().getDisplayName(Locale.getDefault())
                        .toUpperCase(Locale.getDefault())
                )
            )
            .setCancelable(false)
            .setPositiveButton(R.string.yes) { _, _ ->
                stageResourceHolder.endStageActivity()
                openSpeechRecognitionSettings(stageActivity)
            }
            .setNegativeButton(R.string.no) { _, _ ->
                stageResourceHolder.endStageActivity()
            }
            .create()
            .show()
    }

    private fun openSpeechRecognitionSettings(activity: StageActivity) {
        val intent = Intent(Intent.ACTION_MAIN)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        val components = arrayOf(
            ComponentName(
                QUICK_SEARCH_BOX_PKG,
                "com.google.android.apps.gsa.settingsui.VoiceSearchPreferences"
            ),
            ComponentName(
                "com.google.android.voicesearch",
                "com.google.android.voicesearch.VoiceSearchPreferences"
            ),
            ComponentName(
                QUICK_SEARCH_BOX_PKG,
                "com.google.android.voicesearch.VoiceSearchPreferences"
            ),
            ComponentName(
                QUICK_SEARCH_BOX_PKG,
                "com.google.android.apps.gsa.velvet.ui.settings.VoiceSearchPreferences"
            )
        )
        for (componentName in components) {
            try {
                intent.component = componentName
                activity.startActivity(intent)
                break
            } catch (e: ActivityNotFoundException) {
                // PS: this won't ever never happen!!
                Log.e(TAG, "Speech Recognition Engine isn't installed" + e.message)
            }
        }
    }
}
