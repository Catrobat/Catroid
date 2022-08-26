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

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import com.huawei.hms.mlplugin.asr.MLAsrCaptureConstants
import com.huawei.hms.mlsdk.asr.MLAsrConstants
import com.huawei.hms.mlsdk.asr.MLAsrListener
import com.huawei.hms.mlsdk.asr.MLAsrRecognizer
import org.catrobat.catroid.R
import org.catrobat.catroid.formulaeditor.SensorHandler
import org.catrobat.catroid.utils.ToastUtil
import java.lang.ref.WeakReference

class HmsSpeechRecognitionHolder : SpeechRecognitionHolderInterface {
    private var speechRecognizer: MLAsrRecognizer? = null
    private var speechIntent: Intent? = null
    private var context = WeakReference<Context>(null)

    private val listener = object : MLAsrListener {
        override fun onResults(result: Bundle?) {
            val recognizedString = result?.getString(MLAsrRecognizer.RESULTS_RECOGNIZED)
            callback?.onResult(recognizedString.orEmpty())
        }

        override fun onRecognizingResults(result: Bundle?) = Unit

        override fun onError(error: Int, errorMessage: String?) {
            when (error) {
                MLAsrConstants.ERR_NO_NETWORK ->
                    ToastUtil.showError(context.get(), R.string.error_no_network_title)
            }
        }

        override fun onStartListening() = Unit
        override fun onStartingOfSpeech() = Unit
        override fun onVoiceDataReceived(data: ByteArray?, energy: Float, params: Bundle?) = Unit
        override fun onState(state: Int, params: Bundle?) = Unit
    }

    override var callback: OnSpeechRecognitionResultCallback? = null

    override fun forceSetLanguage() {
        speechIntent?.putExtra(
            MLAsrCaptureConstants.LANGUAGE,
            SensorHandler.getListeningLanguageSensor()
        )
    }

    override fun initSpeechRecognition(
        stageActivity: StageActivity,
        stageResourceHolder: StageResourceHolder
    ) {
        context = WeakReference(stageActivity)
        speechIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            .putExtra(
                MLAsrCaptureConstants.LANGUAGE,
                SensorHandler.getListeningLanguageSensor()
            )
            .putExtra(MLAsrConstants.FEATURE, MLAsrConstants.FEATURE_ALLINONE)
    }

    override fun startListening() {
        context.get()?.let {
            MLAsrRecognizer.createAsrRecognizer(it).run {
                speechRecognizer = this
                setAsrListener(listener)
                startRecognizing(speechIntent)
            }
        }
    }

    override fun destroy() {
        speechRecognizer?.let {
            it.destroy()
            speechRecognizer = null
        }
    }
}
