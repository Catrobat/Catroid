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
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction
import com.badlogic.gdx.utils.Array
import org.catrobat.catroid.content.Scope
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.formulaeditor.InterpretationException
import org.catrobat.catroid.formulaeditor.UserVariable
import org.catrobat.catroid.stage.ShowTextActor
import org.catrobat.catroid.stage.StageActivity
import org.catrobat.catroid.utils.ShowTextUtils.AndroidStringProvider

class ShowTextAction : TemporalAction() {
    private var xPosition: Formula? = null
    private var yPosition: Formula? = null
    private var variableToShow: UserVariable? = null
    private var scope: Scope? = null
    private var androidStringProvider: AndroidStringProvider? = null
    private var showTextActor: ShowTextActor? = null
    override fun begin() {
        try {
            val xPosition = xPosition?.interpretInteger(scope)
            val yPosition = yPosition?.interpretInteger(scope)
            if (StageActivity.stageListener != null) {
                val stageActors = StageActivity.stageListener.stage.actors
                iterateActors(stageActors)
                showTextActor = ShowTextActor(
                    variableToShow, xPosition ?: 0, yPosition ?: 0, 1.0f, null,
                    scope?.sprite, androidStringProvider
                )
                StageActivity.stageListener.addActor(showTextActor)
            }
            variableToShow?.visible = true
        } catch (e: InterpretationException) {
            Log.d(TAG, "InterpretationException: $e")
        }
    }

    private fun iterateActors(stageActors: Array<Actor>) {
        for (actor in stageActors) {
            val showTextActor: ShowTextActor? = actor as? ShowTextActor
            if (showTextActor != null && showTextActor.variableNameToCompare == variableToShow?.name &&
                showTextActor.sprite == scope?.sprite) {
                    actor.remove()
            }
        }
    }

    override fun update(percent: Float) {
        try {
            val xPosition = xPosition?.interpretInteger(scope)
            val yPosition = yPosition?.interpretInteger(scope)
            if (showTextActor != null) {
                showTextActor?.setPositionX(xPosition ?: 0)
                showTextActor?.setPositionY(yPosition ?: 0)
            }
        } catch (e: InterpretationException) {
            Log.d(TAG, "InterpretationException $e")
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

    fun setAndroidStringProvider(androidStringProvider: AndroidStringProvider?) {
        this.androidStringProvider = androidStringProvider
    }

    companion object {
        val TAG: String? = ShowTextAction::class.java.simpleName
    }
}
