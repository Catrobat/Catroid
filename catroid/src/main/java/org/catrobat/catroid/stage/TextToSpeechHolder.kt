/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2018 The Catrobat Team
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

import android.content.Context
import android.content.Intent
import android.speech.tts.TextToSpeech
import android.speech.tts.TextToSpeech.OnInitListener
import android.speech.tts.UtteranceProgressListener
import android.speech.tts.Voice
import android.util.Log
import android.view.ContextThemeWrapper
import androidx.appcompat.app.AlertDialog
import org.catrobat.catroid.R
import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.content.bricks.Brick
import org.catrobat.catroid.formulaeditor.SensorHandler
import java.io.File
import java.util.HashMap
import java.util.Locale

class TextToSpeechHolder private constructor() {

    fun initTextToSpeech(stageActivity: StageActivity, stageResourceHolder: StageResourceHolder) {
        textToSpeech = TextToSpeech(stageActivity, OnInitListener { status ->
                if (status == TextToSpeech.SUCCESS) {
                    utteranceProgressListenerContainer = UtteranceProgressListenerContainer()
                    textToSpeech?.setOnUtteranceProgressListener(utteranceProgressListenerContainer)
                    stageResourceHolder.resourceInitialized()
                } else {
                    val builder = AlertDialog.Builder(ContextThemeWrapper(stageActivity, R.style.Theme_AppCompat_Dialog))
                    builder.setMessage(R.string.prestage_text_to_speech_engine_not_installed)
                        .setCancelable(false)
                        .setPositiveButton(R.string.yes) { _, _ ->
                            val installIntent = Intent()
                            installIntent.action = TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA
                            stageActivity.startActivity(installIntent)
                            stageResourceHolder.resourceFailed(Brick.TEXT_TO_SPEECH)
                        }
                        .setNegativeButton(R.string.no) { _, _ ->
                            stageResourceHolder.resourceFailed(Brick.TEXT_TO_SPEECH)
                        }
                    val alert = builder.create()
                    alert.show()
                }
            })
    }

    fun shutDownTextToSpeech() {
            textToSpeech?.stop()
            textToSpeech?.shutdown()
    }

    fun textToSpeech(text: String?, speechFile: File, listener: UtteranceProgressListener, speakParameter: HashMap<String, String?>) {
        if (utteranceProgressListenerContainer?.addUtteranceProgressListener(speechFile, listener,
            speakParameter[TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID]) == true) {
                forceSetLanguage()
            val status = textToSpeech?.synthesizeToFile(text ?: "", speakParameter, speechFile.absolutePath)
            if (status == TextToSpeech.ERROR) {
                Log.e(TAG, "File synthesizing failed")
            }
        }
    }

    fun deleteSpeechFiles() {
        val pathToSpeechFiles = File(Constants.TEXT_TO_SPEECH_TMP_PATH)
        if (pathToSpeechFiles.isDirectory) {
            pathToSpeechFiles.walk().forEach {
                it.delete()
            }
        }
    }

    fun forceSetLanguage() {
        val locale = Locale(SensorHandler.getSpeakingLanguageSensor())
        textToSpeech?.defaultVoice?.let {
            val voice = Voice(it.name, locale, it.quality, it.latency, it
                .isNetworkConnectionRequired, it.features)
            textToSpeech?.voice = voice
        }
        textToSpeech?.language = locale
    }

    fun fetchSupportedLocales(context: Context) {
        textToSpeech = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val defaultLocale = textToSpeech?.defaultVoice?.locale
                textToSpeech?.availableLanguages?.let {
                    supportedLocales.addAll(it)
                }
                defaultLocale?.let {
                    supportedLocales.remove(it)
                    supportedLocales.add(0, it)
                    SensorHandler.setSpeakingLanguageSensor(defaultLocale.toLanguageTag())
                }

            }
        }
    }

    fun getSupportedLocales() = supportedLocales

    companion object {
        private val TAG = TextToSpeechHolder::class.java.simpleName
        private var textToSpeech: TextToSpeech? = null
        private var utteranceProgressListenerContainer: UtteranceProgressListenerContainer? = null
        private val supportedLocales = mutableListOf<Locale>()
        @JvmStatic
        var instance = TextToSpeechHolder()
    }
}
