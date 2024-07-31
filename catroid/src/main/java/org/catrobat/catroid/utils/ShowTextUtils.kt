/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2023 The Catrobat Team
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

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import org.catrobat.catroid.CatroidApplication
import org.catrobat.catroid.utils.NumberFormats.Companion.toMetricUnitRepresentation
import org.catrobat.catroid.utils.NumberFormats.Companion.trimTrailingCharacters
import org.catrobat.catroid.visualplacement.model.TextConfiguration
import java.util.Locale

@Suppress("MagicNumber")
object ShowTextUtils {
    const val DEFAULT_TEXT_SIZE = 45
    const val DEFAULT_X_OFFSET = 3.0f
    const val ALIGNMENT_STYLE_LEFT = 0
    const val ALIGNMENT_STYLE_CENTERED = 1
    const val ALIGNMENT_STYLE_RIGHT = 2

    @JvmStatic
    fun calculateColorRGBs(color: String): IntArray {
        val rgb = IntArray(3)
        val colorValue = color.substring(1).toInt(16)
        rgb[0] = colorValue and 0xFF0000 shr 16
        rgb[1] = colorValue and 0xFF00 shr 8
        rgb[2] = colorValue and 0xFF
        return rgb
    }

    @JvmStatic
    fun sanitizeTextSize(textSize: Float): Float {
        return if (textSize > DEFAULT_TEXT_SIZE * 100.0f) {
            DEFAULT_TEXT_SIZE * 25.0f
        } else if (textSize > 0 && textSize < DEFAULT_TEXT_SIZE * 0.05f) {
            DEFAULT_TEXT_SIZE * 0.25f
        } else {
            textSize
        }
    }

    @JvmStatic
    fun isValidColorString(color: String?) =
        color != null && color.length == 7 && color.matches("#[A-F0-9a-f]+".toRegex())

    @JvmStatic
    fun calculateAlignmentValuesForText(paint: Paint, bitmapWidth: Int, alignment: Int): Int {
        return when (alignment) {
            ALIGNMENT_STYLE_LEFT -> {
                paint.textAlign = Paint.Align.LEFT
                0
            }

            ALIGNMENT_STYLE_RIGHT -> {
                paint.textAlign = Paint.Align.RIGHT
                bitmapWidth
            }

            else -> {
                paint.textAlign = Paint.Align.CENTER
                bitmapWidth / 2
            }
        }
    }

    private fun convertToEnglishDigits(value: String): String {
        return value // Eastern-Arabic ٠
            .replace("١", "1").replace("٢", "2").replace("٣", "3").replace("٤", "4")
            .replace("٥", "5")
            .replace("٦", "6").replace("٧", "7").replace("٨", "8").replace("٩", "9")
            .replace("٠", "0") // Farsi
            .replace("۱", "1").replace("۲", "2").replace("۳", "3").replace("۴", "4")
            .replace("۵", "5")
            .replace("۶", "6").replace("۷", "7").replace("۸", "8").replace("۹", "9")
            .replace("۰", "0") // Hindi
            .replace("१", "1").replace("२", "2").replace("३", "3").replace("४", "4")
            .replace("५", "5")
            .replace("६", "6").replace("७", "7").replace("८", "8").replace("९", "9")
            .replace("०", "0") // Assamese and Bengali
            .replace("১", "1").replace("২", "2").replace("৩", "3").replace("৪", "4")
            .replace("৫", "5")
            .replace("৬", "6").replace("৭", "7").replace("৮", "8").replace("৯", "9")
            .replace("০", "0") // Tamil
            .replace("௧", "1").replace("௦", "0").replace("௨", "2").replace("௩", "3")
            .replace("௪", "4")
            .replace("௫", "5").replace("௬", "6").replace("௭", "7").replace("௮", "8")
            .replace("௯", "9") // Gujarati
            .replace("૧", "1").replace("૨", "2").replace("૩", "3").replace("૪", "4")
            .replace("૫", "5")
            .replace("૬", "6").replace("૭", "7").replace("૮", "8").replace("૯", "9")
            .replace("૦", "0")
    }

    @JvmStatic
    fun isNumberAndInteger(variableValue: String): Boolean {
        return if (variableValue.matches("-?\\d+(\\.\\d+)?".toRegex())) {
            val variableValueIsNumber = convertToEnglishDigits(variableValue).toDouble()

            variableValueIsNumber.toInt() - variableValueIsNumber == 0.0
        } else {
            false
        }
    }

    @JvmStatic
    fun getStringAsInteger(variableValue: String) =
        convertToEnglishDigits(variableValue).toDouble().toInt().toString()

    @JvmStatic
    fun convertColorToString(color: Int): String {
        return String.format(
            "#%02X%02X%02X",
            Color.red(color),
            Color.green(color),
            Color.blue(color)
        )
    }

    @JvmStatic
    fun convertStringToMetricRepresentation(value: String): String {
        var result = value
        try {
            result = toMetricUnitRepresentation(value.toInt())
        } catch (ignored: NumberFormatException) {
        }
        return result
    }

    @JvmStatic
    fun convertObjectToString(objectToConvert: Any): String {
        return if (objectToConvert is Boolean) {
            AndroidStringProvider(CatroidApplication.getAppContext())
                .getTrueOrFalse(objectToConvert)
        } else {
            convertStringToMetricRepresentation(
                trimTrailingCharacters(
                    objectToConvert.toString()
                )
            )
        }
    }

    fun convertTextToBitmap(
        config: TextConfiguration
    ): Bitmap {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        val textSizeInPx = sanitizeTextSize(DEFAULT_TEXT_SIZE * config.relativeSize)
        paint.textSize = textSizeInPx

        if (isValidColorString(config.color)) {
            val rgb: IntArray = calculateColorRGBs(config.color.toUpperCase(Locale.getDefault()))
            paint.color = -0x1000000 or (rgb[0] shl 16) or (rgb[1] shl 8) or rgb[2]
        } else {
            paint.color = Color.BLACK
        }

        val baseline = -paint.ascent()
        val bitmapWidth = paint.measureText(config.text).toInt() * 2
        val height = (baseline + paint.descent()).toInt()
        calculateAlignmentValuesForText(paint, bitmapWidth, config.alignment)

        return Bitmap.createBitmap(bitmapWidth, height * 2, Bitmap.Config.ARGB_8888)
            .also {
                Canvas(it).drawText(
                    config.text,
                    bitmapWidth / 2F - DEFAULT_X_OFFSET,
                    height + textSizeInPx - paint.descent(),
                    paint
                )
            }
    }
}
