/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2018 The Catrobat Team
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

import com.badlogic.gdx.scenes.scene2d.actions.RepeatAction
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.test.utils.Reflection
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.mockito.Mockito
import org.mockito.Mockito.anyFloat
import org.mockito.Mockito.times
import org.mockito.Mockito.verify

@RunWith(Parameterized::class)
class RepeatActionTest(
    private var loopCondition: Formula?,
    private var expectedValue: Int
) {
    private lateinit var sprite: Sprite
    private lateinit var innerLoopAction: MockAction
    private lateinit var repeatAction: RepeatAction

    private val delayByContract = 0.020f
    private val deltaDelayByContract = 0.005f
    private val iterations = 4

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{index}")
        fun parameters() = listOf(
            arrayOf(Formula(4), 4),
            arrayOf(Formula(-1), 0),
            arrayOf(Formula(0), 0),
            arrayOf(Formula("3"), 3),
            arrayOf(Formula(Double.NaN), 0)
        )
    }

    @Before
    fun setUp() {
        sprite = Sprite("testSprite")
        innerLoopAction = Mockito.mock(MockAction()::class.java, Mockito.CALLS_REAL_METHODS)
        repeatAction = sprite.actionFactory.createRepeatAction(sprite, loopCondition, innerLoopAction) as RepeatAction
    }

    @Test
    fun testLoopDelay() {
        repeat((iterations * delayByContract / deltaDelayByContract).toInt()) {
            repeatAction.act(deltaDelayByContract)
        }
        val executedCount = Reflection.getPrivateField(repeatAction, "executedCount")
        Assert.assertEquals(expectedValue, executedCount)
        verify(innerLoopAction, times(expectedValue)).update(anyFloat())
    }

    private class MockAction : TemporalAction() {
        @SuppressWarnings("EmptyFunctionBlock")
        public final override fun update(percent: Float) { }
    }
}
