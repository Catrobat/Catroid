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

package org.catrobat.catroid.test.ui.brickadapter

import org.catrobat.catroid.content.Script
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.StartScript
import org.catrobat.catroid.content.bricks.ChangeSizeByNBrick
import org.catrobat.catroid.content.bricks.ForeverBrick
import org.catrobat.catroid.content.bricks.IfLogicBeginBrick
import org.catrobat.catroid.content.bricks.RepeatBrick
import org.catrobat.catroid.content.bricks.SetXBrick
import org.catrobat.catroid.ui.recyclerview.adapter.BrickAdapter
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

@RunWith(JUnit4::class)
class EndBrickDragAndDropTest {
    lateinit var adapter: BrickAdapter
    lateinit var script: Script
    var ifLogicBeginBrick = IfLogicBeginBrick()
    var foreverBrick = ForeverBrick()
    var repeatBrick = RepeatBrick()
    var changeSizeByNBrick = ChangeSizeByNBrick()
    var setXBrick = SetXBrick()
    lateinit var sprite: Sprite
    val scripts: MutableList<Script> = ArrayList()

    @Before
    fun setUp() {
        script = StartScript()

        scripts.add(script)

        sprite = mock(Sprite::class.java)
        `when`(sprite.scriptList).thenReturn(scripts)
    }

    @Test
    fun testDragDownEndBrick() {
        script.addBrick(ifLogicBeginBrick)
        script.addBrick(changeSizeByNBrick)
        script.addBrick(setXBrick)
        adapter = BrickAdapter(sprite)

        val endBrick = adapter.items[3]

        assertTrue(adapter.onItemMove(3, 4))
        assertTrue(adapter.onItemMove(4, 5))

        adapter.moveItemTo(5, endBrick)

        assertTrue(ifLogicBeginBrick.secondaryNestedBricks.contains(changeSizeByNBrick))
        assertEquals(0, ifLogicBeginBrick.secondaryNestedBricks.indexOf(changeSizeByNBrick))

        assertTrue(ifLogicBeginBrick.secondaryNestedBricks.contains(setXBrick))
        assertEquals(1, ifLogicBeginBrick.secondaryNestedBricks.indexOf(setXBrick))

        assertFalse(script.brickList.contains(changeSizeByNBrick))
        assertFalse(script.brickList.contains(setXBrick))
    }

    @Test
    fun testDragUpEndBrick() {
        ifLogicBeginBrick.addBrickToElseBranch(changeSizeByNBrick)
        ifLogicBeginBrick.addBrickToElseBranch(setXBrick)
        script.addBrick(ifLogicBeginBrick)
        adapter = BrickAdapter(sprite)

        val endBrick = adapter.items[5]

        assertTrue(adapter.onItemMove(5, 4))
        assertTrue(adapter.onItemMove(4, 3))

        adapter.moveItemTo(3, endBrick)

        assertTrue(script.brickList.contains(changeSizeByNBrick))
        assertEquals(1, script.brickList.indexOf(changeSizeByNBrick))

        assertTrue(script.brickList.contains(setXBrick))
        assertEquals(2, script.brickList.indexOf(setXBrick))

        assertFalse(ifLogicBeginBrick.secondaryNestedBricks.contains(changeSizeByNBrick))
        assertFalse(ifLogicBeginBrick.secondaryNestedBricks.contains(setXBrick))
    }

    @Test
    fun testDragEndBrickDownIntoAnotherEnclosure() {
        ifLogicBeginBrick.addBrickToElseBranch(changeSizeByNBrick)
        ifLogicBeginBrick.addBrickToElseBranch(setXBrick)
        script.addBrick(ifLogicBeginBrick)

        script.addBrick(foreverBrick)
        adapter = BrickAdapter(sprite)

        val endBrick = adapter.items[5]

        assertTrue(adapter.onItemMove(5, 6))

        adapter.moveItemTo(6, endBrick)

        assertTrue(foreverBrick.dragAndDropTargetList.contains(ifLogicBeginBrick))
        assertFalse(script.brickList.contains(ifLogicBeginBrick))

        assertEquals(0, ifLogicBeginBrick.secondaryNestedBricks.indexOf(changeSizeByNBrick))
        assertEquals(1, ifLogicBeginBrick.secondaryNestedBricks.indexOf(setXBrick))
    }

    @Test
    fun testDragEndBrickDownIntoAnotherEnclosureInsideEnclosure() {
        repeatBrick.addBrick(setXBrick)
        script.addBrick(repeatBrick)

        foreverBrick.addBrick(ifLogicBeginBrick)
        script.addBrick(foreverBrick)

        adapter = BrickAdapter(sprite)

        val endBrick = adapter.items[3]

        assertTrue(adapter.onItemMove(3, 4))
        assertTrue(adapter.onItemMove(4, 5))
        assertTrue(adapter.onItemMove(5, 6))

        adapter.moveItemTo(6, endBrick)

        assertFalse(script.brickList.contains(repeatBrick))

        assertTrue(ifLogicBeginBrick.secondaryNestedBricks.contains(repeatBrick))
        assertTrue(repeatBrick.dragAndDropTargetList.contains(setXBrick))
    }

    @Test
    fun testDragEndBrickAboveElseBrick() {
        ifLogicBeginBrick.addBrickToElseBranch(changeSizeByNBrick)
        ifLogicBeginBrick.addBrickToElseBranch(setXBrick)
        script.addBrick(ifLogicBeginBrick)
        adapter = BrickAdapter(sprite)

        val endBrick = adapter.items[5]

        assertTrue(adapter.onItemMove(5, 4))
        assertTrue(adapter.onItemMove(4, 3))
        assertTrue(adapter.onItemMove(3, 2))

        adapter.moveItemTo(2, endBrick)
        assertEquals(endBrick, adapter.items[5])

        assertFalse(script.brickList.contains(changeSizeByNBrick))
        assertFalse(script.brickList.contains(setXBrick))

        assertTrue(ifLogicBeginBrick.secondaryNestedBricks.contains(changeSizeByNBrick))
        assertEquals(0, ifLogicBeginBrick.secondaryNestedBricks.indexOf(changeSizeByNBrick))

        assertTrue(ifLogicBeginBrick.secondaryNestedBricks.contains(setXBrick))
        assertEquals(1, ifLogicBeginBrick.secondaryNestedBricks.indexOf(setXBrick))
    }

    @Test
    fun testDragEndBrickAboveHead() {
        foreverBrick.addBrick(setXBrick)
        script.addBrick(foreverBrick)
        adapter = BrickAdapter(sprite)

        val endBrick = adapter.items[3]

        assertTrue(adapter.onItemMove(3, 2))
        assertTrue(adapter.onItemMove(2, 1))

        adapter.moveItemTo(1, endBrick)

        assertEquals(foreverBrick, adapter.items[1])

        assertTrue(foreverBrick.dragAndDropTargetList.contains(setXBrick))
        assertFalse(script.brickList.contains(setXBrick))

        assertEquals(endBrick, adapter.items[3])
    }

    @Test
    fun testDragEndBrickIntoOtherScript() {
        val scriptOther: Script
        scriptOther = StartScript()
        scripts.add(scriptOther)
        scriptOther.addBrick(setXBrick)

        ifLogicBeginBrick.addBrickToElseBranch(changeSizeByNBrick)
        script.addBrick(ifLogicBeginBrick)

        adapter = BrickAdapter(sprite)

        val endBrick = adapter.items[4]

        assertTrue(adapter.onItemMove(4, 5))

        adapter.moveItemTo(5, endBrick)
        assertEquals(endBrick, adapter.items[4])

        assertFalse(scriptOther.brickList.contains(endBrick))
        assertFalse(scriptOther.brickList.contains(ifLogicBeginBrick))

        assertTrue(script.brickList.contains(ifLogicBeginBrick))

        assertEquals(changeSizeByNBrick, adapter.items[3])
    }
}
