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
package org.catrobat.catroid.test.embroidery

import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.catrobat.catroid.embroidery.ZigZagRunningStitch
import org.catrobat.catroid.embroidery.EmbroideryPatternManager
import org.catrobat.catroid.content.Look
import org.catrobat.catroid.content.Sprite
import org.junit.Before
import kotlin.Throws
import org.catrobat.catroid.stage.StageActivity
import org.catrobat.catroid.stage.StageListener
import org.junit.After
import org.junit.Assert
import org.junit.Test
import org.mockito.Mockito
import java.lang.Exception
import java.util.ArrayList
import java.util.Arrays

@RunWith(Parameterized::class)
class ZigZagParametrizedTest {
    @JvmField
    @Parameterized.Parameter
    var name: String? = null
    @JvmField
    @Parameterized.Parameter(1)
    var length: Float? = null
    @JvmField
    @Parameterized.Parameter(2)
    var width: Float? = null
    @JvmField
    @Parameterized.Parameter(3)
    var degrees: Float? = null
    @JvmField
    @Parameterized.Parameter(4)
    var expectedstitchPointsX: List<Float>? = null
    @JvmField
    @Parameterized.Parameter(5)
    var expectedStitchPointsY: List<Float>? = null
    private var zigZagRunningStitch: ZigZagRunningStitch? = null
    private var embroideryPatternManager: EmbroideryPatternManager? = null
    private var sprite: Sprite? = null
    private var spriteLook: Look? = null
    private val actualStitchPointsX = ArrayList<Float>()
    private val actualStitchPointsY = ArrayList<Float>()
    @Before
    @Throws(Exception::class)
    fun setUp() {
        sprite = Mockito.mock(Sprite::class.java)
        spriteLook = Mockito.mock(Look::class.java)
        Mockito.`when`(spriteLook?.motionDirectionInUserInterfaceDimensionUnit)
            .thenReturn(degrees)
        sprite?.look = spriteLook
        embroideryPatternManager = Mockito.mock(
            EmbroideryPatternManager::class.java
        )
        StageActivity.stageListener = Mockito.mock(StageListener::class.java)
        StageActivity.stageListener.embroideryPatternManager = embroideryPatternManager
        zigZagRunningStitch = ZigZagRunningStitch(sprite, length!!, width!!)
        zigZagRunningStitch!!.setListener { x: Float, y: Float ->
            actualStitchPointsX.add(x)
            actualStitchPointsY.add(y)
        }
        zigZagRunningStitch!!.setStartCoordinates(10f, 0f)
        zigZagRunningStitch!!.update(30f, 0f)
    }

    @After
    fun tearDown() {
        StageActivity.stageListener = null
    }

    @Test
    fun testXCoordinates() {
        Assert.assertEquals(expectedstitchPointsX, actualStitchPointsX)
    }

    @Test
    fun testYCoordinates() {
        Assert.assertEquals(expectedStitchPointsY, actualStitchPointsY)
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun data(): Iterable<Array<Any>> {
            return Arrays.asList(
                *arrayOf(
                    arrayOf(
                        "Test Points", 10f, 5f, 90f, Arrays.asList(10.0f, 20f, 30f), Arrays.asList(
                            2.5f,
                            -2.5f,
                            2.5f
                        )
                    ), arrayOf(
                        "Test more Points",
                        5f,
                        2f,
                        90f,
                        Arrays.asList(10f, 15f, 20f, 25f, 30f),
                        Arrays.asList(1f, -1f, 1f, -1f, 1f)
                    ), arrayOf(
                        "Test different length",
                        20f,
                        5f,
                        90f,
                        Arrays.asList(10.0f, 30f),
                        Arrays.asList(
                            2.5f,
                            -2.5f
                        )
                    ), arrayOf(
                        "Test different width",
                        10f,
                        10f,
                        90f,
                        Arrays.asList(10.0f, 20f, 30f),
                        Arrays.asList(
                            5.0f,
                            -5.0f,
                            5.0f
                        )
                    ), arrayOf(
                        "Test degrees",
                        10f,
                        10f,
                        270f,
                        Arrays.asList(10.0f, 20f, 30f),
                        Arrays.asList(
                            -5.0f,
                            5.0f,
                            -5.0f
                        )
                    )
                )
            )
        }
    }
}
