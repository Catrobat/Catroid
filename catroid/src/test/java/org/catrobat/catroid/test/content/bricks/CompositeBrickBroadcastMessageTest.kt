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

import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Scene
import org.catrobat.catroid.content.Script
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.WhenScript
import org.catrobat.catroid.content.bricks.BroadcastBrick
import org.catrobat.catroid.content.bricks.CompositeBrick
import org.catrobat.catroid.content.bricks.ForeverBrick
import org.catrobat.catroid.content.bricks.IfThenLogicBeginBrick
import org.catrobat.catroid.content.bricks.RepeatBrick
import org.catrobat.catroid.content.bricks.RepeatUntilBrick
import org.catrobat.catroid.test.StaticSingletonInitializer.Companion.initializeStaticSingletonMethods
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.util.Arrays

@RunWith(Parameterized::class)
class CompositeBrickBroadcastMessageTest {
    @JvmField
    @Parameterized.Parameter
    var name: String? = null
    @JvmField
    @Parameterized.Parameter(1)
    var compositeBrickClass: Class<CompositeBrick>? = null
    private var scene: Scene? = null
    @Before
    @Throws(IllegalAccessException::class, InstantiationException::class)
    fun setUp() {
        initializeStaticSingletonMethods()
        val project = Project()
        scene = Scene()
        val sprite = Sprite()
        val script: Script = WhenScript()
        val compositeBrick = compositeBrickClass!!.newInstance()
        val primaryListBroadcastBrick = BroadcastBrick()
        primaryListBroadcastBrick.broadcastMessage =
            MESSAGETEXT
        project.addScene(scene)
        scene!!.addSprite(sprite)
        sprite.addScript(script)
        script.addBrick(compositeBrick)
        compositeBrick.nestedBricks.add(primaryListBroadcastBrick)
        ProjectManager.getInstance().currentProject = project
    }

    @Test
    fun testCorrectBroadcastMessages() {
        val usedMessages = scene!!.broadcastMessagesInUse
        Assert.assertTrue(usedMessages.contains(MESSAGETEXT) && usedMessages.size == 1)
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

        private const val MESSAGETEXT = "Test"
    }
}