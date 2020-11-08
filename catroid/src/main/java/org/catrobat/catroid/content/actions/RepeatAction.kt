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
import com.badlogic.gdx.scenes.scene2d.actions.RepeatAction
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.formulaeditor.InterpretationException

class RepeatAction : RepeatAction() {
    private var executedCount = 0
    private var isCurrentLoopInitialized = false
    private var isRepeatActionInitialized = false
    private var repeatCountValue = 0
    private var currentTime = 0f
    lateinit var sprite: Sprite
    var isForeverRepeat = false
    var repeatCount: Formula? = null

    public override fun delegate(delta: Float): Boolean {
        if (!isRepeatActionInitialized) {
            isRepeatActionInitialized = true
            interpretRepeatCount()
        }
        if (!isCurrentLoopInitialized) {
            currentTime = 0f
            isCurrentLoopInitialized = true
        }
        currentTime += delta
        if (repeatCountValue < 0) {
            repeatCountValue = 0
        }
        if (executedCount >= repeatCountValue && !isForeverRepeat) {
            return true
        }
        if (action != null && action.act(delta) && currentTime >= LOOP_DELAY) {
            executedCount++
            if (executedCount >= repeatCountValue && !isForeverRepeat) {
                return true
            }
            isCurrentLoopInitialized = false
            action?.restart()
        }
        return false
    }

    override fun restart() {
        isCurrentLoopInitialized = false
        isRepeatActionInitialized = false
        executedCount = 0
        super.restart()
    }

    private fun interpretRepeatCount() {
        repeatCountValue = try {
            repeatCount?.interpretInteger(sprite) ?: 0
        } catch (interpretationException: InterpretationException) {
            Log.d(
                javaClass.simpleName,
                "Formula interpretation for this specific Brick failed.",
                interpretationException
            )
            0
        }
    }

    companion object {
        private const val LOOP_DELAY = 0.02f
    }
}
