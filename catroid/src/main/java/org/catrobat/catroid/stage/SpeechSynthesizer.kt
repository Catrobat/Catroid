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

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import com.huawei.hms.mlsdk.tts.MLTtsAudioFragment
import com.huawei.hms.mlsdk.tts.MLTtsCallback
import com.huawei.hms.mlsdk.tts.MLTtsConstants
import com.huawei.hms.mlsdk.tts.MLTtsError
import com.huawei.hms.mlsdk.tts.MLTtsWarn
import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.content.Scope
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.utils.PcmToWavConverter.convertPcmToWav
import org.catrobat.catroid.utils.PcmToWavConverter.writePcmToFile
import org.catrobat.catroid.utils.ShowTextUtils.AndroidStringProvider
import org.catrobat.catroid.utils.Utils
import java.io.File

private const val SAMPLE_RATE = 16_000
private const val CHANNEL_COUNT = 1
private const val BITS_PER_SAMPLE = 16
private val TAG = SpeechSynthesizer::class.simpleName

class SpeechSynthesizer(val scope: Scope?, val text: Formula?) {
    private var interpretedText: Any? = null
    private var hashText: String? = null
    var speechFile: File? = null
        private set

    private var listener: Any? = null

    fun synthesize(androidStringProvider: AndroidStringProvider) {
        val listener = listener ?: return
        interpretedText = if (text != null) {
            text.getUserFriendlyString(androidStringProvider, scope)
        } else {
            ""
        }

        hashText = Utils.md5Checksum(interpretedText.toString())
        val fileName = hashText
        val pathToSpeechFile = File(Constants.TEXT_TO_SPEECH_TMP_PATH)
        pathToSpeechFile.mkdirs()
        val speechFile = File(pathToSpeechFile, fileName + Constants.DEFAULT_SOUND_EXTENSION)
        this.speechFile = speechFile

        when (listener) {
            is UtteranceProgressListener -> {
                val speakParameter = HashMap<String, String?>()
                speakParameter[TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID] = hashText
                TextToSpeechHolder.instance.textToSpeech(
                    interpretedText.toString(),
                    speechFile,
                    listener as UtteranceProgressListener,
                    speakParameter
                )
            }
            is MLTtsCallback -> {
                HuaweiTextToSpeechHolder.mlTtsEngine.setTtsCallback(listener as MLTtsCallback)
                HuaweiTextToSpeechHolder.instance.textToSpeech(interpretedText.toString())
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

    fun setHuaweiTextToSpeechListener(onError: () -> Unit, onDone: () -> Unit) {
        listener = object : MLTtsCallback {
            private lateinit var pcmAudioFile: File
            override fun onError(taskId: String, err: MLTtsError) {
                onError()
            }

            override fun onWarn(taskId: String, warn: MLTtsWarn) {
                Log.w(TAG, "Warning when synthesizing voice with message: ${warn.warnMsg}")
            }

            override fun onRangeStart(taskId: String, start: Int, end: Int) {
                Log.d(TAG, "Starting range start: $start end: $end")
            }

            override fun onAudioAvailable(
                taskId: String?,
                audioFragment: MLTtsAudioFragment?,
                offset: Int,
                range: android.util.Pair<Int, Int>?,
                bundle: Bundle?
            ) {
                if (taskId != null && audioFragment != null) {
                    writePcmToFile(audioFragment.audioData, pcmAudioFile, true)
                }
            }

            override fun onEvent(taskId: String?, eventId: Int, bundle: Bundle?) {
                when (eventId) {
                    MLTtsConstants.EVENT_SYNTHESIS_START -> {
                        val path = File(Constants.TEXT_TO_SPEECH_TMP_PATH)
                        path.mkdirs()
                        pcmAudioFile = File(path, "placeholder.pcm")
                    }
                    MLTtsConstants.EVENT_SYNTHESIS_COMPLETE -> {
                        convertPcmToWav(
                            pcmAudioFile,
                            speechFile,
                            CHANNEL_COUNT,
                            SAMPLE_RATE,
                            BITS_PER_SAMPLE
                        )
                        pcmAudioFile.delete()
                        onDone()
                    }
                }
            }
        }
    }
}
