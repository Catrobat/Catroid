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
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction
import org.catrobat.catroid.content.Scope
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.formulaeditor.InterpretationException
import org.catrobat.catroid.formulaeditor.UserVariable
import org.catrobat.catroid.stage.ShowTextActor
import org.catrobat.catroid.stage.StageActivity
import org.catrobat.catroid.utils.ShowTextUtils.AndroidStringProvider

class ShowTextColorSizeAlignmentAction : TemporalAction() {
    private var xPosition: Formula? = null
    private var yPosition: Formula? = null
    private var relativeTextSize: Formula? = null
    private var color: Formula? = null
    private var variableToShow: UserVariable? = null
    private var scope: Scope? = null
    private var alignment = 0
    private var localActor: ShowTextActor? = null
    private var androidStringProvider: AndroidStringProvider? = null
    override fun begin() {
        try {
            val xPosition = xPosition?.interpretInteger(scope) ?: 0
            val yPosition = yPosition?.interpretInteger(scope) ?: 0
            val relativeTextSize = relativeTextSize?.interpretFloat(scope)?.div(TEXT_SCALE_FACTOR) ?: 0f
            val color = color?.interpretString(scope)
            if (StageActivity.stageListener != null) {
                val dummyActor = ShowTextActor(
                    UserVariable("dummyActor"), 0,
                    0, relativeTextSize, color, scope?.sprite, alignment, androidStringProvider
                )
                checkStageActors(dummyActor)
                localActor = ShowTextActor(
                    variableToShow, xPosition, yPosition, relativeTextSize,
                    color, scope?.sprite, alignment, androidStringProvider
                )
            }
            if (relativeTextSize <= 0f) {
                variableToShow?.visible = false
            } else {
                StageActivity.stageListener.addActor(actor)
                variableToShow?.visible = true
            }
        } catch (e: InterpretationException) {
            Log.d(TAG, "InterpretationException: $e")
        }
    }
    private fun checkStageActors(dummyActor: ShowTextActor) {
        val stageActors = StageActivity.stageListener.stage.actors
        for (actor in stageActors) {
            if (actor.javaClass == dummyActor.javaClass) {
                val showTextActor = actor as ShowTextActor
                checkForDelete(showTextActor)
            }
        }
    }
    private fun checkForDelete(showTextActor: ShowTextActor) {
        if (showTextActor.variableNameToCompare == variableToShow?.name &&
            showTextActor.sprite == scope?.sprite) {
            actor.remove()
        }
    }

    override fun update(percent: Float) {
        try {
            val xPosition = xPosition?.interpretInteger(scope)
            val yPosition = yPosition?.interpretInteger(scope)
            if (localActor != null) {
                xPosition?.let { localActor?.setPositionX(it) }
                yPosition?.let { localActor?.setPositionY(it) }
            }
        } catch (e: InterpretationException) {
            Log.d(TAG, "InterpretationException: $e")
        }
    }

    fun setPosition(xPosition: Formula, yPosition: Formula) {
        this.xPosition = xPosition
        this.yPosition = yPosition
    }

    fun setRelativeTextSize(relativeTextSize: Formula) {
        this.relativeTextSize = relativeTextSize
    }

    fun setColor(color: Formula) {
        this.color = color
    }

    fun setScope(scope: Scope) {
        this.scope = scope
    }

    fun setVariableToShow(userVariable: UserVariable) {
        variableToShow = userVariable
    }

    fun setAlignment(alignment: Int) {
        this.alignment = alignment
    }

    fun setAndroidStringProvider(androidStringProvider: AndroidStringProvider) {
        this.androidStringProvider = androidStringProvider
    }

    companion object {
        val TAG: String? = ShowTextColorSizeAlignmentAction::class.java.simpleName
        private const val TEXT_SCALE_FACTOR = 100
    }
}
