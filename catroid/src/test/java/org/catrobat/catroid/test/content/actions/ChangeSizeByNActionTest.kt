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
import junit.framework.Assert
import org.catrobat.catroid.content.ActionFactory
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
class ChangeSizeByNActionTest {
    @JvmField
    @Rule
    val exception: ExpectedException = ExpectedException.none()
    private var sprite: Sprite? = null
    @Before
    @Throws(Exception::class)
    fun setUp() {
        initializeStaticSingletonMethods()
        sprite = Sprite("testSprite")
    }

    @Test
    fun testSize() {
        Assert.assertEquals(INITIALIZED_VALUE, sprite!!.look.sizeInUserInterfaceDimensionUnit)
        sprite!!.actionFactory.createChangeSizeByNAction(
            sprite,
            SequenceAction(), Formula(CHANGE_SIZE)
        ).act(1.0f)
        Assert.assertEquals(
            INITIALIZED_VALUE + CHANGE_SIZE,
            sprite!!.look.sizeInUserInterfaceDimensionUnit,
            DELTA
        )
        sprite!!.actionFactory.createChangeSizeByNAction(
            sprite, SequenceAction(),
            Formula(-CHANGE_SIZE)
        ).act(1.0f)
        Assert.assertEquals(
            INITIALIZED_VALUE,
            sprite!!.look.sizeInUserInterfaceDimensionUnit,
            DELTA
        )
    }

    @Test(expected = NullPointerException::class)
    fun testNullSprite() {
        val factory = ActionFactory()
        val action = factory.createChangeSizeByNAction(
            null,
            SequenceAction(),
            Formula(
                CHANGE_SIZE
            )
        )
        action.act(1.0f)
    }

    @Test
    fun testBrickWithStringFormula() {
        sprite!!.actionFactory.createChangeSizeByNAction(
            sprite, SequenceAction(), Formula(
                CHANGE_VALUE.toString()
            )
        )
            .act(1.0f)
        Assert.assertEquals(
            INITIALIZED_VALUE + CHANGE_VALUE,
            sprite!!.look.sizeInUserInterfaceDimensionUnit
        )
        sprite!!.actionFactory.createChangeSizeByNAction(
            sprite, SequenceAction(), Formula(
                NOT_NUMERICAL_STRING
            )
        ).act(1.0f)
        Assert.assertEquals(
            INITIALIZED_VALUE + CHANGE_VALUE,
            sprite!!.look.sizeInUserInterfaceDimensionUnit
        )
    }

    @Test
    fun testNullFormula() {
        sprite!!.actionFactory.createChangeSizeByNAction(sprite, SequenceAction(), null).act(1.0f)
        Assert.assertEquals(INITIALIZED_VALUE, sprite!!.look.sizeInUserInterfaceDimensionUnit)
    }

    @Test
    fun testNotANumberFormula() {
        sprite!!.actionFactory.createChangeSizeByNAction(
            sprite,
            SequenceAction(),
            Formula(Double.NaN)
        ).act(1.0f)
        Assert.assertEquals(INITIALIZED_VALUE, sprite!!.look.sizeInUserInterfaceDimensionUnit)
    }

    companion object {
        private const val INITIALIZED_VALUE = 100f
        private const val CHANGE_VALUE = 44.4f
        private const val NOT_NUMERICAL_STRING = "size"
        private const val CHANGE_SIZE = 20f
        private const val DELTA = 0.0001f
    }
}
