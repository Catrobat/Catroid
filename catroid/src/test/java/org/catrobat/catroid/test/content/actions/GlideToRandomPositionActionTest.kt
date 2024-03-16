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

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.actions.GlideToRandomPositionAction
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.test.StaticSingletonInitializer.Companion.initializeStaticSingletonMethods
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotSame
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class GlideToRandomPositionActionTest {

    private lateinit var sprite: Sprite
    private lateinit var action: GlideToRandomPositionAction

    private val _duration = 225f
    var duration = Formula(_duration)

    @Before
    fun setUp() {
        initializeStaticSingletonMethods()
        sprite = Sprite("testSprite")
        action = sprite.actionFactory.createGlideToRandomAction(
            sprite, SequenceAction(), duration) as GlideToRandomPositionAction
    }

    @Test
    fun testGlideToRandomPositionAction() {
        sprite.look.xInUserInterfaceDimensionUnit = 0f
        sprite.look.yInUserInterfaceDimensionUnit = 0f

        assertEquals(0f, sprite.look.xInUserInterfaceDimensionUnit)

        var currentTimeDelta = System.currentTimeMillis()
        var step = 0
        do {
            currentTimeDelta = System.currentTimeMillis() - currentTimeDelta
            assertNotSame(action.getXPosition(), sprite.look
                .xInUserInterfaceDimensionUnit)
            assertNotSame(action.getYPosition(), sprite.look
                .yInUserInterfaceDimensionUnit)
            step++
        } while (!action.act(currentTimeDelta.toFloat()))

        assertEquals(action.getXPosition(), sprite.look.xInUserInterfaceDimensionUnit)
        assertEquals(action.getYPosition(), sprite.look.yInUserInterfaceDimensionUnit)
        assertEquals(2, step)
    }

    @Test(expected = NullPointerException::class)
    fun testNullActor() {
        sprite.actionFactory.createGlideToRandomAction(
            null, SequenceAction(), duration
        ).act(1.0f)
    }
}
