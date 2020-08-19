/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2020 The Catrobat Team
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
package org.catrobat.catroid.test.stage

import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.sensing.ColorCollisionDetection
import org.catrobat.catroid.stage.StageListener
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.mockito.Mockito

@RunWith(Parameterized::class)
internal class ColorCollisionParameterTest(
    private val name: String,
    private val parameter: Any?,
    private val expected: Boolean
) {
    private val sprite = Sprite()
    private val projectMock = Mockito.mock(Project::class.java)
    private val stageListenerMock = Mockito.mock(StageListener::class.java)
    private val colorCollisionDetection = ColorCollisionDetection(sprite, projectMock, stageListenerMock)

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun data(): List<Array<out Any?>> {
            return listOf(
                arrayOf("Too Short Parameter", "#FFFFF", true),
                arrayOf("Too Long Parameter", "#FFFFFFF", true),
                arrayOf("Invalid Hex", "#FFDGFF", true),
                arrayOf("No # at the beginning", "FFFFFFF", true),
                arrayOf("Null", null, true),
                arrayOf("Any Object", Any(), true),
                arrayOf("Valid Uppercase Hex", "#FFFFFF", false),
                arrayOf("Valid lowercase Hex", "#ffffff", false),
                arrayOf("Valid lower- uppercase mixed Hex", "#fffFFF", false)
            )
        }
    }

    @Test
    fun testAreParametersInvalid() {
        Assert.assertEquals(expected, colorCollisionDetection.isParameterInvalid(parameter))
    }
}
