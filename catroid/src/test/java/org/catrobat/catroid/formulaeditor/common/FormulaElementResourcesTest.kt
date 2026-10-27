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
package org.catrobat.catroid.formulaeditor.common

import org.catrobat.catroid.content.bricks.Brick
import org.catrobat.catroid.formulaeditor.Sensors
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

/**
 * The resource a sensor needs decides what the stage starts and which AI
 * setting a loaded project switches on, so each sensor must map to its own
 * detection and nothing else.
 */
@RunWith(Parameterized::class)
class FormulaElementResourcesTest(
    private val sensor: Sensors,
    private val expectedResource: Int
) {

    @Test
    fun sensorNeedsExactlyItsOwnResource() {
        val resources = mutableSetOf<Int?>()

        FormulaElementResources.addSensorsResources(resources, sensor)

        assertEquals("Resources needed by $sensor", setOf<Int?>(expectedResource), resources)
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun parameters() = listOf(
            arrayOf(Sensors.TEXT_FROM_CAMERA, Brick.TEXT_DETECTION),
            arrayOf(Sensors.TEXT_BLOCKS_NUMBER, Brick.TEXT_DETECTION),
            arrayOf(Sensors.TEXT_BLOCK_X, Brick.TEXT_DETECTION),
            arrayOf(Sensors.TEXT_BLOCK_Y, Brick.TEXT_DETECTION),
            arrayOf(Sensors.TEXT_BLOCK_SIZE, Brick.TEXT_DETECTION),
            arrayOf(Sensors.TEXT_BLOCK_FROM_CAMERA, Brick.TEXT_DETECTION),
            arrayOf(Sensors.TEXT_BLOCK_LANGUAGE_FROM_CAMERA, Brick.TEXT_DETECTION),
            arrayOf(Sensors.ON_DEVICE_FACE_RECOGNITION, Brick.FACE_NAME_DETECTION)
        )
    }
}
