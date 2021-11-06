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
package org.catrobat.catroid.camera

import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import org.catrobat.catroid.camera.mlkitdetectors.FaceDetector
import org.catrobat.catroid.camera.mlkitdetectors.PoseDetector
import org.catrobat.catroid.camera.mlkitdetectors.TextDetector

object CatdroidImageAnalyzer : ImageAnalysis.Analyzer {
    const val DETECTION_PROCESS_ERROR_MESSAGE = "Could not analyze image." // TODO
    private val detectors = listOf(FaceDetector(), TextDetector(), PoseDetector())
    private val activeDetectors = detectors.map { d -> d.getName() }

    @ExperimentalGetImage
    override fun analyze(imageProxy: ImageProxy) {
        imageProxy.image?.let { mediaImage ->
            val completeListener = DetectorsCompleteListener(activeDetectors, imageProxy)
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            for (detector in detectors) {
                detector.processImage(mediaImage, image, completeListener)
            }
        }
    }
}

// TODO replace string with enum or something similar
//  or just use one byte an OR it
//  Face: 0b1, Text: 0b10, Pose: 0b100
//  finished if 0b111
class DetectorsCompleteListener(
    private val activeDetectors: List<String>,
    private val imageProxy: ImageProxy
) {
    private val finishedDetectors = ArrayList<String>()
    fun onComplete(finishedDetector: String) {
        finishedDetectors.add(finishedDetector)
        if (finishedDetectors.containsAll(activeDetectors)) {
            imageProxy.close()
        }
    }
}
