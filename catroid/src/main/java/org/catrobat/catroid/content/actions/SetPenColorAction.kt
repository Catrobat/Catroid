/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2026 The Catrobat Team
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

class SetPenColorAction : TemporalAction() {
    var scope: Scope? = null
    var red: Formula? = null
    var green: Formula? = null
    var blue: Formula? = null

    override fun update(percent: Float) {
        try {
            val currentScope = scope ?: return
            val newRed = red?.interpretInteger(currentScope) ?: 0
            val newGreen = green?.interpretInteger(currentScope) ?: 0
            val newBlue = blue?.interpretInteger(currentScope) ?: 0

            val color = Color()
            // Using manual ARGB to Color conversion to avoid android.graphics.Color dependency in unit tests.
            // android.graphics.Color.argb(0xFF, newRed, newGreen, newBlue) returns (0xFF shl 24) | (R shl 16) | (G shl 8) | B
            val argb = (ALPHA_OPAQUE shl ALPHA_SHIFT) or
                ((newRed and COLOR_MASK) shl RED_SHIFT) or
                ((newGreen and COLOR_MASK) shl GREEN_SHIFT) or
                (newBlue and COLOR_MASK)

            Color.argb8888ToColor(color, argb)
            currentScope.sprite.penConfiguration.setPenColor(PenColor(color.r, color.g, color.b, color.a))
        } catch (interpretationException: InterpretationException) {
            Log.d(javaClass.simpleName, "Formula interpretation for this specific Brick failed.", interpretationException)
        }
    }

    companion object {
        private const val ALPHA_OPAQUE = 0xFF
        private const val COLOR_MASK = 0xFF
        private const val ALPHA_SHIFT = 24
        private const val RED_SHIFT = 16
        private const val GREEN_SHIFT = 8
    }
}
