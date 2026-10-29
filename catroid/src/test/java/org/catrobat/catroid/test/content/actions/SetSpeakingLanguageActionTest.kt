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

package org.catrobat.catroid.test.content.actions

import org.catrobat.catroid.content.actions.SetSpeakingLanguageAction
import org.catrobat.catroid.formulaeditor.SensorHandler
import org.catrobat.catroid.formulaeditor.Sensors
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SetSpeakingLanguageActionTest {

    @Test
    fun testSetSpeakingLanguageActionUpdatesSensor() {
        val action = SetSpeakingLanguageAction().apply {
            speakingLanguageTag = "de-DE"
        }

        val finished = action.act(0.1f)

        assertTrue(finished)
        assertEquals("de-DE", SensorHandler.getSpeakingLanguageSensor())
        assertEquals("de-DE", SensorHandler.getSensorValue(Sensors.SPEAKING_LANGUAGE))
    }

    @Test
    fun testSetSpeakingLanguageActionIdempotent() {
        val action = SetSpeakingLanguageAction().apply {
            speakingLanguageTag = "fr-FR"
        }

        assertTrue(action.act(0.1f))
        assertEquals("fr-FR", SensorHandler.getSpeakingLanguageSensor())

        // Second act call returns true without errors
        assertTrue(action.act(0.1f))
        assertEquals("fr-FR", SensorHandler.getSpeakingLanguageSensor())
    }
}
