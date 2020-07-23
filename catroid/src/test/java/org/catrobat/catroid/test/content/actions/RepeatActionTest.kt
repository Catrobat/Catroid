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

import junit.framework.TestCase.assertEquals
import org.catrobat.catroid.content.Script
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.StartScript
import org.catrobat.catroid.content.bricks.ChangeYByNBrick
import org.catrobat.catroid.content.bricks.RepeatBrick
import org.catrobat.catroid.content.eventids.EventId
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.test.utils.Reflection
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class RepeatActionTest(
    private val name: String,
    private val repeatTimes: Formula,
    private val expectedLoopCount: Int
) {
    private lateinit var sprite: Sprite
    private lateinit var script: Script
    private lateinit var repeatBrick: RepeatBrick
    private lateinit var changeYByNBrick: ChangeYByNBrick

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun parameters() = listOf(
            arrayOf("SINGLE_REPEAT", Formula(1), 1),
            arrayOf("MULTIPLE_REPEATS", Formula(3), 3),
            arrayOf("NEGATIVE_REPEATS", Formula(-3), 0),
            arrayOf("ZERO_REPEATS", Formula(0), 0),
            arrayOf("NUMERICAL_STRING", Formula("4"), 4),
            arrayOf("NON_NUMERICAL_STRING", Formula("test"), 0),
            arrayOf("NAN", Formula(Double.NaN), 0)
        )
    }

    @Before
    fun setUp() {
        sprite = Sprite("testSprite")
        script = StartScript()
        repeatBrick = RepeatBrick(repeatTimes)
        changeYByNBrick = ChangeYByNBrick(1)
    }

    @Test
    fun testRepeatAction() {
        val repeatAction = sprite.actionFactory.createRepeatAction(sprite, repeatTimes, null)
        repeatAction.act(1.0f)
        Assert.assertEquals(expectedLoopCount, Reflection.getPrivateField(repeatAction, "repeatCountValue"))
    }

    @Test
    fun testRepeatBrick() {
        repeatBrick.addBrick(changeYByNBrick)
        script.addBrick(repeatBrick)
        sprite.addScript(script)

        executeEventThreads()

        assertEquals(expectedLoopCount, sprite.look.yInUserInterfaceDimensionUnit.toInt())
    }

    @Test
    fun testNestedRepeatBrick() {
        val nestedRepeatBrick = RepeatBrick(repeatTimes)

        nestedRepeatBrick.addBrick(changeYByNBrick)
        repeatBrick.addBrick(nestedRepeatBrick)
        script.addBrick(repeatBrick)
        sprite.addScript(script)
        executeEventThreads()

        Assert.assertEquals(expectedLoopCount * expectedLoopCount, sprite.look.yInUserInterfaceDimensionUnit.toInt())
    }

    private fun executeEventThreads() {
        sprite.initializeEventThreads(EventId.START)
        while (!sprite.look.haveAllThreadsFinished()) {
            sprite.look.act(1.0f)
        }
    }
}
