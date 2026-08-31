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
import org.catrobat.catroid.ui.aiassist.diff.DiffStatus
import org.catrobat.catroid.ui.aiassist.diff.buildDiffRows
import org.catrobat.catroid.ui.aiassist.diff.isScriptHeaderRow
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class SpriteDifferTest {

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
    fun identicalSpritesAreAllUnchanged() {
        val statuses = diffStatuses(
            spriteWith(SetXBrick(), SetYBrick()),
            spriteWith(SetXBrick(), SetYBrick())
        )

        assertEquals(listOf(DiffStatus.UNCHANGED, DiffStatus.UNCHANGED), statuses)
    }

    @Test
    fun appendedBrickIsAdded() {
        val statuses = diffStatuses(
            spriteWith(SetXBrick()),
            spriteWith(SetXBrick(), SetYBrick())
        )

        assertEquals(listOf(DiffStatus.UNCHANGED, DiffStatus.ADDED), statuses)
    }

    @Test
    fun deletedBrickIsRemoved() {
        val statuses = diffStatuses(
            spriteWith(SetXBrick(), SetYBrick()),
            spriteWith(SetXBrick())
        )

        assertEquals(listOf(DiffStatus.UNCHANGED, DiffStatus.REMOVED), statuses)
    }

    @Test
    fun sameBrickWithChangedValueIsModified() {
        val statuses = diffStatuses(
            spriteWith(SetXBrick(0)),
            spriteWith(SetXBrick(100))
        )

        assertEquals(listOf(DiffStatus.MODIFIED), statuses)
    }

    @Test
    fun differentBrickClassesAreNotMergedIntoModified() {
        val statuses = diffStatuses(
            spriteWith(SetXBrick()),
            spriteWith(SetYBrick())
        )

        assertEquals(listOf(DiffStatus.REMOVED, DiffStatus.ADDED), statuses)
    }

    private fun diffStatuses(old: Sprite, new: Sprite): List<DiffStatus> =
        buildDiffRows(old, new, activity)
            .filterNot { isScriptHeaderRow(it) }
            .map { it.status }

    private fun spriteWith(vararg bricks: Brick): Sprite {
        val sprite = Sprite("testSprite")
        val script: Script = StartScript()
        bricks.forEach { script.addBrick(it) }
        sprite.addScript(script)
        return sprite
    }
}
