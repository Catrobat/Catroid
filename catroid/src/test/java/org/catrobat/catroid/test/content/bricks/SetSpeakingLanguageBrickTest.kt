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

package org.catrobat.catroid.test.content.bricks

import org.catrobat.catroid.R
import org.catrobat.catroid.content.ActionFactory
import org.catrobat.catroid.content.Script
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.actions.ScriptSequenceAction
import org.catrobat.catroid.content.bricks.Brick
import org.catrobat.catroid.content.bricks.SetSpeakingLanguageBrick
import org.catrobat.catroid.content.bricks.brickspinner.LanguageObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

class SetSpeakingLanguageBrickTest {

    @Test
    fun testViewResource() {
        val brick = SetSpeakingLanguageBrick()
        assertEquals(R.layout.brick_set_speaking_language, brick.viewResource)
    }

    @Test
    fun testRequiredResources() {
        val brick = SetSpeakingLanguageBrick()
        val requiredResources = Brick.ResourcesSet()
        brick.addRequiredResources(requiredResources)

        assertTrue(requiredResources.contains(Brick.TEXT_TO_SPEECH))
    }

    @Test
    fun testAddActionToSequenceWithSelectedLanguage() {
        val brick = SetSpeakingLanguageBrick()
        val languageObject = LanguageObject("German (Germany)", "de-DE")
        brick.onItemSelected(R.id.brick_set_speaking_language_spinner, languageObject)

        val sprite = Sprite()
        val actionFactory = mock(ActionFactory::class.java)
        sprite.actionFactory = actionFactory

        val sequence = ScriptSequenceAction(mock(Script::class.java))
        brick.addActionToSequence(sprite, sequence)

        verify(actionFactory).createSetSpeakingLanguageAction("de-DE")
    }

    @Test
    fun testAddActionToSequenceWithNullLanguage() {
        val brick = SetSpeakingLanguageBrick()

        val sprite = Sprite()
        val actionFactory = mock(ActionFactory::class.java)
        sprite.actionFactory = actionFactory

        val sequence = ScriptSequenceAction(mock(Script::class.java))
        brick.addActionToSequence(sprite, sequence)

        verify(actionFactory).createSetSpeakingLanguageAction(null)
    }
}
