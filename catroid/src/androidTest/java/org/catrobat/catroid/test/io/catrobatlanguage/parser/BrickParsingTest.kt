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

import android.content.Context
import android.content.res.Configuration
import org.catrobat.catroid.CatroidApplication
import org.catrobat.catroid.content.bricks.Brick
import org.catrobat.catroid.content.bricks.ReadListFromDeviceBrick
import org.catrobat.catroid.content.bricks.*
import org.catrobat.catroid.content.bricks.WebRequestBrick
import org.catrobat.catroid.io.catlang.parser.project.CatrobatLanguageParser
import org.catrobat.catroid.io.catlang.serializer.CatrobatLanguageProjectSerializer
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Locale

@Suppress("LargeClass")
class BrickParsingTest {
    private val brickIndention = "          ";

    @Test
    fun testWaitBrick() {
        val inputBrickFormat = "Wait (seconds: (#PARAM_0#));"
        val inputValues = listOf(
            listOf("1"),
            listOf("random value from to(0, random value from to(10, 20))"),
            listOf("modulo(\"var1\", 20)"),
        )
        val expectedValues = listOf(
            listOf("1"),
            listOf("random value from to( 0 , random value from to( 10 , 20 ) )"),
            listOf("modulo( \"var1\" , 20 )"),
        )
        executeTest(inputBrickFormat, inputValues, WaitBrick(), null, expectedValues)
    }
    @Test
    fun testLegoNxtMotorTurnAngleBrick() {
        val inputBrickFormat = "Turn NXT (motor: (#PARAM_0#), degrees: (#PARAM_1#));"
        val inputValues = listOf(
            listOf("A", "180"),
            listOf("B", "modulo(\"var1\", 180)"),
            listOf("C", "if then else(true, 90, 180)"),
            listOf("B+C", "item(\"var2\", *list1*)"),
        )
        val expectedValues = listOf(
            listOf("A", "180"),
            listOf("B", "modulo( \"var1\" , 180 )"),
            listOf("C", "if then else( true , 90 , 180 )"),
            listOf("B+C", "item( \"var2\" , *list1* )"),
        )
        executeTest(inputBrickFormat, inputValues, LegoNxtMotorTurnAngleBrick(), null, expectedValues)
    }
    @Test
    fun testLegoNxtMotorStopBrick() {
        val inputBrickFormat = "Stop NXT (motor: (#PARAM_0#));"
        val inputValues = listOf(
            listOf("A"),
            listOf("B"),
            listOf("C"),
            listOf("B+C"),
            listOf("all")
        )
        executeTest(inputBrickFormat, inputValues, LegoNxtMotorStopBrick(), null, null)
    }
    @Test
    fun testLegoNxtMotorMoveBrick() {
        val inputBrickFormat = "Set NXT (motor: (#PARAM_0#), speed percentage: (#PARAM_1#));"
        val inputValues = listOf(
            listOf("A", "100"),
            listOf("B", "100"),
            listOf("C", "100"),
            listOf("B+C", "100")
        )
        executeTest(inputBrickFormat, inputValues, LegoNxtMotorMoveBrick())
    }
    @Test
    fun testLegoNxtPlayToneBrick() {
        val inputBrickFormat = "Play NXT tone (seconds: (#PARAM_0#), frequency x100Hz: (#PARAM_1#));"
        val inputValues = listOf(
            listOf("phiro front left sensor", "phiro front right sensor"),
            listOf("phiro side left sensor", "phiro side right sensor"),
            listOf("phiro bottom left sensor", "phiro bottom right sensor")
        )
        executeTest(inputBrickFormat, inputValues, LegoNxtPlayToneBrick())
    }
    @Test
    fun testLegoEv3MotorTurnAngleBrick() {
        val inputBrickFormat = "Turn EV3 (motor: (#PARAM_0#), degrees: (#PARAM_1#));"
        val inputValues = listOf(
            listOf("A", "1"),
            listOf("B", "2"),
            listOf("C", "3"),
            listOf("D", "4"),
            listOf("B+C", "5"),
            listOf("all", "180")
        )
        executeTest(inputBrickFormat, inputValues, LegoEv3MotorTurnAngleBrick())
    }
    @Test
    fun testLegoEv3MotorMoveBrick() {
        val inputBrickFormat = "Set EV3 (motor: (#PARAM_0#), speed percentage: (#PARAM_1#));"
        val inputValues = listOf(
            listOf("A", "year"),
            listOf("B", "month"),
            listOf("C", "day"),
            listOf("D", "hour"),
            listOf("B+C", "minute")
        )
        executeTest(inputBrickFormat, inputValues, LegoEv3MotorMoveBrick())
    }
    @Test
    fun testLegoEv3MotorStopBrick() {
        val inputBrickFormat = "Stop EV3 (motor: (#PARAM_0#));"
        val inputValues = listOf(
            listOf("A",),
            listOf("B"),
            listOf("C"),
            listOf("D"),
            listOf("B+C"),
            listOf("all")
        )
        executeTest(inputBrickFormat, inputValues, LegoEv3MotorStopBrick())
    }
    @Test
    fun testLegoEv3PlayToneBrick() {
        val inputBrickFormat = "Play EV3 tone (seconds: (#PARAM_0#), frequency x100Hz: (#PARAM_1#), volume: (#PARAM_2#));"
        val inputValues = listOf(
            listOf("1", "2", "100")
        )
        executeTest(inputBrickFormat, inputValues, LegoEv3PlayToneBrick())
    }
    @Test
    fun testLegoEv3SetLedBrick() {
        val inputBrickFormat = "Set EV3 LED (status: (#PARAM_0#));"
        val inputValues = listOf(
            listOf("off"),
            listOf("green"),
            listOf("red"),
            listOf("orange"),
            listOf("green flashing"),
            listOf("red flashing"),
            listOf("orange flashing"),
            listOf("green pulse"),
            listOf("red pulse"),
            listOf("orange pulse")
        )
        executeTest(inputBrickFormat, inputValues, LegoEv3SetLedBrick())
    }
    @Test
    fun testArduinoSendDigitalValueBrick() {
        val inputBrickFormat = "Set Arduino (digital pin: (#PARAM_0#), value: (#PARAM_1#));"
        val inputValues = listOf(
            listOf("13", "1")
        )
        executeTest(inputBrickFormat, inputValues, ArduinoSendDigitalValueBrick())
    }
    @Test
    fun testArduinoSendPWMValueBrick() {
        val inputBrickFormat = "Set Arduino (PWM~ pin: (#PARAM_0#), value: (#PARAM_1#));"
        val inputValues = listOf(
            listOf("3", "255")
        )
        executeTest(inputBrickFormat, inputValues, ArduinoSendPWMValueBrick())
    }
    @Test
    fun testRaspiSendDigitalValueBrick() {
        val inputBrickFormat = "Set (Raspberry Pi pin: (#PARAM_0#), value: (#PARAM_1#));"
        val inputValues = listOf(
            listOf("3", "1")
        )
        executeTest(inputBrickFormat, inputValues, RaspiSendDigitalValueBrick())
    }
    @Test
    fun testRaspiPwmBrick() {
        val inputBrickFormat = "Set (Raspberry Pi PWM~ pin: (#PARAM_0#), percentage: (#PARAM_1#), Hz: (#PARAM_2#));"
        val inputValues = listOf(
            listOf("3", "50", "100")
        )
        executeTest(inputBrickFormat, inputValues, RaspiPwmBrick())
    }
    @Test
    fun testPhiroMotorMoveForwardBrick() {
        val inputBrickFormat = "Move Phiro (motor: (#PARAM_0#), direction: (#PARAM_1#), speed percentage: (#PARAM_2#));"
        val inputValues = listOf(
            listOf("left", "forward", "100"),
            listOf("right", "forward", "100"),
            listOf("both", "forward", "100")
        )
        executeTest(inputBrickFormat, inputValues, PhiroMotorMoveForwardBrick())
    }
    @Test
    fun testPhiroMotorMoveBackwardBrick() {
        val inputBrickFormat = "Move Phiro (motor: (#PARAM_0#), direction: (#PARAM_1#), speed percentage: (#PARAM_2#));"
        val inputValues = listOf(
            listOf("left", "backward", "100"),
            listOf("right", "backward", "100"),
            listOf("both", "backward", "100")
        )
        executeTest(inputBrickFormat, inputValues, PhiroMotorMoveBackwardBrick())
    }
    @Test
    fun testPhiroMotorStopBrick() {
        val inputBrickFormat = "Stop Phiro (motor: (#PARAM_0#));"
        val inputValues = listOf(
            listOf("left"),
            listOf("right"),
            listOf("both")
        )
        executeTest(inputBrickFormat, inputValues, PhiroMotorStopBrick())
    }
    @Test
    fun testPhiroPlayToneBrick() {
        val inputBrickFormat = "Play Phiro (tone: (#PARAM_0#), seconds: (#PARAM_1#));"
        val inputValues = listOf(
            listOf("do", "1"),
            listOf("re", "1"),
            listOf("mi", "1"),
            listOf("fa", "1"),
            listOf("so", "1"),
            listOf("la", "1"),
            listOf("ti", "1")
        )
        executeTest(inputBrickFormat, inputValues, PhiroPlayToneBrick())
    }
    @Test
    fun testPhiroRGBLightBrick() {
        val inputBrickFormat = "Set Phiro (light: (#PARAM_0#), red: (#PARAM_1#), green: (#PARAM_2#), blue: (#PARAM_3#));"
        val inputValues = listOf(
            listOf("left", "255", "255", "255"),
            listOf("right", "255", "255", "255"),
            listOf("both", "255", "255", "255")
        )
        executeTest(inputBrickFormat, inputValues, PhiroRGBLightBrick())
    }
    @Test
    fun testSetVariableBrick() {
        val inputBrickFormat = "Set (variable: (#PARAM_0#), value: (#PARAM_1#));"
        val inputValues = listOf(
            listOf("\"var1\"", "1"),
            listOf("\"var2\"", "2"),
            listOf("\"var3\"", "3"),
            listOf("\"localVar1\"", "\"var1\""),
            listOf("\"localVar2\"", "\"var2\""),
            listOf("\"localVar3\"", "\"var3\""),
            listOf("\"multiplayerVar1\"", "1"),
            listOf("\"multiplayerVar2\"", "2"),
            listOf("\"multiplayerVar3\"", "3"),
        )
        executeTest(inputBrickFormat, inputValues, SetVariableBrick())
    }
    @Test
    fun testBroadcastBrick() {
        val inputBrickFormat = "Broadcast (message: (#PARAM_0#));"
        val inputValues = listOf(
            listOf("'message 1'")
        )
        executeTest(inputBrickFormat, inputValues, BroadcastBrick())
    }
    @Test
    fun testBroadcastWaitBrick() {
        val inputBrickFormat = "Broadcast and wait (message: (#PARAM_0#));"
        val inputValues = listOf(
            listOf("'message 1'")
        )
        executeTest(inputBrickFormat, inputValues, BroadcastWaitBrick())
    }
    @Test
    fun testCloneBrick() {
        val inputBrickFormat = "Create clone of (actor or object: (#PARAM_0#));"
        val inputValues = listOf(
            listOf("yourself"),
            listOf("'testSprite2'"),
        )
        executeTest(inputBrickFormat, inputValues, CloneBrick())
    }
    @Test
    fun testDeleteThisCloneBrick() {
        val inputBrickFormat = "Delete this clone;"
        val inputValues = listOf(
            listOf<String>()
        )
        executeTest(inputBrickFormat, inputValues, DeleteThisCloneBrick())
    }
    @Test
    fun testNoteBrick() {
        val inputBrickFormat = "# add comment here…"
        val inputValues = listOf(
            listOf<String>()
        )
        executeTest(inputBrickFormat, inputValues, NoteBrick())
    }
    @Test
    fun testSceneTransitionBrick() {
        val inputBrickFormat = "Continue (scene: (#PARAM_0#));"
        val inputValues = listOf(
            listOf("'s1'"),
            listOf("'Scene'"),
            listOf("'s2'")
        )
        executeTest(inputBrickFormat, inputValues, SceneTransitionBrick())
    }
    @Test
    fun testSceneStartBrick() {
        val inputBrickFormat = "Start (scene: (#PARAM_0#));"
        val inputValues = listOf(
            listOf("'s1'"),
            listOf("'Scene'"),
            listOf("'s2'")
        )
        executeTest(inputBrickFormat, inputValues, SceneStartBrick())
    }
    @Test
    fun testExitStageBrick() {
        val inputBrickFormat = "Finish stage;"
        val inputValues = listOf(
            listOf<String>()
        )
        executeTest(inputBrickFormat, inputValues, ExitStageBrick())
    }
    @Test
    fun testStopScriptBrick() {
        val inputBrickFormat = "Stop (script: (#PARAM_0#));"
        val inputValues = listOf(
            listOf("this script"),
            listOf("all scripts"),
            listOf("other scripts of this actor or object")
        )
        executeTest(inputBrickFormat, inputValues, StopScriptBrick())
    }
    @Test
    fun testWaitTillIdleBrick() {
        val inputBrickFormat = "Wait until all other scripts have stopped;"
        val inputValues = listOf(
            listOf<String>()
        )
        executeTest(inputBrickFormat, inputValues, WaitTillIdleBrick())
    }
    @Test
    fun testSetNfcTagBrick() {
        val inputBrickFormat = "Set next NFC tag (text: (#PARAM_0#));"
        val inputValues = listOf(
            listOf("'www.catrobat.org'")
        )
        executeTest(inputBrickFormat, inputValues, SetNfcTagBrick())
    }
    @Test
    fun testTapAtBrick() {
        val inputBrickFormat = "Single tap at (x: (#PARAM_0#), y: (#PARAM_1#));"
        val inputValues = listOf(
            listOf("- 100", "- 200")
        )
        executeTest(inputBrickFormat, inputValues, TapAtBrick())
    }
    @Test
    fun testTapForBrick() {
        val inputBrickFormat = "Touch at position for seconds (x: (#PARAM_0#), y: (#PARAM_1#), seconds: (#PARAM_2#));"
        val inputValues = listOf(
            listOf("- 100", "- 200", "0.3")
        )
        executeTest(inputBrickFormat, inputValues, TapForBrick())
    }
    @Test
    fun testTouchAndSlideBrick() {
        val inputBrickFormat = "Touch at position and slide to position in seconds (start x: (#PARAM_0#), start y: (#PARAM_1#), to x: (#PARAM_2#), to y: (#PARAM_3#), seconds: (#PARAM_4#));"
        val inputValues = listOf(
            listOf("- 100", "- 200", "100", "200", "0.3")
        )
        executeTest(inputBrickFormat, inputValues, TouchAndSlideBrick())
    }
    @Test
    fun testOpenUrlBrick() {
        val inputBrickFormat = "Open in browser (url: (#PARAM_0#));"
        val inputValues = listOf(
            listOf("'https://catrobat.org/'")
        )
        executeTest(inputBrickFormat, inputValues, OpenUrlBrick())
    }
    @Test
    fun testPlaceAtBrick() {
        val inputBrickFormat = "Place at (x: (#PARAM_0#), y: (#PARAM_1#));"
        val inputValues = listOf(
            listOf("100", "200")
        )
        executeTest(inputBrickFormat, inputValues, PlaceAtBrick())
    }
    @Test
    fun testSetXBrick() {
        val inputBrickFormat = "Set (x: (#PARAM_0#));"
        val inputValues = listOf(
            listOf("100")
        )
        executeTest(inputBrickFormat, inputValues, SetXBrick())
    }
    @Test
    fun testSetYBrick() {
        val inputBrickFormat = "Set (y: (#PARAM_0#));"
        val inputValues = listOf(
            listOf("200")
        )
        executeTest(inputBrickFormat, inputValues, SetYBrick())
    }
    @Test
    fun testChangeXByNBrick() {
        val inputBrickFormat = "Change x by (value: (#PARAM_0#));"
        val inputValues = listOf(
            listOf("10")
        )
        executeTest(inputBrickFormat, inputValues, ChangeXByNBrick())
    }
    @Test
    fun testChangeYByNBrick() {
        val inputBrickFormat = "Change y by (value: (#PARAM_0#));"
        val inputValues = listOf(
            listOf("10")
        )
        executeTest(inputBrickFormat, inputValues, ChangeYByNBrick())
    }
    @Test
    fun testGoToBrick() {
        val inputBrickFormat = "Go to (target: (#PARAM_0#));"
        val inputValues = listOf(
            listOf("touch position"),
            listOf("random position"),
            listOf("'testSprite2'")
        )
        executeTest(inputBrickFormat, inputValues, GoToBrick())
    }
    @Test
    fun testIfOnEdgeBounceBrick() {
        val inputBrickFormat = "If on edge, bounce;"
        val inputValues = listOf(
            listOf<String>()
        )
        executeTest(inputBrickFormat, inputValues, IfOnEdgeBounceBrick())
    }
    @Test
    fun testMoveNStepsBrick() {
        val inputBrickFormat = "Move (steps: (#PARAM_0#));"
        val inputValues = listOf(
            listOf("10")
        )
        executeTest(inputBrickFormat, inputValues, MoveNStepsBrick())
    }
    @Test
    fun testTurnLeftBrick() {
        val inputBrickFormat = "Turn (direction: (#PARAM_0#), degrees: (#PARAM_1#));"
        val inputValues = listOf(
            listOf("left", "15")
        )
        executeTest(inputBrickFormat, inputValues, TurnLeftBrick())
    }
    @Test
    fun testTurnRightBrick() {
        val inputBrickFormat = "Turn (direction: (#PARAM_0#), degrees: (#PARAM_1#));"
        val inputValues = listOf(
            listOf("right", "15")
        )
        executeTest(inputBrickFormat, inputValues, TurnRightBrick())
    }
    @Test
    fun testPointInDirectionBrick() {
        val inputBrickFormat = "Point in direction (degrees: (#PARAM_0#));"
        val inputValues = listOf(
            listOf("90")
        )
        executeTest(inputBrickFormat, inputValues, PointInDirectionBrick())
    }
    @Test
    fun testPointToBrick() {
        val inputBrickFormat = "Point towards (actor or object: (#PARAM_0#));"
        val inputValues = listOf(
            listOf("'testSprite1'"),
            listOf("'testSprite2'"),
        )
        executeTest(inputBrickFormat, inputValues, PointToBrick())
    }
    @Test
    fun testSetRotationStyleBrick() {
        val inputBrickFormat = "Set (rotation style: (#PARAM_0#));"
        val inputValues = listOf(
            listOf("left-right only"),
            listOf("all-around"),
            listOf("do not rotate")
        )
        executeTest(inputBrickFormat, inputValues, SetRotationStyleBrick())
    }
    @Test
    fun testGlideToBrick() {
        val inputBrickFormat = "Glide to (x: (#PARAM_0#), y: (#PARAM_1#), seconds: (#PARAM_2#));"
        val inputValues = listOf(
            listOf("100", "200", "1")
        )
        executeTest(inputBrickFormat, inputValues, GlideToBrick())
    }
    @Test
    fun testGoNStepsBackBrick() {
        val inputBrickFormat = "Go back (number of layers: (#PARAM_0#));"
        val inputValues = listOf(
            listOf("1")
        )
        executeTest(inputBrickFormat, inputValues, GoNStepsBackBrick())
    }
    @Test
    fun testComeToFrontBrick() {
        val inputBrickFormat = "Come to front;"
        val inputValues = listOf(
            listOf<String>()
        )
        executeTest(inputBrickFormat, inputValues, ComeToFrontBrick())
    }
    @Test
    fun testSetCameraFocusPointBrick() {
        val inputBrickFormat = "Become focus point with flexibility in percent (horizontal: (#PARAM_0#), vertical: (#PARAM_1#));"
        val inputValues = listOf(
            listOf("0", "0")
        )
        executeTest(inputBrickFormat, inputValues, SetCameraFocusPointBrick())
    }
    @Test
    fun testVibrationBrick() {
        val inputBrickFormat = "Vibrate for (seconds: (#PARAM_0#));"
        val inputValues = listOf(
            listOf("1")
        )
        executeTest(inputBrickFormat, inputValues, VibrationBrick())
    }
    @Test
    fun testSetPhysicsObjectTypeBrick() {
        val inputBrickFormat = "Set (motion type: (#PARAM_0#));"
        val inputValues = listOf(
            listOf("moving and bouncing under gravity"),
            listOf("not moving under gravity, but others bounce off you under gravity"),
            listOf("not moving or bouncing under gravity (default)")
        )
        executeTest(inputBrickFormat, inputValues, SetPhysicsObjectTypeBrick())
    }
    @Test
    fun testSetVelocityBrick() {
        val inputBrickFormat = "Set velocity to (x steps/second: (#PARAM_0#), y steps/second: (#PARAM_1#));"
        val inputValues = listOf(
            listOf("0", "0")
        )
        val expectedValues = listOf(
            listOf("0", "0"),
        )
        executeTest(inputBrickFormat, inputValues, SetVelocityBrick(), null, expectedValues)
    }
    @Test
    fun testTurnLeftSpeedBrick() {
        val inputBrickFormat = "Spin (direction: (#PARAM_0#), degrees/second: (#PARAM_1#));"
        val inputValues = listOf(
            listOf("left", "15")
        )
        executeTest(inputBrickFormat, inputValues, TurnLeftSpeedBrick())
    }
    @Test
    fun testTurnRightSpeedBrick() {
        val inputBrickFormat = "Spin (direction: (#PARAM_0#), degrees/second: (#PARAM_1#));"
        val inputValues = listOf(
            listOf("right", "15")
        )
        executeTest(inputBrickFormat, inputValues, TurnRightSpeedBrick())
    }
    @Test
    fun testSetGravityBrick() {
        val inputBrickFormat = "Set gravity for all actors and objects to (x steps/second²: (#PARAM_0#), y steps/second²: (#PARAM_1#));"
        val inputValues = listOf(
            listOf("0", "- 10")
        )
        executeTest(inputBrickFormat, inputValues, SetGravityBrick())
    }
    @Test
    fun testSetMassBrick() {
        val inputBrickFormat = "Set (mass in kilograms: (#PARAM_0#));"
        val inputValues = listOf(
            listOf("1")
        )
        executeTest(inputBrickFormat, inputValues, SetMassBrick())
    }
    @Test
    fun testSetBounceBrick() {
        val inputBrickFormat = "Set (bounce factor percentage: (#PARAM_0#));"
        val inputValues = listOf(
            listOf("80")
        )
        executeTest(inputBrickFormat, inputValues, SetBounceBrick())
    }
    @Test
    fun testSetFrictionBrick() {
        val inputBrickFormat = "Set (friction percentage: (#PARAM_0#));"
        val inputValues = listOf(
            listOf("20")
        )
        executeTest(inputBrickFormat, inputValues, SetFrictionBrick())
    }
    @Test
    fun testFadeParticleEffectBrick() {
        val inputBrickFormat = "Fade particle (effect: (#PARAM_0#));"
        val inputValues = listOf(
            listOf("in"),
            listOf("out"),
        )
        executeTest(inputBrickFormat, inputValues, FadeParticleEffectBrick())
    }
    @Test
    fun testPlaySoundBrick() {
        val inputBrickFormat = "Start (sound: (#PARAM_0#));"
        val inputValues = listOf(
            listOf("'record'")
        )
        executeTest(inputBrickFormat, inputValues, PlaySoundBrick())
    }
    @Test
    fun testPlaySoundAndWaitBrick() {
        val inputBrickFormat = "Start sound and wait (sound: (#PARAM_0#));"
        val inputValues = listOf(
            listOf("'record'")
        )
        executeTest(inputBrickFormat, inputValues, PlaySoundAndWaitBrick())
    }
    @Test
    fun testPlaySoundAtBrick() {
        val inputBrickFormat = "Start sound and skip seconds (sound: (#PARAM_0#), seconds: (#PARAM_1#));"
        val inputValues = listOf(
            listOf("'record'", "0.5")
        )
        executeTest(inputBrickFormat, inputValues, PlaySoundAtBrick())
    }
    @Test
    fun testStopSoundBrick() {
        val inputBrickFormat = "Stop (sound: (#PARAM_0#));"
        val inputValues = listOf(
            listOf("'record'")
        )
        executeTest(inputBrickFormat, inputValues, StopSoundBrick())
    }
    @Test
    fun testStopAllSoundsBrick() {
        val inputBrickFormat = "Stop all sounds;"
        val inputValues = listOf(
            listOf<String>()
        )
        executeTest(inputBrickFormat, inputValues, StopAllSoundsBrick())
    }
    @Test
    fun testSetVolumeToBrick() {
        val inputBrickFormat = "Set (volume percentage: (#PARAM_0#));"
        val inputValues = listOf(
            listOf("60")
        )
        executeTest(inputBrickFormat, inputValues, SetVolumeToBrick())
    }
    @Test
    fun testChangeVolumeByNBrick() {
        val inputBrickFormat = "Change volume by (value: (#PARAM_0#));"
        val inputValues = listOf(
            listOf("- 10")
        )
        executeTest(inputBrickFormat, inputValues, ChangeVolumeByNBrick())
    }
    @Test
    fun testSetInstrumentBrick() {
        val inputBrickFormat = "Set (instrument: (#PARAM_0#));"
        val inputValues = listOf(
            listOf("piano"),
            listOf("electric piano"),
            listOf("cello"),
            listOf("flute"),
            listOf("vibraphone"),
            listOf("organ"),
            listOf("guitar"),
            listOf("electric guitar"),
            listOf("bass"),
            listOf("pizzicato"),
            listOf("synth pad"),
            listOf("choir"),
            listOf("synth lead"),
            listOf("wooden flute"),
            listOf("trombone"),
            listOf("saxophone"),
            listOf("bassoon"),
            listOf("clarinet"),
            listOf("music box"),
            listOf("steel drum"),
            listOf("marimba")
        )
        executeTest(inputBrickFormat, inputValues, SetInstrumentBrick())
    }
    @Test
    fun testPlayNoteForBeatsBrick() {
        val inputBrickFormat = "Play (note: (#PARAM_0#), number of beats: (#PARAM_1#));"
        val inputValues = listOf(
            listOf("70", "1")
        )
        executeTest(inputBrickFormat, inputValues, PlayNoteForBeatsBrick())
    }
    @Test
    fun testPlayDrumForBeatsBrick() {
        val inputBrickFormat = "Play (drum: (#PARAM_0#), number of beats: (#PARAM_1#));"
        val inputValues = listOf(
            listOf("snare drum", "1 + 2"),
            listOf("bass drum", "\"var1\""),
            listOf("side stick", "\"multiplayerVar1\" + \"localVar1\""),
            listOf("crash cymbal", "1 + 2"),
            listOf("open hi-hat", "1 + 2"),
            listOf("closed hi-hat", "1 + 2"),
            listOf("tambourine", "1 + 2"),
            listOf("hand clap", "1 + 2"),
            listOf("claves", "1 + 2"),
            listOf("wood block", "1 + 2"),
            listOf("cowbell", "1 + 2"),
            listOf("triangle", "1 + 2"),
            listOf("bongo", "1 + 2"),
            listOf("conga", "1 + 2"),
            listOf("cabasa", "1 + 2"),
            listOf("guiro", "1 + 2"),
            listOf("vibraslap", "1 - 2"),
            listOf("open cuica", "1 + 2")
        )
        executeTest(inputBrickFormat, inputValues, PlayDrumForBeatsBrick())
    }
    @Test
    fun testSetTempoBrick() {
        val inputBrickFormat = "Set (tempo: (#PARAM_0#));"
        val inputValues = listOf(
            listOf("60")
        )
        executeTest(inputBrickFormat, inputValues, SetTempoBrick())
    }
    @Test
    fun testChangeTempoByNBrick() {
        val inputBrickFormat = "Change tempo by (value: (#PARAM_0#));"
        val inputValues = listOf(
            listOf("10")
        )
        executeTest(inputBrickFormat, inputValues, ChangeTempoByNBrick())
    }
    @Test
    fun testPauseForBeatsBrick() {
        val inputBrickFormat = "Pause for (number of beats: (#PARAM_0#));"
        val inputValues = listOf(
            listOf("1")
        )
        executeTest(inputBrickFormat, inputValues, PauseForBeatsBrick())
    }
    @Test
    fun testSetLookBrick() {
        val inputBrickFormat = "Switch to (look: (#PARAM_0#));"
        val inputValues = listOf(
            listOf("'testSprite'")
        )
        executeTest(inputBrickFormat, inputValues, SetLookBrick())
    }
    @Test
    fun testSetLookByIndexBrick() {
        val inputBrickFormat = "Switch to (look by number: (#PARAM_0#));"
        val inputValues = listOf(
            listOf("1"),
            listOf("\"var1\"")
        )
        executeTest(inputBrickFormat, inputValues, SetLookByIndexBrick())
    }
    @Test
    fun testNextLookBrick() {
        val inputBrickFormat = "Next look;"
        val inputValues = listOf(
            listOf<String>()
        )
        executeTest(inputBrickFormat, inputValues, NextLookBrick())
    }
    @Test
    fun testPreviousLookBrick() {
        val inputBrickFormat = "Previous look;"
        val inputValues = listOf(
            listOf<String>()
        )
        executeTest(inputBrickFormat, inputValues, PreviousLookBrick())
    }
    @Test
    fun testSetSizeToBrick() {
        val inputBrickFormat = "Set (size percentage: (#PARAM_0#));"
        val inputValues = listOf(
            listOf("60")
        )
        executeTest(inputBrickFormat, inputValues, SetSizeToBrick())
    }
    @Test
    fun testChangeSizeByNBrick() {
        val inputBrickFormat = "Change size by (value: (#PARAM_0#));"
        val inputValues = listOf(
            listOf("10 + 9")
        )
        executeTest(inputBrickFormat, inputValues, ChangeSizeByNBrick())
    }
    @Test
    fun testHideBrick() {
        val inputBrickFormat = "Hide;"
        val inputValues = listOf(
            listOf<String>()
        )
        executeTest(inputBrickFormat, inputValues, HideBrick())
    }
    @Test
    fun testShowBrick() {
        val inputBrickFormat = "Show;"
        val inputValues = listOf(
            listOf<String>()
        )
        executeTest(inputBrickFormat, inputValues, ShowBrick())
    }
    @Test
    fun testAskBrick() {
        val inputBrickFormat = "Ask (question: (#PARAM_0#), answer variable: (#PARAM_1#));"
        val inputValues = listOf(
            listOf("'What\\'s your name?'", "\"var1\""),
            listOf("'What\\'s your name?'", "\"multiplayerVar1\""),
            listOf("'What\\'s your name?'", "\"localVar1\"")
        )
        executeTest(inputBrickFormat, inputValues, AskBrick())
    }
    @Test
    fun testSayBubbleBrick() {
        val inputBrickFormat = "Say (text: (#PARAM_0#));"
        val inputValues = listOf(
            listOf("'Hello!'")
        )
        executeTest(inputBrickFormat, inputValues, SayBubbleBrick())
    }
    @Test
    fun testSayForBubbleBrick() {
        val inputBrickFormat = "Say text for seconds (text: (#PARAM_0#), seconds: (#PARAM_1#));"
        val inputValues = listOf(
            listOf<String>("'Hello!'", "1")
        )
        executeTest(inputBrickFormat, inputValues, SayForBubbleBrick())
    }
    @Test
    fun testThinkBubbleBrick() {
        val inputBrickFormat = "Think (text: (#PARAM_0#));"
        val inputValues = listOf(
            listOf("'Hmmmm!'")
        )
        executeTest(inputBrickFormat, inputValues, ThinkBubbleBrick())
    }
    @Test
    fun testThinkForBubbleBrick() {
        val inputBrickFormat = "Think text for seconds (text: (#PARAM_0#), seconds: (#PARAM_1#));"
        val inputValues = listOf(
            listOf("'Hmmmm!'", "1")
        )
        executeTest(inputBrickFormat, inputValues, ThinkForBubbleBrick())
    }
    @Test
    fun testShowTextBrick() {
        val inputBrickFormat = "Show (variable: (#PARAM_0#), x: (#PARAM_1#), y: (#PARAM_2#));"
        val inputValues = listOf(
            listOf("\"var1\"", "100", "200"),
            listOf("\"localVar1\"", "100", "200"),
            listOf("\"multiplayerVar1\"", "100", "200")
        )
        executeTest(inputBrickFormat, inputValues, ShowTextBrick())
    }
    @Test
    fun testShowTextColorSizeAlignmentBrick() {
        val inputBrickFormat = "Show (variable: (#PARAM_0#), x: (#PARAM_1#), y: (#PARAM_2#), size: (#PARAM_3#), color: (#PARAM_4#), alignment: (#PARAM_5#));"
        val inputValues = listOf(
            listOf("\"var1\"", "100", "200", "120", "'#FF0000'", "centered"),
            listOf("\"localVar1\"", "100", "200", "120", "\"var2\"", "left"),
            listOf("\"multiplayerVar1\"", "100", "200", "120", "'#FF0000'", "right"),
        )
        executeTest(inputBrickFormat, inputValues, ShowTextColorSizeAlignmentBrick())
    }
    @Test
    fun testSetTransparencyBrick() {
        val inputBrickFormat = "Set (transparency percentage: (#PARAM_0#));"
        val inputValues = listOf(
            listOf("50")
        )
        executeTest(inputBrickFormat, inputValues, SetTransparencyBrick())
    }
    @Test
    fun testChangeTransparencyByNBrick() {
        val inputBrickFormat = "Change transparency by (value: (#PARAM_0#));"
        val inputValues = listOf(
            listOf("25")
        )
        executeTest(inputBrickFormat, inputValues, ChangeTransparencyByNBrick())
    }
    @Test
    fun testSetBrightnessBrick() {
        val inputBrickFormat = "Set (brightness percentage: (#PARAM_0#));"
        val inputValues = listOf(
            listOf("50")
        )
        executeTest(inputBrickFormat, inputValues, SetBrightnessBrick())
    }
    @Test
    fun testChangeBrightnessByNBrick() {
        val inputBrickFormat = "Change brightness by (value: (#PARAM_0#));"
        val inputValues = listOf(
            listOf("25")
        )
        executeTest(inputBrickFormat, inputValues, ChangeBrightnessByNBrick())
    }
    @Test
    fun testSetColorBrick() {
        val inputBrickFormat = "Set (color: (#PARAM_0#));"
        val inputValues = listOf(
            listOf("0")
        )
        executeTest(inputBrickFormat, inputValues, SetColorBrick())
    }
    @Test
    fun testChangeColorByNBrick() {
        val inputBrickFormat = "Change color by (value: (#PARAM_0#));"
        val inputValues = listOf(
            listOf("25")
        )
        executeTest(inputBrickFormat, inputValues, ChangeColorByNBrick())
    }
    @Test
    fun testParticleEffectAdditivityBrick() {
        val inputBrickFormat = "Turn (particle effect additivity: (#PARAM_0#));"
        val inputValues = listOf(
            listOf("on"),
            listOf("off")
        )
        executeTest(inputBrickFormat, inputValues, ParticleEffectAdditivityBrick())
    }
    @Test
    fun testSetParticleColorBrick() {
        val inputBrickFormat = "Set (particle color: (#PARAM_0#));"
        val inputValues = listOf(
            listOf("'#ff0000'")
        )
        executeTest(inputBrickFormat, inputValues, SetParticleColorBrick())
    }
    @Test
    fun testClearGraphicEffectBrick() {
        val inputBrickFormat = "Clear graphic effects;"
        val inputValues = listOf(
            listOf<String>()
        )
        executeTest(inputBrickFormat, inputValues, ClearGraphicEffectBrick())
    }
    @Test
    fun testSetBackgroundByIndexBrick() {
        val inputBrickFormat = "Set background to (look by number: (#PARAM_0#));"
        val inputValues = listOf(
            listOf("1")
        )
        executeTest(inputBrickFormat, inputValues, SetBackgroundByIndexBrick())
    }
    @Test
    fun testSetBackgroundByIndexAndWaitBrick() {
        val inputBrickFormat = "Set background and wait (look by number: (#PARAM_0#));"
        val inputValues = listOf(
            listOf("1")
        )
        executeTest(inputBrickFormat, inputValues, SetBackgroundByIndexAndWaitBrick())
    }
    @Test
    fun testCameraBrick() {
        val inputBrickFormat = "Turn (camera: (#PARAM_0#));"
        val inputValues = listOf(
            listOf("on"),
            listOf("off")
        )
        executeTest(inputBrickFormat, inputValues, CameraBrick())
    }
    @Test
    fun testChooseCameraBrick() {
        val inputBrickFormat = "Use (camera: (#PARAM_0#));"
        val inputValues = listOf(
            listOf("front"),
            listOf("rear"),
        )
        executeTest(inputBrickFormat, inputValues, ChooseCameraBrick())
    }
    @Test
    fun testFlashBrick() {
        val inputBrickFormat = "Turn (flashlight: (#PARAM_0#));"
        val inputValues = listOf(
            listOf("on"),
            listOf("off")
        )
        executeTest(inputBrickFormat, inputValues, FlashBrick())
    }
    @Test
    fun testLookRequestBrick() {
        val inputBrickFormat = "Get image and use as current look (source: (#PARAM_0#));"
        val inputValues = listOf(
            listOf("'https://catrob.at/penguin'")
        )
        executeTest(inputBrickFormat, inputValues, LookRequestBrick())
    }
    @Test
    fun testPaintNewLookBrick() {
        val inputBrickFormat = "Paint new look (name: (#PARAM_0#));"
        val inputValues = listOf(
            listOf("'name of new look'")
        )
        executeTest(inputBrickFormat, inputValues, PaintNewLookBrick())
    }
    @Test
    fun testEditLookBrick() {
        val inputBrickFormat = "Edit look;"
        val inputValues = listOf(
            listOf<String>()
        )
        executeTest(inputBrickFormat, inputValues, EditLookBrick())
    }
    @Test
    fun testCopyLookBrick() {
        val inputBrickFormat = "Copy look (name of copy: (#PARAM_0#));"
        val inputValues = listOf(
            listOf("'name of copied look'")
        )
        executeTest(inputBrickFormat, inputValues, CopyLookBrick())
    }
    @Test
    fun testDeleteLookBrick() {
        val inputBrickFormat = "Delete look;"
        val inputValues = listOf(
            listOf<String>()
        )
        executeTest(inputBrickFormat, inputValues, DeleteLookBrick())
    }
    @Test
    fun testPenDownBrick() {
        val inputBrickFormat = "Pen down;"
        val inputValues = listOf(
            listOf<String>()
        )
        executeTest(inputBrickFormat, inputValues, PenDownBrick())
    }
    @Test
    fun testPenUpBrick() {
        val inputBrickFormat = "Pen up;"
        val inputValues = listOf(
            listOf<String>()
        )
        executeTest(inputBrickFormat, inputValues, PenUpBrick())
    }
    @Test
    fun testSetPenSizeBrick() {
        val inputBrickFormat = "Set (pen size: (#PARAM_0#));"
        val inputValues = listOf(
            listOf("3.15")
        )
        executeTest(inputBrickFormat, inputValues, SetPenSizeBrick())
    }
    @Test
    fun testSetPenColorBrick() {
        val inputBrickFormat = "Set pen color (red: (#PARAM_0#), green: (#PARAM_1#), blue: (#PARAM_2#));"
        val inputValues = listOf(
            listOf<String>("255", "\"var1\"", "0")
        )
        executeTest(inputBrickFormat, inputValues, SetPenColorBrick())
    }
    @Test
    fun testStampBrick() {
        val inputBrickFormat = "Stamp;"
        val inputValues = listOf(
            listOf<String>()
        )
        executeTest(inputBrickFormat, inputValues, StampBrick())
    }
    @Test
    fun testClearBackgroundBrick() {
        val inputBrickFormat = "Clear;"
        val inputValues = listOf(
            listOf<String>()
        )
        executeTest(inputBrickFormat, inputValues, ClearBackgroundBrick())
    }
    @Test
    fun testChangeVariableBrick() {
        val inputBrickFormat = "Change (variable: (#PARAM_0#), value: (#PARAM_1#));"
        val inputValues = listOf(
            listOf("\"var1\"", "1"),
            listOf("\"localVar1\"", "1"),
            listOf("\"multiplayerVar1\"", "1")
        )
        executeTest(inputBrickFormat, inputValues, ChangeVariableBrick())
    }
    @Test
    fun testHideTextBrick() {
        val inputBrickFormat = "Hide (variable: (#PARAM_0#));"
        val inputValues = listOf(
            listOf("\"var1\"", "1"),
            listOf("\"localVar1\"", "1"),
            listOf("\"multiplayerVar1\"", "1")
        )
        executeTest(inputBrickFormat, inputValues, HideTextBrick())
    }
    @Test
    fun testWriteVariableOnDeviceBrick() {
        val inputBrickFormat = "Write on device (variable: (#PARAM_0#));"
        val inputValues = listOf(
            listOf("\"var1\"", "1"),
            listOf("\"localVar1\"", "1"),
            listOf("\"multiplayerVar1\"", "1")
        )
        executeTest(inputBrickFormat, inputValues, WriteVariableOnDeviceBrick())
    }
    @Test
    fun testReadVariableFromDeviceBrick() {
        val inputBrickFormat = "Read from device (variable: (#PARAM_0#));"
        val inputValues = listOf(
            listOf("\"var1\"", "1"),
            listOf("\"localVar1\"", "1"),
            listOf("\"multiplayerVar1\"", "1")
        )
        executeTest(inputBrickFormat, inputValues, ReadVariableFromDeviceBrick())
    }
    @Test
    fun testWriteVariableToFileBrick() {
        val inputBrickFormat = "Write to file (file: (#PARAM_1#), variable: (#PARAM_0#));"
        val inputValues = listOf(
            listOf("\"var1\"", "'variable.txt'"),
            listOf("\"localVar1\"", "'variable1.txt'"),
            listOf("\"multiplayerVar1\"", "'variable2.txt'")
        )
        val expectedBrickFormat = "Write to file (variable: (#PARAM_0#), file: (#PARAM_1#));"
        executeTest(inputBrickFormat, inputValues, WriteVariableToFileBrick(), expectedBrickFormat)
    }
    @Test
    fun testReadVariableFromFileBrick() {
        val inputBrickFormat = "Read from file (variable: (#PARAM_0#), file: (#PARAM_1#), action: (#PARAM_2#));"
        val inputValues = listOf(
            listOf("\"var1\"", "'variable.txt'", "keep the file"),
            listOf("\"localVar1\"", "'variable1.txt'", "delete the file"),
            listOf("\"multiplayerVar1\"", "'variable2.txt'", "keep the file")
        )
        executeTest(inputBrickFormat, inputValues, ReadVariableFromFileBrick())
    }
    @Test
    fun testAddItemToUserListBrick() {
        val inputBrickFormat = "Add (list: (#PARAM_0#), item: (#PARAM_1#));"
        val inputValues = listOf(
            listOf("*list1*", "1"),
            listOf("*localList1*", "1")
        )
        executeTest(inputBrickFormat, inputValues, AddItemToUserListBrick())
    }
    @Test
    fun testDeleteItemOfUserListBrick() {
        val inputBrickFormat = "Delete item at (list: (#PARAM_0#), position: (#PARAM_1#));"
        val inputValues = listOf(
            listOf("*list1*", "1"),
            listOf("*localList1*", "1")
        )
        executeTest(inputBrickFormat, inputValues, DeleteItemOfUserListBrick())
    }
    @Test
    fun testClearUserListBrick() {
        val inputBrickFormat = "Delete all items (list: (#PARAM_0#));"
        val inputValues = listOf(
            listOf("*list1*"),
            listOf("*localList1*")
        )
        executeTest(inputBrickFormat, inputValues, ClearUserListBrick())
    }
    @Test
    fun testInsertItemIntoUserListBrick() {
        val inputBrickFormat = "Insert (list: (#PARAM_0#), position: (#PARAM_1#), value: (#PARAM_2#));"
        val inputValues = listOf(
            listOf("*list1*", "1", "\"var1\""),
            listOf("*localList1*", "1", "\"localVar1\""),
        )
        executeTest(inputBrickFormat, inputValues, InsertItemIntoUserListBrick())
    }
    @Test
    fun testReplaceItemInUserListBrick() {
        val inputBrickFormat = "Replace (value: (#PARAM_2#), list: (#PARAM_0#), position: (#PARAM_1#));"
        val inputValues = listOf(
            listOf("*list1*", "1", "\"var1\""),
            listOf("*localList1*", "1", "\"localVar1\""),
        )
        val expectedBrickFormat = "Replace (list: (#PARAM_0#), position: (#PARAM_1#), value: (#PARAM_2#));"
        executeTest(inputBrickFormat, inputValues, ReplaceItemInUserListBrick(), expectedBrickFormat)
    }
    @Test
    fun testWriteListOnDeviceBrick() {
        val inputBrickFormat = "Write on device (list: (#PARAM_0#));"
        val inputValues = listOf(
            listOf("*list1*"),
            listOf("*localList1*")
        )
        executeTest(inputBrickFormat, inputValues, WriteListOnDeviceBrick())
    }
    @Test
    fun testReadListFromDeviceBrick() {
        val inputBrickFormat = "Read from device (list: (#PARAM_0#));"
        val inputValues = listOf(
            listOf("*list1*"),
            listOf("*localList1*")
        )
        executeTest(inputBrickFormat, inputValues, ReadListFromDeviceBrick())
    }
    @Test
    fun testWebRequestBrick() {
        val inputBrickFormat = "Send web request (url: (#PARAM_0#), answer variable: (#PARAM_1#));"
        val inputValues = listOf(
            listOf("'https://catrob.at/joke'", "\"var1\""),
            listOf("'https://catrob.at/joke'", "\"localVar1\""),
            listOf("'https://catrob.at/joke'", "\"multiplayerVar1\"")
        )
        executeTest(inputBrickFormat, inputValues, WebRequestBrick())
    }
    @Test
    fun testResetTimerBrick() {
        val inputBrickFormat = "Reset timer;"
        val inputValues = listOf(
            listOf<String>()
        )
        executeTest(inputBrickFormat, inputValues, ResetTimerBrick())
    }
    @Test
    fun testReportBrick() {
        val inputBrickFormat = "Return (value: (#PARAM_0#));"
        val inputValues = listOf(
            listOf("0")
        )
        executeTest(inputBrickFormat, inputValues, ReportBrick())
    }
    @Test
    fun testAssertEqualsBrick() {
        val inputBrickFormat = "Assert equals (actual: (#PARAM_0#), expected: (#PARAM_1#));"
        val inputValues = listOf(
            listOf("0", "0")
        )
        executeTest(inputBrickFormat, inputValues, AssertEqualsBrick())
    }
    @Test
    fun testAssertUserListsBrick() {
        val inputBrickFormat = "Assert lists (actual: (#PARAM_0#), expected: (#PARAM_1#));"
        val inputValues = listOf(
            listOf<String>("*list1*", "*localList1*")
        )
        executeTest(inputBrickFormat, inputValues, AssertUserListsBrick())
    }
    @Test
    fun testFinishStageBrick() {
        val inputBrickFormat = "Finish tests;"
        val inputValues = listOf(
            listOf<String>()
        )
        executeTest(inputBrickFormat, inputValues, FinishStageBrick())
    }

    @Test
    fun testStoreCSVIntoUserListBrick() {
        val inputBrickFormat = "Store column of comma-separated values to list (list: (#PARAM_0#), csv: (#PARAM_1#), column: (#PARAM_2#));"
        val inputValues = listOf(
            listOf("*list1*", "'kitty,cute\\npuppy,naughty\\noctopus,intelligent'", "1"),
            listOf("*localList1*", "'kitty,cute\\npuppy,naughty\\noctopus,intelligent'", "1")
        )
        executeTest(inputBrickFormat, inputValues, StoreCSVIntoUserListBrick())
    }

    @Test
    fun testSetBackgroundBrick() {
        val inputBrickFormat = "Set background to (look: (#PARAM_0#));"
        val inputValues = listOf(
            listOf("'testSprite'")
        )
        executeTest(inputBrickFormat, inputValues, SetBackgroundBrick())
    }

    @Test
    fun testSetBackgroundAndWaitBrick() {
        val inputBrickFormat = "Set background and wait (look: (#PARAM_0#));"
        val inputValues = listOf(
            listOf("'testSprite'")
        )
        executeTest(inputBrickFormat, inputValues, SetBackgroundAndWaitBrick())
    }

    @Test
    fun testForeverBrick() {
        val inputBrickFormat = "Forever {\n$brickIndention}"
        val inputValues = listOf<List<String>>(
            listOf()
        )
        executeTest(inputBrickFormat, inputValues, ForeverBrick())
    }

    @Test
    fun testIfLogicBeginBrick() {
        val inputBrickFormat = "If (condition: (#PARAM_0#)) {\n" +
            "$brickIndention} else {\n" +
            "$brickIndention}"
        val inputValues = listOf(
            listOf("\"var1\" < 5"),
            listOf("\"multiplayerVar1\" < 10 || ( \"var2\" > 100 && true )")
        )
        executeTest(inputBrickFormat, inputValues, IfLogicBeginBrick())
    }

    @Test
    fun testIfThenLogicBeginBrick() {
        val inputBrickFormat = "If (condition: (#PARAM_0#)) {\n" +
            "$brickIndention}"
        val inputValues = listOf(
            listOf("\"var1\" < 5"),
            listOf("\"multiplayerVar1\" < 10 || ( \"var2\" > 100 && true )")
        )
        executeTest(inputBrickFormat, inputValues, IfThenLogicBeginBrick())
    }

    @Test
    fun testRepeatBrick() {
        val inputBrickFormat = "Repeat (times: (#PARAM_0#)) {\n" +
            "$brickIndention}"
        val inputValues = listOf(
            listOf("10"),
            listOf("\"localVar1\"")
        )
        executeTest(inputBrickFormat, inputValues, RepeatBrick())
    }

    @Test
    fun testRepeatUntilBrick() {
        val inputBrickFormat = "Repeat until (condition: (#PARAM_0#)) {\n" +
            "$brickIndention}"
        val inputValues = listOf(
            listOf("\"var1\" < 5"),
            listOf("\"multiplayerVar1\" < 10 || ( \"var2\" > 100 && true )")
        )
        executeTest(inputBrickFormat, inputValues, RepeatUntilBrick())
    }

    @Test
    fun testForVariableFromToBrick() {
        val inputBrickFormat = "For (value: (#PARAM_0#), from: (#PARAM_1#), to: (#PARAM_2#)) {\n" +
            "$brickIndention}"
        val inputValues = listOf(
            listOf("\"localVar1\"", "\"var1\"", "500")
        )
        executeTest(inputBrickFormat, inputValues, ForVariableFromToBrick())
    }

    @Test
    fun testForItemInUserListBrick() {
        val inputBrickFormat = "For each value in list (value: (#PARAM_0#), list: (#PARAM_1#)) {\n" +
            "$brickIndention}"
        val inputValues = listOf(
            listOf("\"localVar1\"", "*list1*"),
            listOf("\"var1\"", "*localList1*")
        )
        executeTest(inputBrickFormat, inputValues, ForItemInUserListBrick())
    }

    @Test
    fun testParameterizedBrick() {
        val inputBrickFormat = "For each tuple of items in selected lists stored in variables with the same name, assert value equals to the expected item of reference list (lists: (#PARAM_0#), value: (#PARAM_1#), reference list: (#PARAM_2#)) {\n" +
            "$brickIndention}"
        val inputValues = listOf(
            listOf("*list1*, *list2*", "0", "*localList1*"),
            listOf("*localList1*", "\"var1\"", "*list1*"),
            listOf("*localList1*, *list2*", "\"var1\"", "*list3*")
        )
        executeTest(inputBrickFormat, inputValues, ParameterizedBrick())
    }

    @Test
    fun testRaspiIfLogicBeginBrick() {
        val inputBrickFormat = "If (Raspberry Pi pin: (#PARAM_0#)) {\n" +
            "$brickIndention} else {\n" +
            "$brickIndention}"
        val inputValues = listOf(
            listOf("0"),
            listOf("10"),
            listOf("\"var1\"")
        )
        executeTest(inputBrickFormat, inputValues, RaspiIfLogicBeginBrick())
    }

    @Test
    fun testPhiroIfLogicBeginBrick() {
        val inputBrickFormat = "If (activated phiro: (#PARAM_0#)) {\n" +
            "$brickIndention} else {\n" +
            "$brickIndention}"
        val inputValues = listOf(
            listOf("front left sensor"),
            listOf("front right sensor"),
            listOf("side left sensor"),
            listOf("side right sensor"),
            listOf("bottom left sensor"),
            listOf("bottom right sensor")
        )
        executeTest(inputBrickFormat, inputValues, PhiroIfLogicBeginBrick())
    }

    private val serializedProgram = """#! Catrobat Language Version 0.1
Program 'Brick Parsing Test' {
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
        When tapped {
          #BRICK_PLACEHOLDER#
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
        expectedBrickType: Brick,
        expectedBrickFormat: String? = null,
        expectedValues: List<List<String>>? = null) {

        val locales = listOf(Locale.ROOT, Locale.GERMAN, Locale.CHINA)

        for (locale in locales) {
            executeLocalizedTest(inputBrickFormat, inputValues, expectedBrickType, locale, expectedBrickFormat, expectedValues)
        }
    }

    private fun executeLocalizedTest(
        inputBrickFormat: String,
        inputValues: List<List<String>>,
        expectedBrickType: Brick,
        locale: Locale,
        expectedBrickFormat: String? = null,
        expectedValues: List<List<String>>? = null) {

        for (testIndex in inputValues.indices) {
            var inputBrickString = inputBrickFormat
            for (valueIndex in inputValues[testIndex].indices) {
                inputBrickString = inputBrickString.replace("#PARAM_$valueIndex#", inputValues[testIndex][valueIndex])
            }
            var expectedBrickString = expectedBrickFormat ?: inputBrickFormat
            if (expectedValues != null) {
                for (valueIndex in expectedValues[testIndex].indices) {
                    expectedBrickString = expectedBrickString.replace("#PARAM_$valueIndex#", expectedValues[testIndex][valueIndex])
                }
            } else {
                for (valueIndex in inputValues[testIndex].indices) {
                    expectedBrickString = expectedBrickString.replace("#PARAM_$valueIndex#", inputValues[testIndex][valueIndex])
                }
            }

            val programString = serializedProgram.replace("#BRICK_PLACEHOLDER#", inputBrickString)
            val expectedProgram = serializedProgram.replace("#BRICK_PLACEHOLDER#", expectedBrickString)
            try {
                val context = CatroidApplication.getAppContext()
                var configuration = context.resources.configuration
                configuration = Configuration(configuration)
                configuration.setLocale(locale)
                context.createConfigurationContext(configuration)

                val parsedProgram = CatrobatLanguageParser.parseProgramFromString(programString, context)

                val parsedBrick = parsedProgram!!.sceneList[0].spriteList[1].scriptList[0].brickList[0]
                assertEquals(expectedBrickType::class.java, parsedBrick::class.java)

                val serializedProgram = CatrobatLanguageProjectSerializer(parsedProgram, context).serialize()

                assertEquals(expectedProgram, serializedProgram)
            } catch (throwable: Throwable) {
                throw throwable
            }
        }
    }
}