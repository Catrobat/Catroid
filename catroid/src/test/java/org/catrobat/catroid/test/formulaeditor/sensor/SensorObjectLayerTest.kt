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
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.formulaeditor.sensor.Sensor
import org.catrobat.catroid.formulaeditor.sensor.testsensors.SensorObjectLayerTest
import org.catrobat.catroid.formulaeditor.sensor.testutils.LookTestable
import org.junit.Assert
import org.junit.Test

class SensorObjectLayerTest {

    @Test
    fun lookZIndex0Test() {
        val expectedValue = 0
        val sprite = Sprite()
        val spriteList: List<Sprite?> = mutableListOf()
        val look = LookTestable(sprite)
        look.zIndexForTesting = expectedValue
        SensorObjectLayerTest.getInstance().setLook(look)
        SensorObjectLayerTest.getInstance().setSprite(sprite)
        SensorObjectLayerTest.getInstance().setSpriteList(spriteList)
        compareToSensor(expectedValue, SensorObjectLayerTest.getInstance())
    }

    @Test
    fun lookZIndexGreater0Test() {
        val zIndex = 6
        val expectedValue = zIndex - Constants.Z_INDEX_NUMBER_VIRTUAL_LAYERS
        val sprite = Sprite()
        val spriteList: List<Sprite?> = mutableListOf()
        val look = LookTestable(sprite)
        look.zIndexForTesting = zIndex
        SensorObjectLayerTest.getInstance().setLook(look)
        SensorObjectLayerTest.getInstance().setSprite(sprite)
        SensorObjectLayerTest.getInstance().setSpriteList(spriteList)
        compareToSensor(expectedValue, SensorObjectLayerTest.getInstance())
    }

    @Test
    fun lookZIndexLessThan0Test() {
        val zIndex = -2
        val expectedValue = 4
        val sprite = Sprite()
        sprite.name = "good sprite"
        val spriteList = mutableListOf<Sprite?>()
        val look = LookTestable(sprite)
        look.zIndexForTesting = zIndex
        val badSprite = Sprite()
        badSprite.name = "bad sprite"
        spriteList.add(badSprite)
        spriteList.add(badSprite)
        spriteList.add(badSprite)
        spriteList.add(badSprite)
        spriteList.add(sprite)
        spriteList.add(badSprite)
        spriteList.add(badSprite)
        spriteList.add(badSprite)
        spriteList.add(badSprite)
        SensorObjectLayerTest.getInstance().setLook(look)
        SensorObjectLayerTest.getInstance().setSprite(sprite)
        SensorObjectLayerTest.getInstance().setSpriteList(spriteList)
        compareToSensor(expectedValue, SensorObjectLayerTest.getInstance())
    }

    private fun compareToSensor(value: Int, sensor: Sensor) {
        Assert.assertEquals(value.toDouble(), sensor.getSensorValue() as Double, DELTA)
    }

    companion object {
        private const val DELTA = 0.01
    }
}
