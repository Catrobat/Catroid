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

package org.catrobat.catroidfeature.machinelearning

import android.media.Image
import android.util.Log
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions

private val faceDetectionClient by lazy {
    FaceDetection.getClient(
        FaceDetectorOptions.Builder()
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
            .enableTracking()
            .build()
    )
}

object FaceDetector : Detector {
    override fun processImage(
        mediaImage: Image,
        inputImage: InputImage,
        onCompleteListener: DetectorsCompleteListener
    ) {
        faceDetectionClient.process(inputImage)
            .addOnSuccessListener { faces ->
                val translatedFaces =
                    VisualDetectionHandler.translateGoogleFaceToVisualDetectionFace(faces)
                VisualDetectionHandler.handleAlreadyExistingFaces(translatedFaces)
                VisualDetectionHandler.handleNewFaces(translatedFaces)
                VisualDetectionHandler.updateAllFaceSensorValues(
                    mediaImage.width,
                    mediaImage.height
                )
            }
            .addOnFailureListener { e ->
                VisualDetectionHandler.updateFaceDetectionStatusSensorValues()
                Log.e(
                    javaClass.simpleName,
                    "Could not analyze image.",
                    e
                )
            }.addOnCompleteListener {
                onCompleteListener.onComplete()
            }
    }
}
