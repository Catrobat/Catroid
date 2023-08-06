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

package org.catrobat.catroid.utils

import org.catrobat.catroid.visualplacement.model.Size
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class CoordinatesTest {

    @Test
    fun shouldConvertAndroidCoordinatesToGameCoordinates() {
        // given
        val viewSize = Size(40f, 60.0f)
        val topLeftCornerAndroid = AndroidCoordinates(0.0f, 0.0f)
        val middleAndroid = AndroidCoordinates(20.0f, 30.0f)
        val topLeftCornerGame = GameCoordinates(-20f, -30f)
        val middleGame = GameCoordinates(0f, 0f)

        // when & then
        assertEquals(topLeftCornerGame.x, topLeftCornerAndroid.toGameCoordinates(viewSize).x)
        assertEquals(topLeftCornerGame.y, topLeftCornerAndroid.toGameCoordinates(viewSize).y)
        assertEquals(middleGame.x, middleAndroid.toGameCoordinates(viewSize).x)
        assertEquals(middleGame.y, middleAndroid.toGameCoordinates(viewSize).y)
    }

    @Test
    fun shouldConvertGameCoordinatesToAndroidCoordinates() {
        // given
        val viewSize = Size(40f, 60.0f)
        val topLeftCornerAndroid = AndroidCoordinates(0.0f, 0.0f)
        val middleAndroid = AndroidCoordinates(20.0f, 30.0f)
        val topLeftCornerGame = GameCoordinates(-20f, -30f)
        val middleGame = GameCoordinates(0f, 0f)

        // when & then
        assertEquals(topLeftCornerAndroid.x, topLeftCornerGame.toAndroidCoordinates(viewSize).x)
        assertEquals(topLeftCornerAndroid.y, topLeftCornerGame.toAndroidCoordinates(viewSize).y)
        assertEquals(middleAndroid.x, middleGame.toAndroidCoordinates(viewSize).x)
        assertEquals(middleAndroid.y, middleGame.toAndroidCoordinates(viewSize).y)
    }
}
