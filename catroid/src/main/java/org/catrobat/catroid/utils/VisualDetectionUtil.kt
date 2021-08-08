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

package org.catrobat.catroid.utils

import android.graphics.Point
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.common.ScreenValues
import org.catrobat.catroid.formulaeditor.SensorCustomEvent
import org.catrobat.catroid.formulaeditor.SensorCustomEventListener
import org.catrobat.catroid.formulaeditor.Sensors
import org.catrobat.catroid.stage.StageActivity
import kotlin.math.roundToInt

fun translateToStageCoordinates(
    x: Float,
    y: Float,
    imageWidth: Int,
    imageHeight: Int
): Point {
    val frontCamera = StageActivity.getActiveCameraManager().isCameraFacingFront
    val aspectRatio = imageWidth.toFloat() / imageHeight

    return if (ProjectManager.getInstance().isCurrentProjectLandscapeMode) {
        val relativeX = y / imageHeight
        val relativeY = x / imageWidth
        coordinatesFromRelativePosition(
            1 - relativeX,
            ScreenValues.SCREEN_WIDTH / aspectRatio,
            if (frontCamera) relativeY else 1 - relativeY,
            ScreenValues.SCREEN_WIDTH.toFloat()
        )
    } else {
        val relativeX = x / imageHeight
        coordinatesFromRelativePosition(
            if (frontCamera) 1 - relativeX else relativeX,
            ScreenValues.SCREEN_HEIGHT / aspectRatio,
            1 - y / imageWidth,
            ScreenValues.SCREEN_HEIGHT.toFloat()
        )
    }
}

fun coordinatesFromRelativePosition(
    relativeX: Float,
    width: Float,
    relativeY: Float,
    height: Float
) = Point(
    (width * (relativeX - Constants.COORDINATE_TRANSFORMATION_OFFSET)).roundToInt(),
    (height * (relativeY - Constants.COORDINATE_TRANSFORMATION_OFFSET)).roundToInt()
)

fun writeFloatToSensor(
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

fun writeStringToSensor(
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
