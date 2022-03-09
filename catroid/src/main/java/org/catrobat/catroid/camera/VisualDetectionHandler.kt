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

import android.graphics.Point
import android.graphics.Rect
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseLandmark
import com.huawei.hms.mlsdk.face.MLFace
import com.huawei.hms.mlsdk.skeleton.MLJoint
import com.huawei.hms.mlsdk.skeleton.MLSkeleton
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.common.ScreenValues
import org.catrobat.catroid.formulaeditor.SensorCustomEvent
import org.catrobat.catroid.formulaeditor.SensorCustomEventListener
import org.catrobat.catroid.formulaeditor.SensorHandler
import org.catrobat.catroid.formulaeditor.Sensors
import org.catrobat.catroid.formulaeditor.Sensors.LEFT_FOOT_INDEX_X
import org.catrobat.catroid.formulaeditor.Sensors.LEFT_FOOT_INDEX_Y
import org.catrobat.catroid.formulaeditor.Sensors.LEFT_SHOULDER_X
import org.catrobat.catroid.formulaeditor.Sensors.LEFT_SHOULDER_Y
import org.catrobat.catroid.formulaeditor.Sensors.MOUTH_LEFT_CORNER_X
import org.catrobat.catroid.formulaeditor.Sensors.MOUTH_LEFT_CORNER_Y
import org.catrobat.catroid.formulaeditor.Sensors.MOUTH_RIGHT_CORNER_X
import org.catrobat.catroid.formulaeditor.Sensors.MOUTH_RIGHT_CORNER_Y
import org.catrobat.catroid.formulaeditor.Sensors.RIGHT_EYE_INNER_X
import org.catrobat.catroid.formulaeditor.Sensors.RIGHT_EYE_INNER_Y
import org.catrobat.catroid.formulaeditor.Sensors.RIGHT_EYE_OUTER_X
import org.catrobat.catroid.formulaeditor.Sensors.RIGHT_EYE_OUTER_Y
import org.catrobat.catroid.formulaeditor.Sensors.RIGHT_FOOT_INDEX_X
import org.catrobat.catroid.formulaeditor.Sensors.RIGHT_FOOT_INDEX_Y
import org.catrobat.catroid.formulaeditor.Sensors.RIGHT_SHOULDER_X
import org.catrobat.catroid.formulaeditor.Sensors.RIGHT_SHOULDER_Y
import org.catrobat.catroid.stage.StageActivity
import org.koin.java.KoinJavaComponent.inject
import kotlin.math.roundToInt

data class VisualDetectionHandlerFace(val id: Int, val boundingBox: Rect)

object VisualDetectionHandler {
    private const val MAX_FACE_SIZE = 100
    private const val FACE_SENSORS = 2
    private val sensorListeners = mutableSetOf<SensorCustomEventListener>()

    var facesForSensors: Array<VisualDetectionHandlerFace?> = Array(FACE_SENSORS) { null }
    private var faceIds: IntArray = IntArray(FACE_SENSORS) { -1 }

    @JvmStatic
    fun addListener(listener: SensorCustomEventListener) {
        sensorListeners.add(listener)
    }

    @JvmStatic
    fun removeListener(listener: SensorCustomEventListener) {
        sensorListeners.remove(listener)
    }

    fun translateGoogleFaceToVisualDetectionFace(faceList: List<Face>): List<VisualDetectionHandlerFace> {
        val newFacesList = mutableListOf<VisualDetectionHandlerFace>()
        for (face in faceList) {
            newFacesList.add(VisualDetectionHandlerFace(face.trackingId, face.boundingBox))
        }
        return newFacesList
    }

    fun translateHuaweiFaceToVisualDetectionFace(faceList: List<MLFace>):
        List<VisualDetectionHandlerFace> {
        val newFacesList = mutableListOf<VisualDetectionHandlerFace>()
        for (face in faceList) {
            newFacesList.add(VisualDetectionHandlerFace(face.tracingIdentity, face.border))
        }
        return newFacesList
    }

    fun updateTextSensorValues(text: String, numberOfBlocks: Int) {
        sensorListeners.forEach { sensorListener ->
            sensorListener.writeToSensor(Sensors.TEXT_FROM_CAMERA, text)
            sensorListener.writeToSensor(Sensors.TEXT_BLOCKS_NUMBER, numberOfBlocks.toDouble())
        }
    }

    fun updateAllFaceSensorValues(imageWidth: Int, imageHeight: Int) {
        updateFaceDetectionStatusSensorValues()

        facesForSensors.forEachIndexed { index, face ->
            face?.let {
                val faceBounds = face.boundingBox

                val facePosition = translateToStageCoordinates(
                    faceBounds.exactCenterX().toDouble(), faceBounds.exactCenterY().toDouble(),
                    imageWidth, imageHeight
                )
                val relativeFaceSize =
                    (faceBounds.height().toDouble() / imageHeight).coerceAtMost(1.0)
                val faceSize = (MAX_FACE_SIZE * relativeFaceSize).roundToInt()

                updateFaceSensorValues(facePosition, faceSize, index)
            }
        }
        facesForSensors.fill(null)
    }

    fun handleAlreadyExistingFaces(faces: List<VisualDetectionHandlerFace>) {
        for (face in faces) {
            when (face.id) {
                faceIds[0] -> facesForSensors[0] = face
                faceIds[1] -> facesForSensors[1] = face
            }
        }
    }

    fun handleNewFaces(faces: List<VisualDetectionHandlerFace>) {
        facesForSensors.forEachIndexed { index, face ->
            if (face == null) {
                attachNewFaceIfExisting(faces, index)
            }
        }
    }

    private fun attachNewFaceIfExisting(faces: List<VisualDetectionHandlerFace>, index: Int) {
        for (face in faces) {
            if (!face.id.let { faceIds.contains(it) }) {
                faceIds[index] = face.id ?: -1
                facesForSensors[index] = face
                break
            }
        }
    }

    fun updateFaceDetectionStatusSensorValues() {
        val firstSensorValue = if (facesForSensors[0] != null) 1.0 else 0.0
        val secondSensorValue = if (facesForSensors[1] != null) 1.0 else 0.0
        sensorListeners.forEach { sensorListener ->
            sensorListener.writeToSensor(Sensors.FACE_DETECTED, firstSensorValue)
            sensorListener.writeToSensor(Sensors.SECOND_FACE_DETECTED, secondSensorValue)
        }
    }

    fun updateFaceSensorValues(facePosition: Point, faceSize: Int, faceNumber: Int) {
        sensorListeners.filter { faceNumber in 0..1 }.forEach {
            val sensors = when (faceNumber) {
                1 -> Triple(Sensors.SECOND_FACE_X, Sensors.SECOND_FACE_Y, Sensors.SECOND_FACE_SIZE)
                else -> Triple(Sensors.FACE_X, Sensors.FACE_Y, Sensors.FACE_SIZE)
            }
            it.writePositionAccordingToRotationToSensor(
                sensors.first,
                sensors.second,
                facePosition.toPosition()
            )
            it.writeToSensor(sensors.third, faceSize.toDouble())
        }
    }

    fun updateAllPoseSensorValues(pose: Pose?, imageWidth: Int, imageHeight: Int) {
        val allPoseLandmarks = pose?.allPoseLandmarks

        if (allPoseLandmarks.isNullOrEmpty()) return

        allPoseLandmarks.forEach { poseLandmark ->
            poseLandmark?.let {
                val poseLandmarkPositionTranslated =
                    translateToStageCoordinates(
                        poseLandmark.position.x.toDouble(),
                        poseLandmark.position.y.toDouble(),
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
        val positionSensor = when (poseLandmarkType) {
            PoseLandmark.NOSE -> Pair(Sensors.NOSE_X, Sensors.NOSE_Y)
            PoseLandmark.LEFT_EYE_INNER -> Pair(Sensors.LEFT_EYE_INNER_X, Sensors.LEFT_EYE_INNER_Y)
            PoseLandmark.LEFT_EYE -> Pair(Sensors.LEFT_EYE_CENTER_X, Sensors.LEFT_EYE_CENTER_Y)
            PoseLandmark.LEFT_EYE_OUTER -> Pair(Sensors.LEFT_EYE_OUTER_X, Sensors.LEFT_EYE_OUTER_Y)
            PoseLandmark.RIGHT_EYE_INNER -> Pair(RIGHT_EYE_INNER_X, RIGHT_EYE_INNER_Y)
            PoseLandmark.RIGHT_EYE -> Pair(Sensors.RIGHT_EYE_CENTER_X, Sensors.RIGHT_EYE_CENTER_Y)
            PoseLandmark.RIGHT_EYE_OUTER -> Pair(RIGHT_EYE_OUTER_X, RIGHT_EYE_OUTER_Y)
            PoseLandmark.LEFT_EAR -> Pair(Sensors.LEFT_EAR_X, Sensors.LEFT_EAR_Y)
            PoseLandmark.RIGHT_EAR -> Pair(Sensors.RIGHT_EAR_X, Sensors.RIGHT_EAR_Y)
            PoseLandmark.LEFT_MOUTH -> Pair(MOUTH_LEFT_CORNER_X, MOUTH_LEFT_CORNER_Y)
            PoseLandmark.RIGHT_MOUTH -> Pair(MOUTH_RIGHT_CORNER_X, MOUTH_RIGHT_CORNER_Y)
            else -> null
        }
        positionSensor?.let {
            sensorListener.writePositionAccordingToRotationToSensor(
                it.first, it.second,
                position.toPosition()
            )
        }
    }

    private fun updateUpperBodyPoseSensorValues(
        poseLandmarkType: Int,
        sensorListener: SensorCustomEventListener,
        position: Point
    ) {
        val positionSensor = when (poseLandmarkType) {
            PoseLandmark.LEFT_SHOULDER -> Pair(LEFT_SHOULDER_X, LEFT_SHOULDER_Y)
            PoseLandmark.RIGHT_SHOULDER -> Pair(RIGHT_SHOULDER_X, RIGHT_SHOULDER_Y)
            PoseLandmark.LEFT_ELBOW -> Pair(Sensors.LEFT_ELBOW_X, Sensors.LEFT_ELBOW_Y)
            PoseLandmark.RIGHT_ELBOW -> Pair(Sensors.RIGHT_ELBOW_X, Sensors.RIGHT_ELBOW_Y)
            PoseLandmark.LEFT_WRIST -> Pair(Sensors.LEFT_WRIST_X, Sensors.LEFT_WRIST_Y)
            PoseLandmark.RIGHT_WRIST -> Pair(Sensors.RIGHT_WRIST_X, Sensors.RIGHT_WRIST_Y)
            PoseLandmark.LEFT_PINKY -> Pair(Sensors.LEFT_PINKY_X, Sensors.LEFT_PINKY_Y)
            PoseLandmark.RIGHT_PINKY -> Pair(Sensors.RIGHT_PINKY_X, Sensors.RIGHT_PINKY_Y)
            PoseLandmark.LEFT_INDEX -> Pair(Sensors.LEFT_INDEX_X, Sensors.LEFT_INDEX_Y)
            PoseLandmark.RIGHT_INDEX -> Pair(Sensors.RIGHT_INDEX_X, Sensors.RIGHT_INDEX_Y)
            PoseLandmark.LEFT_THUMB -> Pair(Sensors.LEFT_THUMB_X, Sensors.LEFT_THUMB_Y)
            PoseLandmark.RIGHT_THUMB -> Pair(Sensors.RIGHT_THUMB_X, Sensors.RIGHT_THUMB_Y)
            else -> null
        }
        positionSensor?.let {
            sensorListener.writePositionAccordingToRotationToSensor(
                it.first, it.second,
                position.toPosition()
            )
        }
    }

    private fun updateLowerBodyPoseSensorValues(
        poseLandmarkType: Int,
        sensorListener: SensorCustomEventListener,
        position: Point
    ) {
        val positionSensor = when (poseLandmarkType) {
            PoseLandmark.LEFT_HIP -> Pair(Sensors.LEFT_HIP_X, Sensors.LEFT_HIP_Y)
            PoseLandmark.RIGHT_HIP -> Pair(Sensors.RIGHT_HIP_X, Sensors.RIGHT_HIP_Y)
            PoseLandmark.LEFT_KNEE -> Pair(Sensors.LEFT_KNEE_X, Sensors.LEFT_KNEE_Y)
            PoseLandmark.RIGHT_KNEE -> Pair(Sensors.RIGHT_KNEE_X, Sensors.RIGHT_KNEE_Y)
            PoseLandmark.LEFT_ANKLE -> Pair(Sensors.LEFT_ANKLE_X, Sensors.LEFT_ANKLE_Y)
            PoseLandmark.RIGHT_ANKLE -> Pair(Sensors.RIGHT_ANKLE_X, Sensors.RIGHT_ANKLE_Y)
            PoseLandmark.LEFT_HEEL -> Pair(Sensors.LEFT_HEEL_X, Sensors.LEFT_HEEL_Y)
            PoseLandmark.RIGHT_HEEL -> Pair(Sensors.RIGHT_HEEL_X, Sensors.RIGHT_HEEL_Y)
            PoseLandmark.LEFT_FOOT_INDEX -> Pair(LEFT_FOOT_INDEX_X, LEFT_FOOT_INDEX_Y)
            PoseLandmark.RIGHT_FOOT_INDEX -> Pair(RIGHT_FOOT_INDEX_X, RIGHT_FOOT_INDEX_Y)
            else -> null
        }
        positionSensor?.let {
            sensorListener.writePositionAccordingToRotationToSensor(
                it.first, it.second,
                position.toPosition()
            )
        }
    }

    fun updateAllPoseSensorValuesHuawei(
        skeletonList: List<MLSkeleton>,
        imageWidth: Int,
        imageHeight: Int
    ) {
        if (skeletonList.isNullOrEmpty()) return

        skeletonList[0].joints.forEach { joint ->
            joint?.let {
                val jointPositionTranslated = translateToStageCoordinates(
                    joint.pointX.toDouble(),
                    joint.pointY.toDouble(),
                    imageWidth,
                    imageHeight
                )

                updatePoseSensorValuesHuawei(joint.type, jointPositionTranslated)
            }
        }
    }

    private fun updatePoseSensorValuesHuawei(
        jointType: Int,
        position: Point
    ) {
        sensorListeners.forEach() { sensorListener ->
            val positionSensor = when (jointType) {
                MLJoint.TYPE_HEAD_TOP -> Pair(Sensors.HEAD_TOP_X, Sensors.HEAD_TOP_Y)
                MLJoint.TYPE_NECK -> Pair(Sensors.NECK_X, Sensors.NECK_Y)
                MLJoint.TYPE_LEFT_SHOULDER -> Pair(LEFT_SHOULDER_X, LEFT_SHOULDER_Y)
                MLJoint.TYPE_RIGHT_SHOULDER -> Pair(RIGHT_SHOULDER_X, RIGHT_SHOULDER_Y)
                MLJoint.TYPE_LEFT_ELBOW -> Pair(Sensors.LEFT_ELBOW_X, Sensors.LEFT_ELBOW_Y)
                MLJoint.TYPE_RIGHT_ELBOW -> Pair(Sensors.RIGHT_ELBOW_X, Sensors.RIGHT_ELBOW_Y)
                MLJoint.TYPE_LEFT_WRIST -> Pair(Sensors.LEFT_WRIST_X, Sensors.LEFT_WRIST_Y)
                MLJoint.TYPE_RIGHT_WRIST -> Pair(Sensors.RIGHT_WRIST_X, Sensors.RIGHT_WRIST_Y)
                MLJoint.TYPE_LEFT_HIP -> Pair(Sensors.LEFT_HIP_X, Sensors.LEFT_HIP_Y)
                MLJoint.TYPE_RIGHT_HIP -> Pair(Sensors.RIGHT_HIP_X, Sensors.RIGHT_HIP_Y)
                MLJoint.TYPE_LEFT_KNEE -> Pair(Sensors.LEFT_KNEE_X, Sensors.LEFT_KNEE_Y)
                MLJoint.TYPE_RIGHT_KNEE -> Pair(Sensors.RIGHT_KNEE_X, Sensors.RIGHT_KNEE_Y)
                MLJoint.TYPE_LEFT_ANKLE -> Pair(Sensors.LEFT_ANKLE_X, Sensors.LEFT_ANKLE_Y)
                MLJoint.TYPE_RIGHT_ANKLE -> Pair(Sensors.RIGHT_ANKLE_X, Sensors.RIGHT_ANKLE_Y)
                else -> null
            }
            positionSensor?.let {
                sensorListener.writePositionAccordingToRotationToSensor(
                    it.first, it.second,
                    position.toPosition()
                )
            }
        }
    }

    private fun Point.toPosition() = Position(x.toDouble(), y.toDouble())

    private fun translateToStageCoordinates(
        x: Double,
        y: Double,
        imageWidth: Int,
        imageHeight: Int
    ): Point {
        val frontCamera = StageActivity.getActiveCameraManager().isCameraFacingFront
        val aspectRatio = imageWidth.toDouble() / imageHeight

        val projectManager: ProjectManager by inject(ProjectManager::class.java)
        return if (projectManager.isCurrentProjectLandscapeMode) {
            val relativeX = y / imageHeight
            val relativeY = x / imageWidth
            coordinatesFromRelativePosition(
                1 - relativeX,
                ScreenValues.SCREEN_WIDTH / aspectRatio,
                if (frontCamera) relativeY else 1 - relativeY,
                ScreenValues.SCREEN_WIDTH.toDouble()
            )
        } else {
            val relativeX = x / imageHeight
            coordinatesFromRelativePosition(
                if (frontCamera) 1 - relativeX else relativeX,
                ScreenValues.SCREEN_HEIGHT / aspectRatio,
                1 - y / imageWidth,
                ScreenValues.SCREEN_HEIGHT.toDouble()
            )
        }
    }

    fun coordinatesFromRelativePosition(
        relativeX: Double,
        width: Double,
        relativeY: Double,
        height: Double
    ) = Point(
        (width * (relativeX - Constants.COORDINATE_TRANSFORMATION_OFFSET)).roundToInt(),
        (height * (relativeY - Constants.COORDINATE_TRANSFORMATION_OFFSET)).roundToInt()
    )

    private fun SensorCustomEventListener.writePositionAccordingToRotationToSensor(
        sensorX: Sensors,
        sensorY: Sensors,
        position: Position
    ) {
        val stagePosition = SensorHandler.getPositionAccordingToRotation(position)
        writeToSensor(sensorX, stagePosition.x)
        writeToSensor(sensorY, stagePosition.y)
    }

    private fun SensorCustomEventListener.writeToSensor(sourceSensor: Sensors, value: Any) {
        this.onCustomSensorChanged(SensorCustomEvent(sourceSensor, value))
    }
}
