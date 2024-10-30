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

import com.badlogic.gdx.scenes.scene2d.Action
import junit.framework.Assert.assertEquals
import org.catrobat.catroid.common.BrickValues
import org.catrobat.catroid.content.ActionFactory
import org.catrobat.catroid.content.Sprite
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class GoToOtherSpritePositionActionTest {
    private lateinit var sprite: Sprite
    private lateinit var destinationSprite: Sprite
    private lateinit var action: Action
    private val DESTINATION_X_POSITION = 150f
    private val DESTINATION_Y_POSITION = 300f

    @Before
    fun setUp() {
        sprite = Sprite("testSprite")
        destinationSprite = Sprite("destinationSprite")
        action = sprite.actionFactory.createGoToAction(
            sprite, destinationSprite,
            BrickValues.GO_TO_OTHER_SPRITE_POSITION
        )
    }

    @Test
    fun testGoToOtherSpritePositionAction() {
        destinationSprite.look.xInUserInterfaceDimensionUnit = DESTINATION_X_POSITION
        destinationSprite.look.yInUserInterfaceDimensionUnit = DESTINATION_Y_POSITION
        sprite.look.xInUserInterfaceDimensionUnit = 0f
        sprite.look.yInUserInterfaceDimensionUnit = 0f
        assertEquals(0f, sprite.look.xInUserInterfaceDimensionUnit)
        assertEquals(0f, sprite.look.yInUserInterfaceDimensionUnit)
        action.act(1f)
        assertEquals(DESTINATION_X_POSITION, sprite.look.xInUserInterfaceDimensionUnit)
        assertEquals(DESTINATION_Y_POSITION, sprite.look.yInUserInterfaceDimensionUnit)
    }

    @Test
    fun testNullActor() {
        val factory = ActionFactory()
        factory.createGoToAction(
            null, destinationSprite, BrickValues.GO_TO_OTHER_SPRITE_POSITION
        ).act(1.0f)
    }
}
