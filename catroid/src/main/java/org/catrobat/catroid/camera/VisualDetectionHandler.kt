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
import android.graphics.Rect
import androidx.annotation.VisibleForTesting
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.text.Text
import com.huawei.hms.mlsdk.face.MLFace
import org.catrobat.catroid.formulaeditor.SensorCustomEventListener
import org.catrobat.catroid.formulaeditor.Sensors
import org.catrobat.catroid.utils.TextBlockUtil
import org.catrobat.catroid.utils.translateToStageCoordinates
import org.catrobat.catroid.utils.writeFloatToSensor
import org.catrobat.catroid.utils.writeStringToSensor
import kotlin.math.roundToInt

data class VisualDetectionHandlerFace(val id: Int, val boundingBox: Rect)

object VisualDetectionHandler {
    private const val MAX_FACE_SIZE = 100
    private const val FACE_SENSORS = 2
    private val sensorListeners = mutableSetOf<SensorCustomEventListener>()

    var facesForSensors: Array<VisualDetectionHandlerFace?> = Array(FACE_SENSORS) { _ -> null }
    private var faceIds: IntArray = IntArray(FACE_SENSORS) { _ -> -1 }

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

    fun updateTextSensorValues(text: Text, imageWidth: Int, imageHeight: Int) {
        if (text.textBlocks.isEmpty()) return

        TextBlockUtil.setTextBlocks(text.textBlocks, imageWidth, imageHeight)

        sensorListeners.forEach {
            writeStringToSensor(it, Sensors.TEXT_FROM_CAMERA, text.text)
            writeFloatToSensor(it, Sensors.TEXT_BLOCKS_NUMBER, text.textBlocks.size.toFloat())
        }
    }

    fun updateAllFaceSensorValues(imageWidth: Int, imageHeight: Int) {
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
        val firstSensorValue = if (facesForSensors[0] != null) 1f else 0f
        val secondSensorValue = if (facesForSensors[1] != null) 1f else 0f
        sensorListeners.forEach {
            writeFloatToSensor(it, Sensors.FACE_DETECTED, firstSensorValue)
            writeFloatToSensor(it, Sensors.SECOND_FACE_DETECTED, secondSensorValue)
        }
    }

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
}
