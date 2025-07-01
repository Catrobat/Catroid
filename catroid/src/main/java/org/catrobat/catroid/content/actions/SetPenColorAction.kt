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
import org.catrobat.catroid.content.PenColor
import org.catrobat.catroid.content.Scope
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.formulaeditor.InterpretationException

class SetPenColorAction : TemporalAction() {

    private lateinit var scope: Scope
    private var red: Formula? = null
    private var green: Formula? = null
    private var blue: Formula? = null

    public override fun update(delta: Float) {
        try {
            val newRed = red?.interpretInteger(scope) ?: 0
            val newGreen = green?.interpretInteger(scope) ?: 0
            val newBlue = blue?.interpretInteger(scope) ?: 0
            val color = Color()
            Color.argb8888ToColor(color, android.graphics.Color.argb(0xFF, newRed, newGreen, newBlue))
            val penColor = PenColor(color.r, color.g, color.b, color.a)
            scope.sprite.penConfiguration.setPenColor(penColor)
        } catch (interpretationException: InterpretationException) {
            Log.d(javaClass.simpleName, "Formula interpretation for this specific Brick failed.", interpretationException)
        }
    }

    fun setScope(scope: Scope) {
        this.scope = scope
    }

    fun setRed(red: Formula) {
        this.red = red
    }

    fun setGreen(green: Formula) {
        this.green = green
    }

    fun setBlue(blue: Formula) {
        this.blue = blue
    }
}
