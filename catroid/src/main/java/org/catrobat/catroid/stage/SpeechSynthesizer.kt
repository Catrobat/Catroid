/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2021 The Catrobat Team
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

import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.content.Scope
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.formulaeditor.FormulaElement
import org.catrobat.catroid.formulaeditor.InterpretationException
import org.catrobat.catroid.utils.Utils
import java.io.File
import java.util.HashMap

class SpeechSynthesizer(val scope: Scope?, val text: Formula?) {
    private var interpretedText: Any? = null
    private var hashText: String? = null
    var speechFile: File? = null
        private set

    private var listener: UtteranceProgressListener? = null

    fun synthesize() {
        val listener = listener ?: return
        interpretFormula()
        hashText = Utils.md5Checksum(interpretedText.toString())
        val fileName = hashText
        val pathToSpeechFile = File(Constants.TEXT_TO_SPEECH_TMP_PATH)
        pathToSpeechFile.mkdirs()
        val speechFile = File(pathToSpeechFile, fileName + Constants.DEFAULT_SOUND_EXTENSION)
        this.speechFile = speechFile

        val speakParameter = HashMap<String, String?>()
        speakParameter[TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID] = hashText
        TextToSpeechHolder.instance.textToSpeech(
            interpretedText.toString(),
            speechFile,
            listener,
            speakParameter
        )
    }

    private fun interpretFormula() {
        interpretedText = try {
            text?.interpretString(scope) ?: ""
        } catch (interpretationException: InterpretationException) {
            Log.d(
                javaClass.simpleName,
                "Formula interpretation for this specific Brick failed.",
                interpretationException
            )
            ""
        }
        if (text?.root?.elementType != FormulaElement.ElementType.STRING && interpretedText is String) {
            try {
                val doubleValue = java.lang.Double.valueOf(interpretedText as String)
                if (doubleValue.isNaN()) {
                    interpretedText = ""
                }
            } catch (numberFormatException: NumberFormatException) {
                Log.d(javaClass.simpleName, "Couldn't parse String", numberFormatException)
            }
        }
    }

    fun setUtteranceProgressListener(onError: () -> Unit, onDone: () -> Unit) {
        listener = object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String) = Unit
            override fun onError(utteranceId: String) = onError()
            override fun onDone(utteranceId: String) = onDone()
        }
    }
}
