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
import org.catrobat.catroid.stage.StageActivity

const val MAX_TIME_TO_VIBRATE: Long = 10_000_000_000
const val UNIT_CONVERSION: Long = 1000

class VibrateAction : TemporalAction() {
    var scope: Scope? = null
    var duration: Formula? = null

    override fun begin() {
        try {
            var newDuration = duration?.interpretFloat(scope) ?: return
            if (newDuration.toLong() > MAX_TIME_TO_VIBRATE) {
                newDuration = MAX_TIME_TO_VIBRATE.toFloat()
            }
            super.setDuration(newDuration)
            val vibrationManager = StageActivity.activeStageActivity.get()?.vibrationManager
            vibrationManager?.vibrateFor((newDuration * UNIT_CONVERSION).toLong())
        } catch (interpretationException: InterpretationException) {
            Log.d(
                javaClass.simpleName,
                "Formula interpretation for this specific Brick failed.",
                interpretationException
            )
        }
    }

    override fun update(percent: Float) = Unit
}
