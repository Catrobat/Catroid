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

package org.catrobat.catroid.content

import org.catrobat.catroid.content.bricks.ScriptBrick
import org.catrobat.catroid.content.bricks.WhenStageExitedBrick
import org.catrobat.catroid.content.eventids.EventId
import org.catrobat.catroid.content.eventids.StageExitedEventId

/**
 * Script that starts once when the stage is really exited, for example by the brick "Finish stage",
 * so that it can finish its work before the stage closes. See
 * [org.catrobat.catroid.stage.StageExitController] for the exits that start it.
 */
class WhenStageExitedScript : Script() {

    /** Returns the linked brick. A new brick links itself to this script when it is created. */
    override fun getScriptBrick(): ScriptBrick = scriptBrick ?: WhenStageExitedBrick(this)

    override fun createEventId(sprite: Sprite): EventId = StageExitedEventId

    companion object {
        private const val serialVersionUID = 1L
    }
}
