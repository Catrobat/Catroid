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
package org.catrobat.catroid.test.content.bricks

import org.catrobat.catroid.test.StaticSingletonInitializer.Companion.initializeStaticSingletonMethods
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.catrobat.catroid.content.bricks.CompositeBrick
import org.catrobat.catroid.content.bricks.Brick
import org.junit.Before
import kotlin.Throws
import org.catrobat.catroid.test.StaticSingletonInitializer
import org.catrobat.catroid.content.actions.ScriptSequenceAction
import org.catrobat.catroid.content.ActionFactory
import org.catrobat.catroid.content.Script
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.bricks.BrickBaseType
import org.catrobat.catroid.content.bricks.IfThenLogicBeginBrick
import org.catrobat.catroid.content.bricks.ForeverBrick
import org.catrobat.catroid.content.bricks.RepeatBrick
import org.catrobat.catroid.content.bricks.RepeatUntilBrick
import org.junit.Assert
import org.junit.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import java.util.ArrayList
import java.util.Arrays

@RunWith(Parameterized::class)
class CompositeBrickTest {
    @JvmField
    @Parameterized.Parameter
    var name: String? = null
    @JvmField
    @Parameterized.Parameter(1)
    var compositeBrickClass: Class<CompositeBrick>? = null
    private var compositeBrick: CompositeBrick? = null
    private var compositeEndBrick: Brick? = null
    @Before
    @Throws(IllegalAccessException::class, InstantiationException::class)
    fun setUp() {
        initializeStaticSingletonMethods()
        compositeBrick = compositeBrickClass!!.newInstance()
        val compositeBrickParts = compositeBrick!!.getAllParts()
        compositeEndBrick = compositeBrickParts[compositeBrickParts.size - 1]
    }

    @Test
    fun testGetPartsOfCompositeBrick() {
        Assert.assertTrue(compositeBrick!!.consistsOfMultipleParts())
        Assert.assertFalse(compositeBrick!!.hasSecondaryList())
        junit.framework.Assert.assertNull(compositeBrick!!.secondaryNestedBricks)
        Assert.assertEquals(2, compositeBrick!!.allParts.size.toLong())
        Assert.assertSame(compositeBrick, compositeBrick!!.allParts[0])
        Assert.assertSame(
            compositeEndBrick,
            compositeBrick!!.allParts[compositeBrick!!.allParts.size - 1]
        )
    }

    @Test
    fun testSetParents() {
        val mockParent = Mockito.mock(Brick::class.java)
        val nestedBrick = Mockito.mock(Brick::class.java)
        compositeBrick!!.nestedBricks.add(nestedBrick)
        compositeBrick!!.parent = mockParent
        Mockito.verify(compositeBrick!!.nestedBricks[0]).parent = compositeBrick
        Mockito.verifyZeroInteractions(mockParent)
        Assert.assertSame(compositeBrick, compositeEndBrick!!.parent)
    }

    @Test
    fun testGetParentListFromCompositeStartBrick() {
        val mockParent = Mockito.mock(Brick::class.java)
        compositeBrick!!.parent = mockParent
        val parentList = compositeBrick!!.dragAndDropTargetList
        Mockito.verifyZeroInteractions(mockParent)
        Assert.assertSame(compositeBrick!!.nestedBricks, parentList)
    }

    @Test
    fun testGetPositionInParentListFromCompositeStartBrick() {
        val mockParent = Mockito.mock(Brick::class.java)
        compositeBrick!!.nestedBricks.add(Mockito.mock(Brick::class.java))
        compositeBrick!!.parent = mockParent
        Assert.assertEquals(-1, compositeBrick!!.positionInDragAndDropTargetList.toLong())
    }

    @Test
    fun testGetParentListFromCompositeEndBrick() {
        val mockParent = Mockito.mock(Brick::class.java)
        val mockedParentList: MutableList<Brick?> = ArrayList()
        mockedParentList.add(compositeBrick)
        Mockito.`when`(mockParent.dragAndDropTargetList).thenReturn(mockedParentList)
        compositeBrick!!.parent = mockParent
        val parentList = compositeEndBrick!!.dragAndDropTargetList
        Mockito.verify(mockParent).dragAndDropTargetList
        Assert.assertSame(mockedParentList, parentList)
        Assert.assertTrue(parentList.contains(compositeBrick))
    }

    @Test
    fun testGetPositionInParentListFromCompositeEndBrick() {
        val mockParent = Mockito.mock(Brick::class.java)
        val mockedParentList: MutableList<Brick?> = ArrayList()
        mockedParentList.add(Mockito.mock(Brick::class.java))
        mockedParentList.add(compositeBrick)
        Mockito.`when`(mockParent.dragAndDropTargetList).thenReturn(mockedParentList)
        compositeBrick!!.parent = mockParent
        Assert.assertEquals(1, compositeEndBrick!!.positionInDragAndDropTargetList.toLong())
    }

    @Test
    @Throws(CloneNotSupportedException::class)
    fun testCloneCompositeBrick() {
        val nestedBrick = Mockito.mock(Brick::class.java)
        compositeBrick!!.nestedBricks.add(nestedBrick)
        val clone = compositeBrick!!.clone() as CompositeBrick
        Mockito.verify(nestedBrick).clone()
        Mockito.`when`(nestedBrick.clone()).thenReturn(
            Mockito.mock(
                Brick::class.java
            )
        )
        Assert.assertNotSame(compositeBrick, clone)
        Assert.assertEquals(1, clone.nestedBricks.size.toLong())
        Assert.assertNotSame(nestedBrick, clone.nestedBricks[0])
        Assert.assertNotSame(compositeEndBrick, clone.allParts[1])
    }

    @Test
    fun testCommentOutBrickInCompositeBrick() {
        val sprite = Sprite()
        val sequence = ActionFactory.createScriptSequenceAction(
            Mockito.mock(
                Script::class.java
            )
        ) as ScriptSequenceAction
        val brickToCommentOut: Brick = Mockito.spy(BrickStub())
        val otherNestedBrick: Brick = Mockito.spy(BrickStub())
        compositeBrick!!.nestedBricks.add(brickToCommentOut)
        compositeBrick!!.nestedBricks.add(otherNestedBrick)
        brickToCommentOut.isCommentedOut = true
        compositeBrick!!.addActionToSequence(sprite, sequence)
        Mockito.verify(brickToCommentOut, Mockito.never())
            .addActionToSequence(
                ArgumentMatchers.any(
                    Sprite::class.java
                ), ArgumentMatchers.any(
                    ScriptSequenceAction::class.java
                )
            )
        Mockito.verify(otherNestedBrick, Mockito.times(1))
            .addActionToSequence(
                ArgumentMatchers.any(
                    Sprite::class.java
                ), ArgumentMatchers.any(
                    ScriptSequenceAction::class.java
                )
            )
    }

    private class BrickStub : BrickBaseType() {
        override fun getViewResource(): Int {
            return 0
        }

        override fun addActionToSequence(sprite: Sprite, sequence: ScriptSequenceAction) {}
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun data(): Iterable<Array<Any>> {
            return Arrays.asList(
                *arrayOf(
                    arrayOf(
                        IfThenLogicBeginBrick::class.java.simpleName,
                        IfThenLogicBeginBrick::class.java
                    ), arrayOf(
                        ForeverBrick::class.java.simpleName, ForeverBrick::class.java
                    ), arrayOf(
                        RepeatBrick::class.java.simpleName, RepeatBrick::class.java
                    ), arrayOf(
                        RepeatUntilBrick::class.java.simpleName, RepeatUntilBrick::class.java
                    )
                )
            )
        }
    }
}