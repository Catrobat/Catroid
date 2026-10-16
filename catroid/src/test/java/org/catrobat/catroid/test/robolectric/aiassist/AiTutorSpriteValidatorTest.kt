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

package org.catrobat.catroid.test.robolectric.aiassist

import android.os.Build
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Script
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.StartScript
import org.catrobat.catroid.content.bricks.Brick
import org.catrobat.catroid.content.bricks.SetXBrick
import org.catrobat.catroid.content.bricks.SetYBrick
import org.catrobat.catroid.ui.SpriteActivity
import org.catrobat.catroid.ui.aiassist.validation.AiTutorSpriteValidator
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class AiTutorSpriteValidatorTest {

    private lateinit var activity: SpriteActivity

    @Before
    fun setUp() {
        val controller = Robolectric.buildActivity(SpriteActivity::class.java)
        activity = controller.get()
        val project = Project(activity, javaClass.simpleName)
        val hostSprite = Sprite("hostSprite")
        project.defaultScene.addSprite(hostSprite)
        ProjectManager.getInstance().apply {
            currentProject = project
            currentSprite = hostSprite
            currentlyEditedScene = project.defaultScene
        }
        controller.create().resume()
    }

    @After
    fun tearDown() {
        ProjectManager.getInstance().resetProjectManager()
    }

    @Test
    fun completeSpriteIsValid() {
        val result = AiTutorSpriteValidator.validate(spriteWith(SetXBrick()), activity)

        assertTrue(result is AiTutorSpriteValidator.Result.Valid)
    }

    @Test
    fun spriteWithNoBricksIsValid() {
        val result = AiTutorSpriteValidator.validate(spriteWith(), activity)

        assertTrue(result is AiTutorSpriteValidator.Result.Valid)
    }

    @Test
    fun spriteWithMultipleCompleteScriptsIsValid() {
        val sprite = Sprite("testSprite")
        sprite.addScript(StartScript().apply { addBrick(SetXBrick()) })
        sprite.addScript(StartScript().apply { addBrick(SetYBrick()) })

        val result = AiTutorSpriteValidator.validate(sprite, activity)

        assertTrue(result is AiTutorSpriteValidator.Result.Valid)
    }

    @Test
    fun brickMissingRequiredFormulaIsInvalidWithSpecificReason() {
        val brick = SetXBrick()
        // Simulate AI output that parses but omits a required value: the field is still declared in
        // brickFieldToTextViewIdMap but has no entry in formulaMap, which crashes at render time.
        brick.formulaMap.remove(Brick.BrickField.X_POSITION)

        val result = AiTutorSpriteValidator.validate(spriteWith(brick), activity)

        assertTrue(result is AiTutorSpriteValidator.Result.Invalid)
        val reason = (result as AiTutorSpriteValidator.Result.Invalid).reason
        assertTrue(reason.contains("SetXBrick"))
        assertTrue(reason.contains("X_POSITION"))
    }

    private fun spriteWith(vararg bricks: Brick): Sprite {
        val sprite = Sprite("testSprite")
        val script: Script = StartScript()
        bricks.forEach { script.addBrick(it) }
        sprite.addScript(script)
        return sprite
    }
}
