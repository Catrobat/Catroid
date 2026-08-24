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
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction
import com.badlogic.gdx.utils.Array as GdxArray
import org.catrobat.catroid.content.Scope
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.formulaeditor.InterpretationException
import org.catrobat.catroid.formulaeditor.UserVariable
import org.catrobat.catroid.stage.ShowTextActor
import org.catrobat.catroid.stage.StageActivity
import org.catrobat.catroid.utils.ShowTextUtils.AndroidStringProvider

class ShowTextColorSizeAlignmentAction : TemporalAction() {

    companion object {
        val TAG: String = ShowTextColorSizeAlignmentAction::class.java.simpleName
        private const val PERCENTAGE_FACTOR = 100f
    }

    private var xPosition: Formula? = null
    private var yPosition: Formula? = null
    private var relativeTextSize: Formula? = null
    private var color: Formula? = null
    private var variableToShow: UserVariable? = null
    private var scope: Scope? = null
    private var alignment: Int = 0
    private var actor: ShowTextActor? = null
    private var androidStringProvider: AndroidStringProvider? = null

    private fun removeMatchingActors(
        stageActors: GdxArray<Actor>,
        relativeTextSizeVal: Float,
        colorStr: String,
        sprite: Sprite,
        variableToShowName: String
    ) {
        val dummyActor = ShowTextActor(
            UserVariable("dummyActor"), 0, 0,
            relativeTextSizeVal, colorStr, sprite, alignment, androidStringProvider
        )
        for (stageActor in stageActors) {
            val showTextActor = stageActor as? ShowTextActor ?: continue
            if (showTextActor.javaClass == dummyActor.javaClass &&
                showTextActor.variableNameToCompare == variableToShowName &&
                showTextActor.sprite == sprite
            ) {
                stageActor.remove()
            }
        }
    }

    override fun begin() {
        try {
            val scopeLocal = scope ?: return
            val variableToShowLocal = variableToShow ?: return
            val xPos = xPosition?.interpretInteger(scopeLocal) ?: 0
            val yPos = yPosition?.interpretInteger(scopeLocal) ?: 0
            val relativeTextSizeVal = (relativeTextSize?.interpretFloat(scopeLocal) ?: 0f) / PERCENTAGE_FACTOR
            val colorStr = color?.interpretString(scopeLocal) ?: ""

            val listener = StageActivity.stageListener
            if (listener != null) {
                removeMatchingActors(
                    listener.stage.actors,
                    relativeTextSizeVal,
                    colorStr,
                    scopeLocal.sprite,
                    variableToShowLocal.name
                )
                actor = ShowTextActor(
                    variableToShowLocal, xPos, yPos, relativeTextSizeVal,
                    colorStr, scopeLocal.sprite, alignment, androidStringProvider
                )
            }
            if (relativeTextSizeVal <= 0f) {
                variableToShowLocal.visible = false
            } else {
                StageActivity.stageListener?.addActor(actor)
                variableToShowLocal.visible = true
            }
        } catch (e: InterpretationException) {
            Log.d(TAG, "InterpretationException", e)
        }
    }

    override fun update(percent: Float) {
        try {
            val scopeLocal = scope ?: return
            val xPos = xPosition?.interpretInteger(scopeLocal) ?: 0
            val yPos = yPosition?.interpretInteger(scopeLocal) ?: 0

            actor?.apply {
                setPositionX(xPos)
                setPositionY(yPos)
            }
        } catch (e: InterpretationException) {
            Log.d(TAG, "InterpretationException", e)
        }
    }

    fun setPosition(xPosition: Formula?, yPosition: Formula?) {
        this.xPosition = xPosition
        this.yPosition = yPosition
    }

    fun setRelativeTextSize(relativeTextSize: Formula?) {
        this.relativeTextSize = relativeTextSize
    }

    fun setColor(color: Formula?) {
        this.color = color
    }

    fun setScope(scope: Scope?) {
        this.scope = scope
    }

    fun setVariableToShow(userVariable: UserVariable?) {
        this.variableToShow = userVariable
    }

    fun setAlignment(alignment: Int) {
        this.alignment = alignment
    }

    fun setAndroidStringProvider(androidStringProvider: AndroidStringProvider?) {
        this.androidStringProvider = androidStringProvider
    }
}
