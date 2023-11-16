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
import org.catrobat.catroid.content.bricks.IfLogicBeginBrick
import org.catrobat.catroid.content.bricks.SetXBrick
import org.catrobat.catroid.content.bricks.SetYBrick
import org.catrobat.catroid.ui.recyclerview.adapter.BrickAdapter
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`


@RunWith(JUnit4::class)
class ElseBrickDragAndDropTest {
    lateinit var adapter: BrickAdapter
    lateinit var script: Script
    lateinit var sprite: Sprite
    var ifLogicBeginBrick = IfLogicBeginBrick()
    var setXBrick = SetXBrick()

    @Before
    fun setUp() {
        script = StartScript()

        val scripts: MutableList<Script> = ArrayList()
        scripts.add(script)

        sprite = mock(Sprite::class.java)
        `when`(sprite.scriptList).thenReturn(scripts)
    }

    @Test
    fun testDragDownElseBrick() {
        ifLogicBeginBrick.addBrickToElseBranch(setXBrick)
        script.addBrick(ifLogicBeginBrick)
        adapter = BrickAdapter(sprite)

        val elseBrick = adapter.items[2]

        assertTrue(adapter.onItemMove(2, 3))
        adapter.moveItemTo(3, elseBrick)

        assertTrue(ifLogicBeginBrick.secondaryNestedBricks.isEmpty())
        assertTrue(ifLogicBeginBrick.nestedBricks.contains(setXBrick))
        assertTrue(adapter.items[3] == elseBrick)
    }

    @Test
    fun testDragUpElseBrick() {
        ifLogicBeginBrick.addBrickToIfBranch(setXBrick)
        script.addBrick(ifLogicBeginBrick)
        adapter = BrickAdapter(sprite)

        val elseBrick = adapter.items[3]

        assertTrue(adapter.onItemMove(3, 2))
        adapter.moveItemTo(2, elseBrick)

        assertTrue(ifLogicBeginBrick.nestedBricks.isEmpty())
        assertTrue(ifLogicBeginBrick.secondaryNestedBricks.contains(setXBrick))
        assertTrue(adapter.items[2] == elseBrick)
    }

    @Test
    fun testDragElseBrickBelowEndBrick() {
        ifLogicBeginBrick.addBrickToElseBranch(setXBrick)
        script.addBrick(ifLogicBeginBrick)
        adapter = BrickAdapter(sprite)

        val elseBrick = adapter.items[2]

        assertTrue(adapter.onItemMove(2, 3))
        assertTrue(adapter.onItemMove(3, 4))

        adapter.moveItemTo(4, elseBrick)

        assertTrue(ifLogicBeginBrick.nestedBricks.isEmpty())
        assertTrue(ifLogicBeginBrick.secondaryNestedBricks.contains(setXBrick))
        assertTrue(adapter.items[2] == elseBrick)
    }

    @Test
    fun testDragElseBrickAboveIfLogicBeginBrick() {
        ifLogicBeginBrick.addBrickToIfBranch(setXBrick)
        script.addBrick(ifLogicBeginBrick)
        adapter = BrickAdapter(sprite)

        val elseBrick = adapter.items[3]

        assertTrue(adapter.onItemMove(3, 2))
        assertTrue(adapter.onItemMove(2, 1))

        adapter.moveItemTo(1, elseBrick)

        assertTrue(ifLogicBeginBrick.secondaryNestedBricks.isEmpty())
        assertTrue(ifLogicBeginBrick.nestedBricks.contains(setXBrick))
        assertTrue(adapter.items[3] == elseBrick)
    }

    @Test
    fun testDragElseBrickIntoAnotherEnclosure() {
        val nestedIfLogicBeginBrick = IfLogicBeginBrick()
        ifLogicBeginBrick.addBrickToIfBranch(nestedIfLogicBeginBrick)
        script.addBrick(ifLogicBeginBrick)
        adapter = BrickAdapter(sprite)

        val elseBrick = adapter.items[5]

        assertTrue(adapter.onItemMove(5, 4))

        adapter.moveItemTo(4, elseBrick)

        assertTrue(ifLogicBeginBrick.secondaryNestedBricks.isEmpty())
        assertTrue(ifLogicBeginBrick.nestedBricks.contains(nestedIfLogicBeginBrick))
        assertTrue(adapter.items[5] == elseBrick)
    }
}