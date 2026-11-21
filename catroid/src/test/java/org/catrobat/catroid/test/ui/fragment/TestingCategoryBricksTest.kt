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

package org.catrobat.catroid.test.ui.fragment

import android.content.Context
import android.os.Build
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.R
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.bricks.AssertEqualsBrick
import org.catrobat.catroid.content.bricks.AssertUserListsBrick
import org.catrobat.catroid.content.bricks.Brick
import org.catrobat.catroid.content.bricks.FinishStageBrick
import org.catrobat.catroid.content.bricks.ParameterizedBrick
import org.catrobat.catroid.content.bricks.SimulateGamepadButtonBrick
import org.catrobat.catroid.content.bricks.StoreCSVIntoUserListBrick
import org.catrobat.catroid.content.bricks.TapAtBrick
import org.catrobat.catroid.content.bricks.TapForBrick
import org.catrobat.catroid.content.bricks.TouchAndSlideBrick
import org.catrobat.catroid.content.bricks.WaitTillIdleBrick
import org.catrobat.catroid.content.bricks.WebRequestBrick
import org.catrobat.catroid.content.bricks.WhenStageExitedBrick
import org.catrobat.catroid.ui.fragment.CategoryBricksFactory
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class TestingCategoryBricksTest {
    private val context: Context = RuntimeEnvironment.getApplication()

    @Before
    fun setUp() {
        val project = Project(context, javaClass.simpleName)
        val sprite = Sprite("testSprite")
        project.defaultScene.addSprite(sprite)
        ProjectManager.getInstance().currentProject = project
        ProjectManager.getInstance().currentSprite = sprite
        ProjectManager.getInstance().currentlyEditedScene = project.defaultScene
    }

    @After
    fun tearDown() {
        ProjectManager.getInstance().resetProjectManager()
    }

    @Test
    fun `testing category offers simulate gamepad button and when stage is exited`() {
        val testingBricks: List<Class<out Brick>> = CategoryBricksFactory()
            .getBricks(context.getString(R.string.category_assertions), false, context)
            .map { it.javaClass }

        assertEquals(expectedTestingBrickClasses, testingBricks)
    }

    @Test
    fun `when stage is exited brick belongs to the testing category`() {
        val category = CategoryBricksFactoryWithoutMotionBricks()
            .getBrickCategory(WhenStageExitedBrick(), false, context)

        assertEquals(context.getString(R.string.category_assertions), category)
    }

    /**
     * Looks up brick categories without creating the Motion bricks, whose physics defaults load the
     * native libGDX library that JVM unit tests do not have.
     */
    private class CategoryBricksFactoryWithoutMotionBricks : CategoryBricksFactory() {
        override fun setupMotionCategoryList(context: Context?, isBackgroundSprite: Boolean): List<Brick> =
            emptyList()
    }

    private companion object {
        val expectedTestingBrickClasses: List<Class<out Brick>> = listOf(
            AssertEqualsBrick::class.java,
            AssertUserListsBrick::class.java,
            ParameterizedBrick::class.java,
            WaitTillIdleBrick::class.java,
            TapAtBrick::class.java,
            TapForBrick::class.java,
            TouchAndSlideBrick::class.java,
            SimulateGamepadButtonBrick::class.java,
            FinishStageBrick::class.java,
            WhenStageExitedBrick::class.java,
            StoreCSVIntoUserListBrick::class.java,
            WebRequestBrick::class.java
        )
    }
}
