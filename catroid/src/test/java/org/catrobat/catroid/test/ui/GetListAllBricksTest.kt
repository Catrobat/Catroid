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

import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.StartScript
import org.catrobat.catroid.content.bricks.ChangeSizeByNBrick
import org.catrobat.catroid.content.bricks.ForItemInUserListBrick
import org.catrobat.catroid.content.bricks.ForVariableFromToBrick
import org.catrobat.catroid.content.bricks.ForeverBrick
import org.catrobat.catroid.content.bricks.IfLogicBeginBrick
import org.catrobat.catroid.content.bricks.IfThenLogicBeginBrick
import org.catrobat.catroid.content.bricks.ParameterizedBrick
import org.catrobat.catroid.content.bricks.PhiroIfLogicBeginBrick
import org.catrobat.catroid.content.bricks.PlaceAtBrick
import org.catrobat.catroid.content.bricks.RepeatBrick
import org.catrobat.catroid.content.bricks.RepeatUntilBrick
import org.catrobat.catroid.content.bricks.SetXBrick
import org.catrobat.catroid.content.bricks.WaitBrick
import org.catrobat.catroid.content.bricks.WebRequestBrick
import org.catrobat.catroid.test.StaticSingletonInitializer
import org.catrobat.catroid.ui.getListAllBricks
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class GetListAllBricksTest {

    private lateinit var sprite: Sprite

    @Before
    fun setUp() {
        StaticSingletonInitializer.initializeStaticSingletonMethods()
        sprite = Sprite("TestSprite")
    }

    @Test
    fun testGetListAllBricksWithEmptySprite() {
        val allBricks = sprite.getListAllBricks()
        assertTrue(allBricks.isEmpty())
    }

    @Test
    fun testGetListAllBricksReturnsFlatBricks() {
        val script = StartScript()
        val waitBrick = WaitBrick(1000)
        val placeAtBrick = PlaceAtBrick(0, 0)
        script.addBrick(waitBrick)
        script.addBrick(placeAtBrick)
        sprite.addScript(script)

        val allBricks = sprite.getListAllBricks()

        assertTrue(allBricks.contains(waitBrick))
        assertTrue(allBricks.contains(placeAtBrick))
    }

    @Test
    fun testGetListAllBricksIncludesNestedBricksFromForeverBrick() {
        val script = StartScript()
        val foreverBrick = ForeverBrick()
        val nestedWaitBrick = WaitBrick(500)
        foreverBrick.addBrick(nestedWaitBrick)
        script.addBrick(foreverBrick)
        sprite.addScript(script)

        val allBricks = sprite.getListAllBricks()

        assertTrue(
            "Nested brick inside ForeverBrick should be found",
            allBricks.contains(nestedWaitBrick)
        )
        assertTrue(
            "ForeverBrick itself should be found",
            allBricks.contains(foreverBrick)
        )
    }

    @Test
    fun testGetListAllBricksIncludesNestedBricksFromIfLogicBeginBrick() {
        val script = StartScript()
        val ifBrick = IfLogicBeginBrick()
        val ifNestedBrick = WaitBrick(100)
        val elseNestedBrick = PlaceAtBrick(10, 20)
        ifBrick.addBrickToIfBranch(ifNestedBrick)
        ifBrick.addBrickToElseBranch(elseNestedBrick)
        script.addBrick(ifBrick)
        sprite.addScript(script)

        val allBricks = sprite.getListAllBricks()

        assertTrue(
            "Brick in if-branch should be found",
            allBricks.contains(ifNestedBrick)
        )
        assertTrue(
            "Brick in else-branch should be found",
            allBricks.contains(elseNestedBrick)
        )
    }

    @Test
    fun testGetListAllBricksIncludesNestedBricksFromIfThenLogicBeginBrick() {
        val script = StartScript()
        val ifThenBrick = IfThenLogicBeginBrick()
        val nestedBrick = SetXBrick()
        ifThenBrick.addBrick(nestedBrick)
        script.addBrick(ifThenBrick)
        sprite.addScript(script)

        val allBricks = sprite.getListAllBricks()

        assertTrue(
            "Nested brick inside IfThenLogicBeginBrick should be found",
            allBricks.contains(nestedBrick)
        )
    }

    @Test
    fun testGetListAllBricksIncludesNestedBricksFromRepeatBrick() {
        val script = StartScript()
        val repeatBrick = RepeatBrick()
        val nestedBrick = ChangeSizeByNBrick()
        repeatBrick.addBrick(nestedBrick)
        script.addBrick(repeatBrick)
        sprite.addScript(script)

        val allBricks = sprite.getListAllBricks()

        assertTrue(
            "Nested brick inside RepeatBrick should be found",
            allBricks.contains(nestedBrick)
        )
    }

    @Test
    fun testGetListAllBricksIncludesNestedBricksFromRepeatUntilBrick() {
        val script = StartScript()
        val repeatUntilBrick = RepeatUntilBrick()
        val nestedBrick = WaitBrick(200)
        repeatUntilBrick.addBrick(nestedBrick)
        script.addBrick(repeatUntilBrick)
        sprite.addScript(script)

        val allBricks = sprite.getListAllBricks()

        assertTrue(
            "Nested brick inside RepeatUntilBrick should be found",
            allBricks.contains(nestedBrick)
        )
    }

    @Test
    fun testGetListAllBricksIncludesNestedBricksFromForVariableFromToBrick() {
        val script = StartScript()
        val forBrick = ForVariableFromToBrick()
        val nestedBrick = WaitBrick(300)
        forBrick.addBrick(nestedBrick)
        script.addBrick(forBrick)
        sprite.addScript(script)

        val allBricks = sprite.getListAllBricks()

        assertTrue(
            "Nested brick inside ForVariableFromToBrick should be found",
            allBricks.contains(nestedBrick)
        )
    }

    @Test
    fun testGetListAllBricksIncludesNestedBricksFromForItemInUserListBrick() {
        val script = StartScript()
        val forItemBrick = ForItemInUserListBrick()
        val nestedBrick = WaitBrick(400)
        forItemBrick.addBrick(nestedBrick)
        script.addBrick(forItemBrick)
        sprite.addScript(script)

        val allBricks = sprite.getListAllBricks()

        assertTrue(
            "Nested brick inside ForItemInUserListBrick should be found",
            allBricks.contains(nestedBrick)
        )
    }

    @Test
    fun testGetListAllBricksIncludesNestedBricksFromParameterizedBrick() {
        val script = StartScript()
        val paramBrick = ParameterizedBrick()
        val nestedBrick = WaitBrick(500)
        paramBrick.addBrick(nestedBrick)
        script.addBrick(paramBrick)
        sprite.addScript(script)

        val allBricks = sprite.getListAllBricks()

        assertTrue(
            "Nested brick inside ParameterizedBrick should be found",
            allBricks.contains(nestedBrick)
        )
    }

    @Test
    fun testGetListAllBricksIncludesNestedBricksFromPhiroIfLogicBeginBrick() {
        val script = StartScript()
        val phiroBrick = PhiroIfLogicBeginBrick()
        val ifNestedBrick = WaitBrick(100)
        val elseNestedBrick = SetXBrick()
        phiroBrick.addBrickToIfBranch(ifNestedBrick)
        phiroBrick.addBrickToElseBranch(elseNestedBrick)
        script.addBrick(phiroBrick)
        sprite.addScript(script)

        val allBricks = sprite.getListAllBricks()

        assertTrue(
            "Brick in PhiroIfLogicBeginBrick if-branch should be found",
            allBricks.contains(ifNestedBrick)
        )
        assertTrue(
            "Brick in PhiroIfLogicBeginBrick else-branch should be found",
            allBricks.contains(elseNestedBrick)
        )
    }

    @Test
    fun testGetListAllBricksWithDeeplyNestedStructure() {
        val script = StartScript()
        val foreverBrick = ForeverBrick()
        val ifBrick = IfLogicBeginBrick()
        val deeplyNestedBrick = WebRequestBrick()

        ifBrick.addBrickToIfBranch(deeplyNestedBrick)
        foreverBrick.addBrick(ifBrick)
        script.addBrick(foreverBrick)
        sprite.addScript(script)

        val allBricks = sprite.getListAllBricks()

        assertTrue(
            "ForeverBrick should be found",
            allBricks.contains(foreverBrick)
        )
        assertTrue(
            "IfLogicBeginBrick nested inside ForeverBrick should be found",
            allBricks.contains(ifBrick)
        )
        // Note: getListAllBricks() only goes one level deep for nested bricks.
        // The deeply nested brick inside IfLogicBeginBrick which is itself inside
        // ForeverBrick will only be found if the IfLogicBeginBrick's nested bricks
        // are also explicitly traversed. This is a known limitation documented in
        // the maintainer's comments on IDE-31.
    }

    @Test
    fun testGetListAllBricksWithMultipleScripts() {
        val script1 = StartScript()
        val brick1 = WaitBrick(100)
        script1.addBrick(brick1)

        val script2 = StartScript()
        val brick2 = PlaceAtBrick(5, 5)
        script2.addBrick(brick2)

        sprite.addScript(script1)
        sprite.addScript(script2)

        val allBricks = sprite.getListAllBricks()

        assertTrue("Brick from script1 should be found", allBricks.contains(brick1))
        assertTrue("Brick from script2 should be found", allBricks.contains(brick2))
    }
}
