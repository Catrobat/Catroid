/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2022 The Catrobat Team
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

import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.koin.projectManagerModule
import org.catrobat.catroid.koin.stop
import org.catrobat.catroid.test.MockUtil
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.koin.core.module.Module
import org.koin.java.KoinJavaComponent.inject
import java.util.Collections

@RunWith(Parameterized::class)
class IfOnEdgeBounceActionTest(
    private val name: String,
    private val initialPosX: Float,
    private val initialPosY: Float,
    private val expectedPosX: Float,
    private val expectedPosY: Float,
    private val initialDirection: Int,
    private val expectedDirection: Float
) {

    private lateinit var sprite: Sprite

    private val projectManager: ProjectManager by inject(ProjectManager::class.java)
    private val dependencyModules: List<Module> = Collections.singletonList(projectManagerModule)

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0} {5}")
        fun parameters() = (-179..180).flatMap { initialDirection -> listOf(
            arrayOf("TOP_BOUNCE", 0f, TOP_BORDER_POSITION, 0f, BOUNCE_TOP_POSITION, initialDirection,
                    getExpectedDirection(initialDirection, (-90..89).toSet(), 180)),
            arrayOf("BOTTOM_BOUNCE", 0f, BOTTOM_BORDER_POSITION, 0f, BOUNCE_BOTTOM_POSITION, initialDirection,
                    getExpectedDirection(initialDirection, (-179..-91).union(90..180), 180)),
            arrayOf("LEFT_BOUNCE", LEFT_BORDER_POSITION, 0f, BOUNCE_LEFT_POSITION, 0f, initialDirection,
                    getExpectedDirection(initialDirection, (-179..-1).toSet(), 0)),
            arrayOf("RIGHT_BOUNCE", RIGHT_BORDER_POSITION, 0f, BOUNCE_RIGHT_POSITION, 0f, initialDirection,
                    getExpectedDirection(initialDirection, (0..179).toSet(), 0)),
            arrayOf("NO_BOUNCE", 0f, 0f, 0f, 0f, initialDirection, initialDirection.toFloat())
        ) }.plus(listOf(
            arrayOf("LEFT_TOP_BOUNCE", LEFT_BORDER_POSITION, TOP_BORDER_POSITION,
                    BOUNCE_LEFT_POSITION, BOUNCE_TOP_POSITION, -45, 135f),
            arrayOf("LEFT_TOP_NO_BOUNCE", LEFT_BORDER_POSITION, TOP_BORDER_POSITION,
                    BOUNCE_LEFT_POSITION, BOUNCE_TOP_POSITION, 135, 135f),
            arrayOf("RIGHT_TOP_BOUNCE", RIGHT_BORDER_POSITION, TOP_BORDER_POSITION,
                    BOUNCE_RIGHT_POSITION, BOUNCE_TOP_POSITION, 45, -135f),
            arrayOf("RIGHT_TOP_NO_BOUNCE", RIGHT_BORDER_POSITION, TOP_BORDER_POSITION,
                    BOUNCE_RIGHT_POSITION, BOUNCE_TOP_POSITION, -135, -135f),
            arrayOf("LEFT_BOTTOM_BOUNCE", LEFT_BORDER_POSITION, BOTTOM_BORDER_POSITION,
                    BOUNCE_LEFT_POSITION, BOUNCE_BOTTOM_POSITION, -135, 45f),
            arrayOf("LEFT_BOTTOM_NO_BOUNCE", LEFT_BORDER_POSITION, BOTTOM_BORDER_POSITION,
                    BOUNCE_LEFT_POSITION, BOUNCE_BOTTOM_POSITION, 45, 45f),
            arrayOf("RIGHT_BOTTOM_BOUNCE", RIGHT_BORDER_POSITION, BOTTOM_BORDER_POSITION,
                    BOUNCE_RIGHT_POSITION, BOUNCE_BOTTOM_POSITION, 135, -45f),
            arrayOf("RIGHT_BOTTOM_NO_BOUNCE", RIGHT_BORDER_POSITION, BOTTOM_BORDER_POSITION,
                    BOUNCE_RIGHT_POSITION, BOUNCE_BOTTOM_POSITION, -45, -45f)
        ))

        private const val WIDTH = 100f
        private const val HEIGHT = 100f
        private const val SCREEN_WIDTH = 480
        private const val SCREEN_HEIGHT = 800
        private const val TOP_BORDER_POSITION = SCREEN_HEIGHT / 2f
        private const val BOTTOM_BORDER_POSITION = -TOP_BORDER_POSITION
        private const val RIGHT_BORDER_POSITION = SCREEN_WIDTH / 2f
        private const val LEFT_BORDER_POSITION = -RIGHT_BORDER_POSITION
        private const val BOUNCE_TOP_POSITION = TOP_BORDER_POSITION - HEIGHT / 2f
        private const val BOUNCE_BOTTOM_POSITION = -BOUNCE_TOP_POSITION
        private const val BOUNCE_RIGHT_POSITION = RIGHT_BORDER_POSITION - WIDTH / 2f
        private const val BOUNCE_LEFT_POSITION = -BOUNCE_RIGHT_POSITION

        private fun getExpectedDirection(initialDirection: Int, bounceRange: Set<Int>, axis: Int): Float {
            return when {
                initialDirection !in bounceRange -> initialDirection
                initialDirection >= 0 -> axis - initialDirection
                else -> -axis - initialDirection
            }.toFloat()
        }
    }

    @Before
    fun setUp() {
        sprite = Sprite("Test")
        sprite.look.apply {
            width = WIDTH
            height = HEIGHT
            setPositionInUserInterfaceDimensionUnit(initialPosX, initialPosY)
            motionDirectionInUserInterfaceDimensionUnit = initialDirection.toFloat()
        }
        val context = MockUtil.mockContextForProject(dependencyModules)
        Project(context, "Test", false).apply {
            xmlHeader.virtualScreenWidth = SCREEN_WIDTH
            xmlHeader.virtualScreenHeight = SCREEN_HEIGHT
            projectManager.currentProject = this
        }
    }

    @After
    fun tearDown() {
        stop(dependencyModules)
    }

    @Test
    fun testBounceAction() {
        Assert.assertTrue(sprite.actionFactory.createIfOnEdgeBounceAction(sprite).act(1.0f))
        Assert.assertEquals(expectedPosX, sprite.look.xInUserInterfaceDimensionUnit)
        Assert.assertEquals(expectedPosY, sprite.look.yInUserInterfaceDimensionUnit)
        Assert.assertEquals(expectedDirection, sprite.look.motionDirectionInUserInterfaceDimensionUnit)
    }
}
