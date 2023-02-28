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
import junit.framework.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class GoToRandomPositionActionTest {
    @Rule
    val exception = ExpectedException.none()
    private var sprite: org.catrobat.catroid.content.Sprite? = null
    private var dummySprite: org.catrobat.catroid.content.Sprite? = null
    private var action: GoToRandomPositionAction? = null
    @Before
    @Throws(Exception::class)
    fun setUp() {
        sprite = org.catrobat.catroid.content.Sprite("testSprite")
        dummySprite = org.catrobat.catroid.content.Sprite("dummySprite")
        action = sprite.getActionFactory().createGoToAction(
            sprite, dummySprite, BrickValues.GO_TO_RANDOM_POSITION
        ) as GoToRandomPositionAction
    }

    @Test
    fun testGoToOtherSpriteAction() {
        sprite.look.setXInUserInterfaceDimensionUnit(0f)
        sprite.look.setYInUserInterfaceDimensionUnit(0f)
        Assert.assertEquals(0f, sprite.look.getXInUserInterfaceDimensionUnit())
        Assert.assertEquals(0f, sprite.look.getYInUserInterfaceDimensionUnit())
        action.act(1f)
        Assert.assertEquals(action.randomXPosition, sprite.look.getXInUserInterfaceDimensionUnit())
        Assert.assertEquals(action.randomYPosition, sprite.look.getYInUserInterfaceDimensionUnit())
    }

    @Test
    fun testNullActor() {
        val factory = ActionFactory()
        val action: Action =
            factory.createGoToAction(null, dummySprite, BrickValues.GO_TO_RANDOM_POSITION)
        exception.expect(NullPointerException::class.java)
        action.act(1.0f)
    }
}