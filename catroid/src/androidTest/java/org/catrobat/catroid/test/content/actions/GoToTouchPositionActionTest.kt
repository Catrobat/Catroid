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

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.badlogic.gdx.scenes.scene2d.Action
import junit.framework.Assert
import org.catrobat.catroid.common.BrickValues
import org.catrobat.catroid.content.ActionFactory
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.utils.TouchUtil
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GoToTouchPositionActionTest {
    @get:Rule
    val exception = ExpectedException.none()
    private lateinit var sprite: Sprite
    private lateinit var dummySprite: Sprite
    private lateinit var action: Action
    @Before
    @Throws(Exception::class)
    fun setUp() {
        sprite = Sprite("testSprite")
        dummySprite = Sprite("dummySprite")
        action = sprite.actionFactory.createGoToAction(
            sprite,
            dummySprite,
            BrickValues.GO_TO_TOUCH_POSITION
        )
    }

    @Test
    @Throws(InterruptedException::class)
    fun testGoToTouchPositionAction() {
        sprite.look.xInUserInterfaceDimensionUnit = 0f
        sprite.look.yInUserInterfaceDimensionUnit = 0f
        Assert.assertEquals(0f, sprite.look.xInUserInterfaceDimensionUnit)
        Assert.assertEquals(0f, sprite.look.yInUserInterfaceDimensionUnit)
        TouchUtil.setDummyTouchForTest(EXPECTED_X_POSITION, EXPECTED_Y_POSITION)
        action.act(1f)
        Assert.assertEquals(EXPECTED_X_POSITION, sprite.look.xInUserInterfaceDimensionUnit)
        Assert.assertEquals(EXPECTED_Y_POSITION, sprite.look.yInUserInterfaceDimensionUnit)
    }

    @Test
    fun testNullActor() {
        val factory = ActionFactory()
        val action = factory.createGoToAction(null, dummySprite, BrickValues.GO_TO_TOUCH_POSITION)
        exception.expect(NullPointerException::class.java)
        action.act(1.0f)
    }

    companion object {
        private const val EXPECTED_X_POSITION = 20f
        private const val EXPECTED_Y_POSITION = 25f
    }
}
