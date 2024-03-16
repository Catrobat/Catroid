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

package org.catrobat.catroid.io.catlang.parser.project

import org.catrobat.catroid.content.BroadcastScript
import org.catrobat.catroid.content.RaspiInterruptScript
import org.catrobat.catroid.content.Script
import org.catrobat.catroid.content.StartScript
import org.catrobat.catroid.content.WhenBackgroundChangesScript
import org.catrobat.catroid.content.WhenBounceOffScript
import org.catrobat.catroid.content.WhenClonedScript
import org.catrobat.catroid.content.WhenConditionScript
import org.catrobat.catroid.content.WhenGamepadButtonScript
import org.catrobat.catroid.content.WhenNfcScript
import org.catrobat.catroid.content.WhenScript
import org.catrobat.catroid.content.WhenTouchDownScript
import org.catrobat.catroid.io.catlang.parser.project.error.CatrobatLanguageParsingException

object ScriptFactory {
    fun createScriptFromCatrobatLanguage(brickName: String, arguments: List<String>): Script {
        return when (brickName) {
            "When background changes to" -> WhenBackgroundChangesScript()
            "When condition becomes true" -> WhenConditionScript()
            "When NFC gets scanned" -> WhenNfcScript()
            "When Raspberry Pi pin changes to" -> RaspiInterruptScript()
            "When scene starts" -> StartScript()
            "When stage is tapped" -> WhenTouchDownScript()
            "When you bounce off" -> WhenBounceOffScript()
            "When you receive" -> BroadcastScript()
            "When you start as a clone" -> WhenClonedScript()
            "When tapped" -> createWhenTappedScript(arguments)

            else -> throw IllegalArgumentException("Unknown script name: $brickName")
        }
    }

    private fun createWhenTappedScript(arguments: List<String>): Script {
        if (arguments.contains("gamepad button")) {
            return WhenGamepadButtonScript()
        }
        if (arguments.isNullOrEmpty()) {
            return WhenScript()
        }
        throw CatrobatLanguageParsingException("Either parameter 'gamepad button' or no parameter is allowed for 'When tapped' script.")
    }
}