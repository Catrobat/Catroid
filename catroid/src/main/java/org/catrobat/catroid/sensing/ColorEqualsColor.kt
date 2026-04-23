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
package org.catrobat.catroid.sensing

import androidx.core.graphics.ColorUtils
import com.badlogic.gdx.graphics.Color
import org.catrobat.catroid.formulaeditor.common.Conversions.convertArgumentToDouble
import org.catrobat.catroid.formulaeditor.common.Conversions.isValidHexColor

private const val MAX_DISTANCE = 258.693f
private const val MAX_EIGHT_BIT_VALUE = 255f
private const val RATIO_TO_PERCENT_MULTIPLIER = 100f
private const val NUMBER_OF_LAB_COMPONENTS = 3
private const val HEX_COLOR_STRING_LENGTH = 6
private const val MAX_PERCENT = 100
private const val MIN_PERCENT = 0

class ColorEqualsColor {

    fun tryInterpretFunctionColorEqualsColor(firstColor: Any?, secondColor: Any?, tolerance: Any?):
        Boolean {

        if (firstColor !is String || secondColor !is String) return false

        val firstColorHexString = addHashtagIfMissing(firstColor)
        val secondColorHexString = addHashtagIfMissing(secondColor)

        if (!firstColorHexString.isValidHexColor() || !secondColorHexString.isValidHexColor()) {
            return false
        }

        val toleranceAsDouble = convertArgumentToDouble(tolerance) ?: return false

        if (isToleranceParameterInvalid(toleranceAsDouble)) return false

        if (isToleranceParameterOverAHundred(toleranceAsDouble)) return true

        val euclideanDistanceInPercent = calculateEuclideanDistanceInPercent(
            firstColorHexString,
            secondColorHexString
        )

        if (euclideanDistanceInPercent > toleranceAsDouble) return false

        return true
    }

    private fun calculateEuclideanDistanceInPercent(firstColorHex: String, secondColorHex: String):
        Double {
        val firstColorLab = DoubleArray(NUMBER_OF_LAB_COMPONENTS)
        val secondColorLab = DoubleArray(NUMBER_OF_LAB_COMPONENTS)

        val firstColorRGB = Color.valueOf(firstColorHex as String)
        val secondColorRGB = Color.valueOf(secondColorHex as String)

        ColorUtils.RGBToLAB(
            (firstColorRGB.r * MAX_EIGHT_BIT_VALUE).toInt(),
            (firstColorRGB.g
                * MAX_EIGHT_BIT_VALUE).toInt(),
            (firstColorRGB.b * MAX_EIGHT_BIT_VALUE).toInt(),
            firstColorLab
        )

        ColorUtils.RGBToLAB(
            (secondColorRGB.r * MAX_EIGHT_BIT_VALUE).toInt(),
            (secondColorRGB.g
                * MAX_EIGHT_BIT_VALUE).toInt(),
            (secondColorRGB.b * MAX_EIGHT_BIT_VALUE).toInt(),
            secondColorLab
        )

        val euclideanDistance = ColorUtils.distanceEuclidean(firstColorLab, secondColorLab)

        return euclideanDistance / MAX_DISTANCE * RATIO_TO_PERCENT_MULTIPLIER
    }

    private fun addHashtagIfMissing(parameter: String): String {
        return if (parameter.length == HEX_COLOR_STRING_LENGTH) {
            "#$parameter"
        } else {
            parameter
        }
    }

    private fun isToleranceParameterInvalid(parameter: Double): Boolean = parameter.isNaN() ||
        parameter < MIN_PERCENT

    private fun isToleranceParameterOverAHundred(parameter: Double): Boolean = parameter >= MAX_PERCENT
}
