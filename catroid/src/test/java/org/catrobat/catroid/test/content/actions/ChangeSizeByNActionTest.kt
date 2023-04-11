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
import org.catrobat.catroid.content.ActionFactory
import org.junit.Rule
import org.junit.Test
import java.lang.Exception
import java.lang.NullPointerException

@RunWith(JUnit4::class)
class ChangeSizeByNActionTest {
    @Rule
    val exception: ExpectedException = ExpectedException.none()
    private var sprite: Sprite? = null
    @Before
    @Throws(Exception::class)
    fun setUp() {
        StaticSingletonInitializer.Companion.initializeStaticSingletonMethods()
        sprite = Sprite("testSprite")
    }

    @Test
    fun testSize() {
        assertEquals(INITIALIZED_VALUE, sprite.look.getSizeInUserInterfaceDimensionUnit())
        sprite.getActionFactory().createChangeSizeByNAction(
            sprite,
            SequenceAction(), Formula(CHANGE_SIZE)
        ).act(1.0f)
        assertEquals(
            INITIALIZED_VALUE + CHANGE_SIZE,
            sprite.look.getSizeInUserInterfaceDimensionUnit(),
            DELTA
        )
        sprite.getActionFactory().createChangeSizeByNAction(
            sprite, SequenceAction(),
            Formula(-CHANGE_SIZE)
        ).act(1.0f)
        assertEquals(INITIALIZED_VALUE, sprite.look.getSizeInUserInterfaceDimensionUnit(), DELTA)
    }

    @Test(expected = NullPointerException::class)
    fun testNullSprite() {
        val factory = ActionFactory()
        val action: Action = factory.createChangeSizeByNAction(
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
        sprite.getActionFactory().createChangeSizeByNAction(
            sprite, SequenceAction(), Formula(
                CHANGE_VALUE.toString()
            )
        )
            .act(1.0f)
        assertEquals(
            INITIALIZED_VALUE + CHANGE_VALUE,
            sprite.look.getSizeInUserInterfaceDimensionUnit()
        )
        sprite.getActionFactory().createChangeSizeByNAction(
            sprite, SequenceAction(), Formula(
                NOT_NUMERICAL_STRING
            )
        ).act(1.0f)
        assertEquals(
            INITIALIZED_VALUE + CHANGE_VALUE,
            sprite.look.getSizeInUserInterfaceDimensionUnit()
        )
    }

    @Test
    fun testNullFormula() {
        sprite.getActionFactory().createChangeSizeByNAction(sprite, SequenceAction(), null)
            .act(1.0f)
        assertEquals(INITIALIZED_VALUE, sprite.look.getSizeInUserInterfaceDimensionUnit())
    }

    @Test
    fun testNotANumberFormula() {
        sprite.getActionFactory()
            .createChangeSizeByNAction(sprite, SequenceAction(), Formula(Double.NaN)).act(1.0f)
        assertEquals(INITIALIZED_VALUE, sprite.look.getSizeInUserInterfaceDimensionUnit())
    }

    companion object {
        private const val INITIALIZED_VALUE = 100f
        private const val CHANGE_VALUE = 44.4f
        private const val NOT_NUMERICAL_STRING = "size"
        private const val CHANGE_SIZE = 20f
        private const val DELTA = 0.0001f
    }
}