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
import android.text.SpannableStringBuilder
import android.text.style.ImageSpan
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory

object FormulaSpannableStringBuilder {

    fun buildSpannableFormulaString(context: Context, formulaString: String) : SpannableStringBuilder  {
        val stringBuilder = SpannableStringBuilder();
        val formulaStringList = formulaString.split(" ");
        for (variable in formulaStringList) {
            if (isColorString(variable)) {
                addColoredSquareToColorString(context, variable, stringBuilder)
            } else {
                stringBuilder.append("$variable ")
            }
        }
        return stringBuilder
    }

    private fun isColorString(colorString: String): Boolean {
        return colorString.matches(Regex("^'#.{6}'$"))
    }

    private fun getColorValueFromColorString(colorString: String): Int {
        val newString = colorString.replace(Regex("[^A-Za-z0-9]"), "")
        return try {
            newString.toInt(16)
        } catch (e: Exception) {
            return 0
        }
    }

    private fun addColoredSquareToColorString(context: Context, colorString: String, stringBuilder:
    SpannableStringBuilder) {
        val color = getColorValueFromColorString(colorString)
        val colorStringCut = colorString.substring(0, colorString.length)
        val squareBitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.RGB_565)
        squareBitmap.setPixel(0, 0, color)
        val roundedSquareDrawable = RoundedBitmapDrawableFactory.create(
            context.resources, Bitmap.createScaledBitmap(
                squareBitmap,
                60,
                60,
                false
            )
        )
        roundedSquareDrawable.cornerRadius = 20f
        stringBuilder.append(colorStringCut)
        roundedSquareDrawable.setBounds(
            15, 5, roundedSquareDrawable.intrinsicWidth + 15,
            roundedSquareDrawable.intrinsicHeight + 5
        )
        val span = ImageSpan(roundedSquareDrawable, ImageSpan.ALIGN_BOTTOM)
        stringBuilder.setSpan(span, stringBuilder.length - 1, stringBuilder.length, 0)
        stringBuilder.append("' ")
    }
}
