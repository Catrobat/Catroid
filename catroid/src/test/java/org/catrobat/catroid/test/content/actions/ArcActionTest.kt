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
package org.catrobat.catroid.test.content.actions

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction
import junit.framework.Assert.assertEquals
import org.catrobat.catroid.content.ActionFactory
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.bricks.ArcBrick
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.test.StaticSingletonInitializer
import org.junit.Before
import org.junit.Test

class ArcActionTest {
    private lateinit var sprite: Sprite
    private lateinit var actionFactory: ActionFactory

    @Before
    @Throws(Exception::class)
    fun setUp() {
        StaticSingletonInitializer.initializeStaticSingletonMethods()
        sprite = Sprite("test")
        actionFactory = sprite.actionFactory
        sprite.look.setPositionInUserInterfaceDimensionUnit(0f, 0f)
        sprite.look.motionDirectionInUserInterfaceDimensionUnit = 0f
    }

    @Test
    fun testArcRightQuarterCircle() {
        val action = actionFactory.createArcAction(
            sprite,
            SequenceAction(),
            ArcBrick.Directions.RIGHT,
            Formula(10),
            Formula(90)
        )

        action.act(1.0f)

        assertEquals(10f, sprite.look.xInUserInterfaceDimensionUnit, POSITION_DELTA)
        assertEquals(10f, sprite.look.yInUserInterfaceDimensionUnit, POSITION_DELTA)
        assertEquals(90f, sprite.look.motionDirectionInUserInterfaceDimensionUnit, DIRECTION_DELTA)
    }

    @Test
    fun testArcLeftQuarterCircle() {
        val action = actionFactory.createArcAction(
            sprite,
            SequenceAction(),
            ArcBrick.Directions.LEFT,
            Formula(10),
            Formula(90)
        )

        action.act(1.0f)

        assertEquals(-10f, sprite.look.xInUserInterfaceDimensionUnit, POSITION_DELTA)
        assertEquals(10f, sprite.look.yInUserInterfaceDimensionUnit, POSITION_DELTA)
        assertEquals(-90f, sprite.look.motionDirectionInUserInterfaceDimensionUnit, DIRECTION_DELTA)
    }

    @Test
    fun testNegativeDegreesAreHandledByDirectionNormalization() {
        val action = actionFactory.createArcAction(
            sprite,
            SequenceAction(),
            ArcBrick.Directions.RIGHT,
            Formula(10),
            Formula(-90)
        )

        action.act(1.0f)

        assertEquals(-10f, sprite.look.xInUserInterfaceDimensionUnit, POSITION_DELTA)
        assertEquals(10f, sprite.look.yInUserInterfaceDimensionUnit, POSITION_DELTA)
        assertEquals(-90f, sprite.look.motionDirectionInUserInterfaceDimensionUnit, DIRECTION_DELTA)
    }

    companion object {
        private const val POSITION_DELTA = 0.0001f
        private const val DIRECTION_DELTA = 0.5f
    }
}
