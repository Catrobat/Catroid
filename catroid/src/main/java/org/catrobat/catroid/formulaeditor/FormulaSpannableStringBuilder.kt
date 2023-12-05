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
package org.catrobat.catroid.formulaeditor

import android.content.Context
import android.text.SpannableStringBuilder
import android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE

object FormulaSpannableStringBuilder {

    private const val BITMAP_SIZE_MULTIPLIER = 1.25f
    private const val COLOR_SQUARE_PADDING_LEFT = 15
    private const val COLOR_SQUARE_PADDING_TOP = 0
    private const val COLOR_STRING_CONVERSION_CONSTANT = 16

    fun buildSpannableFormulaString(context: Context, formulaString: String, textSize: Float):
        SpannableStringBuilder {
        val stringBuilder = SpannableStringBuilder()
        val formulaStringList = formulaString.split(" ")
        for (variable in formulaStringList) {
            if (isColorString(variable)) {
                addColoredSquareToColorString(context, variable, textSize*BITMAP_SIZE_MULTIPLIER,
                                              stringBuilder)
            } else if (variable.isEmpty()) {
                continue
            } else {
                stringBuilder.append("$variable ")
            }
        }
        return stringBuilder
    }

    private fun isColorString(colorString: String): Boolean = colorString.matches(Regex("^'#.{6}'$"))

    private fun getColorValueFromColorString(colorString: String): Int {
        val newString = colorString.replace(Regex("[^A-Za-z0-9]"), "")
        return try {
            newString.toInt(COLOR_STRING_CONVERSION_CONSTANT)
        } catch (nfe: NumberFormatException) {
            0
        }
    }

    private fun addColoredSquareToColorString(context: Context,
        colorString: String,
        bitmapSize: Float,
        stringBuilder: SpannableStringBuilder) {
        val colorStringCut = colorString.substring(0, colorString.length - 1)
        stringBuilder.append("$colorStringCut ")
        var colorSquare = VisualizeColorString(context, colorString, bitmapSize)
        stringBuilder.setSpan(colorSquare.imageSpan, stringBuilder.length - 1 , stringBuilder.length,
                              SPAN_EXCLUSIVE_EXCLUSIVE)
/*        stringBuilder.setSpan(colorSquare.getClickableSpan(), stringBuilder.length - 1 , stringBuilder.length,
                              SPAN_EXCLUSIVE_EXCLUSIVE)*/
        stringBuilder.append("' ")
    }

    /*private fun addColoredSquareToColorString(context: Context,
        colorString: String,
        bitmapSize: Float,
        stringBuilder: SpannableStringBuilder) {
        val color = getColorValueFromColorString(colorString)
        val colorStringCut = colorString.substring(0, colorString.length - 1)
        val squareBitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.RGB_565)
        squareBitmap.setPixel(0, 0, color)
        val roundedSquareDrawable = RoundedBitmapDrawableFactory.create(
            context.resources, Bitmap.createScaledBitmap(
                squareBitmap,
                bitmapSize.toInt(),
                bitmapSize.toInt(),
                false
            )
        )
        roundedSquareDrawable.cornerRadius = bitmapSize / 4
        stringBuilder.append("$colorStringCut ")
        roundedSquareDrawable.setBounds(
            COLOR_SQUARE_PADDING_LEFT, COLOR_SQUARE_PADDING_TOP, roundedSquareDrawable.intrinsicWidth +
                COLOR_SQUARE_PADDING_LEFT,
            roundedSquareDrawable.intrinsicHeight + COLOR_SQUARE_PADDING_TOP
        )
        val span = ImageSpan(roundedSquareDrawable, ImageSpan.ALIGN_BOTTOM)
        stringBuilder.setSpan(span, stringBuilder.length - 1 , stringBuilder.length, SPAN_EXCLUSIVE_EXCLUSIVE)
        stringBuilder.append("' ")
    }*/
}
