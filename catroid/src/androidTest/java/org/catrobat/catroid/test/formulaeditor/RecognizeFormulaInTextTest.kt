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
package org.catrobat.catroid.test.formulaeditor

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import junit.framework.Assert
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Scene
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.UserDefinedScript
import org.catrobat.catroid.content.bricks.FormulaBrick
import org.catrobat.catroid.content.bricks.UserDefinedBrick
import org.catrobat.catroid.content.bricks.UserDefinedReceiverBrick
import org.catrobat.catroid.formulaeditor.UserList
import org.catrobat.catroid.formulaeditor.UserVariable
import org.catrobat.catroid.test.utils.TestUtils
import org.catrobat.catroid.ui.fragment.FormulaEditorFragment
import org.catrobat.catroid.userbrick.UserDefinedBrickData
import org.catrobat.catroid.userbrick.UserDefinedBrickInput
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.Spy
import java.io.IOException
import java.util.Arrays

@RunWith(Parameterized::class)
class RecognizeFormulaInTextTest {
    @JvmField
    @Parameterized.Parameter
    var name: String? = null
    @JvmField
    @Parameterized.Parameter(1)
    var string: String? = null
    @JvmField
    @Parameterized.Parameter(2)
    var expectedResult = false
    var context: Context? = null
    var project: Project? = null
    var sprite: Sprite? = null
    var userVariable = UserVariable("variable")
    var userList = UserList("list", Arrays.asList(*arrayOf<Any>("a", "b", "c")))

    @Spy
    private val formulaEditorFragmentMock: FormulaEditorFragment? = null

    @Spy
    private val formulaBrick: FormulaBrick? = null
    @Before
    @Throws(Exception::class)
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        val userDefinedScript = UserDefinedScript()
        val userDefinedBrick =
            UserDefinedBrick(listOf<UserDefinedBrickData>(UserDefinedBrickInput("userDefinedBrickInput")))
        userDefinedScript.scriptBrick = UserDefinedReceiverBrick(userDefinedBrick)
        Mockito.doReturn(formulaBrick).`when`(formulaEditorFragmentMock)?.formulaBrick
        Mockito.doReturn(userDefinedScript).`when`(formulaBrick)?.script
        context = InstrumentationRegistry.getInstrumentation().targetContext
        project = Project(context, RecognizeFormulaInTextTest::class.java.simpleName)
        sprite = Sprite()
        val scene = Scene()
        scene.addSprite(sprite)
        sprite!!.addUserVariable(userVariable)
        sprite!!.addUserList(userList)
        project!!.addScene(scene)
    }

    @After
    @Throws(IOException::class)
    fun tearDown() {
        TestUtils.deleteProjects(RecognizeFormulaInTextTest::class.java.simpleName)
    }

    @Test
    fun testEditFormula() {
        Assert.assertEquals(
            formulaEditorFragmentMock!!.recognizedFormulaInText(
                string,
                context,
                project,
                sprite
            ), expectedResult
        )
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun data(): Iterable<Array<Any>> {
            return Arrays.asList(
                *arrayOf(
                    arrayOf("NoFormula", "This is just text", false),
                    arrayOf("WithFormula", "sine(20)", true),
                    arrayOf(
                        "WithCompoundFormula",
                        "random value from to( modulo (20,4), 7)",
                        true
                    ),
                    arrayOf("FormulaWithoutParams", "inclination x", true),
                    arrayOf("FormulaWithUserVariable", "variable + 4", true),
                    arrayOf("FormulaWithUserList", "list", true),
                    arrayOf(
                        "FormulaWithUserDefinedBrickInput",
                        "userDefinedBrickInput + 3",
                        true
                    )
                )
            )
        }
    }
}
