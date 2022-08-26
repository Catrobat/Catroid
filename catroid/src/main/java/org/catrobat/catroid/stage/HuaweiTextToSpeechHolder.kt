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

import android.util.Log
import android.view.ContextThemeWrapper
import androidx.appcompat.app.AlertDialog
import com.huawei.hms.mlsdk.model.download.MLLocalModelManager
import com.huawei.hms.mlsdk.model.download.MLModelDownloadListener
import com.huawei.hms.mlsdk.model.download.MLModelDownloadStrategy
import com.huawei.hms.mlsdk.tts.MLTtsConfig
import com.huawei.hms.mlsdk.tts.MLTtsConstants
import com.huawei.hms.mlsdk.tts.MLTtsEngine
import com.huawei.hms.mlsdk.tts.MLTtsEngine.EXTERNAL_PLAYBACK
import com.huawei.hms.mlsdk.tts.MLTtsEngine.OPEN_STREAM
import com.huawei.hms.mlsdk.tts.MLTtsEngine.QUEUE_APPEND
import com.huawei.hms.mlsdk.tts.MLTtsLocalModel
import org.catrobat.catroid.R
import org.catrobat.catroid.content.bricks.Brick

private val TAG = HuaweiTextToSpeechHolder::class.simpleName

class HuaweiTextToSpeechHolder {

    fun initTextToSpeech(
        stageActivity: StageActivity,
        stageResourceHolder: StageResourceHolder
    ) {
        mlTtsEngine.updateConfig(mlTtsConfig)

        val localModelManager = MLLocalModelManager.getInstance()
        val model =
            MLTtsLocalModel.Factory(MLTtsConstants.TTS_SPEAKER_OFFLINE_EN_US_FEMALE_BEE).create()
        localModelManager.isModelExist(model)
            .addOnSuccessListener { isDownloaded ->
                if (isDownloaded) {
                    stageResourceHolder.resourceInitialized()
                } else {
                    val builder = AlertDialog.Builder(
                        ContextThemeWrapper(
                            stageActivity,
                            R.style.Theme_AppCompat_Dialog
                        )
                    )
                    builder.setMessage(R.string.prestage_text_to_speech_engine_not_installed)
                        .setCancelable(false)
                        .setPositiveButton(R.string.yes) { _, _ ->
                            downloadModel(MLTtsConstants.TTS_SPEAKER_OFFLINE_EN_US_FEMALE_BEE)
                        }
                        .setNegativeButton(R.string.no) { _, _ ->
                            stageResourceHolder.resourceFailed(Brick.TEXT_TO_SPEECH)
                        }
                    val alert = builder.create()
                    alert.show()
                }
            }.addOnFailureListener { e ->
                Log.e(TAG, "Failed to download HMS Text-to-speech engine", e)
                stageResourceHolder.resourceFailed(Brick.TEXT_TO_SPEECH)
            }
    }

    fun textToSpeech(text: String) {
        mlTtsEngine.speak(text, QUEUE_APPEND or OPEN_STREAM or EXTERNAL_PLAYBACK)
    }

    private fun downloadModel(person: String) {
        val localModelManager = MLLocalModelManager.getInstance()
        val model = MLTtsLocalModel.Factory(person).create()
        val request = MLModelDownloadStrategy.Factory().needWifi().create()
        val modelDownloadListener = MLModelDownloadListener { _, _ -> }
        localModelManager.downloadModel(model, request, modelDownloadListener)
            .addOnSuccessListener {
                mlTtsEngine.updateConfig(mlTtsConfig)
            }.addOnFailureListener { e ->
                Log.e(TAG, "Failed to download HMS Text-to-speech engine", e)
            }
    }

    companion object {
        @JvmStatic
        var instance = HuaweiTextToSpeechHolder()
        val mlTtsConfig: MLTtsConfig = MLTtsConfig()
            .setLanguage(MLTtsConstants.TTS_EN_US)
            .setPerson(MLTtsConstants.TTS_SPEAKER_OFFLINE_EN_US_FEMALE_BEE)
            .setSynthesizeMode(MLTtsConstants.TTS_OFFLINE_MODE)
        val mlTtsEngine = MLTtsEngine(mlTtsConfig)
    }
}
