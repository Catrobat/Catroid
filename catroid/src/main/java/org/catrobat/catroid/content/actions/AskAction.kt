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
import com.badlogic.gdx.scenes.scene2d.Action
import org.catrobat.catroid.content.Scope
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.formulaeditor.InterpretationException
import org.catrobat.catroid.formulaeditor.UserVariable
import org.catrobat.catroid.stage.BrickDialogManager
import org.catrobat.catroid.stage.StageActivity

class AskAction : Action() {
    var scope: Scope? = null
    var questionFormula: Formula? = null
    var answerVariable: UserVariable? = null
    var questionAsked = false
    private var answerReceived = false
    private fun askQuestion() {
        StageActivity.messageHandler ?: return
        var question = ""
        try {
            question = questionFormula?.interpretString(scope) ?: ""
        } catch (e: InterpretationException) {
            Log.e(
                javaClass.simpleName,
                "formula interpretation in ask brick failed"
            )
        }

        val params = arrayListOf(BrickDialogManager.DialogType.ASK_DIALOG, this, question)
        StageActivity.messageHandler.obtainMessage(StageActivity.SHOW_DIALOG, params).sendToTarget()
        questionAsked = true
    }

    fun setAnswerText(answer: String) {
        answerVariable ?: return
        answerVariable!!.value = answer
        answerReceived = true
    }

    override fun act(delta: Float): Boolean {
        if (!questionAsked) {
            askQuestion()
        }
        return answerReceived
    }

    override fun restart() {
        questionAsked = false
        answerReceived = false
        super.restart()
    }
}
