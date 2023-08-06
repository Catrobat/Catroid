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
package org.catrobat.catroid.test.visualplacement

import org.catrobat.catroid.camera.Position
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Script
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.StartScript
import org.catrobat.catroid.content.bricks.PlaceAtBrick
import org.catrobat.catroid.content.bricks.RepeatBrick
import org.catrobat.catroid.content.bricks.WaitBrick
import org.catrobat.catroid.content.bricks.WhenStartedBrick
import org.catrobat.catroid.test.UnitTestWithMockedContext
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@RunWith(MockitoJUnitRunner::class)
class VisualPlacementSpritePositionCalculationTest : UnitTestWithMockedContext() {

    lateinit var sprite: Sprite
    lateinit var startScript: Script

    @Before
    fun setUp() {
        whenever(projectManager.currentSprite).doReturn(mock<Sprite>())
        whenever(projectManager.currentProject).doReturn(mock<Project>())
        sprite = Sprite("testSprite")
        startScript = StartScript()
        startScript.scriptBrick = mock<WhenStartedBrick>()
    }

    @Test
    fun testNoScripts() {
        val pos = sprite.getInitialPosition()
        assert(pos == Position(0.0, 0.0))
    }

    @Test
    fun testEmptyScript() {
        sprite.addScript(startScript)
        val pos = sprite.getInitialPosition()
        assert(pos == Position(0.0, 0.0))
        sprite.removeAllScripts()
    }

    @Test
    fun testEmptyScripts() {
        sprite.addScript(startScript)
        sprite.addScript(startScript)
        sprite.addScript(startScript)
        sprite.addScript(startScript)
        val pos = sprite.getInitialPosition()
        assert(pos == Position(0.0, 0.0))
        sprite.removeAllScripts()
    }

    @Test
    fun testPositionOnlyScript() {
        val script = startScript
        script.addBrick(PlaceAtBrick(11, 12))
        sprite.addScript(script)
        val pos = sprite.getInitialPosition()
        assert(pos == Position(11.0, 12.0))
        sprite.removeAllScripts()
    }

    @Test
    fun testPositionOnlyScripts() {
        val script = startScript
        script.addBrick(PlaceAtBrick(11, 12))
        sprite.addScript(script)
        sprite.addScript(script)
        sprite.addScript(script)
        val pos = sprite.getInitialPosition()
        assert(pos == Position(11.0, 12.0))
        sprite.removeAllScripts()
    }

    @Test
    fun testPositionNondeterministicScripts() {
        val script = startScript
        script.addBrick(PlaceAtBrick(11, 1))
        sprite.addScript(script)
        val script2 = startScript
        script.addBrick(PlaceAtBrick(11, 2))
        sprite.addScript(script2)
        val script3 = startScript
        script.addBrick(PlaceAtBrick(11, 3))
        sprite.addScript(script3)
        val pos = sprite.getInitialPosition()
        assert(
            pos == Position(11.0, 1.0) || pos == Position(11.0, 2.0) || pos == Position(
                11.0,
                3.0
            )
        )
        sprite.removeAllScripts()
    }

    @Test
    fun testPositionDelayScript() {
        val script = startScript
        script.addBrick(WaitBrick(1))
        script.addBrick(PlaceAtBrick(11, 12))
        sprite.addScript(script)
        val pos = sprite.getInitialPosition()
        assert(pos == Position(0.0, 0.0))
        sprite.removeAllScripts()
    }

    @Test
    fun testPositionBeforeDelayScript() {
        val script = startScript
        script.addBrick(PlaceAtBrick(1, 2))
        script.addBrick(WaitBrick(1))
        script.addBrick(PlaceAtBrick(11, 12))
        sprite.addScript(script)
        val pos = sprite.getInitialPosition()
        assert(pos == Position(1.0, 2.0))
        sprite.removeAllScripts()
    }

    @Test
    fun testPositionLoopScript() {
        val script = startScript
        script.addBrick(RepeatBrick())
        script.addBrick(PlaceAtBrick(11, 12))
        sprite.addScript(script)
        val pos = sprite.getInitialPosition()
        assert(pos == Position(0.0, 0.0))
        sprite.removeAllScripts()
    }

    @Test
    fun testPositionBeforeLoopScript() {
        val script = startScript
        script.addBrick(PlaceAtBrick(1, 2))
        script.addBrick(RepeatBrick())
        script.addBrick(PlaceAtBrick(11, 12))
        sprite.addScript(script)
        val pos = sprite.getInitialPosition()
        assert(pos == Position(1.0, 2.0))
        sprite.removeAllScripts()
    }
}
