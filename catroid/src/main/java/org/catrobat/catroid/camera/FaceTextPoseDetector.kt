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

import android.util.Log
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.google.mlkit.vision.pose.PoseDetection
import com.google.mlkit.vision.pose.defaults.PoseDetectorOptions
import com.google.mlkit.vision.text.TextRecognition
import org.catrobat.catroid.CatroidApplication
import org.catrobat.catroid.R
import org.catrobat.catroid.camera.VisualDetectionHandler.handleAlreadyExistingFaces
import org.catrobat.catroid.camera.VisualDetectionHandler.handleNewFaces
import org.catrobat.catroid.camera.VisualDetectionHandler.updateAllFaceSensorValues
import org.catrobat.catroid.camera.VisualDetectionHandler.updateAllPoseSensorValues
import org.catrobat.catroid.camera.VisualDetectionHandler.updateFaceDetectionStatusSensorValues
import org.catrobat.catroid.camera.VisualDetectionHandler.updateTextSensorValues
import org.catrobat.catroid.stage.StageActivity
import org.catrobat.catroid.utils.TextBlockUtil.setTextBlocksGoogle

object FaceTextPoseDetector : ImageAnalysis.Analyzer {
    private const val DETECTION_PROCESS_ERROR_MESSAGE = "Could not analyze image."

    private val faceDetectionClient by lazy {
        FaceDetection.getClient(
            FaceDetectorOptions.Builder()
                .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                .enableTracking()
                .build()
        )
    }
    private val poseDetectionClient by lazy {
        PoseDetection.getClient(
            PoseDetectorOptions.Builder()
                .setDetectorMode(PoseDetectorOptions.STREAM_MODE)
                .build()
        )
    }
    private val textDetectionClient = TextRecognition.getClient()

    private var textDetected = false
    private var faceDetected = false
    private var poseDetected = false

    @ExperimentalGetImage
    override fun analyze(imageProxy: ImageProxy) {
        imageProxy.image?.let { mediaImage ->
            val image = InputImage.fromMediaImage(
                mediaImage,
                imageProxy.imageInfo.rotationDegrees
            )

            faceDetected = false
            textDetected = false
            poseDetected = false

            textDetectionClient.process(image)
                .addOnSuccessListener { text ->
                    updateTextSensorValues(text.text, text.textBlocks.size)
                    setTextBlocksGoogle(text.textBlocks, mediaImage.width, mediaImage.height)
                    textDetected = true
                    if (faceDetected && poseDetected) {
                        imageProxy.close()
                    }
                }
                .addOnFailureListener { e ->
                    val context = StageActivity.activeStageActivity.get()
                    StageActivity.messageHandler.obtainMessage(
                        StageActivity.SHOW_TOAST,
                        arrayListOf(context?.getString(R.string.camera_error_text_detection))
                    ).sendToTarget()
                    Log.e(javaClass.simpleName, DETECTION_PROCESS_ERROR_MESSAGE, e)
                }

            faceDetectionClient.process(image)
                .addOnSuccessListener { faces ->
                    val translatedFaces = VisualDetectionHandler.translateGoogleFaceToVisualDetectionFace(faces)
                    handleAlreadyExistingFaces(translatedFaces)
                    handleNewFaces(translatedFaces)
                    updateAllFaceSensorValues(mediaImage.width, mediaImage.height)
                    faceDetected = true
                    if (textDetected && poseDetected) {
                        imageProxy.close()
                    }
                }
                .addOnFailureListener { e ->
                    updateFaceDetectionStatusSensorValues()
                    val context = CatroidApplication.getAppContext()
                    StageActivity.messageHandler.obtainMessage(
                        StageActivity.SHOW_TOAST,
                        arrayListOf(context.getString(R.string.camera_error_face_detection))
                    ).sendToTarget()
                    Log.e(javaClass.simpleName, DETECTION_PROCESS_ERROR_MESSAGE, e)
                }

            poseDetectionClient.process(image)
                .addOnSuccessListener { pose ->
                    updateAllPoseSensorValues(pose, mediaImage.width, mediaImage.height)
                    poseDetected = true
                    if (textDetected && faceDetected) {
                        imageProxy.close()
                    }
                }
                .addOnFailureListener { e ->
                    val context = CatroidApplication.getAppContext()
                    StageActivity.messageHandler.obtainMessage(
                        StageActivity.SHOW_TOAST,
                        arrayListOf(context.getString(R.string.camera_error_pose_detection))
                    ).sendToTarget()
                    Log.e(javaClass.simpleName, DETECTION_PROCESS_ERROR_MESSAGE, e)
                }
        }
    }
}
