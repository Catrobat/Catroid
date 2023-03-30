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
import org.catrobat.catroid.embroidery.EmbroideryPatternManager
import org.catrobat.catroid.embroidery.StitchCommand
import org.junit.Before
import org.catrobat.catroid.embroidery.DSTPatternManager
import org.catrobat.catroid.embroidery.EmbroideryWorkSpace
import org.catrobat.catroid.embroidery.EmbroideryStream
import org.catrobat.catroid.embroidery.DSTStitchCommand
import org.catrobat.catroid.embroidery.DSTStitchPoint
import org.junit.Assert
import org.junit.Test
import org.mockito.Mockito

@RunWith(AndroidJUnit4::class)
class DSTPatternManagerTest {
    private var patternManager: EmbroideryPatternManager? = null
    private var sprite: Sprite? = null
    private var stitchCommand1: StitchCommand? = null
    private var stitchCommand2: StitchCommand? = null
    @Before
    fun setUp() {
        patternManager = DSTPatternManager()
        sprite = Mockito.mock(Sprite::class.java)
        stitchCommand1 = Mockito.mock(StitchCommand::class.java)
        Mockito.`when`(stitchCommand1?.getLayer()).thenReturn(0)
        Mockito.`when`(stitchCommand1?.getSprite()).thenReturn(sprite)
        stitchCommand2 = Mockito.mock(StitchCommand::class.java)
        Mockito.`when`(stitchCommand2?.getLayer()).thenReturn(1)
        Mockito.`when`(stitchCommand2?.getSprite()).thenReturn(sprite)
    }

    @Test
    fun testAddSingleStitchCommand() {
        patternManager!!.addStitchCommand(stitchCommand1)
        Mockito.verify(stitchCommand1, Mockito.times(1))?.act(
            Mockito.any(
                EmbroideryWorkSpace::class.java
            ),
            Mockito.any(
                EmbroideryStream::class.java
            ), Mockito.eq<StitchCommand>(null)
        )
    }

    @Test
    fun testAddMultipleIndependentStitchCommands() {
        val stitchCommand3 = Mockito.mock(StitchCommand::class.java)
        Mockito.`when`(stitchCommand3.layer).thenReturn(1)
        Mockito.`when`(stitchCommand3.sprite).thenReturn(Sprite())
        patternManager!!.addStitchCommand(stitchCommand1)
        patternManager!!.addStitchCommand(stitchCommand3)
        Mockito.verify(stitchCommand3, Mockito.times(1)).act(
            Mockito.any(
                EmbroideryWorkSpace::class.java
            ),
            Mockito.any(
                EmbroideryStream::class.java
            ), Mockito.eq<StitchCommand>(null)
        )
    }

    @Test
    fun testAddMultipleStitchCommands() {
        patternManager!!.addStitchCommand(stitchCommand1)
        patternManager!!.addStitchCommand(stitchCommand2)
        Mockito.verify(stitchCommand2, Mockito.times(1))?.act(
            Mockito.any(
                EmbroideryWorkSpace::class.java
            ),
            Mockito.any(
                EmbroideryStream::class.java
            ), Mockito.eq(stitchCommand1)
        )
    }

    @Test
    fun testClearEmbroideryPattern() {
        patternManager!!.addStitchCommand(stitchCommand1)
        patternManager!!.clear()
        patternManager!!.addStitchCommand(stitchCommand2)
        Mockito.verify(stitchCommand2, Mockito.times(1))?.act(
            Mockito.any(
                EmbroideryWorkSpace::class.java
            ),
            Mockito.any(
                EmbroideryStream::class.java
            ), Mockito.eq<StitchCommand>(null)
        )
    }

    @Test
    fun testInvalidPattern() {
        patternManager!!.addStitchCommand(DSTStitchCommand(0F, 0F, 0, sprite, Color.BLACK))
        Assert.assertFalse(patternManager!!.validPatternExists())
    }

    @Test
    fun testValidPattern() {
        patternManager!!.addStitchCommand(DSTStitchCommand(0F, 0F, 0, sprite, Color.BLACK))
        patternManager!!.addStitchCommand(DSTStitchCommand(0F, 1F, 0, sprite, Color.BLACK))
        Assert.assertTrue(patternManager!!.validPatternExists())
    }

    @Test
    fun testEmptyEmbroideryPattern() {
        Assert.assertTrue(patternManager!!.embroideryPatternList.isEmpty())
    }

    @Test
    fun testSingleLayerEmbroideryPatternList() {
        val command: StitchCommand = DSTStitchCommand(0F, 0F, 0, sprite, Color.BLACK)
        patternManager!!.addStitchCommand(command)
        Assert.assertEquals(1, patternManager!!.embroideryPatternList.size.toLong())
        Assert.assertEquals(
            DSTStitchPoint(command.x, command.y, Color.BLACK),
            patternManager!!.embroideryPatternList[0]
        )
    }

    @Test
    fun testMultilayerEmbroideryPatternList() {
        patternManager!!.addStitchCommand(DSTStitchCommand(0F, 0F, 0, sprite, Color.BLACK))
        patternManager!!.addStitchCommand(DSTStitchCommand(0F, 0F, 1, sprite, Color.BLACK))
        Assert.assertEquals(5, patternManager!!.embroideryPatternList.size.toLong())
        Assert.assertTrue(patternManager!!.embroideryPatternList[1].isColorChangePoint)
        Assert.assertTrue(patternManager!!.embroideryPatternList[2].isJumpPoint)
    }
}
