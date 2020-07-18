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
package org.catrobat.catroid.camera

import android.graphics.Point
import android.util.Log
import androidx.annotation.VisibleForTesting
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import org.catrobat.catroid.CatroidApplication
import org.catrobat.catroid.R
import org.catrobat.catroid.common.Constants.COORDINATE_TRANSFORMATION_OFFSET
import org.catrobat.catroid.common.Constants.ML_KIT_ANALYSIS_HEIGHT
import org.catrobat.catroid.common.Constants.ML_KIT_ANALYSIS_WIDTH
import org.catrobat.catroid.common.ScreenValues.SCREEN_HEIGHT
import org.catrobat.catroid.common.ScreenValues.SCREEN_WIDTH
import org.catrobat.catroid.formulaeditor.SensorCustomEvent
import org.catrobat.catroid.formulaeditor.SensorCustomEventListener
import org.catrobat.catroid.formulaeditor.Sensors
import org.catrobat.catroid.stage.StageActivity
import kotlin.math.roundToInt

object FaceDetector : ImageAnalysis.Analyzer {
    private val detectionClient = FaceDetection.getClient(
        FaceDetectorOptions.Builder()
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
            .enableTracking()
            .build()
    )
    private val sensorListeners = mutableSetOf<SensorCustomEventListener>()
    private const val MAX_FACE_SIZE = 100

    @JvmStatic
    fun addListener(listener: SensorCustomEventListener) {
        sensorListeners.add(listener)
    }

    @JvmStatic
    fun removeListener(listener: SensorCustomEventListener) {
        sensorListeners.remove(listener)
    }

    @androidx.camera.core.ExperimentalGetImage
    override fun analyze(imageProxy: ImageProxy) {
        imageProxy.image?.let { mediaImage ->
            val image = InputImage.fromMediaImage(
                mediaImage,
                imageProxy.imageInfo.rotationDegrees
            )

            detectionClient.process(image)
                .addOnSuccessListener { faces ->
                    updateSensorValues(faces)
                    imageProxy.close()
                }
                .addOnFailureListener { e ->
                    updateDetectionStatus(false)
                    val context = CatroidApplication.getAppContext()
                    StageActivity.messageHandler.obtainMessage(
                        StageActivity.SHOW_TOAST,
                        arrayListOf(context.getString(R.string.camera_error_face_detection))
                    ).sendToTarget()
                    Log.e(javaClass.simpleName, "Could not analyze image.", e)
                }
        }
    }

    private fun updateSensorValues(faces: List<Face>) {
        val detected = faces.isNotEmpty()

        updateDetectionStatus(detected)
        if (detected) {
            translateFaceToSensorValues(faces[0])
        }
    }

    private fun translateFaceToSensorValues(face: Face) {
        val invertX = StageActivity.getActiveCameraManager().isCameraFacingFront.not()
        val relativeX = face.boundingBox.exactCenterX() / ML_KIT_ANALYSIS_WIDTH
        val relativeY = face.boundingBox.exactCenterY() / ML_KIT_ANALYSIS_HEIGHT
        val positionX = (SCREEN_WIDTH * (COORDINATE_TRANSFORMATION_OFFSET - relativeX)).roundToInt()
        val positionY = (SCREEN_HEIGHT * (COORDINATE_TRANSFORMATION_OFFSET - relativeY)).roundToInt()
        val facePosition = Point(if (invertX) -positionX else positionX, positionY)

        val relativeFaceSize = (face.boundingBox.width().toFloat() / ML_KIT_ANALYSIS_WIDTH)
            .coerceAtMost(1f)
        val faceSize = (MAX_FACE_SIZE * relativeFaceSize).roundToInt()
        onFaceDetected(facePosition, faceSize)
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun updateDetectionStatus(faceDetected: Boolean) {
        val sensorValue = if (faceDetected) 1f else 0f
        sensorListeners.forEach {
            it.onCustomSensorChanged(
                SensorCustomEvent(Sensors.FACE_DETECTED, floatArrayOf(sensorValue)))
        }
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun onFaceDetected(position: Point, size: Int) {
        sensorListeners.forEach {
            it.onCustomSensorChanged(
                SensorCustomEvent(Sensors.FACE_X_POSITION, floatArrayOf(position.x.toFloat())))
            it.onCustomSensorChanged(
                SensorCustomEvent(Sensors.FACE_Y_POSITION, floatArrayOf(position.y.toFloat())))
            it.onCustomSensorChanged(
                SensorCustomEvent(Sensors.FACE_SIZE, floatArrayOf(size.toFloat())))
        }
    }
}
