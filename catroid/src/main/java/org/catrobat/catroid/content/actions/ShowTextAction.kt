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
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction
import org.catrobat.catroid.content.Scope
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.formulaeditor.InterpretationException
import org.catrobat.catroid.formulaeditor.UserVariable
import org.catrobat.catroid.stage.ShowTextActor
import org.catrobat.catroid.stage.StageActivity

class ShowTextAction : TemporalAction() {
    private var xPosition: Formula? = null
    private var yPosition: Formula? = null
    private var variableToShow: UserVariable? = null
    private var scope: Scope? = null
    private var showTextActor: ShowTextActor? = null
    override fun begin() {
        try {
            val xPosition = xPosition!!.interpretInteger(scope)
            val yPosition = yPosition!!.interpretInteger(scope)
            if (StageActivity.stageListener != null) {
                val stageActors = StageActivity.stageListener.stage.actors
                val dummyActor = ShowTextActor(
                    UserVariable("dummyActor"), 0, 0,
                    0.0f, null, scope!!.sprite
                )
                for (actor in stageActors) {
                    if (actor.javaClass == dummyActor.javaClass) {
                        val showTextActor = actor as ShowTextActor
                        if (showTextActor.variableNameToCompare == variableToShow!!.name && showTextActor.sprite == scope!!.sprite) {
                            actor.remove()
                        }
                    }
                }
                showTextActor =
                    ShowTextActor(variableToShow, xPosition, yPosition, 1.0f, null, scope!!.sprite)
                StageActivity.stageListener.addActor(showTextActor)
            }
            variableToShow!!.visible = true
        } catch (e: InterpretationException) {
            Log.d(TAG, "InterpretationException: $e")
        }
    }

    override fun update(percent: Float) {
        try {
            val xPosition = xPosition!!.interpretInteger(scope)
            val yPosition = yPosition!!.interpretInteger(scope)
            if (showTextActor != null) {
                showTextActor!!.setPositionX(xPosition)
                showTextActor!!.setPositionY(yPosition)
            }
        } catch (e: InterpretationException) {
            Log.d(TAG, "InterpretationException")
        }
    }

    fun setPosition(xPosition: Formula?, yPosition: Formula?) {
        this.xPosition = xPosition
        this.yPosition = yPosition
    }

    fun setScope(scope: Scope?) {
        this.scope = scope
    }

    fun setVariableToShow(userVariable: UserVariable?) {
        variableToShow = userVariable
    }

    companion object {
        val TAG = ShowTextAction::class.java.simpleName
    }
}