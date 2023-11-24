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
import org.junit.Assert
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.test.StaticSingletonInitializer.Companion.initializeStaticSingletonMethods
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)

class ChangeTransparencyByNActionTest {
    @JvmField
    @Rule
    val exception: ExpectedException = ExpectedException.none()
    private lateinit var sprite: Sprite
    @Before
    @Throws(Exception::class)
    fun setUp() {
        initializeStaticSingletonMethods()
        sprite = Sprite("testSprite")
    }

    @Test
    fun testNormalBehavior() {
        Assert.assertEquals(0f, sprite.look.transparencyInUserInterfaceDimensionUnit)
        sprite.actionFactory.createChangeTransparencyByNAction(
            sprite, SequenceAction(), Formula(
                INCREASE_VALUE
            )
        ).act(1.0f)
        Assert.assertEquals(INCREASE_VALUE, sprite.look.transparencyInUserInterfaceDimensionUnit)
        sprite.actionFactory.createChangeTransparencyByNAction(
            sprite, SequenceAction(), Formula(
                DECREASE_VALUE
            )
        ).act(1.0f)
        Assert.assertEquals(
            INCREASE_VALUE + DECREASE_VALUE,
            sprite.look.transparencyInUserInterfaceDimensionUnit
        )
    }

    @Test(expected = NullPointerException::class)
    fun testNullSprite() {
        val action = sprite.actionFactory.createChangeTransparencyByNAction(
            null,
            SequenceAction(), Formula(
                INCREASE_VALUE
            )
        )
        action.act(1.0f)
    }

    @Test
    fun testBrickWithStringFormula() {
        sprite.actionFactory.createChangeTransparencyByNAction(
            sprite, SequenceAction(), Formula(
                INCREASE_VALUE.toString()
            )
        )
            .act(1.0f)
        Assert.assertEquals(
            INCREASE_VALUE,
            sprite.look.transparencyInUserInterfaceDimensionUnit,
            DELTA
        )
        sprite.actionFactory.createChangeTransparencyByNAction(
            sprite, SequenceAction(), Formula(
                NOT_NUMERICAL_STRING
            )
        ).act(1.0f)
        Assert.assertEquals(
            INCREASE_VALUE,
            sprite.look.transparencyInUserInterfaceDimensionUnit,
            DELTA
        )
    }

    @Test
    fun testNullFormula() {
        sprite.actionFactory.createChangeTransparencyByNAction(sprite, SequenceAction(), null)
            .act(1.0f)
        Assert.assertEquals(0f, sprite.look.transparencyInUserInterfaceDimensionUnit)
    }

    @Test
    fun testNotANumberFormula() {
        sprite.actionFactory.createChangeTransparencyByNAction(
            sprite, SequenceAction(), Formula(
                Double.NaN
            )
        ).act(1.0f)
        Assert.assertEquals(0f, sprite.look.transparencyInUserInterfaceDimensionUnit)
    }

    companion object {
        private const val DELTA = 0.01f
        private const val INCREASE_VALUE = 98.7f
        private const val DECREASE_VALUE = -33.3f
        private const val NOT_NUMERICAL_STRING = "ghosts"
    }
}
