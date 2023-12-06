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
import android.graphics.Bitmap
import android.text.style.ImageSpan
import androidx.core.graphics.drawable.RoundedBitmapDrawable
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory

private const val COLOR_SQUARE_PADDING_LEFT = 15
private const val COLOR_SQUARE_PADDING_TOP = 0
private const val COLOR_STRING_CONVERSION_CONSTANT = 16
private const val COLOR_SQUARE_ROUNDED_CORNER_DIVIDER = 4

class VisualizeColorString(
    context: Context,
    colorString: String,
    bitmapSize: Float
) {

    var drawable: RoundedBitmapDrawable
    var imageSpan: VisualizeColorImageSpan
    var colorValue = 0

    init {
        colorValue = getColorValueFromColorString(colorString)
        val squareBitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.RGB_565)
        squareBitmap.setPixel(0, 0, colorValue)
        drawable = RoundedBitmapDrawableFactory.create(
            context.resources, Bitmap.createScaledBitmap(
                squareBitmap,
                bitmapSize.toInt(),
                bitmapSize.toInt(),
                false
            )
        )
        drawable.cornerRadius = bitmapSize / COLOR_SQUARE_ROUNDED_CORNER_DIVIDER
        drawable.setBounds(
            COLOR_SQUARE_PADDING_LEFT, COLOR_SQUARE_PADDING_TOP,
            drawable.intrinsicWidth + COLOR_SQUARE_PADDING_LEFT,
            drawable.intrinsicHeight + COLOR_SQUARE_PADDING_TOP
        )
        imageSpan = VisualizeColorImageSpan(drawable, colorValue)
    }

    private fun getColorValueFromColorString(colorString: String): Int {
        val newString = colorString.replace(Regex("[^A-Za-z0-9]"), "")
        return try {
            newString.toInt(COLOR_STRING_CONVERSION_CONSTANT)
        } catch (nfe: NumberFormatException) {
            0
        }
    }
}

class VisualizeColorImageSpan(
    drawable: RoundedBitmapDrawable,
    val colorValue: Int
) : ImageSpan(drawable, ALIGN_BOTTOM)
