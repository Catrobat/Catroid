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

    @JvmStatic
    fun buildSpannableFormulaString(context: Context, formulaString: String, textSize: Float):
        SpannableStringBuilder {
        val stringBuilder = SpannableStringBuilder()
        val formulaStringList = formulaString.split(" ")
        for (variable in formulaStringList) {
            if (isColorString(variable)) {
                addColoredSquareToColorString(
                    context,
                    variable,
                    textSize * BITMAP_SIZE_MULTIPLIER,
                    stringBuilder
                )
            } else if (variable.isEmpty()) {
                continue
            } else {
                stringBuilder.append("$variable ")
            }
        }
        return stringBuilder
    }

    private fun isColorString(colorString: String): Boolean =
        colorString.matches(Regex("^'#.{6}'$"))

    private fun addColoredSquareToColorString(
        context: Context,
        colorString: String,
        bitmapSize: Float,
        stringBuilder: SpannableStringBuilder
    ) {
        val colorStringCut = colorString.substring(0, colorString.length - 1)
        stringBuilder.append("$colorStringCut ")
        val colorSquare = VisualizeColorString(context, colorString, bitmapSize)
        stringBuilder.setSpan(
            colorSquare.imageSpan,
            stringBuilder.length - 1,
            stringBuilder.length,
            SPAN_EXCLUSIVE_EXCLUSIVE
        )
        stringBuilder.append("' ")
    }
}
