/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2024 The Catrobat Team
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

package org.catrobat.catroid.test.io.catrobatlanguage.parser

import org.catrobat.catroid.CatroidApplication
import org.catrobat.catroid.content.bricks.SetVariableBrick
import org.catrobat.catroid.io.catlang.parser.project.CatrobatLanguageParser
import org.catrobat.catroid.io.catlang.parser.project.error.CatrobatLanguageParsingException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

@Suppress("LargeClass")
class SpinnerParserTest {
    private val serializedProgram = "#! Catrobat Language Version 0.1\n" +
        "Program 'SpinnerSerializationTest' {\n" +
        "  Metadata {\n" +
        "    Description: '',\n" +
        "    Catrobat version: '1.12',\n" +
        "    Catrobat app version: '1.1.2'\n" +
        "  }\n" +
        "\n" +
        "  Stage {\n" +
        "    Landscape mode: 'false',\n" +
        "    Width: '1080',\n" +
        "    Height: '2154',\n" +
        "    Display mode: 'STRETCH'\n" +
        "  }\n" +
        "\n" +
        "  Globals {\n" +
        "    \"var1\",\n" +
        "    \"var2\",\n" +
        "    \"var3\",\n" +
        "    *list1*,\n" +
        "    *list2*,\n" +
        "    *list3*\n" +
        "  }\n" +
        "\n" +
        "  Scene 'Scene' {\n" +
        "    Background {\n" +
        "    }\n" +
        "    Actor or object 'testSprite' {\n" +
        "      Scripts {\n" +
        "        When scene starts {\n" +
        "          #BRICK_PLACEHOLDER#\n" +
        "        }\n" +
        "      }\n" +
        "    }\n" +
        "    Actor or object 'testSprite1' {\n" +
        "    }\n" +
        "    Actor or object 'testSprite2' {\n" +
        "    }\n" +
        "    Actor or object 'testSprite3' {\n" +
        "    }\n" +
        "  }\n" +
        "  Scene 's1' {\n" +
        "    Background {\n" +
        "    }\n" +
        "    Actor or object 'My actor or object (1)' {\n" +
        "    }\n" +
        "  }\n" +
        "  Scene 's2' {\n" +
        "    Background {\n" +
        "    }\n" +
        "  }\n" +
        "  Scene 's3' {\n" +
        "    Background {\n" +
        "    }\n" +
        "  }\n" +
        "}\n"

    @Test
    fun setVariableOkTest() {
        val brick = "Set (variable: (\"var1\"), value: (1));"
        executeUserVariableWithFormulaBrickTest(brick, SetVariableBrick::class.qualifiedName!!, "var1")
    }
    @Test
    fun setVariableEmptyBrick() {
        val brick = "Set (variable: (), value: (1));"
        executeEmptyUserVariableWithFormulaBrickTest(brick, SetVariableBrick::class.qualifiedName!!)
    }
    @Test
    fun setVariableBrickInvalid() {
        val brick = "Set (variable: (\"abc\"), value: (1));"
        executeInvalidUserVariableWithFormulaBrickTest(brick, "variable")
    }

    fun executeUserVariableWithFormulaBrickTest(serializedBrick: String, expectedBrickType: String, expectedVariableName: String) {
        val projectWithBrick = serializedProgram.replace("#BRICK_PLACEHOLDER#", serializedBrick)
        val project = CatrobatLanguageParser.parseProgramFromString(projectWithBrick, CatroidApplication.getAppContext())
        assert(project != null)
        val deserializedBrick = project!!.sceneList[0].spriteList[1].scriptList[0].brickList[0]
        assert (deserializedBrick.javaClass.name == expectedBrickType)
        val userVariable = (deserializedBrick as SetVariableBrick).userVariable
        assertNotNull(userVariable)
        assert(userVariable.name == expectedVariableName)
    }

    fun executeEmptyUserVariableWithFormulaBrickTest(serializedBrick: String, expectedBrickType: String) {
        val projectWithBrick = serializedProgram.replace("#BRICK_PLACEHOLDER#", serializedBrick)
        val project = CatrobatLanguageParser.parseProgramFromString(projectWithBrick, CatroidApplication.getAppContext())
        assert(project != null)
        val deserializedBrick = project!!.sceneList[0].spriteList[1].scriptList[0].brickList[0]
        assert (deserializedBrick.javaClass.name == expectedBrickType)
        val userVariable = (deserializedBrick as SetVariableBrick).userVariable
        assertNull(userVariable)
    }

    fun executeInvalidUserVariableWithFormulaBrickTest(serializedBrick: String, parameterName: String) {
        try {
            val projectWithBrick = serializedProgram.replace("#BRICK_PLACEHOLDER#", serializedBrick)
            CatrobatLanguageParser.parseProgramFromString(projectWithBrick, CatroidApplication.getAppContext())
            assert(false)
        } catch (exception: CatrobatLanguageParsingException) {
            assertEquals("Unkown variable abc in parameter $parameterName", exception.message)
        } catch (exception: Exception) {
            assert(false)
        }
    }
}