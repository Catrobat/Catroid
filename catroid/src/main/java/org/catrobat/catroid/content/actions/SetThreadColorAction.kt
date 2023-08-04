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
package org.catrobat.catroid.content.actions

import android.util.Log
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction
import org.catrobat.catroid.content.Scope
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.formulaeditor.Formula

open class SetThreadColorAction : TemporalAction() {
    private var scope: Scope? = null
    private var color: Formula? = null
    private var sprite: Sprite? = null
    override fun update(delta: Float) {
        var colorStringInterpretation = "#ff0000"
        var red: String = ""
        var green: String = ""
        var blue: String = ""
        var redInt: Int = 0
        var blueInt: Int = 0
        var greenInt: Int = 0

        if (color != null) {
            colorStringInterpretation = color?.interpretString(scope).toString()
        }

        try {
            red = "0x" + colorStringInterpretation.substring(SUBSTRING_POS_FIRST, SUBSTRING_POS_THIRD)
            green = "0x" + colorStringInterpretation.substring(SUBSTRING_POS_THIRD, SUBSTRING_POS_FIFTH)
            blue = "0x" + colorStringInterpretation.substring(SUBSTRING_POS_FIFTH, SUBSTRING_POS_SEVENTH)
        } catch (exception: StringIndexOutOfBoundsException) {
            Log.d(
                javaClass.simpleName,
                "Formula interpretation for this specific Brick failed.",
                exception
            )
            return
        }
        try {
            redInt = Integer.decode(red)
            greenInt = Integer.decode(green)
            blueInt = Integer.decode(blue)
        } catch (exception: NumberFormatException) {
            Log.d(
                javaClass.simpleName,
                "Formula interpretation for this specific Brick failed.",
                exception
            )
            return
        }
            val colorInterpretation = Color()
            val colorValue = argbToInt(redInt, greenInt, blueInt)
            Color.argb8888ToColor(colorInterpretation, colorValue)
            sprite?.embroideryThreadColor = colorInterpretation
    }

    open fun argbToInt(redInt: Int, greenInt: Int, blueInt: Int) =
        android.graphics.Color.argb(COLOR_ALPHA, redInt, greenInt, blueInt)

    fun setScope(scope: Scope?) {
        this.scope = scope
    }

    fun setSprite(sprite: Sprite?) {
        this.sprite = sprite
    }

    fun setColor(color: Formula?) {
        this.color = color
    }

    companion object {
        private const val COLOR_ALPHA = 0xFF
        private const val SUBSTRING_POS_FIRST = 1
        private const val SUBSTRING_POS_THIRD = 3
        private const val SUBSTRING_POS_FIFTH = 5
        private const val SUBSTRING_POS_SEVENTH = 7
    }
}
