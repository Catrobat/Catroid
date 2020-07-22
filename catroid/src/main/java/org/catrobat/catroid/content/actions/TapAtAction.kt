/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2018 The Catrobat Team
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
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.formulaeditor.InterpretationException
import org.catrobat.catroid.stage.StageActivity

class TapAtAction : TemporalAction() {

    private lateinit var stage: Stage
    private lateinit var touchCoords: Vector2
    private var errorDetected: Boolean = false
    private var pointer: Int = 0
    private var x = 0f
    private var y = 0f
    lateinit var sprite: Sprite

    override fun begin() {
        super.begin()
        if (!errorDetected) {
            pointer = sprite.unusedPointer ?: 0
            stage = StageActivity.stageListener.stage
            touchCoords = Vector2(x, y)
            stage.stageToScreenCoordinates(touchCoords)
            stage.touchDown(touchCoords.x.toInt(), touchCoords.y.toInt(), pointer, 0)
        } else {
            duration = 0f
        }
    }

    override fun update(percent: Float) = Unit

    override fun end() {
        if (!errorDetected) {
            stage.touchUp(touchCoords.x.toInt(), touchCoords.y.toInt(), pointer, 0)
            sprite.releaseUsedPointer(pointer)
        }
    }

    fun setPosition(x: Formula?, y: Formula?) {
        try {
            this.x = x?.interpretFloat(sprite) ?: 0f
            this.y = y?.interpretFloat(sprite) ?: 0f
        } catch (e: InterpretationException) {
            Log.d(TAG, "Position not valid", e)
            errorDetected = true
        }
    }

    fun setDuration(durationFormula: Formula) {
        duration = try {
            durationFormula.interpretFloat(sprite)
        } catch (e: InterpretationException) {
            Log.d(TAG, "Duration not valid", e)
            errorDetected = true
            0f
        }
    }

    companion object {
        val TAG = TapAtAction::class.java.simpleName
    }
}
