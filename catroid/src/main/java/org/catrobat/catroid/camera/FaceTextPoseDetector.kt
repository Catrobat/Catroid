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

import android.graphics.Point
import android.util.Log
import androidx.annotation.VisibleForTesting
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseDetection
import com.google.mlkit.vision.pose.PoseLandmark
import com.google.mlkit.vision.pose.defaults.PoseDetectorOptions
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import org.catrobat.catroid.CatroidApplication
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.R
import org.catrobat.catroid.common.Constants.COORDINATE_TRANSFORMATION_OFFSET
import org.catrobat.catroid.common.ScreenValues.SCREEN_HEIGHT
import org.catrobat.catroid.common.ScreenValues.SCREEN_WIDTH
import org.catrobat.catroid.formulaeditor.SensorCustomEvent
import org.catrobat.catroid.formulaeditor.SensorCustomEventListener
import org.catrobat.catroid.formulaeditor.Sensors
import org.catrobat.catroid.stage.StageActivity
import org.catrobat.catroid.utils.TextBlockUtil
import kotlin.math.roundToInt

object FaceTextPoseDetector : ImageAnalysis.Analyzer {
    private const val DETECTION_PROCESS_ERROR_MESSAGE = "Could not analyze image."
    private const val MAX_FACE_SIZE = 100
    private const val FACE_SENSORS = 2
    private val sensorListeners = mutableSetOf<SensorCustomEventListener>()

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    var facesForSensors: Array<Face?> = Array(FACE_SENSORS) { _ -> null }
    private var faceIds: IntArray = IntArray(FACE_SENSORS) { _ -> -1 }
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

            faceDetected = false
            textDetected = false
            poseDetected = false

            textDetectionClient.process(image)
                .addOnSuccessListener { text ->
                    updateTextSensorValues(text, mediaImage.width, mediaImage.height)
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
                    handleAlreadyExistingFaces(faces)
                    handleNewFaces(faces)
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

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun updateTextSensorValues(text: Text, imageWidth: Int, imageHeight: Int) {
        if (text.textBlocks.isEmpty()) return

        TextBlockUtil.setTextBlocks(text.textBlocks, imageWidth, imageHeight)

        sensorListeners.forEach {
            writeStringToSensor(it, Sensors.TEXT_FROM_CAMERA, text.text)
            writeFloatToSensor(it, Sensors.TEXT_BLOCKS_NUMBER, text.textBlocks.size.toFloat())
        }
    }

    private fun updateAllFaceSensorValues(imageWidth: Int, imageHeight: Int) {
        updateFaceDetectionStatusSensorValues()

        facesForSensors.forEachIndexed { index, face ->
            face?.let {
                val faceBounds = face.boundingBox

                val facePosition = translateToStageCoordinates(
                    faceBounds.exactCenterX(), faceBounds.exactCenterY(),
                    imageWidth, imageHeight
                )
                val relativeFaceSize =
                    (faceBounds.height().toFloat() / imageHeight).coerceAtMost(1f)
                val faceSize = (MAX_FACE_SIZE * relativeFaceSize).roundToInt()

                updateFaceSensorValues(facePosition, faceSize, index)
            }
        }
        facesForSensors.fill(null)
    }

    private fun handleAlreadyExistingFaces(faces: List<Face>) {
        for (face in faces) {
            when (face.trackingId) {
                faceIds[0] -> facesForSensors[0] = face
                faceIds[1] -> facesForSensors[1] = face
            }
        }
    }

    private fun handleNewFaces(faces: List<Face>) {
        facesForSensors.forEachIndexed { index, face ->
            if (face == null) {
                attachNewFaceIfExisting(faces, index)
            }
        }
    }

    private fun attachNewFaceIfExisting(faces: List<Face>, index: Int) {
        for (face in faces) {
            if (face.trackingId?.let { faceIds.contains(it) } == false) {
                faceIds[index] = face.trackingId ?: -1
                facesForSensors[index] = face
                break
            }
        }
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun updateFaceDetectionStatusSensorValues() {
        val firstSensorValue = if (facesForSensors[0] != null) 1f else 0f
        val secondSensorValue = if (facesForSensors[1] != null) 1f else 0f
        sensorListeners.forEach {
            writeFloatToSensor(it, Sensors.FACE_DETECTED, firstSensorValue)
            writeFloatToSensor(it, Sensors.SECOND_FACE_DETECTED, secondSensorValue)
        }
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun updateFaceSensorValues(facePosition: Point, faceSize: Int, faceNumber: Int) {
        sensorListeners.forEach {
            when (faceNumber) {
                0 -> {
                    writeFloatToSensor(it, Sensors.FACE_X_POSITION, facePosition.x.toFloat())
                    writeFloatToSensor(it, Sensors.FACE_Y_POSITION, facePosition.y.toFloat())
                    writeFloatToSensor(it, Sensors.FACE_SIZE, faceSize.toFloat())
                }
                1 -> {
                    writeFloatToSensor(it, Sensors.SECOND_FACE_X_POSITION, facePosition.x.toFloat())
                    writeFloatToSensor(it, Sensors.SECOND_FACE_Y_POSITION, facePosition.y.toFloat())
                    writeFloatToSensor(it, Sensors.SECOND_FACE_SIZE, faceSize.toFloat())
                }
            }
        }
    }

    private fun updateAllPoseSensorValues(pose: Pose?, imageWidth: Int, imageHeight: Int) {
        val allPoseLandmarks = pose?.allPoseLandmarks

        if (allPoseLandmarks.isNullOrEmpty()) return

        allPoseLandmarks.forEach { poseLandmark ->
            poseLandmark?.let {
                val poseLandmarkPositionTranslated = translateToStageCoordinates(
                    poseLandmark.position.x,
                    poseLandmark.position.y,
                    imageWidth,
                    imageHeight
                )

                updatePoseSensorValues(poseLandmark, poseLandmarkPositionTranslated)
            }
        }
    }

    private fun updatePoseSensorValues(poseLandmark: PoseLandmark, position: Point) {
        sensorListeners.forEach { sensorListener ->
            updateHeadPoseSensorValues(poseLandmark.landmarkType, sensorListener, position)
            updateUpperBodyPoseSensorValues(poseLandmark.landmarkType, sensorListener, position)
            updateLowerBodyPoseSensorValues(poseLandmark.landmarkType, sensorListener, position)
        }
    }

    private fun updateHeadPoseSensorValues(
        poseLandmarkType: Int,
        sensorListener: SensorCustomEventListener,
        position: Point
    ) {
        when (poseLandmarkType) {
            PoseLandmark.NOSE -> {
                writeFloatToSensor(sensorListener, Sensors.NOSE_X, position.x.toFloat())
                writeFloatToSensor(sensorListener, Sensors.NOSE_Y, position.y.toFloat())
            }
            PoseLandmark.LEFT_EYE_INNER -> {
                writeFloatToSensor(sensorListener, Sensors.LEFT_EYE_INNER_X, position.x.toFloat())
                writeFloatToSensor(sensorListener, Sensors.LEFT_EYE_INNER_Y, position.y.toFloat())
            }
            PoseLandmark.LEFT_EYE -> {
                writeFloatToSensor(sensorListener, Sensors.LEFT_EYE_CENTER_X, position.x.toFloat())
                writeFloatToSensor(sensorListener, Sensors.LEFT_EYE_CENTER_Y, position.y.toFloat())
            }
            PoseLandmark.LEFT_EYE_OUTER -> {
                writeFloatToSensor(sensorListener, Sensors.LEFT_EYE_OUTER_X, position.x.toFloat())
                writeFloatToSensor(sensorListener, Sensors.LEFT_EYE_OUTER_Y, position.y.toFloat())
            }
            PoseLandmark.RIGHT_EYE_INNER -> {
                writeFloatToSensor(sensorListener, Sensors.RIGHT_EYE_INNER_X, position.x.toFloat())
                writeFloatToSensor(sensorListener, Sensors.RIGHT_EYE_INNER_Y, position.y.toFloat())
            }
            PoseLandmark.RIGHT_EYE -> {
                writeFloatToSensor(sensorListener, Sensors.RIGHT_EYE_CENTER_X, position.x.toFloat())
                writeFloatToSensor(sensorListener, Sensors.RIGHT_EYE_CENTER_Y, position.y.toFloat())
            }
            PoseLandmark.RIGHT_EYE_OUTER -> {
                writeFloatToSensor(sensorListener, Sensors.RIGHT_EYE_OUTER_X, position.x.toFloat())
                writeFloatToSensor(sensorListener, Sensors.RIGHT_EYE_OUTER_Y, position.y.toFloat())
            }
            PoseLandmark.LEFT_EAR -> {
                writeFloatToSensor(sensorListener, Sensors.LEFT_EAR_X, position.x.toFloat())
                writeFloatToSensor(sensorListener, Sensors.LEFT_EAR_Y, position.y.toFloat())
            }
            PoseLandmark.RIGHT_EAR -> {
                writeFloatToSensor(sensorListener, Sensors.RIGHT_EAR_X, position.x.toFloat())
                writeFloatToSensor(sensorListener, Sensors.RIGHT_EAR_Y, position.y.toFloat())
            }
            PoseLandmark.LEFT_MOUTH -> {
                writeFloatToSensor(sensorListener, Sensors.MOUTH_LEFT_CORNER_X, position.x.toFloat())
                writeFloatToSensor(sensorListener, Sensors.MOUTH_LEFT_CORNER_Y, position.y.toFloat())
            }
            PoseLandmark.RIGHT_MOUTH -> {
                writeFloatToSensor(sensorListener, Sensors.MOUTH_RIGHT_CORNER_X, position.x.toFloat())
                writeFloatToSensor(sensorListener, Sensors.MOUTH_RIGHT_CORNER_Y, position.y.toFloat())
            }
        }
    }

    private fun updateUpperBodyPoseSensorValues(
        poseLandmarkType: Int,
        sensorListener: SensorCustomEventListener,
        position: Point
    ) {
        when (poseLandmarkType) {
            PoseLandmark.LEFT_SHOULDER -> {
                writeFloatToSensor(sensorListener, Sensors.LEFT_SHOULDER_X, position.x.toFloat())
                writeFloatToSensor(sensorListener, Sensors.LEFT_SHOULDER_Y, position.y.toFloat())
            }
            PoseLandmark.RIGHT_SHOULDER -> {
                writeFloatToSensor(sensorListener, Sensors.RIGHT_SHOULDER_X, position.x.toFloat())
                writeFloatToSensor(sensorListener, Sensors.RIGHT_SHOULDER_Y, position.y.toFloat())
            }
            PoseLandmark.LEFT_ELBOW -> {
                writeFloatToSensor(sensorListener, Sensors.LEFT_ELBOW_X, position.x.toFloat())
                writeFloatToSensor(sensorListener, Sensors.LEFT_ELBOW_Y, position.y.toFloat())
            }
            PoseLandmark.RIGHT_ELBOW -> {
                writeFloatToSensor(sensorListener, Sensors.RIGHT_ELBOW_X, position.x.toFloat())
                writeFloatToSensor(sensorListener, Sensors.RIGHT_ELBOW_Y, position.y.toFloat())
            }
            PoseLandmark.LEFT_WRIST -> {
                writeFloatToSensor(sensorListener, Sensors.LEFT_WRIST_X, position.x.toFloat())
                writeFloatToSensor(sensorListener, Sensors.LEFT_WRIST_Y, position.y.toFloat())
            }
            PoseLandmark.RIGHT_WRIST -> {
                writeFloatToSensor(sensorListener, Sensors.RIGHT_WRIST_X, position.x.toFloat())
                writeFloatToSensor(sensorListener, Sensors.RIGHT_WRIST_Y, position.y.toFloat())
            }
            PoseLandmark.LEFT_PINKY -> {
                writeFloatToSensor(sensorListener, Sensors.LEFT_PINKY_KNUCKLE_X, position.x.toFloat())
                writeFloatToSensor(sensorListener, Sensors.LEFT_PINKY_KNUCKLE_Y, position.y.toFloat())
            }
            PoseLandmark.RIGHT_PINKY -> {
                writeFloatToSensor(sensorListener, Sensors.RIGHT_PINKY_KNUCKLE_X, position.x.toFloat())
                writeFloatToSensor(sensorListener, Sensors.RIGHT_PINKY_KNUCKLE_Y, position.y.toFloat())
            }
            PoseLandmark.LEFT_INDEX -> {
                writeFloatToSensor(sensorListener, Sensors.LEFT_INDEX_KNUCKLE_X, position.x.toFloat())
                writeFloatToSensor(sensorListener, Sensors.LEFT_INDEX_KNUCKLE_Y, position.y.toFloat())
            }
            PoseLandmark.RIGHT_INDEX -> {
                writeFloatToSensor(sensorListener, Sensors.RIGHT_INDEX_KNUCKLE_X, position.x.toFloat())
                writeFloatToSensor(sensorListener, Sensors.RIGHT_INDEX_KNUCKLE_Y, position.y.toFloat())
            }
            PoseLandmark.LEFT_THUMB -> {
                writeFloatToSensor(sensorListener, Sensors.LEFT_THUMB_KNUCKLE_X, position.x.toFloat())
                writeFloatToSensor(sensorListener, Sensors.LEFT_THUMB_KNUCKLE_Y, position.y.toFloat())
            }
            PoseLandmark.RIGHT_THUMB -> {
                writeFloatToSensor(sensorListener, Sensors.RIGHT_THUMB_KNUCKLE_X, position.x.toFloat())
                writeFloatToSensor(sensorListener, Sensors.RIGHT_THUMB_KNUCKLE_Y, position.y.toFloat())
            }
        }
    }

    private fun updateLowerBodyPoseSensorValues(
        poseLandmarkType: Int,
        sensorListener: SensorCustomEventListener,
        position: Point
    ) {
        when (poseLandmarkType) {
            PoseLandmark.LEFT_HIP -> {
                writeFloatToSensor(sensorListener, Sensors.LEFT_HIP_X, position.x.toFloat())
                writeFloatToSensor(sensorListener, Sensors.LEFT_HIP_Y, position.y.toFloat())
            }
            PoseLandmark.RIGHT_HIP -> {
                writeFloatToSensor(sensorListener, Sensors.RIGHT_HIP_X, position.x.toFloat())
                writeFloatToSensor(sensorListener, Sensors.RIGHT_HIP_Y, position.y.toFloat())
            }
            PoseLandmark.LEFT_KNEE -> {
                writeFloatToSensor(sensorListener, Sensors.LEFT_KNEE_X, position.x.toFloat())
                writeFloatToSensor(sensorListener, Sensors.LEFT_KNEE_Y, position.y.toFloat())
            }
            PoseLandmark.RIGHT_KNEE -> {
                writeFloatToSensor(sensorListener, Sensors.RIGHT_KNEE_X, position.x.toFloat())
                writeFloatToSensor(sensorListener, Sensors.RIGHT_KNEE_Y, position.y.toFloat())
            }
            PoseLandmark.LEFT_ANKLE -> {
                writeFloatToSensor(sensorListener, Sensors.LEFT_ANKLE_X, position.x.toFloat())
                writeFloatToSensor(sensorListener, Sensors.LEFT_ANKLE_Y, position.y.toFloat())
            }
            PoseLandmark.RIGHT_ANKLE -> {
                writeFloatToSensor(sensorListener, Sensors.RIGHT_ANKLE_X, position.x.toFloat())
                writeFloatToSensor(sensorListener, Sensors.RIGHT_ANKLE_Y, position.y.toFloat())
            }
            PoseLandmark.LEFT_HEEL -> {
                writeFloatToSensor(sensorListener, Sensors.LEFT_HEEL_X, position.x.toFloat())
                writeFloatToSensor(sensorListener, Sensors.LEFT_HEEL_Y, position.y.toFloat())
            }
            PoseLandmark.RIGHT_HEEL -> {
                writeFloatToSensor(sensorListener, Sensors.RIGHT_HEEL_X, position.x.toFloat())
                writeFloatToSensor(sensorListener, Sensors.RIGHT_HEEL_Y, position.y.toFloat())
            }
            PoseLandmark.LEFT_FOOT_INDEX -> {
                writeFloatToSensor(sensorListener, Sensors.LEFT_FOOT_INDEX_X, position.x.toFloat())
                writeFloatToSensor(sensorListener, Sensors.LEFT_FOOT_INDEX_Y, position.y.toFloat())
            }
            PoseLandmark.RIGHT_FOOT_INDEX -> {
                writeFloatToSensor(sensorListener, Sensors.RIGHT_FOOT_INDEX_X, position.x.toFloat())
                writeFloatToSensor(sensorListener, Sensors.RIGHT_FOOT_INDEX_Y, position.y.toFloat())
            }
        }
    }

    private fun translateToStageCoordinates(
        x: Float,
        y: Float,
        imageWidth: Int,
        imageHeight: Int
    ): Point {
        // TODO: check - use resolution here?
        val frontCamera = StageActivity.getActiveCameraManager().isCameraFacingFront
        val aspectRatio = imageWidth.toFloat() / imageHeight

        return if (ProjectManager.getInstance().isCurrentProjectLandscapeMode) {
            val relativeX = y / imageHeight
            val relativeY = x / imageWidth
            coordinatesFromRelativePosition(
                1 - relativeX,
                SCREEN_WIDTH / aspectRatio,
                if (frontCamera) relativeY else 1 - relativeY,
                SCREEN_WIDTH.toFloat()
            )
        } else {
            val relativeX = x / imageHeight
            coordinatesFromRelativePosition(
                if (frontCamera) 1 - relativeX else relativeX,
                SCREEN_HEIGHT / aspectRatio,
                1 - y / imageWidth,
                SCREEN_HEIGHT.toFloat()
            )
        }
    }

    private fun coordinatesFromRelativePosition(
        relativeX: Float,
        width: Float,
        relativeY: Float,
        height: Float
    ) = Point(
        (width * (relativeX - COORDINATE_TRANSFORMATION_OFFSET)).roundToInt(),
        (height * (relativeY - COORDINATE_TRANSFORMATION_OFFSET)).roundToInt()
    )

    private fun writeFloatToSensor(
        sensorListener: SensorCustomEventListener,
        sourceSensor: Sensors,
        value: Float
    ) {
        sensorListener.onCustomSensorChanged(
            SensorCustomEvent(
                sourceSensor,
                floatArrayOf(value)
            )
        )
    }

    private fun writeStringToSensor(
        sensorListener: SensorCustomEventListener,
        sourceSensor: Sensors,
        value: String
    ) {
        sensorListener.onCustomSensorChanged(
            SensorCustomEvent(
                sourceSensor,
                arrayOf(value)
            )
        )
    }
}
