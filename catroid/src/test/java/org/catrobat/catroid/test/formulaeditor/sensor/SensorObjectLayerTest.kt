/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2023 The Catrobat Team
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

package org.catrobat.catroid.test.formulaeditor.sensor

import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.content.Look
import org.catrobat.catroid.content.Scene
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.formulaeditor.SensorHandler
import org.catrobat.catroid.formulaeditor.Sensors
import org.junit.Assert
import org.junit.Test
import org.powermock.api.mockito.PowerMockito

class SensorObjectLayerTest {

    @Test
    fun lookZIndex0Test() {
        val expectedValue = 0
        val mockSprite = PowerMockito.mock(Sprite::class.java)
        val mockLook = PowerMockito.mock(Look::class.java)
        val mockScene = PowerMockito.mock(Scene::class.java)
        SensorHandler.currentlyEditedScene = mockScene
        SensorHandler.currentSprite = mockSprite
        mockSprite.look = mockLook
        mockSprite.look.zIndex = 0
        compareToSensor(expectedValue, Sensors.OBJECT_LAYER)
    }

    @Test
    fun lookZIndexGreater0Test() {
        val expectedValue = 45
        val mockSprite = PowerMockito.mock(Sprite::class.java)
        val mockLook = PowerMockito.mock(Look::class.java)
        val mockScene = PowerMockito.mock(Scene::class.java)
        SensorHandler.currentSprite = mockSprite
        SensorHandler.currentlyEditedScene = mockScene
        mockSprite.look = mockLook
        PowerMockito.`when`(mockLook.zIndex).thenReturn(expectedValue)
        compareToSensor(
            expectedValue - Constants.Z_INDEX_NUMBER_VIRTUAL_LAYERS,
            Sensors.OBJECT_LAYER
        )
    }

    @Test
    fun lookZIndexLessThan0Test() {
        val expectedValue = -4
        val mockSprite = PowerMockito.mock(Sprite::class.java)
        val mockLook = PowerMockito.mock(Look::class.java)
        val mockScene = PowerMockito.mock(Scene::class.java)
        val spriteList = mutableListOf<Sprite>()

        spriteList.add(PowerMockito.mock(Sprite::class.java))
        spriteList.add(PowerMockito.mock(Sprite::class.java))
        spriteList.add(PowerMockito.mock(Sprite::class.java))
        spriteList.add(PowerMockito.mock(Sprite::class.java))
        spriteList.add(mockSprite)
        spriteList.add(PowerMockito.mock(Sprite::class.java))
        spriteList.add(PowerMockito.mock(Sprite::class.java))
        spriteList.add(PowerMockito.mock(Sprite::class.java))
        spriteList.add(PowerMockito.mock(Sprite::class.java))

        PowerMockito.`when`(mockScene.spriteList).thenReturn(spriteList)
        SensorHandler.currentSprite = mockSprite
        SensorHandler.currentlyEditedScene = mockScene
        mockSprite.look = mockLook
        PowerMockito.`when`(mockLook.zIndex).thenReturn(expectedValue)
        compareToSensor(4, Sensors.OBJECT_LAYER)
    }

    private fun compareToSensor(value: Int, sensor: Sensors) {
        Assert.assertEquals(value.toDouble(), sensor.getSensor().getSensorValue() as Double, DELTA)
    }

    companion object {
        private const val DELTA = 0.01
    }
}
