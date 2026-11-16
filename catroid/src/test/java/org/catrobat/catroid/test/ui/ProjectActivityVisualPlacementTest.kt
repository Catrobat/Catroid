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

package org.catrobat.catroid.test.ui

import org.catrobat.catroid.content.Look.DEGREE_UI_OFFSET
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.StartScript
import org.catrobat.catroid.content.bricks.PlaceAtBrick
import org.catrobat.catroid.content.bricks.PointInDirectionBrick
import org.catrobat.catroid.content.bricks.SetSizeToBrick
import org.catrobat.catroid.ui.ProjectActivity
import org.catrobat.catroid.ui.ProjectActivity.Companion.DEFAULT_SCALE
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class ProjectActivityVisualPlacementTest {

    private lateinit var sprite: Sprite

    @Before
    fun setUp() {
        sprite = Sprite("TestSprite")
    }

    @Test
    fun testDefaultRotationSuppressesPointInDirectionBrick() {
        ProjectActivity.applyVisualPlacementBricks(
            sprite,
            xCoordinate = 100,
            yCoordinate = 200,
            placementScale = DEFAULT_SCALE,
            placementRotation = DEGREE_UI_OFFSET
        )

        val script = sprite.getScript(0)
        val bricks = script.brickList

        assertEquals(1, bricks.size)
        assertTrue(bricks[0] is PlaceAtBrick)
        assertFalse(
            "PointInDirectionBrick must not be added for default rotation",
            bricks.any { it is PointInDirectionBrick }
        )
    }

    @Test
    fun testDefaultScaleSuppressesSetSizeToBrick() {
        ProjectActivity.applyVisualPlacementBricks(
            sprite,
            xCoordinate = 0,
            yCoordinate = 0,
            placementScale = DEFAULT_SCALE,
            placementRotation = DEGREE_UI_OFFSET
        )

        val script = sprite.getScript(0)
        val bricks = script.brickList

        assertEquals(1, bricks.size)
        assertFalse(
            "SetSizeToBrick must not be added for default scale",
            bricks.any { it is SetSizeToBrick }
        )
    }

    @Test
    fun testNonDefaultRotationAddsPointInDirectionBrick() {
        val customRotation = 45.0f

        ProjectActivity.applyVisualPlacementBricks(
            sprite,
            xCoordinate = 50,
            yCoordinate = -30,
            placementScale = DEFAULT_SCALE,
            placementRotation = customRotation
        )

        val script = sprite.getScript(0)
        val bricks = script.brickList

        assertEquals(2, bricks.size)
        assertTrue(bricks[0] is PlaceAtBrick)
        assertTrue(bricks[1] is PointInDirectionBrick)
    }

    @Test
    fun testNonDefaultScaleAddsSetSizeToBrick() {
        val customScale = 2.5f

        ProjectActivity.applyVisualPlacementBricks(
            sprite,
            xCoordinate = 0,
            yCoordinate = 0,
            placementScale = customScale,
            placementRotation = DEGREE_UI_OFFSET
        )

        val script = sprite.getScript(0)
        val bricks = script.brickList

        assertEquals(2, bricks.size)
        assertTrue(bricks[0] is PlaceAtBrick)
        assertTrue(bricks[1] is SetSizeToBrick)
    }

    @Test
    fun testNonDefaultScaleAndRotationAddsBothBricks() {
        val customScale = 1.5f
        val customRotation = 135.0f

        ProjectActivity.applyVisualPlacementBricks(
            sprite,
            xCoordinate = 10,
            yCoordinate = 20,
            placementScale = customScale,
            placementRotation = customRotation
        )

        val script = sprite.getScript(0)
        val bricks = script.brickList

        assertEquals(3, bricks.size)
        assertTrue(bricks[0] is PlaceAtBrick)
        assertTrue(bricks[1] is SetSizeToBrick)
        assertTrue(bricks[2] is PointInDirectionBrick)
    }

    @Test
    fun testScriptIsPrependedToExistingScripts() {
        sprite.addScript(StartScript())

        ProjectActivity.applyVisualPlacementBricks(
            sprite,
            xCoordinate = 5,
            yCoordinate = 10,
            placementScale = DEFAULT_SCALE,
            placementRotation = DEGREE_UI_OFFSET
        )

        assertEquals(2, sprite.numberOfScripts)
        val firstScript = sprite.getScript(0)
        assertTrue(firstScript.brickList[0] is PlaceAtBrick)
    }

    @Test
    fun testZeroRotationAddsPointInDirectionBrick() {
        ProjectActivity.applyVisualPlacementBricks(
            sprite,
            xCoordinate = 0,
            yCoordinate = 0,
            placementScale = DEFAULT_SCALE,
            placementRotation = 0.0f
        )

        val script = sprite.getScript(0)
        assertTrue(
            "Rotation of 0 differs from DEGREE_UI_OFFSET and must add a brick",
            script.brickList.any { it is PointInDirectionBrick }
        )
    }
}
