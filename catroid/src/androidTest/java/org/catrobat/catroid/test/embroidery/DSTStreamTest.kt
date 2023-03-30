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

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.badlogic.gdx.graphics.Color
import org.junit.runner.RunWith
import org.catrobat.catroid.embroidery.EmbroideryStream
import org.catrobat.catroid.embroidery.EmbroideryHeader
import org.junit.Before
import org.catrobat.catroid.embroidery.DSTHeader
import org.catrobat.catroid.embroidery.DSTStream
import org.catrobat.catroid.embroidery.StitchPoint
import org.catrobat.catroid.embroidery.DSTStitchPoint
import org.junit.Assert
import org.junit.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import java.util.ArrayList

@RunWith(AndroidJUnit4::class)
class DSTStreamTest {
    private var stream: EmbroideryStream? = null
    private var header: EmbroideryHeader? = null
    @Before
    fun setUp() {
        header = Mockito.mock(DSTHeader::class.java)
        stream = DSTStream(header)
    }

    @Test
    fun testAddJumpStitchPoint() {
        stream!!.addJump()
        stream!!.addStitchPoint(0f, 0f, Color.BLACK)
        Assert.assertEquals(1, stream!!.pointList.size.toLong())
        Assert.assertTrue(stream!!.pointList[0].isJumpPoint)
    }

    @Test
    fun testAddColorChangeStitchPoint() {
        stream!!.addColorChange()
        stream!!.addStitchPoint(0f, 0f, Color.BLACK)
        Assert.assertEquals(1, stream!!.pointList.size.toLong())
        Assert.assertTrue(stream!!.pointList[0].isColorChangePoint)
        Mockito.verify(header, Mockito.times(1))?.addColorChange()
    }

    private fun pointAtPositionEqualsCoordinates(index: Int, x: Int, y: Int): Boolean {
        val pointAtPosition = stream!!.pointList[index]
        return x.toFloat() == pointAtPosition.x && y.toFloat() == pointAtPosition.y
    }

    @Test
    fun testInterpolatedStitchPoints() {
        stream!!.addStitchPoint(0f, 0f, Color.BLACK)
        stream!!.addStitchPoint(80f, 80f, Color.BLACK)
        Assert.assertEquals(5, stream!!.pointList.size.toLong())
        Assert.assertTrue(pointAtPositionEqualsCoordinates(0, 0, 0))
        Assert.assertTrue(pointAtPositionEqualsCoordinates(1, 0, 0))
        Assert.assertTrue(stream!!.pointList[1].isJumpPoint)
        Assert.assertTrue(pointAtPositionEqualsCoordinates(2, 40, 40))
        Assert.assertTrue(stream!!.pointList[2].isJumpPoint)
        Assert.assertTrue(pointAtPositionEqualsCoordinates(3, 80, 80))
        Assert.assertTrue(stream!!.pointList[3].isJumpPoint)
        Assert.assertTrue(pointAtPositionEqualsCoordinates(4, 80, 80))
        Mockito.verify(header, Mockito.times(1))?.initialize(0f, 0f)
        Mockito.verify(header, Mockito.times(4))
            ?.update(ArgumentMatchers.anyFloat(), ArgumentMatchers.anyFloat())
    }

    @Test
    fun testAddAllStitchPoints() {
        val stitchPoint1: StitchPoint = DSTStitchPoint(0F, 0F, Color.BLACK)
        stitchPoint1.setColorChange(true)
        val stitchPoint2: StitchPoint = DSTStitchPoint(80F, 80F, Color.BLACK)
        stitchPoint2.setJump(true)
        val stitchPoint3: StitchPoint = DSTStitchPoint(100F, 100F, Color.BLACK)
        val stitchPoints = ArrayList<StitchPoint>()
        stitchPoints.add(stitchPoint1)
        stitchPoints.add(stitchPoint2)
        stitchPoints.add(stitchPoint3)
        stream!!.addAllStitchPoints(stitchPoints)
        Assert.assertEquals(6, stream!!.pointList.size.toLong())
        Assert.assertTrue(stream!!.pointList[0].isColorChangePoint)
        Assert.assertTrue(stream!!.pointList[1].isJumpPoint)
        Assert.assertTrue(stream!!.pointList[2].isJumpPoint)
        Assert.assertTrue(stream!!.pointList[3].isJumpPoint)
        Assert.assertTrue(stream!!.pointList[4].isJumpPoint)
        Assert.assertTrue(stream!!.pointList[5].isConnectingPoint)
        Mockito.verify(header, Mockito.times(1))?.initialize(0f, 0f)
        Mockito.verify(header, Mockito.times(5))
            ?.update(ArgumentMatchers.anyFloat(), ArgumentMatchers.anyFloat())
    }
}
