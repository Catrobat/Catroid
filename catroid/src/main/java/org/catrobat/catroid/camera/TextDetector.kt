/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2020 The Catrobat Team
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

package org.catrobat.catroid.camera

import android.util.Log
import androidx.annotation.VisibleForTesting
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.Text
import org.catrobat.catroid.R
import org.catrobat.catroid.formulaeditor.SensorCustomEvent
import org.catrobat.catroid.formulaeditor.SensorCustomEventListener
import org.catrobat.catroid.formulaeditor.Sensors
import org.catrobat.catroid.stage.StageActivity
import org.catrobat.catroid.utils.TextBlockUtil

object TextDetector : ImageAnalysis.Analyzer {
    private val detectionClient = TextRecognition.getClient()
    private val sensorListeners = mutableSetOf<SensorCustomEventListener>()

    @JvmStatic
    fun addListener(listener: SensorCustomEventListener) {
        sensorListeners.add(listener)
    }

    @JvmStatic
    fun removeListener(listener: SensorCustomEventListener) {
        sensorListeners.remove(listener)
    }

    @ExperimentalGetImage
    override fun analyze(imageProxy: ImageProxy) {
        imageProxy.image?.let { mediaImage ->
            val image = InputImage.fromMediaImage(
                mediaImage,
                imageProxy.imageInfo.rotationDegrees
            )

            detectionClient.process(image)
                .addOnSuccessListener { text ->
                    updateSensorValues(text, mediaImage.width, mediaImage.height)
                    imageProxy.close()
                }
                .addOnFailureListener { e ->
                    val context = StageActivity.activeStageActivity.get()
                    StageActivity.messageHandler.obtainMessage(
                        StageActivity.SHOW_TOAST,
                        arrayListOf(context?.getString(R.string.camera_error_text_detection))
                    ).sendToTarget()
                    Log.e(javaClass.simpleName, "Could not analyze image.", e)
                }
        }
    }

    private fun updateSensorValues(text: Text, imageWidth: Int, imageHeight: Int) {
        val detected = text.textBlocks.isEmpty().not()
        if (detected) {
            translateTextToSensorValues(text, imageWidth, imageHeight)
        }
    }

    private fun translateTextToSensorValues(text: Text, imageWidth: Int, imageHeight: Int) {
        val textFromCamera = text.text

        val textBlocksNumber = text.textBlocks.size

        TextBlockUtil.setTextBlocks(text.textBlocks, imageWidth, imageHeight)

        onTextDetected(textFromCamera, textBlocksNumber)
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun onTextDetected(text: String, size: Int) {
        sensorListeners.forEach {
            it.onCustomSensorChanged(
                SensorCustomEvent(Sensors.TEXT_FROM_CAMERA, arrayOf(text)))
            it.onCustomSensorChanged(
                SensorCustomEvent(Sensors.TEXT_BLOCKS_NUMBER, floatArrayOf(size.toFloat())))
        }
    }
}
