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

import android.content.res.Configuration
import org.catrobat.catroid.CatroidApplication
import org.catrobat.catroid.content.bricks.Brick
import org.catrobat.catroid.content.bricks.BroadcastReceiverBrick
import org.catrobat.catroid.content.bricks.WhenBackgroundChangesBrick
import org.catrobat.catroid.content.bricks.WhenBounceOffBrick
import org.catrobat.catroid.content.bricks.WhenBrick
import org.catrobat.catroid.content.bricks.WhenClonedBrick
import org.catrobat.catroid.content.bricks.WhenConditionBrick
import org.catrobat.catroid.content.bricks.WhenNfcBrick
import org.catrobat.catroid.content.bricks.WhenRaspiPinChangedBrick
import org.catrobat.catroid.content.bricks.WhenStartedBrick
import org.catrobat.catroid.content.bricks.WhenTouchDownBrick
import org.catrobat.catroid.io.catlang.parser.project.CatrobatLanguageParser
import org.catrobat.catroid.io.catlang.serializer.CatrobatLanguageProjectSerializer
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Locale

@Suppress("LargeClass")
class ScriptParsingTest {

    @Test
    fun testWhenBrick() {
        val inputBrickFormat = "When tapped {"
        val inputValues = listOf(
            listOf<String>(),
        )
        executeTest(inputBrickFormat, inputValues, WhenBrick())
    }

    @Test
    fun testWhenStartedBrick() {
        val inputBrickFormat = "When scene starts {"
        val inputValues = listOf(
            listOf<String>(),
        )
        executeTest(inputBrickFormat, inputValues, WhenStartedBrick())
    }

    @Test
    fun testWhenTouchDownBrick() {
        val inputBrickFormat = "When stage is tapped {"
        val inputValues = listOf(
            listOf<String>(),
        )
        executeTest(inputBrickFormat, inputValues, WhenTouchDownBrick())
    }

    @Test
    fun testWhenClonedBrick() {
        val inputBrickFormat = "When you start as a clone {"
        val inputValues = listOf(
            listOf<String>(),
        )
        executeTest(inputBrickFormat, inputValues, WhenClonedBrick())
    }

    @Test
    fun testWhenRaspiPinChangedBrick() {
        val inputBrickFormat = "When Raspberry Pi pin changes to (pin: (#PARAM_0#), position: (#PARAM_1#)) {"
        val inputValues = listOf(
            listOf("3", "high"),
            listOf("5", "low")
        )
        executeTest(inputBrickFormat, inputValues, WhenRaspiPinChangedBrick())
    }

    @Test
    fun testBroadcastReceiverBrick() {
        val inputBrickFormat = "When you receive (message: (#PARAM_0#)) {"
        val inputValues = listOf(
            listOf("'test broadcast message :)'"),
        )
        executeTest(inputBrickFormat, inputValues, BroadcastReceiverBrick())
    }

    @Test
    fun testWhenConditionBrick() {
        val inputBrickFormat = "When condition becomes true (condition: (#PARAM_0#)) {"
        val inputValues = listOf(
            listOf("1 < 2"),
            listOf("\"var1\" > 7"),
            listOf("true")
        )
        executeTest(inputBrickFormat, inputValues, WhenConditionBrick())
    }

    @Test
    fun testWhenBounceOffBrick() {
        val inputBrickFormat = "When you bounce off (actor or object: (#PARAM_0#)) {"
        val inputValues = listOf(
            listOf("any edge, actor, or object"),
            listOf("'testSprite1'"),
            listOf("'testSprite2'")
        )
        executeTest(inputBrickFormat, inputValues, WhenBounceOffBrick())
    }

    @Test
    fun testWhenBackgroundChangesBrick() {
        val inputBrickFormat = "When background changes to (look: (#PARAM_0#)) {"
        val inputValues = listOf(
            listOf("'testSprite'"),
            listOf("")
        )
        executeTest(inputBrickFormat, inputValues, WhenBackgroundChangesBrick())
    }

    @Test
    fun testWhenNfcBrick() {
        val inputBrickFormat = "When NFC gets scanned (nfc tag: (#PARAM_0#)) {"
        val inputValues = listOf(
            listOf("all")
        )
        executeTest(inputBrickFormat, inputValues, WhenNfcBrick())
    }

    private val serializedProgram = """#! Catrobat Language Version 0.1
Program 'Script Parser Test' {
  Metadata {
    Description: '',
    Catrobat version: '1.12',
    Catrobat app version: '1.1.2'
  }

  Stage {
    Landscape mode: 'false',
    Width: '1080',
    Height: '2154',
    Display mode: 'STRETCH'
  }

  Globals {
    "var1",
    "var2",
    "var3",
    *list1*,
    *list2*,
    *list3*
  }

  Multiplayer variables {
    "multiplayerVar1",
    "multiplayerVar2",
    "multiplayerVar3"
  }

  Scene 'Scene' {
    Background {
    }
    Actor or object 'testSprite' {
      Looks {
        'testSprite': 'testSprite.png'
      }
      Sounds {
        'record': 'record'
      }
      Locals {
        "localVar1",
        "localVar2",
        "localVar3",
        *localList1*,
        *localList2*,
        *localList3*
      }
      Scripts {
        #SCRIPT_PLACEHOLDER#
        }
      }
    }
    Actor or object 'testSprite1' {
    }
    Actor or object 'testSprite2' {
    }
    Actor or object 'testSprite3' {
    }
  }
  Scene 's1' {
    Background {
    }
    Actor or object 'My actor or object (1)' {
    }
  }
  Scene 's2' {
    Background {
    }
  }
  Scene 's3' {
    Background {
    }
  }
}
"""

    private fun executeTest(
        inputBrickFormat: String,
        inputValues: List<List<String>>,
        expectedBrickType: Brick
    ) {
        val locales = listOf(Locale.ROOT, Locale.GERMAN, Locale.CHINA)
        for (locale in locales) {
            executeLocalizedTest(inputBrickFormat, inputValues, expectedBrickType, locale)
        }
    }

    private fun executeLocalizedTest(
        inputScriptFormat: String,
        inputValues: List<List<String>>,
        expectedScriptBrickType: Brick,
        locale: Locale
    ) {
        for (testIndex in inputValues.indices) {
            var inputBrickString = inputScriptFormat
            for (valueIndex in inputValues[testIndex].indices) {
                inputBrickString = inputBrickString.replace("#PARAM_$valueIndex#", inputValues[testIndex][valueIndex])
            }
            var expectedBrickString = inputScriptFormat
            for (valueIndex in inputValues[testIndex].indices) {
                expectedBrickString = expectedBrickString.replace("#PARAM_$valueIndex#", inputValues[testIndex][valueIndex])
            }
            val programString = serializedProgram.replace("#SCRIPT_PLACEHOLDER#", inputBrickString)
            val expectedProgram = serializedProgram.replace("#SCRIPT_PLACEHOLDER#", expectedBrickString)
            val context = CatroidApplication.getAppContext()
            var configuration = context.resources.configuration
            configuration = Configuration(configuration)
            configuration.setLocale(locale)
            context.createConfigurationContext(configuration)
            val parsedProgram = CatrobatLanguageParser.parseProgramFromString(programString, context)
            val parsedScriptBrick = parsedProgram!!.sceneList[0].spriteList[1].scriptList[0].scriptBrick
            assertEquals(expectedScriptBrickType::class.java, parsedScriptBrick::class.java)
            val serializedProgram = CatrobatLanguageProjectSerializer(parsedProgram, context).serialize()
            assertEquals(expectedProgram, serializedProgram)
        }
    }
}
