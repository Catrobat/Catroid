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
import org.junit.Before
import org.catrobat.catroid.embroidery.StitchPoint
import org.catrobat.catroid.embroidery.DSTStitchPoint
import org.junit.Assert
import org.junit.Test
import org.mockito.Mockito
import java.io.FileOutputStream
import java.io.IOException
import kotlin.Throws

@RunWith(AndroidJUnit4::class)
class DSTStitchPointTest {
    private var fileOutputStream: FileOutputStream? = null
    @Before
    fun setUo() {
        fileOutputStream = Mockito.mock(FileOutputStream::class.java)
    }

    @Test
    fun testisConnectingPoint() {
        val stitchPoint: StitchPoint = DSTStitchPoint(0F, 0F, Color.BLACK)
        Assert.assertTrue(stitchPoint.isConnectingPoint)
        Assert.assertFalse(stitchPoint.isColorChangePoint)
        Assert.assertFalse(stitchPoint.isJumpPoint)
    }

    @Test
    fun testJumpStitchPoint() {
        val stitchPoint: StitchPoint = DSTStitchPoint(0F, 0F, Color.BLACK)
        stitchPoint.setJump(true)
        Assert.assertTrue(stitchPoint.isJumpPoint)
        Assert.assertFalse(stitchPoint.isConnectingPoint)
    }

    @Test
    fun testColorChangeStitchPoint() {
        val stitchPoint: StitchPoint = DSTStitchPoint(0F, 0F, Color.BLACK)
        stitchPoint.setColorChange(true)
        Assert.assertTrue(stitchPoint.isColorChangePoint)
        Assert.assertFalse(stitchPoint.isConnectingPoint)
    }

    @Test
    @Throws(IOException::class)
    fun testStitchBytesZeroDifference() {
        val expectedStitchBytes = byteArrayOf(0, 0, 0x3.toByte())
        val stitchPoint: StitchPoint = DSTStitchPoint(0F, 0F, Color.BLACK)
        stitchPoint.setRelativeCoordinatesToPreviousPoint(0f, 0f)
        stitchPoint.appendToStream(fileOutputStream)
        Mockito.verify(fileOutputStream, Mockito.times(1))?.write(expectedStitchBytes)
    }

    @Test
    @Throws(IOException::class)
    fun testStitchBytesDifference() {
        val expectedStitchBytes = byteArrayOf(0x5A.toByte(), 0x5A.toByte(), 0x03.toByte())
        val stitchPoint: StitchPoint = DSTStitchPoint(0F, 0F, Color.BLACK)
        stitchPoint.setRelativeCoordinatesToPreviousPoint(20f, 20f)
        stitchPoint.appendToStream(fileOutputStream)
        Mockito.verify(fileOutputStream, Mockito.times(1))?.write(expectedStitchBytes)
    }

    @Test
    @Throws(IOException::class)
    fun testStitchBytesWithColorChange() {
        val expectedStitchBytes = byteArrayOf(0xA5.toByte(), 0xA5.toByte(), 0xC3.toByte())
        val stitchPoint: StitchPoint = DSTStitchPoint(0F, 0F, Color.BLACK)
        stitchPoint.setColorChange(true)
        stitchPoint.setRelativeCoordinatesToPreviousPoint(-20f, -20f)
        stitchPoint.appendToStream(fileOutputStream)
        Mockito.verify(fileOutputStream, Mockito.times(1))?.write(expectedStitchBytes)
    }

    @Test
    @Throws(IOException::class)
    fun testStitchBytesWithJump() {
        val expectedStitchBytes = byteArrayOf(0xAA.toByte(), 0xAA.toByte(), 0x83.toByte())
        val stitchPoint: StitchPoint = DSTStitchPoint(0F, 0F, Color.BLACK)
        stitchPoint.setJump(true)
        stitchPoint.setRelativeCoordinatesToPreviousPoint(20f, -20f)
        stitchPoint.appendToStream(fileOutputStream)
        Mockito.verify(fileOutputStream, Mockito.times(1))?.write(expectedStitchBytes)
    }


    @Test
    fun testStitchPointsEquals() {
        val stitchPoint1: StitchPoint = DSTStitchPoint(0F, 0F, Color.BLACK)
        val stitchPoint2: StitchPoint = DSTStitchPoint(1F, 1F, Color.BLACK)
        val stitchPoint3: StitchPoint = DSTStitchPoint(0F, 0F, Color.BLACK)
        stitchPoint3.setColorChange(true)
        val stitchPoint4: StitchPoint = DSTStitchPoint(0F, 0F, Color.BLACK)
        stitchPoint4.setJump(true)
        val stitchPoint5: StitchPoint = DSTStitchPoint(0F, 0F, Color.BLACK)
        Assert.assertNotEquals(stitchPoint1, stitchPoint2)
        Assert.assertNotEquals(stitchPoint1, stitchPoint3)
        Assert.assertNotEquals(stitchPoint1, stitchPoint4)
        Assert.assertNotEquals(stitchPoint3, stitchPoint4)
        Assert.assertEquals(stitchPoint1, stitchPoint5)
    }

    @Test
    fun testStitchPointsHashCode() {
        val stitchPoint1: StitchPoint = DSTStitchPoint(1F, 2F, Color.BLACK)
        val stitchPoint2: StitchPoint = DSTStitchPoint(0F, 0F, Color.BLACK)
        stitchPoint2.setJump(true)
        val stitchPoint3: StitchPoint = DSTStitchPoint(0F, 0F, Color.BLACK)
        stitchPoint3.setColorChange(true)
        val stitchPoint4: StitchPoint = DSTStitchPoint(2F, 1F, Color.BLACK)
        val stitchPoint5: StitchPoint = DSTStitchPoint(1F, 2F, Color.BLACK)
        Assert.assertNotEquals(stitchPoint1.hashCode().toLong(), stitchPoint2.hashCode().toLong())
        Assert.assertNotEquals(stitchPoint1.hashCode().toLong(), stitchPoint3.hashCode().toLong())
        Assert.assertNotEquals(stitchPoint1.hashCode().toLong(), stitchPoint4.hashCode().toLong())
        Assert.assertEquals(stitchPoint1.hashCode().toLong(), stitchPoint5.hashCode().toLong())
    }
}