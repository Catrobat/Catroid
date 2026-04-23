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
package org.catrobat.catroid.camera

import android.content.Context
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import org.catrobat.catroid.camera.mlkitdetectors.Detector
import org.catrobat.catroid.camera.mlkitdetectors.FaceDetector
import org.catrobat.catroid.camera.mlkitdetectors.ObjectDetector
import org.catrobat.catroid.camera.mlkitdetectors.PoseDetector
import org.catrobat.catroid.camera.mlkitdetectors.TextDetector
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment.isAIFaceDetectionSharedPreferenceEnabled
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment.isAIObjectDetectionSharedPreferenceEnabled
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment.isAIPoseDetectionSharedPreferenceEnabled
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment.isAITextRecognitionSharedPreferenceEnabled

object CatdroidImageAnalyzer : ImageAnalysis.Analyzer {
    const val DETECTION_PROCESS_ERROR_MESSAGE: String = "Could not analyze image."
    private val activeDetectors = ArrayList<Detector>()

    @ExperimentalGetImage
    override fun analyze(imageProxy: ImageProxy) {
        imageProxy.image?.let { mediaImage ->
            val completeListener = DetectorsCompleteListener(activeDetectors.size, imageProxy)
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            for (detector in activeDetectors) {
                detector.processImage(mediaImage, image, completeListener)
            }
        }
    }

    fun setActiveDetectorsWithContext(context: Context?) {
        activeDetectors.clear()
        context?.let {
            if (isAIFaceDetectionSharedPreferenceEnabled(it)) {
                activeDetectors.add(FaceDetector)
            }
            if (isAIPoseDetectionSharedPreferenceEnabled(it)) {
                activeDetectors.add(PoseDetector)
            }
            if (isAITextRecognitionSharedPreferenceEnabled(it)) {
                activeDetectors.add(TextDetector)
            }
            if (isAIObjectDetectionSharedPreferenceEnabled(it)) {
                activeDetectors.add(ObjectDetector)
            }
        }
    }
}

class DetectorsCompleteListener(
    private val numActiveDetectors: Int,
    private val imageProxy: ImageProxy
) {
    private var finishedDetectors = 0
    fun onComplete() {
        if (++finishedDetectors >= numActiveDetectors) {
            imageProxy.close()
        }
    }
}
