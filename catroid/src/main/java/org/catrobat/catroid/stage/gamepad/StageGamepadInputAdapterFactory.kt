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

package org.catrobat.catroid.stage.gamepad

import org.catrobat.catroid.content.Project
import org.catrobat.catroid.stage.StageActivity

/** Creates the [AndroidGamepadInputAdapter] of a [StageActivity]. */
object StageGamepadInputAdapterFactory {

    /**
     * Creates an adapter that forwards controller input to the gamepad input of the running stage,
     * if there is one, and consumes only the buttons that [project] handles, see
     * [GamepadUsage.isHandledByStage]. For a project without gamepad elements it consumes only B,
     * which leaves the stage directly, so Android keeps handling all other controller input.
     */
    @JvmStatic
    fun create(project: Project?): AndroidGamepadInputAdapter {
        val usage = GamepadUsage.of(project)
        return AndroidGamepadInputAdapter(
            gamepadInput = { StageActivity.stageListener?.gamepadInput },
            isButtonHandled = usage::isHandledByStage
        )
    }
}
