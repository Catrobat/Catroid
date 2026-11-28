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

import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction
import org.catrobat.catroid.stage.StageActivity
import org.catrobat.catroid.stage.StageExitController
import org.catrobat.catroid.stage.TestResult

/**
 * Finishes the stage. A [silent] finish is the brick "Finish stage", a real exit of the stage that
 * first lets the "When stage is exited" scripts run, see [StageExitController]. Otherwise it is the
 * Testing brick "Finish tests", which reports success at once.
 */
class FinishStageAction : TemporalAction() {
    var silent: Boolean = false

    override fun update(percent: Float) {
        if (silent) {
            finishStageSilently()
        } else {
            StageActivity.finishTestWithResult(
                TestResult(SUCCESS_MESSAGE, TestResult.STAGE_ACTIVITY_TEST_SUCCESS)
            )
        }
    }

    private fun finishStageSilently() {
        val exitController = StageExitController.ofRunningStage()
        if (exitController != null) {
            exitController.requestFinishStage()
        } else {
            StageActivity.finishStage()
        }
    }

    companion object {
        private const val SUCCESS_MESSAGE = "Success"
    }
}
