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
import org.catrobat.catroid.content.Sprite
import org.junit.runner.RunWith
import org.catrobat.catroid.embroidery.EmbroideryStream
import org.catrobat.catroid.embroidery.EmbroideryWorkSpace
import org.junit.Before
import org.catrobat.catroid.embroidery.DSTStream
import org.catrobat.catroid.embroidery.DSTWorkSpace
import org.catrobat.catroid.embroidery.StitchCommand
import org.catrobat.catroid.embroidery.DSTStitchCommand
import org.catrobat.catroid.embroidery.StitchPoint
import org.junit.Assert
import org.junit.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import java.util.ArrayList

@RunWith(AndroidJUnit4::class)
class DSTStitchCommandTest {
    private var sprite: Sprite? = null
    private var stream: EmbroideryStream? = null
    private var workSpace: EmbroideryWorkSpace? = null
    private val xCoord = 1.5f
    private val yCoord = 2.5f
    private val layer = 3
    @Before
    fun setUp() {
        sprite = Mockito.mock(Sprite::class.java)
        stream = Mockito.mock(DSTStream::class.java)
        workSpace = Mockito.mock(DSTWorkSpace::class.java)
    }

    @Test
    fun testDSTStitchCommand() {
        val command: StitchCommand = DSTStitchCommand(xCoord, yCoord, layer, sprite, Color.BLACK)
        Assert.assertEquals(xCoord, command.x, 0f)
        Assert.assertEquals(yCoord, command.y, 0f)
        Assert.assertEquals(layer.toLong(), command.layer.toLong())
        Assert.assertEquals(sprite, command.sprite)
    }

    @Test
    fun testAddSimpleStitchCommand() {
        val command: StitchCommand = DSTStitchCommand(xCoord, yCoord, layer, sprite, Color.BLACK)
        command.act(workSpace, stream, null)
        Mockito.verify(stream, Mockito.times(1))?.addStitchPoint(
            ArgumentMatchers.eq(xCoord), ArgumentMatchers.eq(yCoord), ArgumentMatchers.eq(
                Color.BLACK
            )
        )
    }

    @Test
    fun testActDuplicateStitchCommand() {
        Mockito.`when`(workSpace!!.currentX).thenReturn(xCoord)
        Mockito.`when`(workSpace!!.currentY).thenReturn(yCoord)
        Mockito.`when`(workSpace!!.lastSprite).thenReturn(sprite)
        val command: StitchCommand = DSTStitchCommand(xCoord, yCoord, layer, sprite, Color.BLACK)
        command.act(workSpace, stream, null)
        Mockito.verify(stream, Mockito.never())?.addStitchPoint(
            ArgumentMatchers.anyFloat(), ArgumentMatchers.anyFloat(), ArgumentMatchers.eq(
                Color.BLACK
            )
        )
    }

    @Test
    fun testSpriteTriggeredColorChange() {
        Mockito.`when`(workSpace!!.currentX).thenReturn(0.0f)
        Mockito.`when`(workSpace!!.currentY).thenReturn(0.0f)
        Mockito.`when`(workSpace!!.lastSprite).thenReturn(sprite)
        val command: StitchCommand = DSTStitchCommand(
            xCoord, yCoord, layer, Mockito.mock(
                Sprite::class.java
            ), Color.BLACK
        )
        command.act(workSpace, stream, null)
        Mockito.verify(stream, Mockito.times(1))?.addColorChange()
        Mockito.verify(stream, Mockito.times(2))?.addStitchPoint(
            ArgumentMatchers.eq(0.0f),
            ArgumentMatchers.eq(0.0f),
            ArgumentMatchers.eq<Color?>(null)
        )
        Mockito.verify(stream, Mockito.times(1))?.addStitchPoint(
            ArgumentMatchers.eq(xCoord), ArgumentMatchers.eq(yCoord), ArgumentMatchers.eq(
                Color.BLACK
            )
        )
    }

    @Test
    fun testLayerSwitchTriggeredColorChange() {
        val pointList = ArrayList<StitchPoint>()
        pointList.add(Mockito.mock(StitchPoint::class.java))
        Mockito.`when`(stream!!.pointList).thenReturn(pointList)
        val previousCommand: StitchCommand = DSTStitchCommand(
            1F, 1F, layer - 1, Mockito.mock(
                Sprite::class.java
            ), Color.BLACK
        )
        val command: StitchCommand = DSTStitchCommand(
            xCoord, yCoord, layer, Mockito.mock(
                Sprite::class.java
            ), Color.BLACK
        )
        command.act(workSpace, stream, previousCommand)
        val inOrder = Mockito.inOrder(stream)
        inOrder.verify(stream, Mockito.times(1))?.addColorChange()
        inOrder.verify(stream, Mockito.times(1))?.addStitchPoint(
            ArgumentMatchers.eq(1.0f), ArgumentMatchers.eq(1.0f), ArgumentMatchers.eq(
                Color.BLACK
            )
        )
        inOrder.verify(stream, Mockito.times(1))?.addJump()
        inOrder.verify(stream, Mockito.times(2))?.addStitchPoint(
            ArgumentMatchers.eq(1.0f), ArgumentMatchers.eq(1.0f), ArgumentMatchers.eq(
                Color.BLACK
            )
        )
        inOrder.verify(stream, Mockito.times(1))?.addStitchPoint(
            ArgumentMatchers.eq(xCoord), ArgumentMatchers.eq(yCoord), ArgumentMatchers.eq(
                Color.BLACK
            )
        )
    }

    @Test
    fun testAddPreviousCommandOfSpriteOfOtherLayer() {
        val pointList = ArrayList<StitchPoint>()
        Mockito.`when`(stream!!.pointList).thenReturn(pointList)
        val previousCommand: StitchCommand = DSTStitchCommand(
            1F, 1F, layer - 1, Mockito.mock(
                Sprite::class.java
            ), Color.BLACK
        )
        val command: StitchCommand = DSTStitchCommand(
            xCoord, yCoord, layer, Mockito.mock(
                Sprite::class.java
            ), Color.BLACK
        )
        command.act(workSpace, stream, previousCommand)
        Mockito.verify(stream, Mockito.times(1))?.addStitchPoint(
            ArgumentMatchers.eq(1.0f), ArgumentMatchers.eq(1.0f), ArgumentMatchers.eq(
                Color.BLACK
            )
        )
        Mockito.verify(stream, Mockito.times(1))?.addStitchPoint(
            ArgumentMatchers.eq(xCoord), ArgumentMatchers.eq(yCoord), ArgumentMatchers.eq(
                Color.BLACK
            )
        )
    }

    @Test
    fun testStitchCommandEquals() {
        val sprite = Sprite("firstSprite")
        val command1: StitchCommand = DSTStitchCommand(0F, 0F, 0, sprite, Color.BLACK)
        val command2: StitchCommand = DSTStitchCommand(0F, 1F, 0, sprite, Color.BLACK)
        val command3: StitchCommand = DSTStitchCommand(0F, 0F, 1, sprite, Color.BLACK)
        val command4: StitchCommand = DSTStitchCommand(0F, 0F, 0, Sprite("secondSprite"), Color.BLACK)
        val command5: StitchCommand = DSTStitchCommand(0F, 0F, 0, sprite, Color.BLACK)
        Assert.assertEquals(command1, command5)
        Assert.assertNotEquals(command1, command2)
        Assert.assertNotEquals(command1, command3)
        Assert.assertNotEquals(command1, command4)
    }

    @Test
    fun testStitchCommandHashCode() {
        val sprite = Sprite("firstSprite")
        val command1: StitchCommand = DSTStitchCommand(0F, 0F, 0, sprite, Color.BLACK)
        val command2: StitchCommand = DSTStitchCommand(0F, 1F, 0, sprite, Color.BLACK)
        val command3: StitchCommand = DSTStitchCommand(0F, 0F, 1, sprite, Color.BLACK)
        val command4: StitchCommand = DSTStitchCommand(0F, 0F, 0, Sprite("secondSprite"), Color.BLACK)
        val command5: StitchCommand = DSTStitchCommand(0F, 0F, 0, sprite, Color.BLACK)
        Assert.assertEquals(command1.hashCode().toLong(), command5.hashCode().toLong())
        Assert.assertNotEquals(command1.hashCode().toLong(), command2.hashCode().toLong())
        Assert.assertNotEquals(command1.hashCode().toLong(), command3.hashCode().toLong())
        Assert.assertNotEquals(command1.hashCode().toLong(), command4.hashCode().toLong())
    }
}
