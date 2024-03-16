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
    var changeSizeByNBrick = ChangeSizeByNBrick()
    var setXBrick = SetXBrick()
    lateinit var sprite: Sprite

    @Before
    fun setUp() {
        script = StartScript()

        val scripts: MutableList<Script> = ArrayList()
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
        assertTrue(ifLogicBeginBrick.secondaryNestedBricks.contains(setXBrick))
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
        assertTrue(script.brickList.contains(setXBrick))
        assertFalse(ifLogicBeginBrick.secondaryNestedBricks.contains(changeSizeByNBrick))
        assertFalse(ifLogicBeginBrick.secondaryNestedBricks.contains(setXBrick))
    }

    @Test
    fun testDragDownIntoAnotherEnclosure() {
        ifLogicBeginBrick.addBrickToElseBranch(changeSizeByNBrick)
        ifLogicBeginBrick.addBrickToElseBranch(setXBrick)
        script.addBrick(ifLogicBeginBrick)
        val foreverBrick = ForeverBrick()
        script.addBrick(foreverBrick)
        adapter = BrickAdapter(sprite)
        val endBrick = adapter.items[5]

        assertTrue(adapter.onItemMove(5, 6))

        adapter.moveItemTo(6, endBrick)

        assertTrue(foreverBrick.dragAndDropTargetList.contains(ifLogicBeginBrick))
        assertEquals(changeSizeByNBrick, adapter.items[4])
        assertEquals(setXBrick, adapter.items[5])
    }
}
