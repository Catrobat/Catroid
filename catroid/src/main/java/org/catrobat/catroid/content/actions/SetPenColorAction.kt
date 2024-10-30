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
package org.catrobat.catroid.content.actions

import android.util.Log
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction
import org.catrobat.catroid.content.PenColor
import org.catrobat.catroid.content.Scope
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.formulaeditor.InterpretationException

open class SetPenColorAction : TemporalAction() {
    private var scope: Scope? = null
    private var red: Formula? = null
    private var green: Formula? = null
    private var blue: Formula? = null
    override fun update(delta: Float) {
        try {
            val newRed = (if (red == null) 0 else red?.interpretInteger(scope)) ?: 0
            val newGreen = if (green == null) 0 else green?.interpretInteger(scope) ?: 0
            val newBlue = if (blue == null) 0 else blue?.interpretInteger(scope) ?: 0
            val color = Color()
            Color.argb8888ToColor(
                color,
                argbToInt(newRed, newGreen, newBlue)
            )
            scope?.sprite?.penConfiguration?.penColor = PenColor(color.r, color.g, color.b, color.a)
        } catch (interpretationException: InterpretationException) {
            Log.d(
                javaClass.simpleName,
                "Formula interpretation for this specific Brick failed.",
                interpretationException
            )
        }
    }

    open fun argbToInt(redInt: Int, greenInt: Int, blueInt: Int) =
        android.graphics.Color.argb(COLOR_ALPHA, redInt, greenInt, blueInt)

    fun setScope(scope: Scope?) {
        this.scope = scope
    }

    fun setRed(red: Formula?) {
        this.red = red
    }

    fun setGreen(green: Formula?) {
        this.green = green
    }

    fun setBlue(blue: Formula?) {
        this.blue = blue
    }

    companion object {
        private const val COLOR_ALPHA = 0xFF
    }
}
