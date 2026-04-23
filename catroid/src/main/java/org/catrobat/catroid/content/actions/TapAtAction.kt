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
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction
import org.catrobat.catroid.content.Scope
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.formulaeditor.InterpretationException
import org.catrobat.catroid.stage.StageActivity

class TapAtAction : TemporalAction() {

    private lateinit var stage: Stage
    private var touchCoords: Vector2 = Vector2(0f, 0f)
    private var dragCoords: Vector2? = null
    private var errorDetected: Boolean = false
    private var pointer: Int = -1
    private var skipUpdate: Boolean = true
    lateinit var scope: Scope
    lateinit var startX: Formula
    lateinit var startY: Formula
    var changeX: Formula? = null
    var changeY: Formula? = null
    var durationFormula: Formula? = null

    override fun begin() {
        super.begin()
        try {
            touchCoords = Vector2(startX.interpretFloat(scope), startY.interpretFloat(scope))
            if (changeX != null && changeY != null) {
                dragCoords = Vector2(
                    changeX?.interpretFloat(scope) ?: 0f,
                    changeY?.interpretFloat(scope) ?: 0f
                )
                skipUpdate = false
            }

            duration = durationFormula?.interpretFloat(scope) ?: 0f
        } catch (e: InterpretationException) {
            Log.d(TAG, "Position not valid", e)
            errorDetected = true
        }

        if (!errorDetected) {
            pointer = scope.sprite.unusedPointer
            stage = StageActivity.stageListener.stage
            if (!skipUpdate) {
                stage.stageToScreenCoordinates(dragCoords)
            }
            stage.stageToScreenCoordinates(touchCoords)
            stage.touchDown(touchCoords.x.toInt(), touchCoords.y.toInt(), pointer, 0)
        } else {
            duration = 0f
        }
    }

    override fun update(percent: Float) {
        if (!skipUpdate) {
            val x: Int =
                (touchCoords.x * (1 - percent) + (dragCoords?.x?.times(percent) ?: 0f)).toInt()
            val y: Int =
                (touchCoords.y * (1 - percent) + (dragCoords?.y?.times(percent) ?: 0f)).toInt()
            stage.touchDragged(x, y, pointer)
        }
    }

    override fun end() {
        if (!errorDetected) {
            if (skipUpdate) {
                stage.touchUp(touchCoords.x.toInt(), touchCoords.y.toInt(), pointer, 0)
            } else {
                stage.touchUp(
                    dragCoords?.x?.toInt() ?: touchCoords.x.toInt(),
                    dragCoords?.y?.toInt() ?: touchCoords.y.toInt(), pointer, 0
                )
            }
            scope.sprite.releaseUsedPointer(pointer)
            pointer = -1
        }
    }

    override fun restart() {
        if (pointer != -1) {
            stage.touchUp(touchCoords.x.toInt(), touchCoords.y.toInt(), pointer, 0)
            scope?.sprite?.releaseUsedPointer(pointer)
            pointer = -1
        }
        super.restart()
    }

    companion object {
        val TAG = TapAtAction::class.java.simpleName
    }
}
