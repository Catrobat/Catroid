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

import junit.framework.Assert
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import kotlin.Throws
import org.catrobat.catroid.content.bricks.ScriptBrick
import org.hamcrest.CoreMatchers
import org.catrobat.catroid.content.BroadcastScript
import org.catrobat.catroid.content.WhenBounceOffScript
import org.catrobat.catroid.content.RaspiInterruptScript
import org.catrobat.catroid.content.Script
import org.catrobat.catroid.content.StartScript
import org.catrobat.catroid.content.WhenBackgroundChangesScript
import org.catrobat.catroid.content.WhenClonedScript
import org.catrobat.catroid.content.WhenConditionScript
import org.catrobat.catroid.content.WhenGamepadButtonScript
import org.catrobat.catroid.content.WhenNfcScript
import org.catrobat.catroid.content.WhenScript
import org.catrobat.catroid.content.WhenTouchDownScript
import org.catrobat.catroid.content.UserDefinedScript
import org.junit.Test
import java.util.Arrays

@RunWith(Parameterized::class)
class CloneScriptBrickTest {
    @JvmField
    @Parameterized.Parameter
    var name: String? = null
    @JvmField
    @Parameterized.Parameter(1)
    var script: Script? = null
    @Test
    @Throws(CloneNotSupportedException::class)
    fun testScriptIsClonedWithScriptBrick() {
        val brick = script!!.scriptBrick
        val clone = brick.clone() as ScriptBrick
        Assert.assertNotSame(clone, brick)
        val cloneScript = clone.script
        org.junit.Assert.assertNotNull(cloneScript)
        val originalScript = brick.script
        org.junit.Assert.assertThat(
            cloneScript,
            CoreMatchers.`is`(CoreMatchers.instanceOf(originalScript.javaClass))
        )
        Assert.assertNotSame(cloneScript, originalScript)
        Assert.assertSame(clone.script.scriptBrick, clone)
        Assert.assertSame(brick.script.scriptBrick, brick)
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun data(): Iterable<Array<Any>> {
            return Arrays.asList(
                *arrayOf(
                    arrayOf(
                        BroadcastScript::class.java.simpleName, BroadcastScript("test")
                    ), arrayOf(
                        WhenBounceOffScript::class.java.simpleName, WhenBounceOffScript("test")
                    ), arrayOf(
                        RaspiInterruptScript::class.java.simpleName,
                        RaspiInterruptScript("testPin", "testEvent")
                    ), arrayOf(
                        StartScript::class.java.simpleName, StartScript()
                    ), arrayOf(
                        WhenBackgroundChangesScript::class.java.simpleName,
                        WhenBackgroundChangesScript()
                    ), arrayOf(
                        WhenClonedScript::class.java.simpleName, WhenClonedScript()
                    ), arrayOf(
                        WhenConditionScript::class.java.simpleName, WhenConditionScript()
                    ), arrayOf(
                        WhenGamepadButtonScript::class.java.simpleName,
                        WhenGamepadButtonScript("testAction")
                    ), arrayOf(
                        WhenNfcScript::class.java.simpleName, WhenNfcScript()
                    ), arrayOf(
                        WhenScript::class.java.simpleName, WhenScript()
                    ), arrayOf(
                        WhenTouchDownScript::class.java.simpleName, WhenTouchDownScript()
                    ), arrayOf(
                        UserDefinedScript::class.java.simpleName, UserDefinedScript()
                    )
                )
            )
        }
    }
}
