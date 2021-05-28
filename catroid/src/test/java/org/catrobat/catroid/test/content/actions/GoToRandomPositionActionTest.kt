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

import org.junit.Assert.assertEquals
import org.catrobat.catroid.common.BrickValues
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.actions.GoToRandomPositionAction
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class GoToRandomPositionActionTest {
    lateinit var sprite: Sprite
    lateinit var dummySprite: Sprite
    lateinit var nullSprite: Sprite
    lateinit var action: GoToRandomPositionAction

    @Before
    @Throws(Exception::class)
    fun setUp() {
        sprite = Sprite("testSprite")
        dummySprite = Sprite("dummySprite")
        nullSprite = Sprite()
        action = sprite.actionFactory.createGoToAction(sprite, dummySprite, BrickValues
            .GO_TO_RANDOM_POSITION) as GoToRandomPositionAction
    }

    @Test
    fun testGoToOtherSpriteAction() {
        sprite.look.xInUserInterfaceDimensionUnit = 0f
        sprite.look.yInUserInterfaceDimensionUnit = 0f

        assertEquals(0f, sprite.look.xInUserInterfaceDimensionUnit)
        assertEquals(0f, sprite.look.yInUserInterfaceDimensionUnit)

        action.act(1f)

        assertEquals(action.randomXPosition, sprite.look.xInUserInterfaceDimensionUnit)
        assertEquals(action.randomYPosition, sprite.look.yInUserInterfaceDimensionUnit)
    }

    @Test
    fun testCheckPositionOnNullSpriteAfterAction() {
        action = sprite.actionFactory.createGoToAction(null, sprite, BrickValues
            .GO_TO_RANDOM_POSITION) as GoToRandomPositionAction

        action.act(1f)
    }
}
